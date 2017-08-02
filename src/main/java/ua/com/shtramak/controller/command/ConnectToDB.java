package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.util.Commands;
import ua.com.shtramak.view.View;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

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
        if (!command.startsWith("connect")) return false;

        if (dataBaseManager.isConnected()) {
            view.writeln("Disconnection from current database...");
            dataBaseManager.disconnect();
        }

        return true;
    }

    @Override
    public void execute(String command) {
        try {
            if (command.equals("connect")){
                autoConnect();
            }else {

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
                String dbName = connectionData[dbIndex];
                String userName = connectionData[userNameIndex];
                String password = connectionData[userPasswordIndex];
                dataBaseManager.connect(dbName, userName, password);
                view.writeln(String.format("Hello %s! Welcome to %s database", userName, dbName));
            }
        } catch (Exception e) {
            printError(e);
            if (e.getClass().getSimpleName().equals("UnsupportedOperationException")) return;
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

    private boolean connectedWithConfig(DataBaseManager dataBaseManager){
        Properties properties = configData();
        if (!properties.isEmpty()){
            String host = properties.getProperty("db.host");
            String dbName = properties.getProperty("db.name");
            String userName = properties.getProperty("db.user");
            String userPassword = properties.getProperty("db.password");

            try {
                dataBaseManager.connect(host,dbName,userName,userPassword);
                return true;
            } catch (Exception e) {
                //NOP
            }
        }
        return false;
    }

    private Properties configData() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("src/main/resources/config.properties"));
        } catch (IOException e) {
            System.out.println("File config.properties not found!");
        }
        return properties;
    }
}
