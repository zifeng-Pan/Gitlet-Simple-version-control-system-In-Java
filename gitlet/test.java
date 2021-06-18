package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/16/14:37
 * @Description:
 */
public class test {
    public static void main(String... args) {
        Repo repo = Utils.readObject(new File("E:\\workspace_idea1\\proj2\\.gitlet\\REPO"),Repo.class);
        commits currCommit = Utils.readObject(Utils.join(repo.getCommits(),repo.getHEAD()),commits.class);


        System.out.println("repo info:");
        repoInfo(repo);

        System.out.print("\n\n");

        System.out.println("commit info:");
        commitInfo(currCommit);



    }
    public static void commitInfo(commits comm){
        System.out.println("----commitID----");
        System.out.println(comm.get_CommitID());
        System.out.println("----ParentID-----");
        System.out.println(comm.get_ParentCommitID());
        System.out.println("-------Time-----");
        System.out.println(comm.get_TimeStamp());
        System.out.println("-----message----");
        System.out.println(comm.get_LogMessage());
        System.out.println("-----blobs------");
        HashMap<String, String> blobs = comm.getBlobs();
        for (Map.Entry<String,String> blob : blobs.entrySet()){
            System.out.println(blob.getKey() + ":" + blob.getValue());
        }
    }

    public static void repoInfo(Repo repo){
        System.out.println("------HEAD-------");
        System.out.println(repo.getHEAD());
        System.out.println("---deletedFile---");
        System.out.println(repo.getDeletedFile());
        System.out.println("----RemovedFile----");
        System.out.println(repo.getRemovedFile());
        System.out.println("----modifiedFile---");
        System.out.println(repo.getModifiedFile());
        System.out.println("---untrackedFile---");
        System.out.println(repo.getUntrackedFile());
        System.out.println("----stageBlobs-----");
        HashMap<String, String> stageBlobs = repo.getStageBlobs();
        for (Map.Entry<String,String> blobs : stageBlobs.entrySet()){
            System.out.println(blobs.getKey() + ":" + blobs.getValue());
        }
        System.out.println("-----Branches----");
        ArrayList<String> branches = repo.getBranches();
        for (String branch1 : branches){
            branch branch = Utils.readObject(Utils.join(repo.getBranch(), repo.getCurrBranch()), gitlet.branch.class);
            System.out.println(branch1 + ":" + branch.get_CommitID());
        }
        System.out.println("-----CurrentBranch---");
        System.out.println(repo.getCurrBranch());
    }
}
