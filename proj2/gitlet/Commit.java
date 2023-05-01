package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;

import static gitlet.Repository.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable, Dumpable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timestamp;
    private String[] parents;
    private Map<String, String> blobs;

    /* TODO: fill in the rest of this class. */
    public Commit(String message, String parent, Date timestamp, Map<String, String> blobs) {
        this.message = message;
        this.parents = new String[]{parent, null};
        this.blobs = blobs;
        this.timestamp = timestamp;
    }

    public Commit(String message, String[] parents, Date timestamp, Map<String, String> blobs) {
        this.message = message;
        this.parents = parents;
        this.blobs = blobs;
        this.timestamp = timestamp;
    }
    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String[] getParents() {
        return this.parents;
    }

    public Map<String, String> getBlobs() {
        return blobs;
    }

    public String getBlobID(String fileName) {
        return this.blobs.get(fileName);
    }

    public void addBlob(String fileName, String blobID) {
        this.blobs.put(fileName, blobID);
    }

    public void removeBlob(String fileName) {
        this.blobs.remove(fileName);
    }

    public boolean isMergeCommit() {
        return this.parents == null ? false : this.parents[1] !=  null;
    }

    @Override
    public void dump() {
        System.out.println("Message: " + this.message);
        System.out.println("Timestamp: " + this.timestamp);
        for (String parent : this.parents) {
            System.out.println("Parent: " + parent);
        }
        for (String file : this.blobs.keySet()) {
            System.out.println(file + ": " + this.blobs.get(file));
        }
    }
}
