package ua.com.shtramak.utils;

import ua.com.shtramak.view.View;

public class Commands {

    private static String splitter = "\\|";

    public static int sizeOf(String line) {
        return line.split(splitter).length;
    }

    public static String[] arrayOf(String line) {
        return line.split(splitter);
    }

    public static boolean isExit(String command) {
        return command.toLowerCase().equals("exit");
    }

    public static boolean isSureInActingWithTable(String tableName, String message, View view){
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
/*
    public static void setSplitter(String splitter) {
        Commands.splitter = splitter;
    }
*/
}
