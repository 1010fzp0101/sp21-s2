package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Blob;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    public static final File BLOBs = join(OBJECT_DIR, "blob");
    public static final File COMMITs = join(OBJECT_DIR, "commits");
    public static final File INDEX = join(GITLET_DIR, "index");
    public static final File INDEX_RM = join(GITLET_DIR, "index_rm");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static File HEAD = join(GITLET_DIR, "HEAD");

    /* TODO: fill in the rest of this class. */

    public static void initCommand() {
        /* check whether the directory has been initialized*/
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        try {
            GITLET_DIR.mkdir();
            INDEX.createNewFile();
            INDEX_RM.createNewFile();
            REFS_DIR.mkdir();
            OBJECT_DIR.mkdir();
            BLOBs.mkdir();
            COMMITs.mkdir();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // add the initial Commit
        Commit initialCommit = new Commit("initial commit");

        // update the pointer pointed by branch and HEAD pointer and
        File master = join(REFS_DIR, "master");
        writeContents(HEAD, master.getName());
        writeContents(master, initialCommit.getID());

        // update the commitDir
        addCommitToDirCommit(initialCommit);
    }

    @SuppressWarnings("unchecked")
    public static void add(String filename) {
        File file = join(CWD, filename);

        /*check whether the file exists */
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        HashMap<String, String> fileToHash = readFromStagingArea(INDEX);
        byte[] bytes = readContents(file);
        String hash = sha1((Object) bytes);

        /*If the current working version of the file is identical
        to the version in the current commit, do not stage it to be added, and
        remove it from the staging area if it is already there
         */
        HashMap<String, String> map = getCommitByHEAD().getNameToHash();
        if (map.containsKey(filename) && map.get(filename).equals(hash)) {
            if (isInIndex(filename)) {
                fileToHash.remove(filename);
                writeObject(INDEX, fileToHash);
            }
            System.exit(0);
        }

        writeContents(join(BLOBs, hash), bytes);
        fileToHash.put(filename, hash);
        writeObject(INDEX, fileToHash);
    }

    @SuppressWarnings("unchecked")
    public static void commit(String message) {
        //check whether there exists some files to be staged
        if (indexIsEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        String parentID = getCommitIDByHEAD();
        Commit parentCommit = getCommitByHEAD();

        HashMap<String, String> blobToName = parentCommit.getNameToHash();
        HashMap<String, String> indexMap = readObject(INDEX, HashMap.class);
        HashMap<String, String> indexRmMap = readFromStagingArea(INDEX_RM);

        // change the file in last commit being modified
        for (Map.Entry<String, String> entry : indexMap.entrySet()) {
            String name = entry.getKey();
            String hash = entry.getValue();
            blobToName.put(name, hash);
        }
        for (String name : indexRmMap.keySet()) {
            blobToName.remove(name);
        }

        Commit cmt = new Commit(message, blobToName, parentID);
        addCommitToDirCommit(cmt);

        // clear up the staging area.
        clearIndex();
        clearIndexRm();
    }

    @SuppressWarnings("unchecked")
    public static void rm(String filename) {
        File file = join(CWD, filename);
        HashMap<String, String> rmMap = readFromStagingArea(INDEX_RM);
        HashMap<String, String> indexMap = readObject(INDEX, HashMap.class);

        /* If the file is neither staged nor tracked by the head commit
         , print the error message */
        if (!isTrackedByHEAD(filename) && !isInIndex(filename)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        // Unstage the file if it is currently staged for addition
        if (isInIndex(filename)) {
            indexMap.remove(filename);
            writeObject(INDEX, indexMap);
        }

        //If the file is tracked in the current commit
        if (isTrackedByHEAD(filename)) {
            //stage it for removal
            String hash = getCommitByHEAD().getNameToHash().get(filename);
            rmMap.put(filename, hash);
            writeObject(INDEX_RM, rmMap);

            //remove the file from the working directory
            file.delete();
        }
    }

    /* display commit id, the time the commit made, and the commit message*/
    public static void log() {
        Commit current = getCommitByHEAD();
        while (current != null) {
            System.out.println("===");
            System.out.println("commit " + current.getID());
            System.out.println("Date: " + current.getTimestamp());
            System.out.println(current.getMessage());
            System.out.println();
            if (current.getParent1ID() == null) {
                break;
            }
            current = prevCommit(current);
        }
    }

    public static void globalLog() {
        List<String> list = plainFilenamesIn(COMMITs);
        for (String hash : list) {
            Commit cmt = readObject(join(COMMITs, hash), Commit.class);
            System.out.println("===");
            System.out.println("commit " + cmt.getID());
            System.out.println("Date: " + cmt.getTimestamp());
            System.out.println(cmt.getMessage());
            System.out.println();
        }
    }

    public static void find(String message) {
        boolean found = false;
        List<String> list = plainFilenamesIn(COMMITs);
        for (String hash : list) {
            Commit cmt = readObject(join(COMMITs, hash), Commit.class);
            if (cmt.getMessage().equals(message)) {
                System.out.println(hash);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    @SuppressWarnings("unchecked")
    public static void status() {
        System.out.println("=== Branches ===");
        String currentBranch = getCurrentBranchName();
        System.out.println("*" + currentBranch);
        List<String> branchList = plainFilenamesIn(REFS_DIR);
        for (String branch : branchList) {
            if (!branch.equals(currentBranch)) {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        HashMap<String, String> indexMap = readFromStagingArea(INDEX);
        for (String name : indexMap.keySet()) {
            System.out.println(name);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        HashMap<String, String> rmMap = readFromStagingArea(INDEX_RM);
        for (String name : rmMap.keySet()) {
            System.out.println(name);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        /* 1. Tracked in the current commit, changed in the working directory,
            but not staged
        2. Staged for addition, but with different contents than in the working directory
        3. Staged for addition, but deleted in the working directory
        4. Not staged for removal, but tracked in the current commit and deleted from the
            working directory
         */
        List<String> cwdList = plainFilenamesIn(CWD);
        HashMap<String, String> currentCommitList = getCommitByHEAD().getNameToHash();
        for (Map.Entry<String, String> entry : currentCommitList.entrySet()) {
            String name = entry.getKey();
            String hash = entry.getValue();
            File file = join(CWD, name);
            if (file.exists()) {
                String fileHash = sha1((Object) readContents(file));
                if (!fileHash.equals(hash) && !isInIndex(name)) {
                    System.out.println(name);
                }
            } else {
                if (!isInRmIndex(name)) {
                    System.out.println(name);
                }
            }
        }
        for (Map.Entry<String, String> entry : indexMap.entrySet()) {
            String name = entry.getKey();
            String hash = entry.getValue();
            File file = join(CWD, name);
            if (file.exists()) {
                String fileHash = sha1((Object) readContents(file));
                if (!fileHash.equals(hash)) {
                    System.out.println(name);
                }
            }
            System.out.println();
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        /*files present in the working directory but neither staged
        for addition nor tracked
         */
        for (String name : cwdList) {
            if (!isInIndex(name) && !isTrackedByHEAD(name)) {
                System.out.println(name);
            }
        }
        System.out.println();
    }

    public static void checkout1(String filename) {
        checkout2(getCommitIDByHEAD(), filename);
    }

    /*Takes the version of the file as it exists in the commit with the given id,
     and puts it in the working directory, overwriting the version of the file
     that’s already there if there is one
     */
    public static void checkout2(String commitID, String filename) {
        List<String> commitList = plainFilenamesIn(COMMITs);
        boolean existCommit = false;
        for (String hash : commitList) {
            if (hash.startsWith(commitID)) {
                existCommit = true;
                break;
            }
        }
        if (!existCommit) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File commitFile = join(COMMITs, commitID);
        Commit cmt = readObject(commitFile, Commit.class);
        HashMap<String, String> commitMap = cmt.getNameToHash();
        if (!commitMap.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String fileHash = commitMap.get(filename);
        File blobFile = join(BLOBs, fileHash);
        File cwdFile = join(CWD, filename);
        writeContents(cwdFile, (Object) readContents(blobFile));
    }

    /* Takes all files in the commit at the head of the given branch, and puts them
    in the working directory, overwriting the versions of the files that are already
    there if they exist. Also, at the end of this command, the given branch will now
    be considered the current branch (HEAD). Any files that are tracked in the current
    branch but are not present in the checked-out branch are deleted, The staging area
    is cleared,unless the checked-out branch is the current branch
     */
    public static void checkout3(String branchName) {
        if (branchName.equals(getCurrentBranchName())) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        List<String> refList = plainFilenamesIn(REFS_DIR);
        if (!refList.contains(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        HashMap<String, String> commitFilesOfBranch = getFileNamesOfBranch(branchName);
        List<String> cwdList = plainFilenamesIn(CWD);
        String currentBranch = getCurrentBranchName();
        for (String name : cwdList) {
            if (!isTrackedByBranch(name, currentBranch)
                    && commitFilesOfBranch.containsKey(name)) {
                System.out.println("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        for (String name : cwdList) {
            if (!commitFilesOfBranch.containsKey(name) && isTrackedByHEAD(name)) {
                restrictedDelete(name);
                continue;
            }
            File currentFile = join(CWD, name);
            File branchFile = join(BLOBs, commitFilesOfBranch.get(name));
            writeContents(currentFile, (Object) readContents(branchFile));
        }
        for (Map.Entry<String, String> entry : commitFilesOfBranch.entrySet()) {
            String name = entry.getKey();
            if (!cwdList.contains(name)) {
                File currentFile = join(CWD, name);
                File branch = join(BLOBs, entry.getValue());
                writeContents(currentFile, (Object) readContents(branch));
            }
        }

        clearIndex();
        clearIndexRm();
        writeContents(HEAD, branchName);
    }

    /* Creates a new branch with the given name, and points it at the current head commit*/
    public static void branch(String newBranch) {
        File branch = join(REFS_DIR, newBranch);
        if (branch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        writeContents(branch, getCommitIDByHEAD());
    }

    /* Deletes the branch with the given name */
    public static void rmBranch(String branchName) {
        List<String> refList = plainFilenamesIn(REFS_DIR);
        if (!refList.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (getCurrentBranchName().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        restrictedDelete(join(REFS_DIR, branchName));
    }

    public static void reset(String commitID) {
        writeContents(getCurrentBranchFile(), commitID);
        HashMap<String, String> currentFileName = getFileNamesOfBranch(getCurrentBranchName());
        for (String names : currentFileName.keySet()) {
            checkout2(commitID, names);
        }
    }

    public static void merge() {}

    public static void addRemote() {}

    public static void rmRemote() {}

    public static void push() {}

    public static void fetch() {}

    public static void pull() {}
}
