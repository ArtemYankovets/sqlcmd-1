package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

public class Clear implements Command {

    private DataBaseManager dataBaseManager;
    private View view;

    public Clear(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public String description() {
        final String LINE_SEPARATOR = System.lineSeparator();
        return "\tclear|tableName" +
                LINE_SEPARATOR +
                "\t\tdelete all data from selected table";
    }

    @Override
    public boolean isDetected(String command) {
        return command.startsWith("clear|");
    }

    @Override
    public void execute(String command) {
        final String[] COMMANDS_TEMPLATE = "clear|tableName".split("\\|");
        String[] commands = command.split("\\|");
        if (commands.length != COMMANDS_TEMPLATE.length) {
            view.write("clear command failed because of wrong input. Use 'help' command for details");
            return;
        }

        int tableNameIndex = 1;
        String tableName = commands[tableNameIndex];
        if (isSure(tableName))
            dataBaseManager.clear(tableName);
        else
            view.write("Don't worry! Data is safe");
    }

    private boolean isSure(String tableName) {
        view.write(String.format("You are going to delete all data from %s! Are you sure? [Yes/No]", tableName));
        while (true) {
            String answer = view.read().toLowerCase();
            if (answer.equals("yes"))
                return true;
            if (answer.equals("no"))
                return false;
            else
                view.write("Please enter Yes or No. No other options available");
        }
    }
}
