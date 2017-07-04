package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.DataSet;
import ua.com.shtramak.view.View;

public class Insert implements Command {
    private DataBaseManager dataBaseManager;
    private View view;

    public Insert(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public String description() {
        final String LINE_SEPARATOR = System.lineSeparator();
        return "\tinsert|tableName|col1Name|value1|col2Name|value2|...col#Name|value#" +
                LINE_SEPARATOR +
                "\t\tinsert entered data to selected table";
    }

    @Override
    public boolean isDetected(String command) {
        return command.startsWith("insert|");
    }

    @Override
    public void execute(String command) {
        String[] commands = command.split("\\|");
        int commandSize = commands.length % 2;
        if (commandSize == 1) {
            view.write("'insert' command failed because of wrong input: incorrect number of elements. Use 'help' command for details");
            return;
        }

        DataSet insertData = new DataSet();
        for (int i = 2; i < commands.length; i++) {
            insertData.put(commands[i],commands[++i]);
        }

        int tableNameIndex = 1;
        String tableName = commands[tableNameIndex];
        try {
            dataBaseManager.insert(tableName, insertData);
        } catch (Exception e) {
            view.write("Entered data cannot be inserted to the table because of wrong format. Use 'help' command for details");
            return;
        }
        view.write("Data successfully added to the current table");
    }
}
