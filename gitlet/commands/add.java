package gitlet.commands;

import gitlet.Blob;
import gitlet.Repo;
import gitlet.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/19:57
 * @Description:
 */
public class add {
    /* add a file to the stage area */
    public static void add(Repo repo, String... args) {
        argumentcheck.argumentCheck(2, """
                java gitlet.Main add [filename]
                java gitlet.Main add .
                """,args);
        ArrayList<String> modifiedFile = repo.getModifiedFile();
        ArrayList<String> deletedFile = repo.getDeletedFile();
        ArrayList<String> untrackedFile = repo.getUntrackedFile();

        if (args[1].equals(".")) {
            addAll(repo, modifiedFile, deletedFile, untrackedFile);
            return;
        }
        /* create the FILE Object of the file waiting to be added */
        for (int i = 1; i < args.length; i++) {
            File addFile = new File(args[i]);
            addHelper(repo, addFile, modifiedFile, deletedFile, untrackedFile);
        }

    }

    private static void addAll(Repo repo, ArrayList<String> modifiedFiles, ArrayList<String> deletedFiles,
                               ArrayList<String> untrackedFiles) {
        File allFile = new File(".");
        List<String> addFiles = Utils.plainFilenamesIn(allFile);
        if (addFiles == null) {
            Utils.error("Files does not exist");
            return;
        }
        for (String addFile : addFiles) {
            addHelper(repo, new File(addFile), modifiedFiles, deletedFiles, untrackedFiles);
        }

    }

    private static void addHelper(Repo repo, File addFile, ArrayList<String> modifiedFiles, ArrayList<String> deletedFiles,
                                  ArrayList<String> untrackedFiles) {
        if (addFile.exists()) {
            HashMap<String, String> stageBlobs = repo.getStageBlobs();
            /* get the content of the file */
            String content = Utils.readContentsAsString(addFile);

            /* calculate the SHA - 1 ID of this blob and set it*/
            String sha1ID = Utils.sha1(addFile.getName() + content);

            /* if the blob has been added into the stage Folder */
            if (Utils.join(repo.getStageArea(), sha1ID).exists()) return;

            /* create the new blob object */
            Blob blob = new Blob(addFile.getName(), content);

            /* set the sha1Id */
            blob.set_BlobID(sha1ID);

            /* create a file in the stage folder */
            Utils.writeObject(Utils.join(repo.getStageArea(), sha1ID), blob);

            /* add to the staged area*/
            stageBlobs.put(addFile.getName(), sha1ID);

            /* remove from list */
            removeFromList(modifiedFiles, deletedFiles, untrackedFiles, addFile);
        } else if (repo.getDeletedFile().contains(addFile.getName())) {
            repo.getRemovedFile().add(addFile.getName());
            repo.getDeletedFile().remove(addFile.getName());
        } else Utils.error("File does not exist");
    }

    /* when a operation has push to the stage area, remove it from the list */
    private static void removeFromList(ArrayList<String> modifiedFiles, ArrayList<String> deletedFiles,
                                       ArrayList<String> untrackedFiles, File file) {
        String name = file.getName();
        modifiedFiles.remove(name);
        deletedFiles.remove(name);
        untrackedFiles.remove(name);

    }

}
