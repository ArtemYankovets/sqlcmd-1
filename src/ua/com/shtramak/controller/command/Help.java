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
        view.write("");
        view.write("List of available commands:");
        view.write("\tlist");
        view.write("\t\tdisplay available tables in selected database");
        view.write("\tfind|tableName");
        view.write("\t\tdisplay available tables in selected database");
        view.write("\tclear|tableName");
        view.write("\t\tdelete all data from selected table");
        view.write("\tinsert|tableName|col1Name|value1|col2Name|value2|...col#Name|value#");
        view.write("\t\tinsert entered data to selected table");
        view.write("\tupdate|tableName");
        view.write("\t\tupdate entry in selected table using own command interface");
        view.write("\texit");
        view.write("\t\tto exit from this session");
        view.write("\thelp");
        view.write("\t\twill display this message again... try it! )");
    }
}
