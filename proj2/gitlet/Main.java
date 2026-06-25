package gitlet;

import java.io.IOException;
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
            throw new RuntimeException("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                valideNumArgs(args, 1);
                initCommand();
                break;
            case "add":
                // check whether the file exists
                if (args.length != 2) {
                    System.out.println("File does not exist.");
                }

                add(args[1]);
                break;
            // TODO: FILL THE REST IN.
            case "commit":
                // check whether there exists message
                if (args.length != 2) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                valideNumArgs(args, 2);
                commit(args[1]);
                break;
            case "rm":
                valideNumArgs(args, 2);
                rm(args[1]);
                break;
            case "log":
                valideNumArgs(args, 1);
                log();
                break;
            case "global-log":
                valideNumArgs(args, 1);
                globalLog();
                break;
            case "find":
                valideNumArgs(args, 2);
                find(args[1]);
                break;
            case "status":
                valideNumArgs(args, 1);
                status();
                break;
            case "checkout":
                if (args.length == 3) {
                    checkout1(args[2]);
                } else if (args.length == 4) {
                    checkout2(args[1], args[3]);
                } else if (args.length == 2) {
                    checkout3(args[1]);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "branch":
                valideNumArgs(args, 2);
                branch(args[1]);
                break;
            case "rm-branch":
                valideNumArgs(args, 2);
                rmBranch(args[1]);
                break;
            case "reset":
                valideNumArgs(args, 2);
                reset(args[1]);
                break;
            case "merge":
                break;
            case "add-remote":
                break;
            case "rm-remote":
                break;
            case "push":
                break;
            case "fetch":
                break;
            case "pull":
                break;
            default:
                throw new RuntimeException("No command with that name exists.");
        }
    }

    public static void valideNumArgs(String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException("Incorrect operands.");
        }
    }
}
