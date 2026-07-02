package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

public class Repository implements Serializable {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    public static final File BLOBS = join(OBJECT_DIR, "blob");
    public static final File COMMITS = join(OBJECT_DIR, "commits");
    public static final File INDEX = join(GITLET_DIR, "index");
    public static final File INDEX_RM = join(GITLET_DIR, "index_rm");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    private static File HEAD = join(GITLET_DIR, "HEAD");


    public static void initCommand() {
        /* check whether the directory has been initialized*/
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists " +
                    "in the current directory.");
            System.exit(0);
        }

        try {
            GITLET_DIR.mkdir();
            INDEX.createNewFile();
            INDEX_RM.createNewFile();
            REFS_DIR.mkdir();
            OBJECT_DIR.mkdir();
            BLOBS.mkdir();
            COMMITS.mkdir();
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

        HashMap<String, String> index = readFromStagingArea(INDEX);
        HashMap<String, String> rmInex = readFromStagingArea(INDEX_RM);
        byte[] bytes = readContents(file);
        String hash = sha1((Object) bytes);

        /*If the current working version of the file is identical
        to the version in the current commit, do not stage it to be added, and
        remove it from the staging area if it is already there
         */
        HashMap<String, String> map = getCommitByHEAD().getNameToHash();
        if (map.containsKey(filename) && map.get(filename).equals(hash)) {
            if (isInIndex(filename)) {
                index.remove(filename);
                writeObject(INDEX, index);
            }
            if (isInRmIndex(filename)) {
                rmInex.remove(filename);
                writeObject(INDEX_RM, rmInex);
            }
            System.exit(0);
        }

        writeContents(join(BLOBS, hash), bytes);
        index.put(filename, hash);
        writeObject(INDEX, index);
    }

    @SuppressWarnings("unchecked")
    public static void commit(String message) {
        //check whether there exists some files to be staged
        if (indexIsEmpty() && indexRmIsEmpty()) {
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
        HashMap<String, String> indexMap = readFromStagingArea(INDEX);

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
            if (current.getParent2ID() != null) {
                System.out.println("Merge: " + current.getParent1ID().substring(0, 7)
                    + " " + current.getParent2ID().substring(0, 7));
            }
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
        List<String> list = plainFilenamesIn(COMMITS);
        for (String hash : list) {
            Commit cmt = readObject(join(COMMITS, hash), Commit.class);
            System.out.println("===");
            System.out.println("commit " + cmt.getID());
            if (cmt.getParent2ID() != null) {
                System.out.println("Merge: " + cmt.getParent1ID().substring(0, 7)
                        + " " + cmt.getParent2ID().substring(0, 7));
            }
            System.out.println("Date: " + cmt.getTimestamp());
            System.out.println(cmt.getMessage());
            System.out.println();
        }
    }

    public static void find(String message) {
        boolean found = false;
        List<String> list = plainFilenamesIn(COMMITS);
        for (String hash : list) {
            Commit cmt = readObject(join(COMMITS, hash), Commit.class);
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
                    System.out.println(name + " (modified)");
                }
            } else {
                if (!isInRmIndex(name)) {
                    System.out.println(name + " (deleted)");
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
            } else {
                System.out.println(name + " (deleted)");
            }
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
        List<String> commitList = plainFilenamesIn(COMMITS);
        boolean existCommit = false;
        for (String hash : commitList) {
            if (hash.startsWith(commitID)) {
                commitID = hash;
                existCommit = true;
                break;
            }
        }
        if (!existCommit) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File commitFile = join(COMMITS, commitID);
        Commit cmt = readObject(commitFile, Commit.class);
        HashMap<String, String> commitMap = cmt.getNameToHash();
        if (!commitMap.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String fileHash = commitMap.get(filename);
        File blobFile = join(BLOBS, fileHash);
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
            if (!isTrackedByBranch(currentBranch, name)
                    && commitFilesOfBranch.containsKey(name)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        for (String name : cwdList) {
            if (!commitFilesOfBranch.containsKey(name) && isTrackedByHEAD(name)) {
                restrictedDelete(name);
                continue;
            }
            File currentFile = join(CWD, name);
            File branchFile = join(BLOBS, commitFilesOfBranch.get(name));
            writeContents(currentFile, (Object) readContents(branchFile));
        }
        for (Map.Entry<String, String> entry : commitFilesOfBranch.entrySet()) {
            String name = entry.getKey();
            if (!cwdList.contains(name)) {
                File currentFile = join(CWD, name);
                File branch = join(BLOBS, entry.getValue());
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
        join(REFS_DIR, branchName).delete();
    }

    public static void reset(String commitID) {
        List<String> commitList = plainFilenamesIn(COMMITS);
        String fullID = null;
        for (String id : commitList) {
            if (id.startsWith(commitID)) {
                fullID = id;
            }
        }
        if (fullID == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        HashMap<String, String> commitMap = getFileNamesOfCommitID(fullID);
        List<String> cwdList = plainFilenamesIn(CWD);
        if (cwdList == null) {
            cwdList = new ArrayList<>();
        }
        for (String fileName : cwdList) {
            if (!isTrackedByHEAD(fileName)
                    && commitMap.containsKey(fileName)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (Map.Entry<String, String> entry : commitMap.entrySet()) {
            String name = entry.getKey();
            String id = entry.getValue();
            checkout2(fullID, name);
        }
        HashMap<String, String> currentFile = getFileNamesOfBranch(getCurrentBranchName());
        for (String name : currentFile.keySet()) {
            if (!commitMap.containsKey(name)) {
                restrictedDelete(name);
            }
        }
        clearIndex();
        clearIndexRm();
        writeContents(getCurrentBranchFile(), fullID);
    }

    public static void merge(String givenBranchName) {
        if (!indexIsEmpty() || !indexRmIsEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!isBranchExist(givenBranchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        String currentBranchName = getCurrentBranchName();
        Commit currentCommit = getCommitByHEAD();
        Commit givenCommit = getCommitOfBranch(givenBranchName);
        String currentCommitID = currentCommit.getID();
        String givenCommitID = givenCommit.getID();
        String splitCommitID = findSplitPoint(currentCommitID, givenCommitID);
        HashMap<String, String> givenBranchFileMap = getFileNamesOfBranch(givenBranchName);
        HashMap<String, String> currentBranchFileMap = getFileNamesOfBranch(currentBranchName);
        List<String> cwdList = plainFilenamesIn(CWD);
        Boolean isConflict = false;

        if (givenBranchName.equals(currentBranchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        if (splitCommitID.equals(givenCommitID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }

        if (splitCommitID.equals(currentCommitID)) {
            checkout3(givenBranchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }

        if (cwdList != null) {
            for (String name : cwdList) {
                if (!isTrackedByHEAD(name) && isInBranch(name, givenBranchName)) {
                    System.out.println("There is an untracked file in the way; delete it, "
                            + "or add and commit it first.");
                    System.exit(0);
                }
            }
        }

        for (Map.Entry<String, String> entry : givenBranchFileMap.entrySet()) {
            String name = entry.getKey();
            String hash = entry.getValue();
            String givenContent = readContentsAsString(join(BLOBS, hash));
            if (isInBranch(name, currentBranchName)
                && isInCommit(name, splitCommitID)
                && isModifiedID(name, splitCommitID, givenCommitID)
                && !isModifiedID(name, splitCommitID, currentCommitID)) {
                checkout2(givenCommitID, name);
                addToStagingArea(name, hash, INDEX);
            }
            if (!isInBranch(name, currentBranchName)
                && !isInCommit(name, splitCommitID)) {
                checkout2(givenCommitID, name);
                addToStagingArea(name, hash, INDEX);
            }
            if (isInCommit(name, splitCommitID)) {
                if (isInBranch(name, currentBranchName)
                    && !currentBranchFileMap.get(name).equals(hash)
                    && isModifiedID(name, splitCommitID, currentCommitID)) {
                    String currentContent = readContentsAsString(join(CWD, name));
                    writeConflict(currentContent, givenContent, name);
                    isConflict = true;
                } else if (!isInBranch(name, currentBranchName)
                    && !getHashInCommitID(name, splitCommitID).equals(hash)) {
                    writeConflict("", givenContent, name);
                    isConflict = true;
                }
            } else {
                if (isInBranch(name, currentBranchName)
                        && !currentBranchFileMap.get(name).equals(hash)) {
                    String currentContent = readContentsAsString(join(CWD, name));
                    writeConflict(currentContent, givenContent, name);
                    isConflict = true;
                }
            }
        }

        for (Map.Entry<String, String> entry : currentBranchFileMap.entrySet()) {
            String name = entry.getKey();
            String hash = entry.getValue();
            String currentContent = readContentsAsString(join(BLOBS, hash));
            if (isInCommit(name, splitCommitID)
                && !isModifiedID(name, splitCommitID, currentCommitID)
                && !isInBranch(name, givenBranchName)) {
                rm(name);
            }
            if (isInCommit(name, splitCommitID)
                && !isInBranch(name, givenBranchName)
                && isModifiedID(name, splitCommitID, currentCommitID)) {
                writeConflict(currentContent, "", name);
                isConflict = true;
            }
        }

        commit("Merged " + givenBranchName + " into " + currentBranchName + ".");
        if (isConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private static void writeConflict(String currentContent,
                                      String givenContent, String filename) {
        String conflict = "<<<<<<< HEAD\n" + currentContent + "=======\n"
                + givenContent + ">>>>>>>\n";
        writeContents(join(CWD, filename), conflict);
    }

    /* the file being added must be in blob dir */
    private static void addToStagingArea(String fileName, String hash, File stagingArea) {
        HashMap<String, String> index = readFromStagingArea(stagingArea);
        index.put(fileName, hash);
        writeObject(stagingArea, index);
    }

    private static String findSplitPoint(String currentBranchID, String givenBranchID) {
        Set<String> currentAncestor = new HashSet<>();
        String id = currentBranchID;
        while (id != null && !id.isEmpty()) {
            currentAncestor.add(id);
            File currentCommitFile = join(COMMITS, id);
            Commit currentCommit = readObject(currentCommitFile, Commit.class);
            id = currentCommit.getParent1ID();
        }
        id = givenBranchID;
        while (id != null && !id.isEmpty()) {
            if (currentAncestor.contains(id)) {
                return id;
            }
            File givenCommitFile = join(COMMITS, id);
            Commit givenCommit = readObject(givenCommitFile, Commit.class);
            id = givenCommit.getParent1ID();
        }
        return null;
    }

}
