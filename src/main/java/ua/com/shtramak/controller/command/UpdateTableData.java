package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.DataSet;
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
        return "\tupdate|tableName" +
                System.lineSeparator() +
                "\t\tupdate entry in selected table using own command interface";
    }

    @Override
    public boolean isDetected(String command) {
        return command.startsWith("update|");
    }

    @Override
    public void execute(String command) {
        final String[] commandsTemplate = "find|tableName".split("\\|");
        String[] inputCommands = command.split("\\|");
        if (inputCommands.length != commandsTemplate.length) {
            view.writeln("update command failed because of wrong input. Use 'help' command for details");
            return;
        }

        int tableNameIndex = 1;
        String tableName = inputCommands[tableNameIndex];
        if (!isAcceptableTableName(tableName)) return;

        view.writeln("Please input wanted colName and value of the row you want to update:");
        view.write("Enter column name: ");
        String colName = view.read();
        view.write("Enter value: ");
        String value = view.read();

        if (!isAcceptableColumnName(tableName, colName)) return;

        view.writeln("Now please input update data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value# or exit");

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
            dataBaseManager.update(tableName, colName, value, updateData);
            view.writeln("Data successfully updated...");
        } catch (Exception e) {
            String message = "Smth goes wrong... Reason: " + e.getMessage();
            view.writeln(message);
        }
    }

    private boolean isAcceptableColumnName(String tableName, String colName) {
        for (String element : dataBaseManager.getTableColumns(tableName)) {
            if (element.equals(colName)) return true;
        }
            view.writeln(String.format("Column '%s' doesn't exists! See below the list with available columns of table %s:",colName, tableName));
            view.writeln("Available columns: " + Arrays.toString(dataBaseManager.getTableColumns(tableName)));
        return false;
    }

    private boolean isAcceptableTableName(String tableName) {
        if (!dataBaseManager.tableExists(tableName)) {
            view.writeln(String.format("Table '%s' doesn't exists! See below the list with available tables:", tableName));
            view.writeln("Available tables: " + Arrays.toString(dataBaseManager.getTableNames()));
            return false;
        }
        return true;
    }
}
