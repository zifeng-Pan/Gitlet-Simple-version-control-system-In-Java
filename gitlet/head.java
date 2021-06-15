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
public class head implements Serializable {
    private String _BranchName;

    public head(String _BranchName) {
        this._BranchName = "master";
    }

    public String get_BranchName() {
        return _BranchName;
    }

    public void set_BranchName(String _BranchName) {
        this._BranchName = _BranchName;
    }
}
