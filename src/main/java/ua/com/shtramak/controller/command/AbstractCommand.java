package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

public abstract class AbstractCommand {
    DataBaseManager dataBaseManager;
    View view;

    abstract public boolean isDetected(String command);

    abstract public void execute(String command);

    abstract public String description();
}
