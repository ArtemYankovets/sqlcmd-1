package ua.com.shtramak.controller.command;

import ua.com.shtramak.controller.CommandsArray;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

public class Help implements Command {
    private View view;
    private DataBaseManager dataBaseManager;

    public Help(DataBaseManager dataBaseManager, View view) {
        this.view = view;
        this.dataBaseManager = dataBaseManager;
    }

    @Override
    public String description() {
        final String LINE_SEPARATOR = System.lineSeparator();
        return "\thelp" +
                LINE_SEPARATOR +
                "\t\twill display this message again... try it! )";
    }

    @Override
    public boolean isDetected(String command) {
        return command.equals("help");
    }

    @Override
    public void execute(String command) {
        Command[] commands = new CommandsArray(dataBaseManager, view).commandsList();
        view.write(System.lineSeparator() + "List of available commands:");
        for (Command element : commands) {
            view.write(element.description());
        }
    }
}
