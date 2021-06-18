package gitlet.commands;

import gitlet.Repo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/15/9:46
 * @Description:
 */
public class status {
    /* print the status of this git */
    public static void status(Repo repo, String... args) {
        ArrayList<String> branches = repo.getBranches();
        String currBranch = repo.getCurrBranch();
        HashMap<String, String> stageBlobs = repo.getStageBlobs();
        ArrayList<String> removedFile = repo.getRemovedFile();
        ArrayList<String> deletedFile = repo.getDeletedFile();
        ArrayList<String> modifiedFile = repo.getModifiedFile();
        ArrayList<String> untrackedFile = repo.getUntrackedFile();

        System.out.println("===Branches===");
        for (String branch : branches) {
            if (branch.equals(currBranch)) {
                System.out.println(("*" + currBranch));
            } else System.out.println(branch);
        }
        System.out.print("\n\n");

        System.out.println("===Staged Files===");
        for (String blobName : stageBlobs.keySet()) System.out.println(blobName);
        System.out.print("\n\n");

        System.out.println("===Removed Files===");
        removedFile.forEach(System.out::println);
        System.out.print("\n\n");

        System.out.println("-------- Untracked PART -------");
        System.out.println("===Modifications Not Staged For Commit===");
        for (String deleted : deletedFile) {
            System.out.println((deleted + "(deleted)"));
        }
        for (String modified : modifiedFile) {
            System.out.println((modified + "(modified)"));
        }
        System.out.print("\n\n");

        System.out.println("===Untracked Files===");
        untrackedFile.forEach(System.out::println);
        System.out.print("\n\n");
    }
}
