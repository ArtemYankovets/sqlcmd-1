package ua.com.shtramak.util;

public class Commands {

    private static String splitter = "\\|";

    public static int sizeOf(String line) {
        return line.split(splitter).length;
    }

    public static String[] arrayOf(String line) {
        return line.split(splitter);
    }

    public boolean isExit(String command) {
        return command.toLowerCase().equals("exit");
    }

/*
    public static void setSplitter(String splitter) {
        Commands.splitter = splitter;
    }
*/
}
