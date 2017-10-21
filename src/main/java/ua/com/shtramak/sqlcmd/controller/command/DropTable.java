package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.utils.Commands;
import ua.com.shtramak.sqlcmd.view.View;

public class DropTable extends AbstractCommand {
    public DropTable(DataBaseManager dataBaseManager, View view) {
        super(CommandType.DROP_TABLE);
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
                view.writeln(String.format("Table %s doesn't exists! See the list with available tables below:", tableName));
                view.writeln("ShowTablesList with available tables: " + dataBaseManager.getTableNames());
                return;
            }

            String message = String.format("You are going to drop existing table '%s'! Are you sure? [Yes/No]", tableName);
            if (Commands.isSureInActingWithTable(message, view)) {
                dataBaseManager.dropTable(tableName);
                view.writeln(String.format("Table %s was successfully dropped from database", tableName));
                return;
            }
            view.writeln("Command 'drop' was canceled...");
        } catch (NotExecutedRequestException e) {
            view.writeln(e.getMessage());
        }
    }
}
