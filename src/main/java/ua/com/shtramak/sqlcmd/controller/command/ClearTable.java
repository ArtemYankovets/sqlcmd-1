package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.utils.Commands;
import ua.com.shtramak.sqlcmd.view.View;

public class ClearTable extends AbstractCommand {
    public ClearTable(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public String description() {
        return ComandType.CLEAR_TABLE.description();
    }

    @Override
    public boolean isDetected(String command) {
        return command.startsWith(ComandType.CLEAR_TABLE.getName());
    }

    @Override
    public void execute(String command) {
        final String[] commandsTemplate = Commands.arrayOf(ComandType.CLEAR_TABLE.template());
        String[] commands = Commands.arrayOf(command);
        if (commands.length != commandsTemplate.length) {
            view.writeln("'clear' command failed because of wrong input. Use 'help' command for details");
            return;
        }

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