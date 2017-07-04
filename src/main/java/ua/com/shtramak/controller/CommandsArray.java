package ua.com.shtramak.controller;

import ua.com.shtramak.controller.command.*;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

public class CommandsArray {
    private DataBaseManager dataBaseManager;
    private View view;

    public CommandsArray(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }

    public Command[] commandsList() {
        return new Command[]{ new Connect(dataBaseManager,view),
                new List(dataBaseManager, view),
                new Find(dataBaseManager, view),
                new Clear(dataBaseManager, view),
                new Insert(dataBaseManager, view),
                new UpdateById(dataBaseManager, view),
                new Exit(dataBaseManager, view),
                new Help(dataBaseManager,view)};
    }
}
