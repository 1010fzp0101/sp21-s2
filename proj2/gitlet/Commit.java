package gitlet;

import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message; // the commit message
    private String parent1ID = null;  // the parent hash value
    private String parent2ID = null;  // the parent2(which happens when merge) hash value
    private String timestamp; // the commit time
    private String ID; // the commit's id
    //The commit's tracked file's content
    private Map<String, String> blobNameTohash;



    public Commit(String message) {
        this(message, new HashMap<>(), null);
        this.timestamp = dateToTimeStamp((new Date(0)));
    }

    public Commit(String message, HashMap<String, String> map, String parent1ID) {
        this(message, map, parent1ID, null);
    }

    public Commit(String message, HashMap<String, String> map, String parent1ID, String parent2ID) {
        this.message = message;
        this.parent1ID = parent1ID;
        this.parent2ID = parent2ID;
        this.blobNameTohash = map;
        this.timestamp = dateToTimeStamp(new Date());
        this.ID = sha1((Object) serialize(this));
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getParent1ID() {
        return parent1ID;
    }

    public String getParent2ID() {
        if (containParent2()) {
            return parent2ID;
        }
        return null;
    }

    public String getID() {
        return ID;
    }

    private boolean containParent2() {
        return parent2ID != null;
    }

    public HashMap<String, String> getNameToHash() {
        return (HashMap<String, String>) blobNameTohash;
    }

    public List<String> getFileNames() {
        return new ArrayList<>(blobNameTohash.keySet());
    }

    public List<String> getFileHashes() {
        return new ArrayList<>(blobNameTohash.values());
    }

}
