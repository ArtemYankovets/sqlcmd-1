package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.DataSet;
import ua.com.shtramak.view.View;

import java.util.Arrays;

public class Find implements Command {

    private DataBaseManager dataBaseManager;
    private View view;

    public Find(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public String description() {
        final String LINE_SEPARATOR = System.lineSeparator();
        return "\tfind|tableName" +
                LINE_SEPARATOR +
                "\t\tdisplay available tables in selected database";
    }

    @Override
    public boolean isDetected(String command) {
        return command.startsWith("find|");
    }

    @Override
    public void execute(String command) {

        int tableNameIndex = 1;
        String tableName = command.split("\\|")[tableNameIndex];
        boolean fakeName = true;

        for (String name : dataBaseManager.getTableNames()) {
            if (name.equals(tableName)) fakeName = false;
        }

        if (fakeName) {
            view.write("Wrong table name! Check the list below to see if your table exists");
            view.write("List with available tables: " + Arrays.toString(dataBaseManager.getTableNames()));
            return;
        }

        printTableData(tableName);
    }

    private void printTableData(String tableName) {
        String[] tableColumns = dataBaseManager.getTableColumns(tableName);
        printFormattedRow(tableColumns);
        DataSet[] tableData = dataBaseManager.getTableData(tableName);
        for (DataSet tableItem : tableData) {
            printFormattedRow(tableItem.getStringValues());
        }
        if (tableColumns != null)
            view.write("----------------------------------------");
    }

    private void printFormattedRow(String[] dataArray) {
        if (dataArray == null) {
            view.write("Nothing to show! No data found. First insert data to the table using 'insert' command");
            return;
        }

        String row = "|";
        view.write("----------------------------------------");
        for (String rowItem : dataArray) {
            row += String.format(" %-10s |", rowItem);
        }
        view.write(row);

    }
}
