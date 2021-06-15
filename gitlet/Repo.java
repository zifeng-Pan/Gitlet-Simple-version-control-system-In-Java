package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/14:36
 * @Description: used to control the whole .gitlet file system, and will be stored at an binary file
 */
public class Repo implements Serializable {
    /* this class is used to control the whole .gitlet file system, and will be stored at an binary file */

    /* HEAD */
    private static final String HEAD = "HEAD";
    /* current work directory */
    private final String cwd;
    /* stage area directory */
    private final String stageArea;
    /* commits file directory */
    private final String commits;
    /* branches directory */
    private final String branch;
    /* untracked file in wd(work directory) */
    private final ArrayList<String> untrackedFile;
    /* modified(include the deleted file tracked by current commit) but not staged file in the wd */
    private final ArrayList<String> modifiedFile;
    /* removed(git rm?) file in the wd */
    private final ArrayList<String> removedFile;
    /* the list of the branch */
    private ArrayList<String> branches;

    // constructor ..
    public Repo(String cwd) {
        this.cwd = cwd + File.separator + ".gitlet";
        this.stageArea = this.cwd + File.separator + "Stage";
        this.commits = this.cwd + File.separator + "Commit";
        this.branch = this.cwd + File.separator + "Branch";
        this.untrackedFile = new ArrayList<>();
        this.modifiedFile = new ArrayList<>();
        this.removedFile = new ArrayList<>();
        this.branches = new ArrayList<>();
    }

    public static String getHEAD() {
        return HEAD;
    }

    public String getCwd() {
        return cwd;
    }

    public String getStageArea() {
        return stageArea;
    }

    public String getCommits() {
        return commits;
    }

    public String getBranch() {
        return branch;
    }

    public ArrayList<String> getBranches() {
        return branches;
    }

    public void setBranches(ArrayList<String> branches) {
        this.branches = branches;
    }

    // the function to deal with the command and the args passed by the main
    public void command(String... args) {

    }

    // check whether some files are modified, created or deleted
    public void checkFile() {
    }

    // getter and setter ...

}
