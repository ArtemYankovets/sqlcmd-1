package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

import java.util.Arrays;

public class ShowTablesList extends AbstractCommand {
    public ShowTablesList(DataBaseManager dataBaseManager, View view) {
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
        if (dataBaseManager.getTableNames() != null)
            view.writeln("Here's the names of available tables: " + Arrays.toString(dataBaseManager.getTableNames()));
        else view.writeln("Database is empty. Nothing to show");
    }
}
