package ua.com.shtramak.controller;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.Console;
import ua.com.shtramak.view.View;

public class MainController {

    private View view;
    private DataBaseManager dataBaseManager;

    public MainController(View view, DataBaseManager dataBaseManager) {
        this.view = view;
        this.dataBaseManager = dataBaseManager;
    }

    public void run() {
        View view = new Console();
        view.write("Hello, user! For connecting to database please enter required input data using next format:\n" +
                "database|userName|password");

        connectToDataBase();
    }

    private void connectToDataBase() {
        // sqlcmd|shtramak|qqq or sqlcmd|postgres|postgres
        boolean connect = false;
        while (!connect) {
            try {
                String userInput = view.read();
                String[] inputData = userInput.split("\\|");
                if (inputData.length != 3) {
                    String message = String.format("Incorrect input data. Number of recieved elements is %d instead of 3", inputData.length);
                    message += "\nPlease enter required input data in format: database|userName|password";
                    throw new IllegalArgumentException(message);
                }
                String database = inputData[0];
                String userName = inputData[1];
                String password = inputData[2];
                dataBaseManager.connect(database, userName, password);
                connect = true;
            } catch (Exception e) {
                printError(e);
                if (e.getClass().getSimpleName().equals("UnsupportedOperationException"))
                    System.exit(0);
                view.write("Try again!");
            }
        }
    }

    private void printError(Exception e) {
        view.write("Connection failed!");
        String reason = e.getMessage();
        if (e.getCause() != null)
            reason +=e.getCause().getMessage();
        view.write("Reason: " + reason);
    }
}
