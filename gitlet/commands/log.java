package gitlet.commands;

import gitlet.Repo;
import gitlet.Utils;
import gitlet.commits;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/19:57
 * @Description:
 */
public class log {
    public static void log(Repo repo, String... args) {
        String head = repo.getHEAD();
        commits currcommit = Utils.readObject(Utils.join(repo.getCommits(), head), gitlet.commits.class);
        logHelper(repo, currcommit);
    }

    public static void logHelper(Repo repo, commits currcommit) {
        while (true) {
            System.out.println("===");
            System.out.println("Commit:" + currcommit.get_CommitID());
            System.out.println(currcommit.get_TimeStamp());
            System.out.println(currcommit.get_LogMessage());
            System.out.print("\n\n");
            if (currcommit.get_ParentCommitID() == null) break;
            currcommit = Utils.readObject(Utils.join(repo.getCommits(), currcommit.get_ParentCommitID()), gitlet.commits.class);
        }
    }
}
