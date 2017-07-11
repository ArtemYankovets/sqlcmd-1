package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.util.Commands;
import ua.com.shtramak.view.View;

public class ConnectToDB implements Command {

    private DataBaseManager dataBaseManager;
    private View view;

    public ConnectToDB(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public String description() {
        return "\tconnect|database|userName|password" +
                System.lineSeparator() +
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
            final String[] commandsTemplate = Commands.arrayOf("connect|database|userName|password");
            String[] connectionData = Commands.arrayOf(command);
            if (connectionData.length != commandsTemplate.length) {
                view.writeln("Connection failed!");
                String message = "Incorrect input. Please enter required input data in format: connect|database|userName|password";
                throw new IllegalArgumentException(message);
            }

            int dbIndex = 1;
            int userNameIndex = 2;
            int userPasswordIndex = 3;
            String database = connectionData[dbIndex];
            String userName = connectionData[userNameIndex];
            String password = connectionData[userPasswordIndex];
            dataBaseManager.connect(database, userName, password);
        } catch (Exception e) {
            printError(e);
            if (e.getClass().getSimpleName().equals("UnsupportedOperationException")) return;
            view.writeln("Try again!" + System.lineSeparator());
        }
    }

    private void printError(Exception e) {
        String reason = e.getMessage();
        if (e.getCause() != null)
            reason += e.getCause().getMessage();
        view.writeln("Error message: " + reason);
    }
}
