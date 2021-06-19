package gitlet.commands;

import gitlet.Repo;
import gitlet.Utils;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/17/18:17
 * @Description:
 */
public class rmbranch {
    public static void rmbranch(Repo repo, String... args) {
        if(!argumentcheck.argumentCheck(2, """
                java gitlet.Main rmbranch [branchName]
                """,args)) return;

        boolean flag = false;
        String currBranch = repo.getCurrBranch();
        String rmBranchName = args[1];

        if (rmBranchName.equals(currBranch)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }

        ArrayList<String> branches = repo.getBranches();

        for (String tBranch : branches) {
            if (tBranch.equals(rmBranchName)) {
                flag = true;
                Utils.join(repo.getBranch(), tBranch).delete();
                branches.remove(tBranch);
                break;
            }
        }

        if (!flag) System.out.println("A branch with that name does not exist.");
    }
}
