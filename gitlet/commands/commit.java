package gitlet.commands;

import gitlet.Repo;
import gitlet.Utils;
import gitlet.commits;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/19:57
 * @Description:
 */
public class commit {
    public static void commit(commits com, Repo repo) {
        File commitFile = Utils.join(repo.getCommits(), File.separator, com.get_CommitID());
        Utils.writeObject(commitFile, com);
    }
}
