package gitlet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import static gitlet.Repository.*;

import static java.time.ZoneOffset.UTC;


/** Assorted utilities.
 *
 * Give this file a good read as it provides several useful utility functions
 * to save you some time.
 *
 *  @author P. N. Hilfinger
 */
class Utils {

    /** The length of a complete SHA-1 UID as a hexadecimal numeral. */
    static final int UID_LENGTH = 40;

    /* SHA-1 HASH VALUES. */

    /** Returns the SHA-1 hash of the concatenation of VALS, which may
     *  be any mixture of byte arrays and Strings. */
    static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /** Returns the SHA-1 hash of the concatenation of the strings in
     *  VALS. */
    static String sha1(List<Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    /* FILE DELETION */

    /** Deletes FILE if it exists and is not a directory.  Returns true
     *  if FILE was deleted, and false otherwise.  Refuses to delete FILE
     *  and throws IllegalArgumentException unless the directory designated by
     *  FILE also contains a directory named .gitlet. */
    static boolean restrictedDelete(File file) {
        if (!(new File(file.getParentFile(), ".gitlet")).isDirectory()) {
            throw new IllegalArgumentException("not .gitlet working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /** Deletes the file named FILE if it exists and is not a directory.
     *  Returns true if FILE was deleted, and false otherwise.  Refuses
     *  to delete FILE and throws IllegalArgumentException unless the
     *  directory designated by FILE also contains a directory named .gitlet. */
    static boolean restrictedDelete(String file) {
        return restrictedDelete(new File(file));
    }

    /* READING AND WRITING FILE CONTENTS */

    /** Return the entire contents of FILE as a byte array.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Return the entire contents of FILE as a String.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }

    /** Write the result of concatenating the bytes in CONTENTS to FILE,
     *  creating or overwriting it as needed.  Each object in CONTENTS may be
     *  either a String or a byte array.  Throws IllegalArgumentException
     *  in case of problems. */
    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                    new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }


    /* translate the object to the according hash value and write it
    to the file.
     */

    /** Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
     *  Throws IllegalArgumentException in case of problems. */
    static <T extends Serializable> T readObject(File file,
                                                 Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                 | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    static HashMap<String, String> readFromStagingArea(File file) {
        if (file.length() == 0) {
            return new HashMap<>();
        } else {
            return readObject(file, HashMap.class);
        }
    }

    /** Write OBJ to FILE. */
    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }


    /* DIRECTORIES */

    /** Filter out all but plain files. */
    private static final FilenameFilter PLAIN_FILES =
        new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        };

    /** Returns a list of the names of all plain files in the directory DIR, in
     *  lexicographic order as Java Strings.  Returns null if DIR does
     *  not denote a directory. */
    static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    /** Returns a list of the names of all plain files in the directory DIR, in
     *  lexicographic order as Java Strings.  Returns null if DIR does
     *  not denote a directory. */
    static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }

    /* OTHER FILE UTILITIES */

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     *  method. */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     *  method. */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }


    /* SERIALIZATION UTILITIES */

