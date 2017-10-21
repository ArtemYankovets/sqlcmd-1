package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.utils.Commands;
import ua.com.shtramak.sqlcmd.view.View;

public class CreateTable extends AbstractCommand {
    public CreateTable(DataBaseManager dataBaseManager, View view) {
        super(CommandType.CREATE_TABLE);
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public void execute(String command) {
        if (!isValidCommand(command)) return;

        view.write("Please enter a name of table you want to create: ");
        String tableName = view.read();

        try {
            if (dataBaseManager.hasTable(tableName)) {
                view.writeln("Such table already exists...");
                return;
            }

            view.writeln("Please enter columns data in format: col1Name|dataType1|col2Name|dataType2...col#Name|dataType#");
            String columnsData = view.read();

            if (Commands.sizeOf(columnsData) % 2 != 0) {
                view.writeln(String.format("Wrong input! Number of elements must be even, but entered %s", Commands.sizeOf(columnsData)));
                return;
            }

            dataBaseManager.createTable(tableName, columnsData);
            view.writeln("Table successfully created...");
        } catch (NotExecutedRequestException e) {
            view.writeln(e.getMessage());
        }
    }

}
