package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NoJDBCDriverException;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.model.exceptions.UnsuccessfulConnectionException;
import ua.com.shtramak.sqlcmd.utils.Commands;
import ua.com.shtramak.sqlcmd.view.View;

import java.io.IOException;
import java.util.Properties;

public class ConnectToDB extends AbstractCommand {
    public ConnectToDB(DataBaseManager dataBaseManager, View view) {
        super(CommandType.CONNECT_TO_DB);
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    @Override
    public boolean isDetected(String command) {
        if (!command.startsWith(CommandType.CONNECT_TO_DB.getName())) return false;

        try {
            if (dataBaseManager.isConnected()) {
                view.writeln("Disconnection from current database...");
                dataBaseManager.disconnect();
            }
        } catch (NotExecutedRequestException e) {
            view.writeln("Disconnection from current database failed! Try again!" +
                    "Reason: " + e.getMessage());
        }

        return true;
    }

    @Override
    public void execute(String command) {
        try {
            if (command.equals("connect")) {
                autoConnect();
            } else {
                final String[] commandsTemplate = Commands.arrayOf(CommandType.CONNECT_TO_DB.template());
                String[] connectionData = Commands.arrayOf(command);
                if (connectionData.length != commandsTemplate.length) {
                    view.writeln("Connection failed!");
                    String message = "Incorrect input. Please enter required input data in format: connect|database|userName|password";
                    throw new UnsuccessfulConnectionException(message);
                }

                int dbIndex = 1;
                int userNameIndex = 2;
                int userPasswordIndex = 3;
                String dbName = connectionData[dbIndex];
                String userName = connectionData[userNameIndex];
                String password = connectionData[userPasswordIndex];
                dataBaseManager.connect(dbName, userName, password);
                view.writeln(String.format("Hello %s! Welcome to %s database", userName, dbName));
            }
        } catch (NoJDBCDriverException | UnsuccessfulConnectionException e) {
            printError(e);
            view.writeln("Try again!" + System.lineSeparator());
        }
    }

    private void autoConnect() {
        if (connectedWithConfig(dataBaseManager)) {
            String userName = configData().getProperty("db.user");
            String dbName = configData().getProperty("db.name");
            view.writeln(String.format("Hello %s! You're automatically logged in to %s database", userName, dbName));
            return;
        }
        view.writeln("Automatically connection failed! Check if file 'config.properties' is correct");
    }

    private void printError(Exception e) {
        String reason = e.getMessage();
        if (e.getCause() != null)
            reason += e.getCause().getMessage();
        view.writeln("Error message: " + reason);
    }

    private boolean connectedWithConfig(DataBaseManager dataBaseManager) {
        Properties properties = configData();
        if (!properties.isEmpty()) {
            String host = properties.getProperty("db.host");
            String dbName = properties.getProperty("db.name");
            String userName = properties.getProperty("db.user");
            String userPassword = properties.getProperty("db.password");

            try {
                dataBaseManager.connect(host, dbName, userName, userPassword);
                return true;
            } catch (NoJDBCDriverException | UnsuccessfulConnectionException e) {
                view.writeln(e.getMessage());
            }
        }
        return false;
    }

    private Properties configData() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            view.writeln(e.getMessage());
        }
        return properties;
    }
}