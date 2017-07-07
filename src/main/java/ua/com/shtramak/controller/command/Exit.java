package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

public class Exit implements Command {
    private DataBaseManager dataBaseManager;
    private View view;

    public Exit(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public boolean isDetected(String command) {
        return command.equals("exit");
    }

    @Override
    public String description() {
        return "\texit" +
                System.lineSeparator() +
                "\t\tto exit from this session";
    }

    @Override
    public void execute(String command) {
        dataBaseManager.disconnect();
        view.writeln("Good Luck!");
    }
}
