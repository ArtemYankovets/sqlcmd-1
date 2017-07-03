package ua.com.shtramak.controller;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.JDBCDataBaseManager;
import ua.com.shtramak.view.Console;
import ua.com.shtramak.view.View;

public class Main {
    public static void main(String[] args) {
        DataBaseManager dataBaseManager = new JDBCDataBaseManager();
        View view = new Console();

        MainController controller = new MainController(view, dataBaseManager);
        controller.run();
    }
}