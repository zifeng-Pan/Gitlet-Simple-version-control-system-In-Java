package gitlet.commands;

import gitlet.Repo;
import gitlet.Utils;
import gitlet.branch;
import gitlet.commits;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/17/17:25
 * @Description:
 */
public class globallog {
    public static void globallog(Repo repo, String... args) {
        /* get the branches list */
        ArrayList<String> branches = repo.getBranches();
        /* get the HEAD commit SHA-1 ID */
        String head = repo.getHEAD();
        /* for loop(print the log of every branch) */
        for (String tempBranch : branches) {
            /* get the tempBranch */
            branch branch = Utils.readObject(Utils.join(repo.getBranch(), tempBranch), gitlet.branch.class);
            commits currCommit = Utils.readObject(Utils.join(repo.getCommits(), branch.get_CommitID()), gitlet.commits.class);
            if (currCommit.get_CommitID().equals(head) || currCommit.isSplitPoint()) continue;
            log.logHelper(repo, currCommit);
        }
        log.log(repo, args);
    }
}
