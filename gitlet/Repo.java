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
        HashMap<String, String> blobs = comm.getBlobs();


        untrackedFile.removeIf(untrackFile -> !(new File(untrackFile).exists()));
        deletedFile.removeIf(deleteFile -> !(new File(deleteFile).exists()));

        for (String blobName : blobs.keySet()){
            if (!(new File(blobName).exists()) && !removedFile.contains(blobName) && !deletedFile.contains(blobName))
                this.deletedFile.add(blobName);
        }

        for (String work : files) {
            checkFile(new File(work), blobs);
        }
    }

    /**
     * @Description: check Files whether they are modified, removed or created
     * @Author: Pan Zifeng
     * @Date: 2021/6/15 21:37
     * @Param: file: the file waiting for checking
     * comm: the current commit
     **/
    private void checkFile(File file, HashMap<String, String> blobs) {
        String fileName = file.getName();

        boolean contains = (blobs.containsKey(fileName) || stageBlobs.containsKey(fileName));
        boolean contains1 = untrackedFile.contains(file.getName());
        boolean contains3 = modifiedFile.contains(file.getName());

        if (contains3 && !modifiedCheck(blobs,file)) modifiedFile.remove(fileName);

        if (contains1 || contains3) return;

        if (file.exists() && !contains && !stageBlobs.containsKey(fileName)) this.untrackedFile.add(fileName);
        else if (file.exists() && contains && modifiedCheck(blobs,file)) {
            this.modifiedFile.add(fileName);
        }
    }

    private boolean modifiedCheck(HashMap<String,String> blobs,File file){
        String commitContents;
        if (stageBlobs.containsKey(file.getName())) commitContents = Utils.readObject(Utils.join(this.stageArea, stageBlobs.get(file.getName())), Blob.class).get_Content();
        else commitContents = Utils.readObject(Utils.join(this.stageArea, blobs.get(file.getName())), Blob.class).get_Content();

        String currContents = Utils.readContentsAsString(file);
        if (commitContents.length() != currContents.length()) {
            return true;
        } else {
            for (int i = 0; i < currContents.length(); i++) {
                if (commitContents.charAt(i) != currContents.charAt(i)) {
                    return true;
                }
            }
            return false;
        }
    }

    public ArrayList<String> getBranches() {
        return branches;
    }

    /**
     * getter and setter functions
     */

    public String getCwd() {
        return cwd;
    }

    public String getStageArea() {
        return stageArea;
    }

    public String getCommits() {
        return commits;
    }

    public String getBranch() {
        return branch;
    }

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

    public void setBranches(ArrayList<String> branches) {
        this.branches = branches;
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

    /**
     * Fields
     */

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
    /* the list of the branch */
    private ArrayList<String> branches;
}
