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
    public boolean isDetected(String command) {
        return command.startsWith("update|");
    }

    @Override
    public void execute(String command) {
        final String[] COMMANDS_TEMPLATE = "find|tableName".split("\\|");
        if (command.split("\\|").length != COMMANDS_TEMPLATE.length) {
            view.write("update command failed because of wrong input. Use 'help' command for details");
            return;
        }

        view.write("Please input existing id number you want to update. Must be integer:");
        int id;
        while (true) {
            try {
                id = Integer.parseInt(view.read());
                break;
            } catch (NumberFormatException e) {
                view.write("The entered value is not an integer! Enter an integer value:");
            }
        }

        view.write("Now please input update data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value#");

        String inputData = view.read();
        while (true) {

            if (inputData.equals("exit")) {
                view.write("Update command failed!");
                return;
            }

            if (inputData.split("\\|").length == 0 || inputData.split("\\|").length % 2 == 1) {
                view.write("Wrong input! Input must be according to the template described above.");
                view.write("Try again using correct format col1Name|value1|col2Name|value2|...col#Name|value# or enter 'exit' command");
                inputData = view.read();
            } else {
                break;
            }
        }
        String[] commands = inputData.split("\\|");

        DataSet updateData = new DataSet();
        for (int i = 0; i < commands.length / 2; i++) {
            updateData.put(commands[i], commands[++i]);
        }

        String tableName = command.split("\\|")[1];
        try {
            dataBaseManager.updateById(tableName, id, updateData);
        } catch (Exception e) {
            String message = "Smth goes wrong... Reason: " + e.getMessage();
            view.write(message);
        }
    }
}
