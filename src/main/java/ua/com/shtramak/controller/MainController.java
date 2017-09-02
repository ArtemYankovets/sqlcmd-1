package ua.com.shtramak.controller;

import ua.com.shtramak.controller.command.AbstractCommand;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.utils.Commands;
import ua.com.shtramak.utils.CommandsStorage;
import ua.com.shtramak.view.View;

public class MainController {

    private View view;
    private DataBaseManager dataBaseManager;
    private AbstractCommand[] commands;

    public MainController(View view, DataBaseManager dataBaseManager) {
        this.view = view;
        this.dataBaseManager = dataBaseManager;
        commands = new CommandsStorage(dataBaseManager, view).commandsList();
    }

    public void run() {
        String lineSeparator = System.lineSeparator();
        final String greetingMessage =
                "Hello user! For connection to database using config file, please enter command 'connect'" + lineSeparator +
                "For connection to database using your login and password enter required input command in format: 'connect|database|userName|password'" + lineSeparator +
                "Enter 'exit' command to leave the application. 'exit' command is always available";
        view.writeln(greetingMessage);

        String inputCommand = view.read();
        requestMandatoryConnection(inputCommand);

        if (!dataBaseManager.isConnected()) {
            view.writeln("Exiting before connection to database... Good luck!");
            return;
        }

        executeCommands();
    }

    private void executeCommands() {
        while (true) {
            view.writeln("");
            view.writeln("Type a command or 'help' to see the command list");

            String inputCommand = view.read();

            boolean exist = false;
            for (AbstractCommand command : commands) {
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
                String lineSeparator = System.lineSeparator();
                String message = "Incorrect input! You have 3 options:"+lineSeparator+
                        "1. For auto connect using config file enter command 'connect'"+lineSeparator+
                        "2. For connection to database using your login and password enter command in format: 'connect|database|userName|password'"+lineSeparator+
                        "3. Enter 'exit' command to stop the program";
                view.writeln(message);
            }

            if (dataBaseManager.isConnected()) return;

            inputCommand = view.read();

            if (inputCommand.equals("exit")) break;
        }
    }
}