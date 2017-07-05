package ua.com.shtramak.controller;

import ua.com.shtramak.controller.command.*;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

public class MainController {

    private View view;
    private DataBaseManager dataBaseManager;
    Command[] commands;

    private static final String LINE_SEPARATOR = System.lineSeparator();

    public MainController(View view, DataBaseManager dataBaseManager) {
        this.view = view;
        this.dataBaseManager = dataBaseManager;
        commands = new CommandsArray(dataBaseManager,view).commandsList();
    }

    public void run() {
        view.write("Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password");

        String inputCommand = view.read();

        if (inputCommand.equals("exit")) {
            view.write("Good Luck!");
            return;
        }

        requestMandatoryConnection(inputCommand);

        if (!dataBaseManager.isConnected()) {
            view.write("Exiting before connection... Good luck!");
            return;
        }

        while (true) {

            view.write("");
            view.write("Type a command or 'help' to see the command list");

            inputCommand = view.read();

            boolean exist = false;
            for (Command command : commands) {
                if (command.isDetected(inputCommand)) {
                    command.execute(inputCommand);
                    exist = true;
                    break;
                }
            }

            if (inputCommand.equals("exit")) break;

            if (!exist)
                view.write(String.format("Command %s doesn't exists. Use 'help' command for details", inputCommand));
        }
    }

    private void requestMandatoryConnection(String inputCommand) {
        while (true) {
            int connectIndex = 0;
            if (commands[connectIndex].isDetected(inputCommand)) {
                commands[connectIndex].execute(inputCommand);
            } else {
                view.write(String.format("Invalid data! Your input is %s: ", inputCommand) + " Try again using next format:" + LINE_SEPARATOR +
                        "connect|database|userName|password");
            }

            if (dataBaseManager.isConnected()) return;
            inputCommand = view.read();
            if (inputCommand.equals("exit")) break;
        }
    }
}