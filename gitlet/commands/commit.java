package gitlet.commands;

import gitlet.Repo;
import gitlet.Utils;
import gitlet.branch;
import gitlet.commits;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/19:57
 * @Description:
 */
public class commit {
    public static void commit(Repo repo, String... args) {
        HashMap<String, String> stageBlobs = repo.getStageBlobs();

        if (!checkout.areaCheck(repo)) return;
        if (stageBlobs.isEmpty() && repo.getRemovedFile().isEmpty()) {
            System.out.println("No changes added to the commit");
            return;
        }
        if(!argumentcheck.argumentCheck(2, """
                java gitlet.Main commit [commitMessage]
                """,args)) return;


        commitHelper(repo,args);

    }

    public static void commitHelper(Repo repo, String ... args){
        /* read the currCommits and create the new commit*/
        commits currCommits = Utils.readObject(Utils.join(repo.getCommits(), repo.getHEAD()), gitlet.commits.class);
        commits newCommits = new commits(currCommits.get_CommitID(), args[1]);

        /* copy the blobs and deal with blobs */
        Mycopy(currCommits, newCommits);
        blobsHelper(repo, newCommits);

        /* set the SHA1 ID */
        newCommits.set_CommitID(Utils.sha1(newCommits.get_LogMessage() + newCommits.get_TimeStamp() + newCommits.get_ParentCommitID()));

        /* write back */
        Utils.writeObject(Utils.join(repo.getCommits(), newCommits.get_CommitID()), newCommits);
        head_branch(repo, newCommits);
    }

    public static void inicommit(commits com, Repo repo) {
        File commitFile = Utils.join(repo.getCommits(), com.get_CommitID());
        Utils.writeObject(commitFile, com);
    }

    private static void Mycopy(commits src, commits des) {
        HashMap<String, String> blobs1 = src.getBlobs();
        HashMap<String, String> blobs = des.getBlobs();

        blobs.putAll(blobs1);
    }

    private static void blobsHelper(Repo repo, commits comm) {
        ArrayList<String> removedFile = repo.getRemovedFile();
        HashMap<String, String> stageBlobs = repo.getStageBlobs();
        HashMap<String, String> blobs = comm.getBlobs();

        for (Map.Entry<String, String> entry : stageBlobs.entrySet()) {
            blobs.put(entry.getKey(), entry.getValue());

        }
        /* clear stage area*/
        repo.getStageBlobs().clear();

        for (String removeFiles : removedFile) {
            blobs.remove(removeFiles);
        }
        /* clear the removed area */
        repo.getRemovedFile().clear();
    }

    private static void head_branch(Repo repo, commits comm) {
        /* set the repository */
        repo.setHEAD(comm.get_CommitID());
        /* get the branch */
        branch branch = Utils.readObject(Utils.join(repo.getBranch(), repo.getCurrBranch()), gitlet.branch.class);
        /* change the branch pointer */
        branch.set_CommitID(comm.get_CommitID());
        /* write back */
        Utils.writeObject(Utils.join(repo.getBranch(), repo.getCurrBranch()), branch);
    }
}
