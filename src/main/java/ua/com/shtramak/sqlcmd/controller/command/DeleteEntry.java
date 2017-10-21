package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.utils.Commands;
import ua.com.shtramak.sqlcmd.view.View;

public class DeleteEntry extends AbstractCommand {
    public DeleteEntry(DataBaseManager dataBaseManager, View view) {
        super(CommandType.DELETE_ENTRY);
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public void execute(String command) {
        if (!isValidCommand(command)) return;

        try {
            String[] inputCommands = Commands.arrayOf(command);
            int tableNameIndex = 1;
            String tableName = inputCommands[tableNameIndex];
            if (!isAcceptableTableName(tableName)) return;

            view.writeln("Please input wanted 'colName' and 'value' of the row you want to update:");
            view.write("Enter column name: ");

            String colName = view.read();
            if (!isAcceptableColumnName(tableName, colName)) return;

            view.writeln("");
            view.write("Enter value: ");
            String value = view.read();
            view.writeln("");
            if (!isAcceptableColumnValue(tableName, colName, value)) return;

            dataBaseManager.deleteTableData(tableName, colName, value);
            view.writeln("Entry successfully deleted...");
        } catch (NotExecutedRequestException e) {
            view.writeln(e.getMessage());
        }
    }

    private boolean isAcceptableColumnValue(String tableName, String colName, String value) throws NotExecutedRequestException {
        if (!dataBaseManager.hasValue(tableName, colName, value)) {
            view.writeln(String.format("There's no value '%s' in column '%s'", value, colName));
            return false;
        }
        return true;
    }

    private boolean isAcceptableColumnName(String tableName, String colName) throws NotExecutedRequestException {
        if (!dataBaseManager.hasColumn(tableName, colName)) {
            view.writeln(String.format("Column '%s' doesn't exists! See below the list with available columns of table %s:", colName, tableName));
            view.writeln("Available columns: " + dataBaseManager.getTableColumns(tableName));
            return false;
        }
        return true;
    }

    private boolean isAcceptableTableName(String tableName) throws NotExecutedRequestException {
        if (!dataBaseManager.hasTable(tableName)) {
            view.writeln(String.format("Table '%s' doesn't exists! See below the list with available tables:", tableName));
            view.writeln("Available tables: " + dataBaseManager.getTableNames());
            return false;
        }
        return true;
    }
}