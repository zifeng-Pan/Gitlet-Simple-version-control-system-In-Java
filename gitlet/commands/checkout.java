package gitlet.commands;

import gitlet.*;
import gitlet.branch;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* without checking */

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/20:10
 * @Description:
 */
public class checkout {
    public static void checkout(Repo repo, String... args) {
        int cmdLen = args.length;
        switch (cmdLen){
            case 2: {
                checkoutBranch(repo,args);
                break;
            } case 3:{
                checkoutBackCurrentCommit(repo,args);
                break;
            } case 4:{
                checkoutBackFromCommit(repo,args);
                break;
            } default:{
                System.out.println("Wrong number of arguments");
                Utils.message("-----HELP INFO -----");
                help();
                break;
            }
        }
    }

    /* take the file back from the stage area  */
    private static void checkoutBackCurrentCommit(Repo repo,String... args) {
        String fileName = args[2];
        String head = repo.getHEAD();
        String[] newArgs = {args[0],head,args[1],fileName};
        checkoutBackFromCommit(repo,newArgs);
    }

    /* take the file back from the commit with the given commitID */
    private static void checkoutBackFromCommit(Repo repo,String... args) {
        String commitID = args[1];
        String fileName = args[3];

        /* commitId list */
        List<String> commitList = Utils.plainFilenamesIn(repo.getCommits());
        if(!commitList.contains(commitID)) {
            System.out.println("No commit with that id exists.");
            return;
        }

        /* get the commit by the given commitID*/
        commits retrieveCommit = Utils.readObject(Utils.join(repo.getCommits(), commitID), gitlet.commits.class);

        /* get the blobs of the comm */
        HashMap<String, String> blobs = retrieveCommit.getBlobs();

        /* check the blobs */
        if(!blobs.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }

        /* get the blob */
        Blob blob = Utils.readObject(Utils.join(repo.getStageArea(), blobs.get(fileName)), Blob.class);

        overWrite(repo,blob,fileName);


        /* check whether  the new file need to be added to the UntrackedFile */
        commits currCommit = Utils.readObject(Utils.join(repo.getCommits(), repo.getHEAD()), commits.class);
        HashMap<String, String> currBlobs = currCommit.getBlobs();

        if (!currBlobs.containsKey(fileName)) return;

        /* aiming to get the content of current blob named [fileName] */
        Blob currBlob = Utils.readObject(Utils.join(repo.getStageArea(), currBlobs.get(fileName)), Blob.class);

        if (currBlob.get_Content().equals(blob.get_Content())) repo.getUntrackedFile().remove(fileName);
    }

    /* just switch the branch and clear the stage area */
    private static void checkoutBranch(Repo repo,String... args) {

        ArrayList<String> branches = repo.getBranches();

        String desBranch = args[1];

        if (!areaCheck(repo))return;
        else if (desBranch.equals(repo.getCurrBranch())){
            System.out.println("No need to checkout the current branch.");
            return;
        } else if(!branches.contains(desBranch)) {
            System.out.println("No such branch exists.");
            return;
        }

        headChange(repo,desBranch);

    }

    private static void help(){
        System.out.print(("""
                The command format:
                java gitlet.Main checkout -- [file name]
                java gitlet.Main checkout [commit id] -- [file name]
                java gitlet.Main checkout [branch name]
                """));
    }

    public static void headChange(Repo repo,String desBranch){
        /* get dBranch */
        branch dBranch = Utils.readObject(Utils.join(repo.getBranch(), desBranch), gitlet.branch.class);

        /* change the head pointer */
        repo.setHEAD(dBranch.get_CommitID());

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

        repo.setCurrBranch(desBranch);
    }

    private static void recoverHelper(Repo repo, String file){
        ArrayList<String> deletedFile = repo.getDeletedFile();
        ArrayList<String> modifiedFile = repo.getModifiedFile();
        deletedFile.remove(file);
        modifiedFile.remove(file);
        repo.getRemovedFile().remove(file);
        repo.getStageBlobs().remove(file);
        repo.getUntrackedFile().add(file);
    }

    private static void overWrite(Repo repo,Blob blob,String fileName){
        /* currFileList */
        List<String> currFilesList = Utils.plainFilenamesIn(repo.getOutWd());

        if (currFilesList.contains(fileName)) Utils.restrictedDelete(fileName);
        recoverHelper(repo,fileName);

        Utils.writeContents(Utils.join(repo.getOutWd(),fileName),blob.get_Content());
    }

    public static boolean areaCheck(Repo repo){
        ArrayList<String> untrackedFile = repo.getUntrackedFile();
        ArrayList<String> modifiedFile = repo.getModifiedFile();
        ArrayList<String> deletedFile = repo.getDeletedFile();
        if(untrackedFile.size() > 0 || modifiedFile.size() >0 || deletedFile.size() > 0) {
            System.out.println("There is an untracked file in the way; delete it or add it first.");
            return false;
        }
        return true;
    }
}
