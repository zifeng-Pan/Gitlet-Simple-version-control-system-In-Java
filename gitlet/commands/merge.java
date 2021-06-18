package gitlet.commands;

import gitlet.*;
import gitlet.branch;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/19:58
 * @Description:
 */
public class merge {
    public static void merge(Repo repo,String ... args) {
        argumentcheck.argumentCheck(2, """
                java gitlet.Main merge [targetBranchName]
                """,args);

        ArrayList<String> branches = repo.getBranches();
        ArrayList<String> untrackedFile = repo.getUntrackedFile();
        ArrayList<String> modifiedFile = repo.getModifiedFile();
        ArrayList<String> deletedFile = repo.getDeletedFile();

        /* do nothing if the branchName as same as the branch that HEAD points and other errors*/
        if (args[1].equals(repo.getCurrBranch())) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        } else if(branches.size() == 1){
            System.out.println("The number of branches is 1,can't merge");
            return;
        } else if (repo.getStageBlobs().size() != 0){
            System.out.println("You have uncommitted changes.");
            return;
        } else if (!branches.contains(args[1])){
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (untrackedFile.size() > 0 || modifiedFile.size() > 0 || deletedFile.size() > 0){
            System.out.println("There is an untracked file in the way; delete it or add it first.");
            return;
        }

        commits currCommits = Utils.readObject(Utils.join(repo.getCommits(), repo.getHEAD()), gitlet.commits.class);
        if (currCommits.isSplitPoint())mergeFastForward(repo,args[1]);
        else realMerge(repo,args[1],currCommits);

    }

    /* FastForward merge */
    private static void mergeFastForward(Repo repo, String targetBranch) {
        /* get the targetBranch and the currentBranch */
        branch tBranch = Utils.readObject(Utils.join(repo.getBranch(), targetBranch), gitlet.branch.class);
        branch currBranch = Utils.readObject(Utils.join(repo.getBranch(), repo.getCurrBranch()), gitlet.branch.class);

        /* change the branch pointer */
        currBranch.set_CommitID(tBranch.get_CommitID());

        /* change the HEAD */
        repo.setHEAD(tBranch.get_CommitID());

        /* write back */
        Utils.writeObject(Utils.join(repo.getBranch(), repo.getCurrBranch()),currBranch);

        System.out.println("Current branch fast-forwarded");
    }

    /*  merge two branch and create a new commit */
    private static void realMerge(Repo repo, String targetBranch,commits comm) {

        /* get the target branch */
        branch tBranch = Utils.readObject(Utils.join(repo.getBranch(), targetBranch), gitlet.branch.class);

        /* get the stageBlobs(to stage some file after merging) */
        HashMap<String, String> stageBlobs = repo.getStageBlobs();

        /* get Commits to compare file */
        commits splitPointCommit = trackSplitPoint(repo);
        commits targetCommit = Utils.readObject(Utils.join(repo.getCommits(), tBranch.get_CommitID()), gitlet.commits.class);

        /* get the three blobs */
        HashMap<String, String> sBlobs = splitPointCommit.getBlobs();
        HashMap<String, String> tBlobs = targetCommit.getBlobs();
        HashMap<String, String> cBlobs = comm.getBlobs();

        dealWithAbsent(repo,targetCommit.get_CommitID(),sBlobs,tBlobs,cBlobs);
        dealWithModify(repo,targetCommit.get_CommitID(),sBlobs,tBlobs,cBlobs);
        dealWithConflict(repo,targetCommit.get_CommitID(),tBlobs,cBlobs);
    }


    /**
     * @Description: deal with the problem of absence
     * @Author: Pan Zifeng
     * @Date:   2021/6/18 18:43
     * @Param:  sBlobs: the Blobs of splitPoint;    tBlobs: the Blobs of the given branch
     *          cBlobs: the Blobs of currentBranch;  tCommitId: the given branch's commitID
     **/
    /* deal with the problem of file removing */
    private static void dealWithAbsent(Repo repo, String tCommitID,
                                HashMap<String,String> sBlobs,
                                HashMap<String,String>tBlobs,
                                HashMap<String,String> cBlobs) {
        /* deal with the file not exists at the split point */
        /**
         * Any files that were not present at the split point
         *      and are present only in the current branch should remain as they are.
         * Any files that were not present at the split point
         *      and are present only in the given branch should be checked out and staged.
         */
        for (String tFile : tBlobs.keySet()){
            if(!sBlobs.containsKey(tFile) && !cBlobs.containsKey(tFile)){
                checkout.checkout(repo,"checkout",tCommitID,"--",tFile);
                add.add(repo,"add",tFile);
            }
        }

        /**
         * Any files present at the split point,
         *      unmodified in the current branch, and absent in the given branch should be removed (and untracked).
         * Any files present at the split point,
         *      unmodified in the given branch, and absent in the current branch should remain absent.
         */

        /* deal with the file exists at the split point and the given branch or current branch */
        for (String sFile : sBlobs.keySet()){
            Blob sBlob = Utils.readObject(Utils.join(repo.getStageArea(), sBlobs.get(sFile)), Blob.class);
            if (cBlobs.containsKey(sFile) && !tBlobs.containsKey(sFile)){
                Blob cBlob = Utils.readObject(Utils.join(repo.getStageArea(), cBlobs.get(sFile)), Blob.class);
                if (sBlob == cBlob){
                    rm.rm(repo,"rm",sFile);
                }
            }
        }
    }

