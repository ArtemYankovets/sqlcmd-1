package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.utils.CommandsStorage;
import ua.com.shtramak.sqlcmd.view.View;

public class Help extends AbstractCommand {
    public Help(DataBaseManager dataBaseManager, View view) {
        super(CommandType.HELP);
        this.view = view;
        this.dataBaseManager = dataBaseManager;
    }

    @Override
    public void execute(String command) {
        if (!isValidCommand(command)) return;

        AbstractCommand[] commands = new CommandsStorage(dataBaseManager, view).commandsList();
        view.writeln(System.lineSeparator() + "List of available commands:");
        for (AbstractCommand element : commands) {
            view.writeln(element.description());
        }
    }
}
