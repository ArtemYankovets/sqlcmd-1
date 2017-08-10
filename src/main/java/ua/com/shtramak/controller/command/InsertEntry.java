package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.DataSet;
import ua.com.shtramak.utils.Commands;
import ua.com.shtramak.view.View;

public class InsertEntry implements Command {
    private DataBaseManager dataBaseManager;
    private View view;

    public InsertEntry(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public String description() {
        return "\tinsert|tableName|col1Name|value1|col2Name|value2|...col#Name|value#" +
                System.lineSeparator() +
                "\t\tinsert entered data to selected table";
    }

    @Override
    public boolean isDetected(String command) {
        return command.startsWith("insert|");
    }

    @Override
    public void execute(String command) {
        String[] commands = Commands.arrayOf(command);
        int commandsSize = Commands.sizeOf(command);
        if (commandsSize % 2 == 1) {
            view.writeln("'insert' command failed because of wrong input: incorrect number of elements. Use 'help' command for details");
            return;
        }

        DataSet insertData = new DataSet();
        int firstCommandIndex = 2;
        for (int i = firstCommandIndex; i < commandsSize; i++) {
            insertData.put(commands[i], commands[++i]);
        }

        int tableNameIndex = 1;
        String tableName = commands[tableNameIndex];
        try {
            dataBaseManager.insert(tableName, insertData);
            view.writeln(String.format("Data successfully added to %s", tableName));
        } catch (RuntimeException e) {
            view.writeln(e.getMessage());
        }
    }
}