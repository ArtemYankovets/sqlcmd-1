package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

public class CommandsStorage {
    private DataBaseManager dataBaseManager;
    private View view;

    public CommandsStorage(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    public Command[] commandsList() {
        return new Command[]{new ConnectToDB(dataBaseManager, view),
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
