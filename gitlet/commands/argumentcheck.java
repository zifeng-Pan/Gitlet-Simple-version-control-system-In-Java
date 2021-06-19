package gitlet.commands;

/**
 * Created with IntelliJ IDEA.
 * Copyright@:
 *
 * @Author: Pan Zifeng
 * @Date: 2021/06/18/10:45
 * @Description:
 */
public class argumentcheck {
    public static boolean argumentCheck(int number,String info,String... args){
        if(args.length != number){
            System.out.println("Wrong number of arguments");
            System.out.println("-------HELP INFO------");
            System.out.println(info);
            return false;
        }
        return true;
    }
}
