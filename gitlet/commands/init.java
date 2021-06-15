package gitlet.commands;


import gitlet.branch;
import gitlet.*;

import java.io.File;
import java.util.ArrayList;


/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/19:56
 * @Description:
 */
public class init {
    public static void init(String path) {
        Repo repo = new Repo(path);
        File repoDir = new File(repo.getCwd());
        /* make a .gitlet repository */
        repoDir.mkdir();
        /* create Folder */
        createFolder(repo.getCwd());
        /* create a new commit */
        commits inicommit = new commits("initial commit");
        /* commit a initial commit */
        inicommit.set_CommitID(Utils.sha1(inicommit.get_LogMessage()));
        /* create the HEAD file, branch file and the REPO file */
        createFile(inicommit, repo);
        commit.commit(inicommit, repo);
    }

    private static void createFolder(String repoPath) {
        String[] folders = {"Stage", "Commit", "Branch"};
        for (String folder : folders) {
            new File(repoPath + File.separator + folder).mkdir();
        }
    }

    private static void createFile(commits comm, Repo repo) {
        branch tempBranch = new branch("master", comm.get_CommitID());
        head tempHead = new head(tempBranch.get_BranchName());
        Utils.writeObject(Utils.join(repo.getBranch(), File.separator, tempBranch.get_BranchName()), tempBranch);
        Utils.writeObject(Utils.join(repo.getBranch(), File.separator, Repo.getHEAD()), tempHead);

        ArrayList<String> tempList = new ArrayList<>();
        tempList.add("master");
        repo.setBranches(tempList);

        Utils.writeObject(Utils.join(repo.getCwd(), File.separator, "REPO"), repo);
    }
}
