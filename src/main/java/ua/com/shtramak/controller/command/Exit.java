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
        final String LINE_SEPARATOR = System.lineSeparator();
        return "\texit" +
                LINE_SEPARATOR +
                "\t\tto exit from this session";
    }

    @Override
    public void execute(String command) {
        dataBaseManager.disconnect();
        view.write("Good Luck!");
    }
}
