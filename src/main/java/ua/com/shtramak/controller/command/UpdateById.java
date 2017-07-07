package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.DataSet;
import ua.com.shtramak.view.View;

public class UpdateById implements Command {

    private DataBaseManager dataBaseManager;
    private View view;

    public UpdateById(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public String description() {
        final String LINE_SEPARATOR = System.lineSeparator();
        return "\tupdate|tableName" +
                LINE_SEPARATOR +
                "\t\tupdate entry in selected table using own command interface";
    }

    @Override
    public boolean isDetected(String command) {
        return command.startsWith("update|");
    }

    @Override
    public void execute(String command) {
        final String[] COMMANDS_TEMPLATE = "find|tableName".split("\\|");
        if (command.split("\\|").length != COMMANDS_TEMPLATE.length) {
            view.writeln("update command failed because of wrong input. Use 'help' command for details");
            return;
        }
        // TODO Проверить что введенная таблица существует до того как продолжать работу
        view.writeln("Please input colName and its value you want to find for update:");
        view.write("Enter column name: ");
        String colName = view.read();
        view.write("Enter value: ");
        String value = view.read();

        // TODO Проверить введенные данные
        view.writeln("Now please input update data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value#");

        String inputData = view.read();
        while (true) {

            if (inputData.equals("exit")) {
                view.writeln("Update command failed!");
                return;
            }

            if (inputData.split("\\|").length == 0 || inputData.split("\\|").length % 2 == 1) {
                view.writeln("Wrong input! Input must be according to the template described above.");
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

        int tableNameIndex = 1;
        String tableName = command.split("\\|")[tableNameIndex];
        try {
            dataBaseManager.update(tableName, colName, value, updateData);
        } catch (Exception e) {
            String message = "Smth goes wrong... Reason: " + e.getMessage();
            view.writeln(message);
        }
    }
}
