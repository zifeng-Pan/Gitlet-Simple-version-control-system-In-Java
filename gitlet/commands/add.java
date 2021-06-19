package gitlet.commands;

import gitlet.Blob;
import gitlet.Repo;
import gitlet.Utils;
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
 * @Date: 2021/06/13/19:57
 * @Description:
 */
public class add {
    /* add a file to the stage area */
    public static void add(Repo repo, String... args) {
        if(!argumentcheck.argumentCheck(2, """
                java gitlet.Main add [filename]
                java gitlet.Main add .
                """,args)) return;
        ArrayList<String> modifiedFile = repo.getModifiedFile();
        ArrayList<String> deletedFile = repo.getDeletedFile();
        ArrayList<String> untrackedFile = repo.getUntrackedFile();
        /* get the currcommit */
        commits comm = Utils.readObject(Utils.join(repo.getCommits(), repo.getHEAD()), gitlet.commits.class);

        if (args[1].equals(".")) {
            addAll(repo, modifiedFile, deletedFile, untrackedFile,comm);
            return;
        }

        /* create the FILE Object of the file waiting to be added */
        for (int i = 1; i < args.length; i++) {
            File addFile = new File(args[i]);
            addHelper(repo, addFile, modifiedFile, deletedFile, untrackedFile,comm);
        }

    }

    private static void addAll(Repo repo, ArrayList<String> modifiedFiles, ArrayList<String> deletedFiles,
                               ArrayList<String> untrackedFiles,commits comm) {
        File allFile = new File(".");
        List<String> addFiles = Utils.plainFilenamesIn(allFile);
        ArrayList<String> deletedFile = repo.getDeletedFile();

        if (addFiles == null) {
            Utils.error("Files does not exist");
            return;
        }
        for (String addFile : addFiles) {
            addHelper(repo, new File(addFile), modifiedFiles, deletedFiles, untrackedFiles,comm);
        }

        for (int i = 0; i < deletedFile.size(); i++){
            addHelper(repo, new File(deletedFile.get(i)), modifiedFiles, deletedFiles, untrackedFiles,comm);
        }
    }

    private static void addHelper(Repo repo, File addFile, ArrayList<String> modifiedFiles, ArrayList<String> deletedFiles,
                                  ArrayList<String> untrackedFiles,commits comm) {
        if (addFile.exists()) {
            warning(repo,addFile);
            HashMap<String, String> stageBlobs = repo.getStageBlobs();
            /* get the content of the file */
            String content = Utils.readContentsAsString(addFile);

            /* calculate the SHA - 1 ID of this blob and set it*/
            String sha1ID = Utils.sha1(addFile.getName() + content);

            if (Utils.join(repo.getStageArea(), sha1ID).exists() &&
                    comm.getBlobs().containsKey(addFile.getName())){
                if ( modifiedFiles.contains(addFile.getName()) || untrackedFiles.contains(addFile.getName())){
                    /* add to the staged area*/
                    stageBlobs.put(addFile.getName(), sha1ID);

                    /* remove from list */
                    removeFromList(modifiedFiles, deletedFiles, untrackedFiles, addFile);
                }
            }else if (!Utils.join(repo.getStageArea(), sha1ID).exists() ) {
                /* create the new blob object */
                Blob blob = new Blob(addFile.getName(), content);

                /* set the sha1Id */
                blob.set_BlobID(sha1ID);

                /* replace Check */
                replace(repo, addFile, modifiedFiles);

                /* create a file in the stage folder */
                Utils.writeObject(Utils.join(repo.getStageArea(), sha1ID), blob);

                /* add to the staged area*/
                stageBlobs.put(addFile.getName(), sha1ID);

                /* remove from list */
                removeFromList(modifiedFiles, deletedFiles, untrackedFiles, addFile);
            }
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

    private static void replace(Repo repo, File file, ArrayList<String> modifiedFiles){
        String fileName = file.getName();
        boolean existFlag = (repo.getStageBlobs().containsKey(fileName) && modifiedFiles.contains(fileName));
        if (existFlag){
            Utils.join(repo.getStageArea(),repo.getStageBlobs().get(fileName)).delete();
        }
    }

    private static void warning(Repo repo, File file){
        if (repo.getStageBlobs().containsKey(file.getName()))
            System.out.println("WARNING: The Blob of this file in staging area will be replaced");
    }
}
