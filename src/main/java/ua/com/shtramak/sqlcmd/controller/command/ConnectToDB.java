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
    public void execute(String command) {

        try {
            if (command.equals("connect") && Commands.sizeOf(command) == 1) {
                autoConnect();
                return;
            }

            if (!isValidCommand(command) || !isReadyForConnection()) {
                view.writeln("Connection failed!");
                String message = "Incorrect input. Please enter required input data in format: connect|database|userName|password";
                throw new UnsuccessfulConnectionException(message);
            }

            String[] connectionData = Commands.arrayOf(command);
            int dbIndex = 1;
            int userNameIndex = 2;
            int userPasswordIndex = 3;
            String dbName = connectionData[dbIndex];
            String userName = connectionData[userNameIndex];
            String password = connectionData[userPasswordIndex];
            dataBaseManager.connect(dbName, userName, password);
            view.writeln(String.format("Hello %s! Welcome to %s database", userName, dbName));
        } catch (NoJDBCDriverException | UnsuccessfulConnectionException e) {
            printError(e);
            view.writeln("Try again!" + System.lineSeparator());
        }
    }

    private void autoConnect() throws UnsuccessfulConnectionException {
        if (isReadyForConnection() && connectedWithConfig(dataBaseManager)) {
            String userName = configData().getProperty("db.user");
            String dbName = configData().getProperty("db.name");
            view.writeln(String.format("Hello %s! You're automatically logged in to %s database", userName, dbName));
            return;
        }
        view.writeln("Automatically connection failed! Check if file 'config.properties' is correct");
    }

    private boolean isReadyForConnection() throws UnsuccessfulConnectionException {
        if (!dataBaseManager.isConnected()) return true;

        try {
            if (dataBaseManager.isConnected()) {
                if (Commands.isSureInActingWithTable("You are going to disconnect from current database. Proceed? [Yes/No]", view)) {
                    view.writeln("Disconnection from current database...");
                    dataBaseManager.disconnect();
                    return true;
                }
            }
        } catch (NotExecutedRequestException e) {
            String message = "Disconnection from current database failed! Try again! Reason: " + e.getMessage();
            throw new UnsuccessfulConnectionException(message);
        }

        return false;
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