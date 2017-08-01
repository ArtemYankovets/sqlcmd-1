package ua.com.shtramak.controller;

import ua.com.shtramak.controller.command.Command;
import ua.com.shtramak.controller.command.CommandsStorage;
import ua.com.shtramak.controller.command.ConnectToDB;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.util.Commands;
import ua.com.shtramak.view.View;

public class MainController {

    private View view;
    private DataBaseManager dataBaseManager;
    Command[] commands;

    public MainController(View view, DataBaseManager dataBaseManager) {
        this.view = view;
        this.dataBaseManager = dataBaseManager;
        commands = new CommandsStorage(dataBaseManager, view).commandsList();
    }

    public void run() {

        view.writeln("Hello user! For connection to database using config file, please enter command 'connect'");
        view.writeln("For connection to database using your login and password enter required input command in format: 'connect|database|userName|password'");
        view.writeln("Enter 'exit' command to leave the application. 'exit' command is always available");

        String inputCommand = view.read();

        if (inputCommand.equals("connect")) {
            if (ConnectToDB.connectedWithConfig(dataBaseManager)) {
                String userName = ConnectToDB.configData().getProperty("db.user");
                String dbName = ConnectToDB.configData().getProperty("db.name");
                view.writeln(String.format("Hello %s! You're automatically logged in to %s database", userName, dbName));
            }
        } else {
            view.writeln("Automatically connection failed! Check if file 'config.properties' is correct");
            if (Commands.isExit(inputCommand)) {
                view.writeln("Good Luck!");
                return;
            }

            requestMandatoryConnection(inputCommand);

            if (!dataBaseManager.isConnected()) {
                view.writeln("Exiting before connection to database... Good luck!");
                return;
            }
        }

        while (true) {

            view.writeln("");
            view.writeln("Type a command or 'help' to see the command list");

            inputCommand = view.read();

            boolean exist = false;
            for (Command command : commands) {
                if (command.isDetected(inputCommand)) {
                    command.execute(inputCommand);
                    exist = true;
                    break;
                }
            }

            if (Commands.isExit(inputCommand)) {
                break;
            }

            if (!exist)
                view.writeln(String.format("Command '%s' doesn't exists. Use 'help' command for details", inputCommand));
        }
    }

    private void requestMandatoryConnection(String inputCommand) {
        while (true) {
            int connectIndex = 0;
            if (commands[connectIndex].isDetected(inputCommand)) {
                commands[connectIndex].execute(inputCommand);
            } else {
                view.writeln(String.format("Invalid data! Your input was %s: ", inputCommand) + "Try again using next format:" + System.lineSeparator() +
                        "connect|database|userName|password");
            }

            if (dataBaseManager.isConnected()) return;

            inputCommand = view.read();

            if (inputCommand.equals("exit")) break;
        }
    }
}