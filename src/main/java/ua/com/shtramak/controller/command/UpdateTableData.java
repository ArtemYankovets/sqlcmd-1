package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.DataSet;
import ua.com.shtramak.util.Commands;
import ua.com.shtramak.view.View;

import java.util.Arrays;

public class UpdateTableData implements Command {

    private DataBaseManager dataBaseManager;
    private View view;

    public UpdateTableData(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public String description() {
        return "\tupdateTableData|tableName" +
                System.lineSeparator() +
                "\t\tupdate entry in selected table using own command interface";
    }

    @Override
    public boolean isDetected(String command) {
        return command.startsWith("updateTable|");
    }

    @Override
    public void execute(String command) {
        final String[] commandsTemplate = Commands.arrayOf("find|tableName");
        String[] inputCommands = Commands.arrayOf(command);
        if (inputCommands.length != commandsTemplate.length) {
            view.writeln("updateTableData command failed because of wrong input. Use 'help' command for details");
            return;
        }

        int tableNameIndex = 1;
        String tableName = inputCommands[tableNameIndex];
        if (!isAcceptableTableName(tableName)) return;

        view.writeln("Please input wanted 'colName' and 'value' of the row you want to updateTableData:");
        view.write("Enter column name: ");

        String colName = view.read();
        if (!isAcceptableColumnName(tableName, colName)) return;

        view.write("Enter value: ");
        String value = view.read();
        if(!isAcceptableColumnValue(tableName, colName, value)) return;

        view.writeln("Now please input updateTableData data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value# or exit");

        String inputData = view.read();
        while (true) {

            if (inputData.equals("exit")) {
                view.writeln("Update command failed!");
                return;
            }

            if (inputData.split("\\|").length == 0 || inputData.split("\\|").length % 2 == 1) {
                view.writeln("Wrong input! Input must be according to the template");
                view.writeln("Try again using correct format col1Name|value1|col2Name|value2|...col#Name|value# or enter 'exit' command");
                inputData = view.read();
            } else {
                break;
            }
        }
        String[] commands = inputData.split("\\|");

        DataSet updateData = new DataSet();
        for (int i = 0; i <= commands.length / 2; i++) {
            updateData.put(commands[i], commands[++i]);
        }

        try {
            dataBaseManager.updateTableData(tableName, colName, value, updateData);
            view.writeln("Data successfully updated...");
        } catch (Exception e) {
            String message = "Smth goes wrong... Reason: " + e.getMessage();
            view.writeln(message);
        }
    }

    private boolean isAcceptableColumnValue(String tableName, String colName, String value) {
        if(!dataBaseManager.hasValue(tableName,colName,value)){
            view.writeln(String.format("There's no value '%s' in column '%s')",value, colName));
            return false;
        }
        return  true;
    }

    private boolean isAcceptableColumnName(String tableName, String colName) {
        if (!dataBaseManager.hasColumn(tableName, colName)) {
            view.writeln(String.format("Column '%s' doesn't exists! See below the list with available columns of table %s:", colName, tableName));
            view.writeln("Available columns: " + Arrays.toString(dataBaseManager.getTableColumns(tableName)));
            return false;
        }
        return true;
    }

    private boolean isAcceptableTableName(String tableName) {
        if (!dataBaseManager.hasTable(tableName)) {
            view.writeln(String.format("Table '%s' doesn't exists! See below the list with available tables:", tableName));
            view.writeln("Available tables: " + Arrays.toString(dataBaseManager.getTableNames()));
            return false;
        }
        return true;
    }
}
