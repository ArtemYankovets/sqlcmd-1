package ua.com.shtramak.controller.command;

import ua.com.shtramak.view.View;

public class Help implements Command {
    View view;

    public Help(View view) {
        this.view = view;
    }

    @Override
    public boolean isDetected(String command) {
        return command.equals("help");
    }

    @Override
    public void execute() {
        view.write("\nList of available commands:");
        view.write("\tlist");
        view.write("\t\tdisplay available tables in selected database");
        view.write("\tfind|tableName");
        view.write("\t\tdisplay data from a table in selected database");
        view.write("\texit");
        view.write("\t\tto exit from this session");
        view.write("\thelp");
        view.write("\t\twill display this message again... try it! )");
    }
}
