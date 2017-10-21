package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.view.View;

public class ShowTablesList extends AbstractCommand {
    public ShowTablesList(DataBaseManager dataBaseManager, View view) {
        super(CommandType.SHOW_TABLES_LIST);
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public void execute(String command) {
        try {
            if (dataBaseManager.getTableNames() != null)
                view.writeln("Here's the names of available tables: " + dataBaseManager.getTableNames());
            else view.writeln("Database is empty. Nothing to show");
        } catch (NotExecutedRequestException e) {
            view.writeln(e.getMessage());
        }
    }
}
