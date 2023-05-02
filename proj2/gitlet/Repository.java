package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File HEADS_DIR = join(GITLET_DIR, "refs", "heads");
    public static final File BLOBS_DIR = join(GITLET_DIR, "objects", "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "objects", "commits");
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File REMOVAL_DIR = join(GITLET_DIR, "removal");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");

    public static void checkGitletDirectory() {
        if (!GITLET_DIR.isDirectory()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static void checkArgsLength(String[] args, int expectedLength) {
        if (args.length != expectedLength) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    public static int getCheckoutUsage(String[] args) {
        if (args.length < 2 || args.length > 4) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }

        if (args.length == 3 && args[1].equals("--")) {
            return 1;
        }
        if (args.length == 4 && args[2].equals("--")) {
            return 2;
        }
        if (args.length == 2) {
            return 3;
        }

        System.out.println("Incorrect operands.");
        System.exit(0);
        return 0;
    }

    public static void initRepository() {
        if (GITLET_DIR.isDirectory()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);
        }
        setupEmptyRepository();
    }

    public static void addToStaging(String fileName) {
        // LOAD CURRENT COMMIT

        if (!join(CWD, fileName).isFile()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        Commit currentCommit = getLastCommit();
        byte[] fileContents = readContents(join(CWD, fileName));
        String fileSha1 = sha1(fileContents);
        String currentBlob = currentCommit.getBlobID(fileName);

        if ((currentBlob != null) && currentBlob.equals(fileSha1)) {
            join(STAGING_DIR, fileName).delete();
        } else {
            writeContents(join(STAGING_DIR, fileName), fileContents);
        }
        join(REMOVAL_DIR, fileName).delete();
    }

    public static void commit(String message) {
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        List<String> stagedFiles = plainFilenamesIn(STAGING_DIR);
        List<String> removalFiles = plainFilenamesIn(REMOVAL_DIR);
        if (stagedFiles.isEmpty() && removalFiles.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        Commit currentCommit = getLastCommit();
        Commit newCommit = new Commit(message, getLastCommitID(),
                new Date(System.currentTimeMillis()), currentCommit.getBlobs());

        for (String file : stagedFiles) {
            byte[] blob = readContents(join(STAGING_DIR, file));
            String blobID = persistBlob(blob);
            newCommit.addBlob(file, blobID);
        }

        for (String file : removalFiles) {
            newCommit.removeBlob(file);
        }

        String newCommitID = persistCommit(newCommit);
        updateHeadCommit(newCommitID);
        clearDirectory(STAGING_DIR);
        clearDirectory(REMOVAL_DIR);
    }

    public static void remove(String filename) {
        Commit lastCommit = getLastCommit();
        if (lastCommit.getBlobID(filename) != null) {
            try {
                join(REMOVAL_DIR, filename).createNewFile();
            } catch (IOException e) {
                System.out.println("Unable to stage file for removal");
                throw new RuntimeException(e);
            }
            join(CWD, filename).delete();
        } else if (!join(STAGING_DIR, filename).delete()) {
            System.out.println("No reason to remove the file.");
        }
    }

    public static void printLog() {
        String commitID = getLastCommitID();
        Commit commit;

        while (true) {
            commit = getCommitByID(commitID);
            System.out.println(commitStringRep(commitID, commit));
            if (commit.getParents() != null) {
                commitID = commit.getParents()[0];
            } else {
                break;
            }
        }
    }

    public static void printGlobalLog() {
        Commit commit;
        for (String commitID : plainFilenamesIn(COMMITS_DIR)) {
            commit = getCommitByID(commitID);
            System.out.println(commitStringRep(commitID, commit));
        }
    }

    public static void findCommitsByMessage(String message) {
        Commit commit;
        boolean found = false;
        for (String commitID : plainFilenamesIn(COMMITS_DIR)) {
            commit = getCommitByID(commitID);
            if (commit.getMessage().equals(message)) {
                System.out.println(commitID);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void printStatus() {
        Formatter fmt = new Formatter();
        String currentBranch = readContentsAsString(HEAD);

        fmt.format("=== Branches ===\n");
        for (String branch : plainFilenamesIn(HEADS_DIR)) {
            if (branch.equals(currentBranch)) {
                branch = '*' + branch;
            }
            fmt.format("%s\n", branch);
        }
        fmt.format("\n");

        fmt.format("=== Staged Files ===\n");
        for (String file : plainFilenamesIn(STAGING_DIR)) {
            fmt.format("%s\n", file);
        }
        fmt.format("\n");

        fmt.format("=== Removed Files ===\n");
        for (String file : plainFilenamesIn(REMOVAL_DIR)) {
            fmt.format("%s\n", file);
        }
        fmt.format("\n");

        fmt.format("=== Modifications Not Staged For Commit ===\n");
        fmt.format("\n");
        fmt.format("=== Untracked Files ===\n");
        fmt.format("\n");

        System.out.println(fmt);
    }

    public static void checkoutFile(String commitID, String filename) {
        if (commitID.length() > 6) {
            commitID = commitID.substring(0, 6);
        }
        String blobID = getCommitByShortID(commitID).getBlobID(filename);
        if (blobID == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        byte[] blob = getBlobByID(blobID);
        writeContents(join(CWD, filename), blob);
    }

    public static void checkoutFile(String filename) {
        checkoutFile(getLastCommitID().substring(0, 6), filename);
    }

    public static void checkoutBranch(String branch) {
        if (branch.equals(readContentsAsString(HEAD))) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        validateBranch(branch);

        Commit branchHead = getCommitByID(readContentsAsString(join(HEADS_DIR, branch)));
        checkUntrackedFiles(branchHead);
        removeTrackedFilesFromCWD(branchHead);
        copyCommitFilesToCWD(branchHead);

        writeContents(HEAD, branch);
        clearDirectory(STAGING_DIR);
        clearDirectory(REMOVAL_DIR);
    }

    public static void createBranch(String branch) {
        File branchFile = join(HEADS_DIR, branch);
        if (branchFile.isFile()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        try {
            branchFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Unable to create new branch");
            throw new RuntimeException(e);
        }
        writeContents(branchFile, getLastCommitID());
    }

    public static void removeBranch(String branch) {
        if (branch.equals(readContentsAsString(HEAD))) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        File branchFile = join(HEADS_DIR, branch);
        if (!branchFile.isFile()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        branchFile.delete();
    }

    public static void resetToCommit(String commitID) {
        if (commitID.length() > 6) {
            commitID = commitID.substring(0, 6);
        }

        Commit commit = getCommitByShortID(commitID);
        checkUntrackedFiles(commit);
        removeTrackedFilesFromCWD(commit);
        copyCommitFilesToCWD(commit);

        writeContents(join(HEADS_DIR, readContentsAsString(HEAD)),
                getCommitIDByShortID(commitID));
        clearDirectory(STAGING_DIR);
        clearDirectory(REMOVAL_DIR);
    }

    public static void merge(String given) {
        validateMerge(given);
        String givenID = readContentsAsString(join(HEADS_DIR, given));
        String currentID = getLastCommitID();
        String splitPoint = findSplitPoint(currentID, givenID);

        if (splitPoint.equals(givenID)) {
            System.out.println("Given branch is an ancestor "
                    + "of the current branch.");
            System.exit(0);
        }
        if (splitPoint.equals(currentID)) {
            checkoutBranch(given);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }

        Commit givenCommit = getCommitByID(givenID);
        Commit currentCommit = getCommitByID(currentID);
        Commit splitCommit = getCommitByID(splitPoint);
        Set<String> filesSet = getAllFiles(currentCommit,
                givenCommit, splitCommit);

        for (String filename : filesSet) {
            if (join(CWD, filename).isFile()
                    && currentCommit.getBlobID(filename) == null
                    && givenCommit.getBlobID(filename) == null
                    && splitCommit.getBlobID(filename) != null) {
                continue;
            }
            if (join(CWD, filename).isFile()
                    && currentCommit.getBlobID(filename) == null) {
                System.out.println("There is an untracked file in the "
                        + "way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        Set<String> currentFiles = currentCommit.getBlobs().keySet();
        Set<String> givenFiles = givenCommit.getBlobs().keySet();
        Set<String> splitFiles = splitCommit.getBlobs().keySet();
        boolean conflict = false;

        for (String filename : filesSet) {
            if (splitFiles.contains(filename)) {
                if (currentFiles.contains(filename) &&
                        currentCommit.getBlobID(filename).equals(
                                splitCommit.getBlobID(filename))) {
                    if (givenFiles.contains(filename)) {
                        if (!givenCommit.getBlobID(filename).equals(
                                splitCommit.getBlobID(filename))) {
                            writeBlobToCWDAndStaging(givenCommit, filename);
                        }
                    } else {
                        try {
                            join(REMOVAL_DIR, filename).createNewFile();
                        } catch (IOException e) {
                            System.out.println("Unable to stage file for removal");
                            throw new RuntimeException(e);
                        }
                        join(CWD, filename).delete();
                    }
                }

                if (givenFiles.contains(filename) &&
                        givenCommit.getBlobID(filename).equals(
                                splitCommit.getBlobID(filename))) {
                    if (currentFiles.contains(filename)
                            && !currentCommit.getBlobID(filename).equals(
                                    splitCommit.getBlobID(filename))) {
                        writeBlobToCWDAndStaging(currentCommit, filename);
                    }
                }

                if (givenFiles.contains(filename) && currentFiles.contains(filename)
                        && !givenCommit.getBlobID(filename).equals(
                        currentCommit.getBlobID(filename))) {
                    writeContents(join(CWD, filename), buildMergeString(
                            currentCommit, givenCommit, filename));
                    writeContents(join(STAGING_DIR, filename), buildMergeString(
                            currentCommit, givenCommit, filename));
                    conflict = true;
                }

            } else {
                if (!givenFiles.contains(filename)
                        && currentFiles.contains(filename)) {
                    writeBlobToCWDAndStaging(currentCommit, filename);
                    continue;

                }
                if (!currentFiles.contains(filename)
                        && givenFiles.contains(filename)) {
                    writeBlobToCWDAndStaging(givenCommit, filename);
                    continue;
                }
                if (givenFiles.contains(filename)
                        && currentFiles.contains(filename)) {
                    if (!currentCommit.getBlobID(filename).equals(
                            givenCommit.getBlobID(filename))) {
                        writeContents(join(CWD, filename), buildMergeString(
                                currentCommit, givenCommit, filename));
                        writeContents(join(STAGING_DIR, filename), buildMergeString(
                                currentCommit, givenCommit, filename));
                        conflict = true;
                    }
                }
            }
        }


        commit("Merged " + given + " into "
                + readContentsAsString(HEAD) + ".");
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private static void writeBlobToCWDAndStaging(Commit commit, String filename) {
        File file = join(BLOBS_DIR, commit.getBlobID(filename));
        writeContents(join(CWD, filename), readContents(file));
        writeContents(join(STAGING_DIR, filename), readContents(file));
    }

    private static void validateMerge(String given) {
        if (!plainFilenamesIn(STAGING_DIR).isEmpty()
                || !plainFilenamesIn(REMOVAL_DIR).isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!join(HEADS_DIR, given).isFile()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (given.equals(readContentsAsString(HEAD))) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }

    private static String buildMergeString(Commit current, Commit given,
                                           String filename) {
        String currentContent;
        String givenContent;
        if (current.getBlobID(filename) != null) {
            currentContent = readContentsAsString(join(BLOBS_DIR,
                    current.getBlobID(filename)));
        } else {
            currentContent = "";
        }

        if (given.getBlobID(filename) != null) {
            givenContent = readContentsAsString(join(BLOBS_DIR,
                    given.getBlobID(filename)));
        } else {
            givenContent = "";
        }

        String stringBuilder = "<<<<<<< HEAD\n"
                + currentContent + "=======\n" + givenContent
                + ">>>>>>>\n";

        return stringBuilder;
    }

    private static void setupEmptyRepository() {
        GITLET_DIR.mkdir();
        HEADS_DIR.mkdirs();
        COMMITS_DIR.mkdirs();
        BLOBS_DIR.mkdirs();
        STAGING_DIR.mkdir();
        REMOVAL_DIR.mkdir();

        Commit initialCommit = new Commit("initial commit", (String[]) null,
                new Date(0L), new HashMap<>());
        String commitHash = persistCommit(initialCommit);

        File masterFile = join(HEADS_DIR, "master");
        writeContents(masterFile, commitHash);
        writeContents(HEAD, "master");
    }

    private static void clearDirectory(File dir) {
        for (String fileName : plainFilenamesIn(dir)) {
            join(dir, fileName).delete();
        }
    }

    private static Commit getCommitByID(String commitID) {
        File commitFile = join(COMMITS_DIR, commitID);
        if (!commitFile.isFile()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        return readObject(commitFile, Commit.class);
    }

    private static Commit getCommitByShortID(String commitID) {
        for (String commit : plainFilenamesIn(COMMITS_DIR)) {
            if (commit.substring(0, 6).equals(commitID)) {
                return readObject(join(COMMITS_DIR, commit), Commit.class);
            }
        }
        System.out.println("No commit with that id exists.");
        System.exit(0);
        return null;
    }

    private static String getCommitIDByShortID(String commitID) {
        for (String commit : plainFilenamesIn(COMMITS_DIR)) {
            if (commit.substring(0, 6).equals(commitID)) {
                return commit;
            }
        }
        return null;
    }

    private static String persistCommit(Commit commit) {
        byte[] serializedCommit = serialize(commit);
        String commitHash = sha1(serializedCommit);
        writeContents(join(COMMITS_DIR, commitHash), serializedCommit);
        return commitHash;
    }

    public static String getLastCommitID() {
        String currentBranch = readContentsAsString(HEAD);
        return readContentsAsString(join(HEADS_DIR, currentBranch));
    }

    private static void updateHeadCommit(String commitID) {
        String currentBranch = readContentsAsString(HEAD);
        writeContents(join(HEADS_DIR, currentBranch), commitID);
    }

    private static Commit getLastCommit() {
        return getCommitByID(getLastCommitID());
    }

    private static byte[] getBlobByID(String blobID) {
        return readContents(join(BLOBS_DIR, blobID));
    }

    private static String persistBlob(byte[] blob) {
        String blobID = sha1(blob);
        writeContents(join(BLOBS_DIR, blobID), blob);
        return blobID;
    }

    private static String commitStringRep(String commitID, Commit commit) {
        SimpleDateFormat formatter =
                new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        String formattedDate = formatter.format(commit.getTimestamp());
        Formatter fmt = new Formatter();

        if (!commit.isMergeCommit()) {
            fmt.format("===\ncommit %s\nDate: %s\n%s\n", commitID,
                    formattedDate, commit.getMessage());
        } else {
            String parent1 = commit.getParents()[0].substring(0, 7);
            String parent2 = commit.getParents()[1].substring(0, 7);
            fmt.format("===\ncommit %s\nMerge: %s %s\nDate: %s\n%s\n", commitID,
                    parent1, parent2, formattedDate, commit.getMessage());
        }
        return fmt.toString();
    }

    private static void validateBranch(String branch) {
        File branchFile = join(HEADS_DIR, branch);
        if (!branchFile.isFile()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
    }

    /* Checks if CWD contains an untracked file that would be
        overwritten by checking out branch.
     */
    private static void checkUntrackedFiles(Commit commit) {
        Commit currentHeadCommit = getLastCommit();
        for (String filename : commit.getBlobs().keySet()) {
            if (join(CWD, filename).isFile()
                    && currentHeadCommit.getBlobID(filename) == null) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    /* Puts files from branch head commit in CWD, overwriting existing
        files */
    private static void copyCommitFilesToCWD(Commit commit) {
        for (String filename : commit.getBlobs().keySet()) {
            byte[] blob = getBlobByID(commit.getBlobID(filename));
            writeContents(join(CWD, filename), blob);
        }
    }

    /* Removes tracked files not present in branch from CWD */
    private static void removeTrackedFilesFromCWD(Commit commit) {
        Commit currentHeadCommit = getLastCommit();
        for (String filename : currentHeadCommit.getBlobs().keySet()) {
            if (commit.getBlobID(filename) == null) {
                join(CWD, filename).delete();
            }
        }
    }

    private static Set<String> findAllAncestors(String commitID) {
        Set<String> visited = new HashSet<>();
        LinkedList<String> queue = new LinkedList<>();
        Commit commit;
        visited.add(commitID);
        queue.add(commitID);

        while (!queue.isEmpty()) {
            commit = getCommitByID(queue.peek());
            String[] parents = commit.getParents();
            queue.remove();

            if (parents == null) {
                return visited;
            }
            for (String parent : parents) {
                if (parent == null) {
                    continue;
                }
                if (!visited.contains(parent)) {
                    visited.add(parent);
                    queue.add(parent);
                }
            }
        }
        return visited;
    }

    private static String findLatestCommonAncestor
            (String commitID, Set<String> ancestors) {

        if (ancestors.contains(commitID)) {
            return commitID;
        }

        Set<String> visited = new HashSet<>();
        LinkedList<String> queue = new LinkedList<>();
        Commit commit;
        visited.add(commitID);
        queue.add(commitID);

        while (!queue.isEmpty()) {
            commit = getCommitByID(queue.peek());
            String[] parents = commit.getParents();
            queue.remove();

            if (parents == null) {
                return null;
            }
            for (String parent : parents) {
                if (parent == null) {
                    continue;
                }
                if (!visited.contains(parent)) {
                    if (ancestors.contains(parent)) {
                            return parent;
                        }
                    visited.add(parent);
                    queue.add(parent);
                    }
                }
            }
        return null;
    }
    private static String findSplitPoint(String current, String given) {
        Set<String> currentAncestors = findAllAncestors(current);
        return findLatestCommonAncestor(given, currentAncestors);
    }
    private static Set<String> getAllFiles(Commit... commits) {
        Set<String> files = new HashSet<>();
        for (Commit c : commits) {
            files.addAll(c.getBlobs().keySet());
        }
        return files;
    }
}
