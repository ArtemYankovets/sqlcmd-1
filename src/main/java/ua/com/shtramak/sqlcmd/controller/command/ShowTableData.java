package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.DataSet;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.utils.Commands;
import ua.com.shtramak.sqlcmd.view.View;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ShowTableData extends AbstractCommand {
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
        try {
            if (!dataBaseManager.hasTable(tableName)) {
                view.write(String.format("Table %s doesn't exists! Available tables: ", tableName));
                view.writeln(dataBaseManager.getTableNames().toString());
                return;
            }
            printTableData(tableName);
        } catch (NotExecutedRequestException e) {
            view.writeln(e.getMessage());
        }
    }

    private void printTableData(String tableName) throws NotExecutedRequestException {
        List<DataSet> tableData = dataBaseManager.getTableData(tableName);
        Set<String> tableColumns = dataBaseManager.getTableColumns(tableName);
        printFormattedRow(tableColumns);

        if (tableData.isEmpty()) {
            view.writeln("----------------------------------------");
            view.write("The table is empty. Use 'insert' command for data insertion" + System.lineSeparator());
            return;
        }

        for (DataSet tableItem : tableData) {
            printFormattedRow(tableItem.stringValues());
        }
        view.writeln("----------------------------------------");
    }

    private <T> void printFormattedRow(Collection<T> data) {
        if (data == null) {
            view.writeln("Nothing to show! No data found");
            return;
        }

        String row = "|";
        view.writeln("----------------------------------------");
        for (T rowItem : data) {
            row += String.format(" %-10s |", rowItem);
        }
        view.writeln(row);
    }
}