    /**
     * @Description: deal with the problem of modify and conflict
     * @Author: Pan Zifeng
     * @Date:   2021/6/18
     * @Param:  sBlobs: the Blobs of splitPoint;    tBlobs: the Blobs of the given branch
     *          cBlobs: the Blobs of currentBranch;  tCommitId: the given branch's commitID
     **/
    private static void dealWithModify(Repo repo, String tCommitID,
                                HashMap<String,String> sBlobs,
                                HashMap<String,String> tBlobs,
                                HashMap<String,String> cBlobs) {
        /**
         * Any files that have been modified in the given branch since the split point,
         *      but not modified in the current branch since the split point should be changed to their versions in the given branch
         *      (checked out from the commit at the front of the given branch).
         * Any files that have been modified in the current branch
         *      but not in the given branch since the split point should stay as they are.
         */
        for (String sFile : sBlobs.keySet()){
            Blob sBlob = Utils.readObject(Utils.join(repo.getStageArea(), sBlobs.get(sFile)), Blob.class);
            if (cBlobs.containsKey(sFile) && tBlobs.containsKey(sFile)){
                Blob cBlob = Utils.readObject(Utils.join(repo.getStageArea(), cBlobs.get(sFile)), Blob.class);
                Blob tBlob = Utils.readObject(Utils.join(repo.getStageArea(), tBlobs.get(sFile)), Blob.class);
                /* contents */
                String sBlobContent = sBlob.get_Content();
                String tBlobContent = tBlob.get_Content();
                String cBlobContent = cBlob.get_Content();
                if (!sBlobContent.equals(tBlobContent) && sBlobContent.equals(cBlobContent)){
                    checkout.checkout(repo,"checkout",tCommitID,"--",sFile);
                }
            }
        }
    }

    // deal with the problem of file contents conflict
    private static void dealWithConflict(Repo repo, String tCommitID,
                                  HashMap<String,String> tBlobs,
                                  HashMap<String,String> cBlobs) {
        for (String tFile : tBlobs.keySet()){
            if (cBlobs.containsKey(tFile)){
                Blob cBlob = Utils.readObject(Utils.join(repo.getStageArea(), cBlobs.get(tFile)), Blob.class);
                Blob tBlob = Utils.readObject(Utils.join(repo.getStageArea(), tBlobs.get(tFile)), Blob.class);
            }
        }


    }

    private static void conflictHelper(Repo repo, Blob cBlob, Blob tBlob, String file){
        byte[] cBytes = cBlob.get_Content().getBytes(StandardCharsets.UTF_8);
        byte[] tBytes = tBlob.get_Content().getBytes(StandardCharsets.UTF_8);

        ArrayList<Byte> sameBytes = new ArrayList<>();
        ArrayList<Byte> diffCBytes = new ArrayList<>();
        ArrayList<Byte> diffTBytes = new ArrayList<>();
        int diffPos = 0;

        int maxLen = Math.max(cBytes.length,tBytes.length);
        for (int i = 0; i < maxLen; i++){
            if (cBytes[i] == tBytes[i]) sameBytes.add(cBytes[i]);
            else{
                diffPos = i;
                break;
            }
        }

        if (diffPos == maxLen) {
            System.out.println("Merged [current branch name] with [given branch name]..");
            return;
        }

        for (int i = diffPos; i < cBytes.length; i++)diffCBytes.add(cBytes[i]);
        for (int i = diffPos; i < tBytes.length; i++) diffTBytes.add(tBytes[i]);
        File sameFile = Utils.join(repo.getOutWd(), file);

        Utils.restrictedDelete(sameFile);
        Utils.writeContents(sameFile,sameBytes.toArray());
        Utils.writeContents(sameFile,"<<<<<<< HEAD\n");
        Utils.writeContents(sameFile,diffCBytes.toArray());
        Utils.writeContents(sameFile,"=======\n");
        Utils.writeContents(sameFile,diffTBytes.toArray());
        Utils.writeContents(sameFile,"=======\n");

        repo.getModifiedFile().add(file);
        System.out.println("Encountered a merge conflict.");
    }

    /* retrieve the commit which is the split point. */
    private static commits trackSplitPoint(Repo repo) {
        commits currCommit = Utils.readObject(Utils.join(repo.getCommits(), repo.getHEAD()), commits.class);
        while (true){
            if(currCommit.isSplitPoint()) return currCommit;
            currCommit = Utils.readObject(Utils.join(repo.getCommits(), currCommit.get_ParentCommitID()), commits.class);
        }
    }

    private static boolean modifyCompare(Blob pastBlob, Blob currBlob){
        return pastBlob.get_Content().equals(currBlob.get_Content());
    }
}
