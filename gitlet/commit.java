package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/12/21:31
 * @Description:
 */
public class commit implements Serializable {
    private final String _commitId;
    private final String _TimeStamp;
    private final String _Message;
    private final String _ParentCommitID;
    HashMap<String, Blob> blobs;
    private String _Branch;
    private String _Author;

    public commit(String _commitId, String _TimeStamp, String _Message, String _ParentCommitID, HashMap<String, Blob> blobs) {
        this._commitId = _commitId;
        this._TimeStamp = _TimeStamp;
        this._Message = _Message;
        this._ParentCommitID = _ParentCommitID;
        this.blobs = blobs;
    }
}
