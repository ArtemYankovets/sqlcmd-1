package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.DataSet;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.utils.Commands;
import ua.com.shtramak.sqlcmd.utils.TableFormatter;
import ua.com.shtramak.sqlcmd.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShowTableData extends AbstractCommand {
    public ShowTableData(DataBaseManager dataBaseManager, View view) {
        super(CommandType.SHOW_TABLE_DATA);
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public void execute(String command) {
        if (!isValidCommand(command)) return;

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

        try {
            if (tableData.isEmpty()) {
                Set<String> headers = dataBaseManager.getTableColumns(tableName);
                view.writeln(TableFormatter.formattedTableRow(headers, headers.size()));
                return;
            }

            int numOfColumns = tableData.get(0).size();
            List<String> strTableData = dataSetListToString(tableData);
            String result = TableFormatter.formattedTableRow(strTableData, numOfColumns);
            view.writeln(result);
        } catch (IOException e) {
            String message = "Table data was not retrieved because of:" + e.getMessage();
            throw new NotExecutedRequestException(message);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> dataSetListToString(List<DataSet> dataSets) {
        List<String> result = new ArrayList<>();
        result.addAll(dataSets.get(0).names());
        for (DataSet dataSet : dataSets) {
            List tmp = dataSet.values();
            result.addAll(tmp);
        }
        return result;
    }

}