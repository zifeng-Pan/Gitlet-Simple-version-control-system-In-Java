package gitlet.commands;

import gitlet.Blob;
import gitlet.Repo;
import gitlet.Utils;
import gitlet.branch;
import gitlet.commits;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/20:11
 * @Description:
 */
public class reset {
    /*  return to a commit version, and move the HEAD pointer */
    public static void reset(Repo repo, String... args) {
        if(!argumentcheck.argumentCheck(2, """
                java gitlet.Main reset [commitID]
                """,args)) return;
        ArrayList<String> untrackedFile = repo.getUntrackedFile();
        ArrayList<String> modifiedFile = repo.getModifiedFile();
        ArrayList<String> deletedFile = repo.getDeletedFile();
        if(untrackedFile.size() > 0 || modifiedFile.size() >0 || deletedFile.size() > 0) {
            System.out.println("There is an untracked file in the way; delete it or add it first.");
            return;
        }

        String commitId = args[1];
        resetHelper(repo,commitId);
    }

    private static void resetHelper(Repo repo,String commitID){
        /* get the commitID list */
        List<String> commitList = Utils.plainFilenamesIn(repo.getCommits());

        /* check the commitId */
        if(!commitList.contains(commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        }

        /* change the head pointer and current branch */
        repo.setHEAD(commitID);
        branch currentBranch = Utils.readObject(Utils.join(repo.getBranch(), repo.getCurrBranch()), gitlet.branch.class);

        /* get the current commit and its blobs */
        commits currCommit = Utils.readObject(Utils.join(repo.getCommits(), repo.getHEAD()), gitlet.commits.class);
        HashMap<String, String> currBlobs = currCommit.getBlobs();

        /* get the current file list */
        List<String> filesList = Utils.plainFilenamesIn(repo.getOutWd());

        /* get the stage list */
        HashMap<String, String> stageBlobs = repo.getStageBlobs();

        for (String currFile : filesList){
            if(!stageBlobs.containsKey(currFile) ){
                new File(currFile).delete();
            }
        }

        for (String filename : currBlobs.keySet()){
            /* get the blob */
            Blob blob = Utils.readObject(Utils.join(repo.getStageArea(), currBlobs.get(filename)), Blob.class);
            Utils.writeContents(Utils.join(repo.getOutWd(),filename),blob.get_Content());
        }

        currentBranch.set_CommitID(commitID);
    }
}
