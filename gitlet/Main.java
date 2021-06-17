package gitlet;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


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

    public static void main(String... args) throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        // FILL THIS IN

        String cwd = System.getProperty("user.dir");
        Repo repo = null;
        boolean valid;

        if (args.length == 0) {
            emptyCommand(args);
            return;
        }

        String commandPath = Class.forName("gitlet.commands.add").getResource("").getPath();
        valid = vaildCommand(commandPath, args);

        if (!valid) return;

        if (checkRepo(cwd)) {
            if (args[0].equals("init")) {
                System.out.println("A gitlet version-control system already exists in the current directory.");
                return;
            }
            repo = getRepo(cwd + File.separator + ".gitlet" + File.separator + "REPO");
            command(repo, args);
        } else if (!args[0].equals("init")) {
            Utils.error("The gitlet repository have not been created!");
        } else {
            gitlet.commands.init.init(cwd);
        }
    }

    public static void command(Repo repo, String... args) throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        repo.command(args);
    }

    public static void emptyCommand(String... args) {
        Utils.error("The COMMAND is EMPTY!");
    }

    public static Boolean vaildCommand(String path, String... args) {
        String mycommand = args[0] + ".java";
        List<String> commands = Utils.plainFilenamesIn(new File(path));
        for (String command : commands) {
            if (mycommand.equals(command)) return true;
        }
        Utils.error("The command is invalid");
        return false;
    }

    public static Repo getRepo(String path) {
        return (Repo) Utils.deserialize(new File(path).toPath());
    }

    public static boolean checkRepo(String cwd) {
        String[] files = new File(cwd).list();
        if (files == null) return false;
        for (String filename : files) {
            if (filename.equals(".gitlet")) return true;
        }
        return false;
    }

}
