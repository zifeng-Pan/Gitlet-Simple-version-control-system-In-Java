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
    String _BranchName;
    String _CommitID; // represents the SHA-1 ID of the commit, we can use it to read commit object file.

    public branch(String _BranchName, String _commitID) {
    }
}
