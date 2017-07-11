package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.DataSet;
import ua.com.shtramak.util.Commands;
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
        return "\tfind|tableName" +
                System.lineSeparator() +
                "\t\tdisplay available tables in selected database";
    }

    @Override
    public boolean isDetected(String command) {
        return command.startsWith("find|");
    }

    @Override
    public void execute(String command) {

        int tableNameIndex = 1;
        String tableName = Commands.arrayOf(command)[tableNameIndex];

        if (!dataBaseManager.hasTable(tableName)) { //TODO утилитный клас под методы в командах
            view.writeln(String.format("Table %s doesn't exists! See the list with available tables below:", tableName));
            view.writeln("List with available tables: " + Arrays.toString(dataBaseManager.getTableNames()));
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
            view.writeln("----------------------------------------");
    }

    private void printFormattedRow(String[] dataArray) {
        if (dataArray == null) {
            view.writeln("Nothing to show! No data found. First insert data to the table using 'insert' command");
            return;
        }

        String row = "|";
        view.writeln("----------------------------------------");
        for (String rowItem : dataArray) {
            row += String.format(" %-10s |", rowItem);
        }
        view.writeln(row);

    }
}
