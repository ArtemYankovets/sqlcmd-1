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

    public static boolean connectedWithConfig(DataBaseManager dataBaseManager){
        Properties properties = configData();
        if (!properties.isEmpty()){
            String host = properties.getProperty("db.host");
            String name = properties.getProperty("db.name");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            try {
                dataBaseManager.connect(host,name,user,password);
                return true;
            } catch (Exception e) {
                //NOP
            }
        }
        return false;
    }

    private static Properties configData() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("src/main/resources/config.properties"));
        } catch (IOException e) {
            System.out.println("File config.properties not found!");
        }
        return properties;
    }
}
