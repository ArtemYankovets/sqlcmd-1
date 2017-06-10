package ua.com.shtramak.controller;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.Console;
import ua.com.shtramak.view.View;

import java.util.Arrays;

public class MainController {

    private View view;
    private DataBaseManager dataBaseManager;

    public MainController(View view, DataBaseManager dataBaseManager) {
        this.view = view;
        this.dataBaseManager = dataBaseManager;
    }

    public void run() {
        View view = new Console();
        view.write("Hello, user! For connection to database please enter required input data using next format:\n" +
                "database|userName|password");

        connectToDataBase();

        boolean finished = false;
        while (!finished) {
            view.write("\nType a command or 'help' to see the command list");

            String command = view.read();
            switch (command) {
                case "list":
                    cmdList();
                    break;
                case "help":
                    cmdHelp();
                    break;
                case "exit":
                    finished = true;
                    System.out.println("Good Luck!");
                    break;
                default:
                    System.out.println("No such command found!");
            }
        }
    }


    private void cmdList() {
        view.write("Here's the names of available tables:\n" + Arrays.toString(dataBaseManager.getTableNames()));
    }

    private void cmdHelp() {
        view.write("\nList of available commands:");
        view.write("\tlist");
        view.write("\t\tdisplay available tables in selected database");
        view.write("\texit");
        view.write("\t\tto exit from this session");
        view.write("\thelp");
        view.write("\t\twill display this message again... try it! )");
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
            reason += e.getCause().getMessage();
        view.write("Reason: " + reason);
    }
}
