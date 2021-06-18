package gitlet.commands;

import gitlet.Repo;
import gitlet.Utils;
import gitlet.commits;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/19:57
 * @Description: Untracked File
 */
public class rm {
    public static void rm(Repo repo, String... args) {
        argumentcheck.argumentCheck(2, """
                java gitlet.Main rm [fileName]
                """,args);

        /* get the current commit */
        commits currCommit = Utils.readObject(Utils.join(repo.getCommits(), repo.getHEAD()), gitlet.commits.class);
        /* get the hashmap of tracked files */
        HashMap<String, String> currBlobs = currCommit.getBlobs();
        /* get the hashmap of stage area files */
        HashMap<String, String> stageBlobs = repo.getStageBlobs();

        for (String currBlob : currBlobs.keySet()) {
            if (currBlob.equals(args[1])) {
                rmTracked(repo, stageBlobs, currBlob);
                Utils.writeObject(Utils.join(repo.getCommits(), repo.getHEAD()), currCommit);
                return;
            }
        }

        if (stageBlobs.containsKey(args[1])) {
            Utils.join(repo.getStageArea(), repo.getStageBlobs().get(args[1])).delete();
            repo.getStageBlobs().remove(args[1]);
            repo.getUntrackedFile().add(args[1]);
            Utils.writeObject(Utils.join(repo.getCommits(), repo.getHEAD()), currCommit);
            return;
        }

        System.out.println("No reason to remove the file.");
    }

    private static void rmTracked(Repo repo, HashMap<String, String> stage, String rmFile) {
        /* delet the file in the work directory */
        Utils.join(repo.getOutWd(), rmFile).delete();
        if (stage.containsKey(rmFile)) repo.getStageBlobs().remove(rmFile);
        repo.getDeletedFile().add(rmFile);

    }
}
