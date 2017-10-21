package ua.com.shtramak.sqlcmd.controller.command;

public enum CommandType {
    CLEAR_TABLE(
            "clear",
            "\tclear|tableName"
                    +System.lineSeparator()
                    +"\t\tdelete all data from selected table",
            "clear|tableName"
    ),
    CONNECT_TO_DB(
            "connect",
            "\tconnect|database|userName|password"
                    +System.lineSeparator()
                    +"\t\tconnection with database under userName with password",
            "connect|database|userName|password"
    ),
    CREATE_TABLE(
            "create",
            "\tcreate"
                    +System.lineSeparator()
                    +"\t\tredirects to dialog to create a new table",
            "create"
    ),
    DELETE_ENTRY(
            "deleteEntry",
            "\tdeleteEntry|tableName"
                    +System.lineSeparator()
                    +"\t\tdeletes entry in selected table using own command interface",
            "deleteEntry|tableName"
    ),
    DROP_TABLE(
            "drop",
            "\tdrop|tableName"
                    +System.lineSeparator()
                    +"\t\tdrop an existing table in a database",
            "drop|tableName"
    ),
    EXIT(
            "exit",
            "\texit"
                    +System.lineSeparator()
                    +"\t\tto exit from this session",
            "exit"
    ),
    HELP(
            "help",
            "\thelp"
                    +System.lineSeparator()
                    +"\t\twill display this message again... try it! )",
            "help"
    ),
    INSERT_ENTRY(
            "insert",
            "\tinsert|tableName|col1Name|value1|col2Name|value2|...col#Name|value#"
                    +System.lineSeparator()
                    +"\t\tinsert entered data to selected table",
            "insert|tableName|col1Name|value1|col2Name...col#Name|value#"
    ),
    SHOW_TABLE_DATA(
            "show",
            "\tshow|tableName"
                    +System.lineSeparator()
                    +"\t\tdisplay table data from selected database",
            "show|tableName"
    ),
    SHOW_TABLES_LIST(
            "list",
            "\tlist"
                    +System.lineSeparator()
                    +"\t\tdisplay available tables in selected database",
            "list"
    ),
    UPDATE_TABLE_DATA(
            "updateTable",
            "\tupdateTable|tableName"
                    +System.lineSeparator()
                    +"\t\tupdates entry in selected table using own command interface",
            "updateTable|tableName"
    );

    private String name;
    private String description;
    private String template;

    CommandType(String name, String description, String template) {
        this.name = name;
        this.description = description;
        this.template = template;
    }

    public String getName() {
        return name;
    }

    public String description() {
        return description;
    }

    public String template() {
        return template;
    }
}