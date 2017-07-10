package ua.com.shtramak.controller.command;

import ua.com.shtramak.controller.CommandsStorage;
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
        return "\thelp" +
                System.lineSeparator() +
                "\t\twill display this message again... try it! )";
    }

    @Override
    public boolean isDetected(String command) {
        return command.equals("help");
    }

    @Override
    public void execute(String command) {
        Command[] commands = new CommandsStorage(dataBaseManager, view).commandsList();
        view.writeln(System.lineSeparator() + "List of available commands:");
        for (Command element : commands) {
            view.writeln(element.description());
        }
    }
}
