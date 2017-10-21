package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.view.View;

public class Exit extends AbstractCommand {
    public Exit(DataBaseManager dataBaseManager, View view) {
        super(CommandType.EXIT);
        this.dataBaseManager = dataBaseManager;
        this.view = view;
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
