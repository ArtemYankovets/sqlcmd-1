package ua.com.shtramak.controller;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.DataSet;
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
                    doListCmd();
                    break;
                case "help":
                    doHelpCmd();
                    break;
                case "exit":
                    finished = true;
                    dataBaseManager.disconnect();
                    System.out.println("Good Luck!");
                    break;
                default:
                    if (command.startsWith("find|")) {
                        doTableDataCmd(command);
                    } else {
                        System.out.println("No such command found!");
                    }
            }
        }
    }

    private void doTableDataCmd(String command) {
        String tableName = command.split("\\|")[1];
        boolean fakeName = true;

        for (String name : dataBaseManager.getTableNames()) {
            if (name.equals(tableName)) fakeName = false;
        }

        if (fakeName) {
            view.write("Wrong table name! Check if your table exists from the list below");
            view.write("List with available tables: " + Arrays.toString(dataBaseManager.getTableNames()));
            return;
        }

        printTableData(tableName);
    }

    //Use toString for DataSet[] from getTableData(tableName) with formatter
    private void printTableData(String tableName) {
        String[] tableColumns = dataBaseManager.getTableColumns(tableName);
        printFormattedRow(tableColumns);
        DataSet[] tableData = dataBaseManager.getTableData(tableName);
        for (DataSet tableItem : tableData){
            printFormattedRow(tableItem.getStringValues());
        }
        view.write("----------------------------------------");
    }

    private void printFormattedRow(String[] dataArray) {
        String row = "|";
        view.write("----------------------------------------");
        for (String rowItem : dataArray) {
            row += String.format(" %-10s |", rowItem);
        }
        view.write(row);

    }

    private void doListCmd() {
        view.write("Here's the names of available tables: " + Arrays.toString(dataBaseManager.getTableNames()));
    }

    private void doHelpCmd() {
        view.write("\nList of available commands:");
        view.write("\tlist");
        view.write("\t\tdisplay available tables in selected database");
        view.write("\tdata|tableName");
        view.write("\t\tdisplay data from a table in selected database");
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
                if (e.getClass().getSimpleName().equals("UnsupportedOperationException")) System.exit(0);
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