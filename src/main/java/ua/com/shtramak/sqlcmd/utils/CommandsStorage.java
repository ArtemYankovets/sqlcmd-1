package ua.com.shtramak.sqlcmd.utils;

import ua.com.shtramak.sqlcmd.controller.command.*;
import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.view.View;

public class CommandsStorage {
    private final DataBaseManager dataBaseManager;
    private final View view;

    public CommandsStorage(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    public AbstractCommand[] commandsList() {
        return new AbstractCommand[]{
                new ConnectToDB(dataBaseManager, view),
                new ShowTablesList(dataBaseManager, view),
                new ShowTableData(dataBaseManager, view),
                new ClearTable(dataBaseManager, view),
                new InsertEntry(dataBaseManager, view),
                new UpdateTableData(dataBaseManager, view),
                new CreateTable(dataBaseManager, view),
                new DropTable(dataBaseManager,view),
                new Exit(dataBaseManager, view),
                new Help(dataBaseManager, view)};
    }
}