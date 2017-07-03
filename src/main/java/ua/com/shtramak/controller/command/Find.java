package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.DataSet;
import ua.com.shtramak.view.View;

import java.util.Arrays;

public class Find implements Command {

    DataBaseManager dataBaseManager;
    View view;

    public Find(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public boolean isDetected(String command) {
        if (!command.startsWith("find|")) return false;

        return true;
    }

    @Override
    public void execute(String command) {
        String tableName = command.split("\\|")[1];
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
            view.write("Nothig to show! No data found. First insert data to the table using 'insert' command");
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
