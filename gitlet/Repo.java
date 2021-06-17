package gitlet;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/14:36
 * @Description: this class is used to control the whole .gitlet file system, and will be stored at an binary file
 */
public class Repo implements Serializable {
    /* .gitlet work directory */
    private final String cwd;
    /* stage area directory */
    private final String stageArea;
    /* commits file directory */
    private final String commits;
    /* branches directory */
    private final String branch;
    /* outside work directory */
    private final String outWd;
    /**
     * Fields
     */

    /* HEAD */
    private String HEAD = "HEAD";


    /* this function won't be use in this proj, because this proj is the simple git(not consider the subdirectories)
    public void checkFolder(File directory,commits comm){
        HashMap<String, String> blobs = comm.getBlobs();
        boolean contains = blobs.containsKey(directory.getName());
        String Path = directory.getPath();

        if (!contains){
            this.untrackedFile.add(Path);
        }
        else {
            checkHelper(Path,comm);
        }

    }
    */
    /* current branch */
    private String currBranch;
    /* untracked file in wd(work directory) */
    private ArrayList<String> untrackedFile;
    /* modified(include the deleted file tracked by current commit) but not staged file in the wd */
    private ArrayList<String> modifiedFile;
    /* file that removed by the git rm in the wd */
    private ArrayList<String> removedFile;
    /* the deleted file */
    private ArrayList<String> deletedFile;
    /* the hashMap from the filename to the file SHA1ID */
    private HashMap<String, String> stageBlobs;

    /**
     * constructor
     */
    public Repo(String cwd) {
        this.outWd = cwd;
        this.cwd = cwd + File.separator + ".gitlet";
        this.stageArea = this.cwd + File.separator + "Stage";
        this.commits = this.cwd + File.separator + "Commit";
        this.branch = this.cwd + File.separator + "Branch";
        this.untrackedFile = new ArrayList<>();
        this.modifiedFile = new ArrayList<>();
        this.removedFile = new ArrayList<>();
        this.deletedFile = new ArrayList<>();
        this.branches = new ArrayList<>();
        this.stageBlobs = new HashMap<>();
    }

    public String getCwd() {
        return cwd;
    }

    public String getStageArea() {
        return stageArea;
    }

    public String getCommits() {
        return commits;
    }

    /* the list of the branch */
    private ArrayList<String> branches;

    public String getBranch() {
        return branch;
    }

    /**
     * Functions
     */

    // the function to deal with the command and the args passed by the main
    // use the reflection to achieve it
    public void command(String... args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        check(this.outWd);
        String cmd = args[0];
        String Path = "gitlet.commands." + cmd;
        Class.forName(Path).getDeclaredMethod(cmd, gitlet.Repo.class, java.lang.String[].class).
                invoke(null, this, args);
        updateREPO(this);
    }

    public void updateREPO(Repo repo) {
        Utils.writeObject(Utils.join(this.cwd, "REPO"), repo);
    }

    /**
     * @Description: check whether the work directory has changes
     * @Author: Pan Zifeng
     * @Date: 2021/6/15 21:41
     * @Param: dir: the absolute path of work directory
     **/
    public void check(String dir) {
        gitlet.commits currCommit = Utils.readObject(Utils.join(this.commits, this.HEAD), gitlet.commits.class);
        checkHelper(dir, currCommit);
    }

    private void checkHelper(String dir, commits comm) {
        File checkDir = new File(dir);
        List<String> files = Utils.plainFilenamesIn(checkDir);
        if (files == null) return;
        for (String work : files) {
            checkFile(new File(work), comm);
        }
        /* the complicated code
        File tempWork;
        File dirFile = new File(dir);
        String[] file_folder_list = dirFile.list();
        if (file_folder_list == null) return;
        for (String work : file_folder_list){

            if (dir.equals(this.outWd)) tempWork = new File(work);
            else tempWork = Utils.join(dir,work);


            if (tempWork.isDirectory()){
                checkFolder(tempWork,comm);
            }
            else checkFile(tempWork,comm);
        }

         */
    }

    /**
     * @Description: check Files whether they are modified, removed or created
     * @Author: Pan Zifeng
     * @Date: 2021/6/15 21:37
     * @Param: file: the file waiting for checking
     * comm: the current commit
     **/
    private void checkFile(File file, commits comm) {
        if (stageBlobs.containsKey(file.getName()) || untrackedFile.contains(file.getName())
                || modifiedFile.contains(file.getName()) || deletedFile.contains(file.getName())) return;
        HashMap<String, String> blobs = comm.getBlobs();
        boolean contains = blobs.containsKey(file.getName());
        String fileName = file.getName();

        if (!file.exists() && contains && !removedFile.contains(fileName)) {
            this.deletedFile.add(fileName);
        } else if (file.exists() && !contains) this.untrackedFile.add(fileName);
        else if (file.exists() && contains) {
            String commitContents = Utils.readObject(Utils.join(this.stageArea, blobs.get(file.getName())), Blob.class).get_Content();
            String currContents = Utils.readContentsAsString(file);
            if (commitContents.length() != currContents.length()) {
                this.modifiedFile.add(fileName);
            } else {
                for (int i = 0; i < currContents.length(); i++) {
                    if (commitContents.charAt(i) != currContents.charAt(i)) {
                        this.modifiedFile.add(fileName);
                        break;
                    }
                }
            }
        }
    }

    public ArrayList<String> getBranches() {
        return branches;
    }

    public void setBranches(ArrayList<String> branches) {
        this.branches = branches;
    }

    /**
     * getter and setter ...
     */

    public String getCurrBranch() {
        return currBranch;
    }

    public void setCurrBranch(String currBranch) {
        this.currBranch = currBranch;
    }

    public String getHEAD() {
        return HEAD;
    }

    public void setHEAD(String HEAD) {
        this.HEAD = HEAD;
    }

    public ArrayList<String> getUntrackedFile() {
        return untrackedFile;
    }

    public void setUntrackedFile(ArrayList<String> untrackedFile) {
        this.untrackedFile = untrackedFile;
    }

    public ArrayList<String> getModifiedFile() {
        return modifiedFile;
    }

    public void setModifiedFile(ArrayList<String> modifiedFile) {
        this.modifiedFile = modifiedFile;
    }

    public ArrayList<String> getDeletedFile() {
        return deletedFile;
    }

    public void setDeletedFile(ArrayList<String> deletedFile) {
        this.deletedFile = deletedFile;
    }

    public ArrayList<String> getRemovedFile() {
        return removedFile;
    }

    public void setRemovedFile(ArrayList<String> removedFile) {
        this.removedFile = removedFile;
    }

    public HashMap<String, String> getStageBlobs() {
        return stageBlobs;
    }

    public void setStageBlobs(HashMap<String, String> stageBlobs) {
        this.stageBlobs = stageBlobs;
    }

    public String getOutWd() {
        return outWd;
    }
}
