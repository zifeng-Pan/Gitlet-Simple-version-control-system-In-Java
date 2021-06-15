package gitlet;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/14:36
 * @Description: used to control the whole .gitlet file system, and will be stored at an binary file
 */
public class Repo {
    File cwd; // current work directory
    File stageArea; // stage area directory
    File blobs; // blobs directory
    File commits; // commits directory
    File branches; // branches directory
    File[] untrackedFile; // untracked file in wd(work directory)
    File[] modifiedFile; // modified file in the wd
    File[] deletedFile; // deleted file in the wd.
    gitlet.command cmd;

    public Repo() {
    }

}