    /** Returns a byte array containing the serialized contents of OBJ. */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw error("Internal error serializing commit.");
        }
    }

    /* MESSAGES AND ERROR REPORTING */

    /** Return a GitletException whose message is composed from MSG and ARGS as
     *  for the String.format method. */
    static GitletException error(String msg, Object... args) {
        return new GitletException(String.format(msg, args));
    }

    /** Print a message composed from MSG and ARGS as for the String.format
     *  method, followed by a newline. */
    static void message(String msg, Object... args) {
        System.out.printf(msg, args);
        System.out.println();
    }

    /* Thu Nov 9 20:00:05 2017 -0800 */
    static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return dateFormat.format(date);
    }

    static String getCurrentBranchName() {
        return readContentsAsString(HEAD);
    }

    static File getCurrentBranchFile() {
        return join(REFS_DIR, getCurrentBranchName());
    }

    static String getCommitIDByHEAD() {
        String branchName = getCurrentBranchName();
        return readContentsAsString(join(REFS_DIR, branchName));
    }

    static Commit getCommitByHEAD() {
        String commitID = getCommitIDByHEAD();
        File currentCommitFile = join(COMMITs, commitID);
        Commit currentCommit = readObject(currentCommitFile, Commit.class);
        return currentCommit;
    }

    static Commit getCommitOfBranch(String branchName) {
        String commitID = readContentsAsString(join(REFS_DIR, branchName));
        File commitFileOfTheBranch = join(COMMITs, commitID);
        return readObject(commitFileOfTheBranch, Commit.class);
    }

    static Commit getCommitByCommitID(String commitID) {
        File commitFile = join(COMMITs, commitID);
        return readObject(commitFile, Commit.class);
    }

    static HashMap<String, String> getFileNamesOfBranch(String branchName) {
        Commit commitOfBranch = getCommitOfBranch(branchName);
        return commitOfBranch.getNameToHash();
    }

    static HashMap<String, String> getFileNamesOfCommitID(String commitID) {
        Commit commit = getCommitByCommitID(commitID);
        return commit.getNameToHash();
    }

    static String getHashInCommitID(String fileName, String commitID) {
        HashMap<String, String> map = getFileNamesOfCommitID(commitID);
        return map.get(fileName);
    }

    static List<String> fileNamesByHEAD() {
        return getCommitByHEAD().getFileNames();
    }

    static List<String> fileHashesByHEAD() {
        return getCommitByHEAD().getFileHashes();
    }

    static boolean isTrackedByHEAD(String name) {
        List<String> list = fileNamesByHEAD();
        return list.contains(name);
    }

    static boolean isTrackedByBranch(String branchName, String filename) {
        HashMap<String, String> commitMap = getFileNamesOfBranch(branchName);
        return commitMap.containsKey(filename);
    }

    @SuppressWarnings("unchecked")
    static boolean isInIndex(String name) {
        HashMap<String, String> index = readFromStagingArea(INDEX);
        return index.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    static boolean isInRmIndex(String name) {
        HashMap<String, String> rmIndex = readFromStagingArea(INDEX_RM);
        return rmIndex.containsKey(name);
    }

    static boolean isInBranch(String fileName, String branchName) {
        HashMap<String, String> commitMap = getFileNamesOfBranch(branchName);
        return commitMap.containsKey(fileName);
    }

    static boolean
    isModifiedID(String fileName, String currentCommitID, String givenCommitID) {
        Commit currentCommit = getCommitByCommitID(currentCommitID);
        Commit givenCommit = getCommitByCommitID(givenCommitID);
        HashMap<String, String> currentMap = currentCommit.getNameToHash();
        HashMap<String, String> givenMap = givenCommit.getNameToHash();
        return !givenMap.get(fileName).equals(currentMap.get(fileName));
    }

    static boolean
    isModifiedName(String fileName, String currentBranchName, String givenBranchName) {
        HashMap<String, String> currentBranchFileMap = getFileNamesOfBranch(currentBranchName);
        HashMap<String, String> givenBranchFileMap = getFileNamesOfBranch(givenBranchName);
        return !givenBranchFileMap.get(fileName).equals(currentBranchFileMap.get(fileName));
    }

    static boolean isBranchExist(String branchName) {
        List<String> branchNameList = plainFilenamesIn(REFS_DIR);
        return branchNameList.contains(branchName);
    }

    static boolean isInCommit(String filename, String commitID) {
        Commit commit = getCommitByCommitID(commitID);
        HashMap<String, String> map = commit.getNameToHash();
        return map.containsKey(filename);
    }

    static void clearIndex() {
        HashMap<String, String> map = new HashMap<>();
        writeObject(INDEX, map);
    }

    static void clearIndexRm() {
        HashMap<String, String> map = new HashMap<>();
        writeObject(INDEX_RM, map);
    }

    static void addCommitToDirCommit(Commit cmt) {
        //write the commit object to the file in the Dir for Commit
        String hash = cmt.getID();
        File file = join(COMMITs, hash);
        writeObject(file, cmt);

        // update the branch
        File branch = getCurrentBranchFile();
        writeContents(branch, hash);
    }

    @SuppressWarnings("unchecked")
    static boolean indexIsEmpty() {
        HashMap<String, String> indexMap = readFromStagingArea(INDEX);
        return indexMap.isEmpty();
    }

    static boolean indexRmIsEmpty() {
        HashMap<String, String> indexRmMap = readFromStagingArea(INDEX_RM);
        return indexRmMap.isEmpty();
    }

    static Commit prevCommit(Commit cmt) {
        String parentID = cmt.getParent1ID();
        File parentFile = join(COMMITs, parentID);
        return readObject(parentFile, Commit.class);
    }

}
