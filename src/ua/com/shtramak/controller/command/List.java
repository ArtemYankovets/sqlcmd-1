package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

import java.util.Arrays;

public class List implements Command {
    DataBaseManager dataBaseManager;
    View view;

    public List(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public boolean isDetected(String command) {
        return command.equals("list");
    }

    @Override
    public void execute() {
        view.write("Here's the names of available tables: " + Arrays.toString(dataBaseManager.getTableNames()));
    }
}
