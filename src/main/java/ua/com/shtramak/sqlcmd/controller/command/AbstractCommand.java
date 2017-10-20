package ua.com.shtramak.sqlcmd.controller.command;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.view.View;

public abstract class AbstractCommand {
    DataBaseManager dataBaseManager;
    View view;

    abstract public boolean isDetected(String command);

    abstract public void execute(String command);

    abstract public String description();

    //TODO проверка команды на полное соответсвие шаблону. "command != commanddd"
}
