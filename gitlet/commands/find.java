package gitlet.commands;

import gitlet.Repo;
import gitlet.Utils;
import gitlet.commits;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/15/9:45
 * @Description:
 */
public class find {
    public static void find(Repo repo, String... args) {
        if (!argumentcheck.argumentCheck(2, """
                java gitlet.Main find [targetCommitMessage]
                """, args)) return;

        boolean findFlag = false;
        String commitMsg = args[1];
        List<String> commitList = Utils.plainFilenamesIn(repo.getCommits());
        for (String tCommit : commitList) {
            commits tempCommit = Utils.readObject(Utils.join(repo.getCommits(), tCommit), gitlet.commits.class);
            if (tempCommit.get_LogMessage().equals(commitMsg)) {
                System.out.println(tempCommit.get_CommitID());
                findFlag = true;
                break;
            }
        }
        if (!findFlag) System.out.println(("Can't find the commitID with " + commitMsg));
    }
}
