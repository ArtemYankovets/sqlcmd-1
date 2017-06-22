package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

public class Clear implements Command {

    DataBaseManager dataBaseManager;
    View view;
    String command;

    public Clear(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public boolean isDetected(String command) {
        this.command = command;
        return command.startsWith("clear|");
    }

    @Override
    public void execute() {
        if (command.split("\\|").length != 2) {
            view.write("clear command failed because of wrong input. Use 'help' command for details");
            return;
        }

        String tableName = command.split("\\|")[1];
        if (isSure(tableName))
            dataBaseManager.clear(tableName);
        else
            view.write("Don't worry! Data is in safe");
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
