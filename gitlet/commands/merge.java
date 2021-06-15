package gitlet.commands;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/13/19:58
 * @Description:
 */
public class merge {
    public void merge(String branch, String brnanch) {
    }

    private void mergeSame() {
    } // do nothing if the branchname as same as the branch that HEAD points

    private void mergeFastForward() {
    } // FastForward merge

    private void realMerge() {
    } // merge two branch and create a new commit

    private void dealWithAbsent() {
    } // deal with the problem of file removing

    private void dealWithModify() {
    } // deal with the problem of file modify

    private void dealWithConflict() {
    } // deal with the problem of file contents conflict

    private void trackSplitPoint() {
    } // track the split point
}
