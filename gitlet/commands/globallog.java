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
            if(branch.get_CommitID().equals(head)) continue;
            commits currCommit = Utils.readObject(Utils.join(repo.getCommits(), branch.get_CommitID()), gitlet.commits.class);
            while (true) {
                if(currCommit.isSplitPoint()) break;
                System.out.println("===");
                System.out.println("Commit:" + currCommit.get_CommitID());
                System.out.println(currCommit.get_TimeStamp());
                System.out.println(currCommit.get_LogMessage());
                System.out.print("\n\n");
                if (currCommit.get_ParentCommitID() == null ) break;
                currCommit = Utils.readObject(Utils.join(repo.getCommits(), currCommit.get_ParentCommitID()), gitlet.commits.class);
            }
        }
        log.log(repo, args);
    }
}
