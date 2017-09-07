package ua.com.shtramak.sqlcmd.controller;

import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.JDBCDataBaseManager;
import ua.com.shtramak.sqlcmd.view.ConsoleView;
import ua.com.shtramak.sqlcmd.view.View;

public class Main {
    public static void main(String[] args) {
        DataBaseManager dataBaseManager = new JDBCDataBaseManager();
        View view = new ConsoleView();

        MainController controller = new MainController(view, dataBaseManager);
        controller.run();
    }
}