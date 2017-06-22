package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.DataSet;
import ua.com.shtramak.view.View;

public class UpdateById implements Command {

    DataBaseManager dataBaseManager;
    View view;
    String command;

    public UpdateById(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public boolean isDetected(String command) {
        this.command = command;
        return command.startsWith("update|");
    }

    @Override
    public void execute() {
        if (command.split("\\|").length != 2) {
            view.write("update command failed because of wrong input. Use 'help' command for details");
            return;
        }

        view.write("Please input existing id number you want to update. Must be integer:");
        int id = Integer.parseInt(view.read());

        view.write("Now please input update data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value#");

        String inputData = view.read();
        while (true) {

            if (inputData.equals("exit")) {
                view.write("Update command failed!");
                return;
            }

            if (inputData.split("\\|").length == 0 || inputData.split("\\|").length % 2 == 1) {
                view.write("Wrong input! Intput must be according to the template described above.");
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
        dataBaseManager.updateById(tableName, id, updateData);
    }
}
