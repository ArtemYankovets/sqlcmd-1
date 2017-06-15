package ua.com.shtramak.controller;

import ua.com.shtramak.controller.command.*;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

public class MainController {

    private View view;
    private DataBaseManager dataBaseManager;
    Command[] commands;

    public MainController(View view, DataBaseManager dataBaseManager) {
        this.view = view;
        this.dataBaseManager = dataBaseManager;
        commands = new Command[]{new Connect(dataBaseManager, view),
                new List(dataBaseManager, view),
                new Help(view),
                new Find(dataBaseManager, view),
                new Exit(dataBaseManager)};
    }

    public void run() {

        while (true) {

            if (!dataBaseManager.isConnected()) {
                view.write("Hello, user! For first connection to database please enter required input data using next format:\n" +
                        "connect|database|userName|password");
                String inputCommand = view.read();
                commands[0].isDetected(inputCommand);
                commands[0].execute();
            }

            if (!dataBaseManager.isConnected()) continue;

            view.write("\nType a command or 'help' to see the command list");

            String inputCommand = view.read();
            boolean isDetected = false;
            for (Command command : commands) {
                if (command.isDetected(inputCommand)) {
                    command.execute();
                    isDetected = true;
                    break;
                }
            }

            if (inputCommand.equals("exit")) break;

            if (!isDetected)
                view.write(String.format("Command %s doesn't exists", inputCommand));
        }
    }
}