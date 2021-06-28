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
 * @Description: the merge command used to merge two branch(including the fast-forward and merging with conflict)
 */
public class merge {
    public static void merge(Repo repo,String ... args) {
        if(!argumentcheck.argumentCheck(2, """
                java gitlet.Main merge [targetBranchName]
                """,args)) return;

        ArrayList<String> branches = repo.getBranches();
        ArrayList<String> untrackedFile = repo.getUntrackedFile();
        ArrayList<String> modifiedFile = repo.getModifiedFile();
        ArrayList<String> deletedFile = repo.getDeletedFile();

        /* do nothing if the branchName as same as the branch that HEAD points and other errors*/
        if (args[1].equals(repo.getCurrBranch())) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        } else if (Utils.readObject(Utils.join(repo.getBranch(),args[1]),gitlet.branch.class).get_CommitID()
                .equals(trackSplitPoint(repo).get_CommitID())){
            System.out.println("Given branch is an ancestor of the current branch.");
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

    /* FastForward merge, just move the pointer */
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

        /* reset */
        reset.reset(repo,"reset",currBranch.get_CommitID());

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
        dealWithConflict(repo,targetBranch,tBlobs,cBlobs);
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
     * @Description: deal with the file content modification 
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

                if (modifyCompare(sBlob,tBlob) && modifyCompare(sBlob,cBlob)){
                    checkout.checkout(repo,"checkout",tCommitID,"--",sFile);
                }
            }
        }
    }

    /* handle the conflict after merging two branches */
    private static void dealWithConflict(Repo repo, String targetBranch,
                                  HashMap<String,String> tBlobs,
                                  HashMap<String,String> cBlobs) {

        boolean conflictFlag = false;

        for (String tFile : tBlobs.keySet()){
            if (cBlobs.containsKey(tFile)){
                Blob cBlob = Utils.readObject(Utils.join(repo.getStageArea(), cBlobs.get(tFile)), Blob.class);
                Blob tBlob = Utils.readObject(Utils.join(repo.getStageArea(), tBlobs.get(tFile)), Blob.class);
                if(conflictHelper(repo,cBlob,tBlob,tFile,targetBranch))conflictFlag = true;
            }
        }

        if (conflictFlag) System.out.println("Encountered a merge conflict.");
        else System.out.println("Merged " + repo.getCurrBranch() + " with " + targetBranch);
    }
    
    /* the Helper function used for conflict handling */
    private static boolean conflictHelper(Repo repo, Blob cBlob, Blob tBlob, String file,String targetBranch){
        byte[] cBytes = cBlob.get_Content().getBytes(StandardCharsets.UTF_8);
        byte[] tBytes = tBlob.get_Content().getBytes(StandardCharsets.UTF_8);
        ArrayList<Byte> sameBytes = new ArrayList<>();

        int diffPos = 0;

        int maxLen = Math.max(cBytes.length,tBytes.length);
        for (int i = 0; i < maxLen; i++){
            if (cBytes[i] == tBytes[i]) sameBytes.add(cBytes[i]);
            else{
                diffPos = i;
                break;
            }
        }

        if (diffPos == 0) {
            return false;
        }


        byte[] sameByte   = new byte[sameBytes.size()];
        for (int i = 0; i < sameBytes.size(); i ++) sameByte[i] = sameBytes.get(i);

        File sameFile = Utils.join(repo.getOutWd(), file);

        String content = new String(sameByte) + " <<<<<<< HEAD\n" + new String(cBytes,diffPos,cBytes.length - diffPos)
               +"\n========\n" + new String(tBytes,diffPos,tBytes.length - diffPos) + "\n >>>>>>>" + targetBranch + "\n";

        Utils.restrictedDelete(sameFile);
        Utils.writeContents(sameFile, content);

        repo.getModifiedFile().add(file);
        return true;
    }

    /* retrieve the commit which is the split point. */
    private static commits trackSplitPoint(Repo repo) {
        commits currCommit = Utils.readObject(Utils.join(repo.getCommits(), repo.getHEAD()), commits.class);
        while (true){
            if(currCommit.isSplitPoint()) return currCommit;
            currCommit = Utils.readObject(Utils.join(repo.getCommits(), currCommit.get_ParentCommitID()), commits.class);
        }
    }

    /* compare two files to judge whether there are some changes */
    private static boolean modifyCompare(Blob pastBlob, Blob currBlob){
        return pastBlob.get_Content().equals(currBlob.get_Content());
    }
}
