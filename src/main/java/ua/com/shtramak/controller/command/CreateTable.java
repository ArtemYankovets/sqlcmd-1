package ua.com.shtramak.controller.command;

import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.utils.Commands;
import ua.com.shtramak.view.View;

public class CreateTable extends AbstractCommand {
    public CreateTable(DataBaseManager dataBaseManager, View view) {
        this.dataBaseManager = dataBaseManager;
        this.view = view;
    }


    @Override
    public boolean isDetected(String command) {
        return command.equals("create");
    }

    @Override
    public void execute(String command) {
        view.write("Please enter a name of table you want to create: ");
        String tableName = view.read();

        if(dataBaseManager.hasTable(tableName)){
            view.writeln("Such table already exists...");
            return;
        }

        view.writeln("Please enter columns data in format: col1Name|dataType1|col2Name|dataType2...col#Name|dataType#");
        String columnsData = view.read();

        if(Commands.sizeOf(columnsData)%2!=0){
            view.writeln(String.format("Wrong input! Number of elements must be even, but entered %s",Commands.sizeOf(columnsData)));
            return;
        }

        dataBaseManager.createTable(tableName,columnsData);
        view.writeln("Table successfully created...");
    }

    @Override
    public String description() {
        return "\tcreate" +
                System.lineSeparator() +
                "\t\tredirects to dialog to create a new table";
    }

}
