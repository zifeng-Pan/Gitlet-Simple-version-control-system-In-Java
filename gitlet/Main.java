package gitlet;

import java.io.File;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author: Panzi Feng
 * @Date: 2021/6/12
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */
    public static void main(String... args) {
        // FILL THIS IN
        File dir = new File("E:\\workspace_idea1\\proj2\\gitlet");
        File dump1 = new File(dir, "Dumpable.java");

        String fileHash1 = Utils.sha1(dump1.getName() + Utils.readContentsAsString(dump1));
        String fileHash2 = Utils.sha1(dump1.getName() + Utils.readContentsAsString(dump1));
        System.out.println(fileHash1);
        System.out.println(fileHash2);
    }
}
