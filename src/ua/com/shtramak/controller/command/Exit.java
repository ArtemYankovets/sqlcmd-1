package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.Console;
import ua.com.shtramak.view.View;

public class Exit implements Command {
    DataBaseManager dataBaseManager;

    public Exit(DataBaseManager dataBaseManager) {
        this.dataBaseManager = dataBaseManager;
    }

    @Override
    public boolean isDetected(String command) {
        return command.equals("exit");
    }

    @Override
    public void execute() {
        dataBaseManager.disconnect();
        new Console().write("Good Luck!");
    }
}
