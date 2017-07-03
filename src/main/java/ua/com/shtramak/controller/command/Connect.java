package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

public class Connect implements Command{

    private DataBaseManager dataBaseManager;
    private View view;

    public Connect(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
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
            String[] inputData = command.split("\\|");
            if (inputData.length != 4) {
                view.write("Connection failed!");
                String message = "Incorrect input. Please enter required input data in format: connect|database|userName|password";
                throw new IllegalArgumentException(message);
            }
            String database = inputData[1];
            String userName = inputData[2];
            String password = inputData[3];
            dataBaseManager.connect(database, userName, password);
        } catch (Exception e) {
            printError(e);
            if (e.getClass().getSimpleName().equals("UnsupportedOperationException")) return;
            view.write("Try again!");
            view.write("");
        }
    }

    private void printError(Exception e) {
        String reason = e.getMessage();
        if (e.getCause() != null)
            reason += e.getCause().getMessage();
        view.write("Error message: " + reason);
    }
}
