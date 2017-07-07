package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

import java.util.Arrays;

public class List implements Command {
    private DataBaseManager dataBaseManager;
    private View view;

    public List(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public String description() {
        return "\tlist" +
                System.lineSeparator() +
                "\t\tdisplay available tables in selected database";
    }

    @Override
    public boolean isDetected(String command) {
        return command.equals("list");
    }

    @Override
    public void execute(String command) {
        view.write("Here's the names of available tables: " + Arrays.toString(dataBaseManager.getTableNames()));
    }
}
