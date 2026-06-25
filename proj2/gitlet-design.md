# Gitlet Design Document

**Name**:John Feng

## Classes and Data Structures

### Repository
***CWD*** is the working directory.

***GITLET_DIR*** is the initialized directory

***OBJECT_DIR*** stores the object committed

***INDEX_DIR*** stores the blobs

the function ***initialCommand*** implements gitlet init.

the function ***add*** implements gitlet add ...

### Commit
***message*** is the commit message

***parent1*** and ***parent2***(if exist) is the previous commit which the current point to.



#### Fields

1. Field 1
2. Field 2


### Class 2

#### Fields

1. Field 1
2. Field 2

## Algorithms

## Persistence

## Points worth noting
1. You can commit a coherent set of files at the same time. 
2. You could change multiple files in one commit.
3. Each commit contains a parent commit pointed by the commit.
4. Gitlet will never implement a detached HEAD state, a HEAD pointer 
is the only one active pointer.

    Checkout will never do it, 

    The reset will do that though move the branch pointer.
5. Gitlet allows you to save the commits of different versions of a file.
   (e.t. Multiple branches are allowed), it's more like a tree.
6. Commit trees are never destroyed, and we can only add new thins to it.

7. the real git has:
    1. blobs: the saved contents of files
    2. trees: Directory structures mapping names to reference to blobs and other trees.
    3. commits: Combinations of log messages, other metadata (commit date, author, etc.), a reference to a tree, and references to parent commits. The repository also maintains a mapping from branch heads to references to commits, so that certain important commits have symbolic names.
   
    what will gitlet do is:
   1. incorporating trees into commits and not deal with subdirectories.
   2. Limiting to merge that reference two parents.
   3. Having our metadata consist only of a timestamp and a log message.

        And a commit only consists of a log message, a timestamp, a mapping of file names
to blob references and at most two parent references.

8. Every blob and commit has a unique integer id which depending on the content, which in the case 
of blob, means the same file content, and in the case of commit, means the same metadata, 
the same mappings of names to references, and the same parent references.

    Gitlet uses the SHA-1(Secure Hash 1) to implement it.
9. When using SHA-1, make sure that:
    1. including all the metadata and references
   2. distinguish between hashes for commit and hashes for blobs.
10. For remotes, we only use another gitlet repositories.
11. make sure implement serialization(interface java.io.serializable) when reading and writing internal objects and files.

    writing(serialize) using java.io.ObjectOutputStream

    reading(deserialize) using java.io.ObjectInputStream
12. You can use all of the Java standard library.
13. Main method should call helper methods, and you cannot implement everything in the main class
14. Copies of files and other metadata should be stored in a directory called .gitlet
15. the failure cases that we must handle
    1. If a user doesn’t input any arguments, print the message *Please enter a command.* and exit
    2. If a user inputs a command that doesn’t exist, print the message *No command with that name exists.* and exit
    3. If a user inputs a command with the wrong number or format of operands, print the message *Incorrect operands.* and exit
    4. If a user inputs a command that requires being in an initialized Gitlet working directory (i.e., one containing a .gitlet subdirectory), but is not in such a directory, print the message *Not in an initialized Gitlet directory.*
16. Do NOT print out anything except for what the spec says
17. You should always supply the argument 0 to the System.exit(0)command


   