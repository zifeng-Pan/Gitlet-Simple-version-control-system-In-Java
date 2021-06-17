package gitlet;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/14:37
 * @Description:
 */
public class branch implements Serializable {

    /**
     * represents the SHA-1 ID of the commit, we can use it to read commit object file.
     */

    public branch(String _BranchName, String _commitID) {
        this._BranchName = _BranchName;
        this._CommitID = _commitID;
    }

    public String get_BranchName() {
        return _BranchName;
    }

    public void set_BranchName(String _BranchName) {
        this._BranchName = _BranchName;
    }

    public String get_CommitID() {
        return _CommitID;
    }

    public void set_CommitID(String _CommitID) {
        this._CommitID = _CommitID;
    }

    private String _CommitID;
    private String _BranchName;
}
