package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.utils.Commands;
import ua.com.shtramak.sqlcmd.view.View;

public class ClearTable extends AbstractCommand {
    public ClearTable(DataBaseManager dataBaseManager, View view) {
        super(CommandType.CLEAR_TABLE);
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public void execute(String command) {
        if (!isValidCommand(command)) return;

        String[] commands = Commands.arrayOf(command);
        int tableNameIndex = 1;
        String tableName = commands[tableNameIndex];

        try {
            if (!dataBaseManager.hasTable(tableName)) {
                view.writeln(String.format("Table %s doesn't exists! See the list with available tables: %s", tableName, dataBaseManager.getTableNames()));
                return;
            }

            String message = String.format("You are going to delete all data from table '%s'! Are you sure? [Yes/No]", tableName);
            if (Commands.isSureInActingWithTable(message, view)) {
                dataBaseManager.clear(tableName);
                view.writeln(String.format("Data from '%s' was successfully deleted", tableName));
                return;
            }
            view.writeln("Command 'clear' was canceled...");
        } catch (NotExecutedRequestException e) {
            view.writeln(e.getMessage());
        }
    }
}