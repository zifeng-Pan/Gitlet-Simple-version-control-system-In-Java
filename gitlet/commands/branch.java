package gitlet.commands;

import gitlet.Repo;
import gitlet.Utils;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/19:57
 * @Description: create a new branch
 */
public class branch {
    public static void branch(Repo repo, String... args) {
        /* get the branch name */
        String newBranchName = args[1];

        /* get back the branch list */
        ArrayList<String> branches = repo.getBranches();

        /* branch check */
        for (String tBranch : branches) {
            if (tBranch.equals(newBranchName)) System.out.println("A branch with that name already exists.");
        }

        /* create a new branch object */
        gitlet.branch branch = new gitlet.branch(newBranchName, repo.getHEAD());

        /* branch list add a new branch */
        branches.add(newBranchName);

        /* write to the branch Foloder */
        Utils.writeObject(Utils.join(repo.getBranch(), newBranchName), branch);

    }
}
