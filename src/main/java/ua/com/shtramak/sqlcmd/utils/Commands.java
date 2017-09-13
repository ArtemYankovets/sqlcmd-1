package ua.com.shtramak.sqlcmd.utils;

import ua.com.shtramak.sqlcmd.view.View;

public class Commands {

    public static int sizeOf(String line) {
        return line.split("\\|").length;
    }

    public static String[] arrayOf(String line) {
        return line.split("\\|");
    }

    public static boolean isExit(String command) {
        return command.toLowerCase().equals("exit");
    }

    public static boolean isSureInActingWithTable(String message, View view){
        view.writeln(message);
        while (true) {
            String answer = view.read().toLowerCase();
            if (answer.equals("yes")||answer.equals("y"))
                return true;
            if (answer.equals("no")||answer.equals("n"))
                return false;
            else
                view.writeln("Please enter Yes or No. No other options available");
        }
    }
}
