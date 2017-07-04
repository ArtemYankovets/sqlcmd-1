package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

public class Connect implements Command {

    private DataBaseManager dataBaseManager;
    private View view;

    public Connect(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public String description() {
        final String LINE_SEPARATOR = System.lineSeparator();
        return "\tconnect|database|userName|password" +
                LINE_SEPARATOR +
                "\t\tconnection with database under userName with password";
    }

    @Override
    public boolean isDetected(String command) {
        if (!command.startsWith("connect|")) return false;

        if (dataBaseManager.isConnected())
            dataBaseManager.disconnect();

        return true;
    }

    @Override
    public void execute(String command) {
        try {
            final String[] COMMANDS_TEMPLATE = "connect|database|userName|password".split("\\|");
            String[] inputData = command.split("\\|");
            if (inputData.length != COMMANDS_TEMPLATE.length) {
                view.write("Connection failed!");
                String message = "Incorrect input. Please enter required input data in format: connect|database|userName|password";
                throw new IllegalArgumentException(message);
            }

            int dbIndex = 1;
            int userNameIndex = 2;
            int userPasswordIndex = 3;
            String database = inputData[dbIndex];
            String userName = inputData[userNameIndex];
            String password = inputData[userPasswordIndex];
            dataBaseManager.connect(database, userName, password);
        } catch (Exception e) {
            printError(e);
            if (e.getClass().getSimpleName().equals("UnsupportedOperationException")) return;
            view.write("Try again!" + System.lineSeparator());
        }
    }

    private void printError(Exception e) {
        String reason = e.getMessage();
        if (e.getCause() != null)
            reason += e.getCause().getMessage();
        view.write("Error message: " + reason);
    }
}
