package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.DataSet;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.utils.Commands;
import ua.com.shtramak.sqlcmd.view.View;

public class UpdateTableData extends AbstractCommand {
    public UpdateTableData(DataBaseManager dataBaseManager, View view) {
        super(CommandType.UPDATE_TABLE_DATA);
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public void execute(String command) {
        if (!isValidCommand(command)) return;
        ;

        final String[] commandsTemplate = Commands.arrayOf(CommandType.UPDATE_TABLE_DATA.template());
        String[] inputCommands = Commands.arrayOf(command);
        if (inputCommands.length != commandsTemplate.length) {
            view.writeln("updateTableData command failed because of wrong input. Use 'help' command for details");
            return;
        }

        try {
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

            view.writeln("Now please input updateTableData data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value# or exit");
            String inputData = readUpdateData();

            if (inputData.equals("exit")) return;

            String[] commands = Commands.arrayOf(inputData);
            DataSet updateData = new DataSet();
            for (int i = 0; i < commands.length; i++) {
                updateData.put(commands[i], commands[++i]);
            }

            dataBaseManager.updateTableData(tableName, colName, value, updateData);
            view.writeln("Data successfully updated...");
        } catch (NotExecutedRequestException e) {
            view.writeln(e.getMessage());
        }
    }

    private String readUpdateData() {
        String inputData;
        while (true) {
            inputData = view.read();
            if (inputData.equals("exit")) {
                view.writeln("Update command failed!");
                break;
            }

            if (inputData.split("\\|").length == 0 || inputData.split("\\|").length % 2 == 1) {
                view.writeln("Wrong input! Input must be according to the template");
                view.writeln("Try again using correct format col1Name|value1|col2Name|value2|...col#Name|value# or enter 'exit' command");
            } else {
                break;
            }
        }

        return inputData;
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