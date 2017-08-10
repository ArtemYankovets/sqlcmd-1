package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.DataSet;
import ua.com.shtramak.util.Commands;
import ua.com.shtramak.view.View;

import java.util.Arrays;

public class ShowTableData implements Command {

    private DataBaseManager dataBaseManager;
    private View view;

    public ShowTableData(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public String description() {
        return "\tshow|tableName" +
                System.lineSeparator() +
                "\t\tdisplay table data from selected database";
    }

    @Override
    public boolean isDetected(String command) {
        return command.startsWith("show|");
    }

    @Override
    public void execute(String command) {

        int tableNameIndex = 1;
        String tableName = Commands.arrayOf(command)[tableNameIndex];

        if (!dataBaseManager.hasTable(tableName)) {
            view.write(String.format("Table %s doesn't exists! Available tables: ", tableName));
            view.writeln(Arrays.toString(dataBaseManager.getTableNames()));
            return;
        }

        printTableData(tableName);
    }

    private void printTableData(String tableName) {
        String[] tableColumns = dataBaseManager.getTableColumns(tableName);
        printFormattedRow(tableColumns);
        DataSet[] tableData = dataBaseManager.getTableData(tableName);
        if (tableData.length == 0) {
            view.writeln("----------------------------------------");
            view.write("The table is empty. Use 'insert' command for data insertion"+System.lineSeparator());
            return;
        }
        for (DataSet tableItem : tableData) {
            printFormattedRow(tableItem.stringValues());
        }
        view.writeln("----------------------------------------");
    }

    private void printFormattedRow(String[] dataArray) {
        if (dataArray == null) {
            view.writeln("Nothing to show! No data found");
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
