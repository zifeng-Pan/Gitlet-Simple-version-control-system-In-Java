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
    String _BranchName;
    String _CommitID;

    public head(String _BranchName, String _commitID) {
    }
}
