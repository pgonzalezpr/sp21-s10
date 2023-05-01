package gitlet;

import java.io.File;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                checkArgsLength(args, 1);
                initRepository();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                checkGitletDirectory();
                checkArgsLength(args, 2);
                addToStaging(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                checkGitletDirectory();
                checkArgsLength(args, 2);
                commit(args[1]);
                break;
            case "rm":
                checkGitletDirectory();
                checkArgsLength(args, 2);
                remove(args[1]);
                break;
            case "log":
                checkGitletDirectory();
                checkArgsLength(args, 1);
                printLog();
                break;
            case "global-log":
                checkGitletDirectory();
                checkArgsLength(args, 1);
                printGlobalLog();
                break;
            case "find":
                checkGitletDirectory();
                checkArgsLength(args, 2);
                findCommitsByMessage(args[1]);
                break;
            case "status":
                checkGitletDirectory();
                checkArgsLength(args, 1);
                printStatus();
                break;
            case "checkout":
                checkGitletDirectory();
                int usage = getCheckoutUsage(args);
                if (usage == 1) {
                    checkoutFile(args[2]);
                } else if (usage == 2) {
                    checkoutFile(args[1], args[3]);
                } else if (usage == 3) {
                    checkoutBranch(args[1]);
                }
                break;
            case "branch":
                checkGitletDirectory();
                checkArgsLength(args, 2);
                createBranch(args[1]);
                break;
            case "rm-branch":
                checkGitletDirectory();
                checkArgsLength(args, 2);
                removeBranch(args[1]);
                break;
            case "reset":
                checkGitletDirectory();
                checkArgsLength(args, 2);
                resetToCommit(args[1]);
                break;
            case "merge":
                checkGitletDirectory();
                checkArgsLength(args, 2);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }

         /*
        File CWD = new File(System.getProperty("user.dir"));
        File inputFile = join(CWD, "gitlet", "GitExperiment.java");
        byte[] byteStream = readContents(inputFile);
        String sha1 = sha1(byteStream);
        File outputFile = join(CWD, "gitlet", sha1);
        writeContents(outputFile, byteStream);
        */
    }
}
