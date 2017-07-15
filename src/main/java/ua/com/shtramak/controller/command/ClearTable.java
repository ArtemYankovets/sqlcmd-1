package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.util.Commands;
import ua.com.shtramak.view.View;

import java.util.Arrays;

public class ClearTable implements Command {

    private DataBaseManager dataBaseManager;
    private View view;

    public ClearTable(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public String description() {
        return "\tclear|tableName" +
                System.lineSeparator() +
                "\t\tdelete all data from selected table";
    }

    @Override
    public boolean isDetected(String command) {
        return command.startsWith("clear|");
    }

    @Override
    public void execute(String command) {
        final String[] commandsTemplate = Commands.arrayOf("clear|tableName");
        String[] commands = Commands.arrayOf(command);
        if (commands.length != commandsTemplate.length) {
            view.writeln("'clear' command failed because of wrong input. Use 'help' command for details");
            return;
        }

        int tableNameIndex = 1;
        String tableName = commands[tableNameIndex];

        if (!dataBaseManager.hasTable(tableName)) {
            view.writeln(String.format("Table %s doesn't exists! See the list with available tables below:", tableName));
            view.writeln("List with available tables: " + Arrays.toString(dataBaseManager.getTableNames()));
            return;
        }

        if (!isSure(tableName)) {
            view.writeln("Command 'clear' was canceled...");
            return;
        }

            dataBaseManager.clear(tableName);
            view.writeln(String.format("Data from '%s' was successfully deleted", tableName));
    }

    private boolean isSure(String tableName) {
        view.writeln(String.format("You are going to delete all data from table '%s'! Are you sure? [Yes/No]", tableName));
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
