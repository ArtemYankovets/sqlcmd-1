package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.view.View;

public class Exit extends AbstractCommand {
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
        try {
            dataBaseManager.disconnect();
            view.writeln("Good Luck!");
        } catch (NotExecutedRequestException e) {
            view.writeln(e.getMessage());
        }
    }
}