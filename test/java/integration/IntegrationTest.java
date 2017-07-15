package integration;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.com.shtramak.controller.Main;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.JDBCDataBaseManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    private ByteArrayOutputStream out;
    private ConsoleInputStream in;
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @BeforeClass
    public static void initTestTable() { //TODO connect через properties
        DataBaseManager dataBaseManager = new JDBCDataBaseManager();
        dataBaseManager.connect("sqlcmd", "postgres", "postgres");
        if (dataBaseManager.hasTable("tmpusers")) {
            dataBaseManager.dropTable("tmpusers");
        }
        dataBaseManager.createTable("tmpusers", "id|serial|name|text|password|text");
        dataBaseManager.disconnect();
    }

    @AfterClass
    public static void dropTestTable() {
        DataBaseManager dataBaseManager = new JDBCDataBaseManager();
        dataBaseManager.connect("sqlcmd", "postgres", "postgres");
        dataBaseManager.dropTable("tmpusers");
        dataBaseManager.disconnect();
    }

    @Before
    public void setup() {
        in = new ConsoleInputStream();
        System.setIn(in);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    public void testExit() {
        in.addCommand("exit");

        Main.main(new String[0]);

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;
        assertEquals(expected, getData());
    }

    @Test
    public void testCommandBeforeConnect() {
        in.addCommand("list");
        in.addCommand("exit");

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Invalid data! Your input was list:  Try again using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Exiting before connection to database... Good luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testAnyCommandBeforeConnect() {
        in.addCommand("help");
        in.addCommand("exit");

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Invalid data! Your input was help:  Try again using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Exiting before connection to database... Good luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("exit");

        String expected = expectedMessageAfterConnection() +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testListAfterConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("list");
        in.addCommand("exit");

        String expected = expectedMessageAfterConnection() +
                "Here's the names of available tables: [users, tmpusers]" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testWrongFindAfterConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("find|user");
        in.addCommand("exit");

        String expected = expectedMessageAfterConnection() +
                "Table user doesn't exists! Available tables: [users, tmpusers]" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testClear() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("clear|tmpusers");
        in.addCommand("yes");
        in.addCommand("exit");

        String expected = expectedMessageAfterConnection() +
                "You are going to delete all data from table 'tmpusers'! Are you sure? [Yes/No]" + LINE_SEPARATOR +
                "Data from 'tmpusers' was successfully deleted" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testFindInEmptyTable() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("clear|tmpusers");
        in.addCommand("yes");
        in.addCommand("find|tmpusers");
        in.addCommand("exit");

        String expected = expectedMessageAfterConnection() +
                "You are going to delete all data from table 'tmpusers'! Are you sure? [Yes/No]" + LINE_SEPARATOR +
                "Data from 'tmpusers' was successfully deleted" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| id         | name       | password   |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "The table is empty. Use 'insert' command for data insertion" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());

    }

    @Test
    public void testInsert() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("clear|tmpusers");
        in.addCommand("yes");
        in.addCommand("insert|tmpusers|id|11|name|TestName1|password|qqq");
        in.addCommand("insert|tmpusers|id|8|name|TestName2|password|qwerty");
        in.addCommand("insert|tmpusers|id|1|name|TestName3|password|qwerty");
        in.addCommand("exit");

        String expected = expectedMessageAfterConnection() +
                "You are going to delete all data from table 'tmpusers'! Are you sure? [Yes/No]" + LINE_SEPARATOR +
                "Data from 'tmpusers' was successfully deleted" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to tmpusers" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to tmpusers" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to tmpusers" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());


    }

    @Test
    public void testFindAndUpdate() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("clear|tmpusers");
        in.addCommand("yes");
        in.addCommand("insert|tmpusers|id|11|name|TestName1|password|pass1");
        in.addCommand("insert|tmpusers|id|8|name|TestName2|password|pass2");
        in.addCommand("insert|tmpusers|id|1|name|TestName3|password|pass3");
        in.addCommand("find|tmpusers");
        in.addCommand("updateTableData|tmpusers");
        in.addCommand("name");
        in.addCommand("TestName1");
        in.addCommand("name|TestUpd|password|passUpd");
        in.addCommand("find|tmpusers");
        in.addCommand("exit");

        String expected = expectedMessageAfterConnection() +
                "You are going to delete all data from table 'tmpusers'! Are you sure? [Yes/No]" + LINE_SEPARATOR +
                "Data from 'tmpusers' was successfully deleted" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to tmpusers" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to tmpusers" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to tmpusers" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| id         | name       | password   |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| 11         | TestName1  | pass1      |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| 8          | TestName2  | pass2      |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| 1          | TestName3  | pass3      |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Please input wanted 'colName' and 'value' of the row you want to updateTableData:" + LINE_SEPARATOR +
                "Enter column name: Enter value: Now please input updateTableData data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value# or exit" + LINE_SEPARATOR +
                "Data successfully updated..." + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| id         | name       | password   |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| 8          | TestName2  | pass2      |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| 1          | TestName3  | pass3      |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| 11         | TestUpd    | passUpd    |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testConnectAfterConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("connect|postgres|postgres|postgres");
        in.addCommand("exit");

        String expected = expectedMessageAfterConnection() +
                "Hello postgres! Welcome to postgres database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testConnectWithError() {
        in.addCommand("connect|sqlcmd|postgres|wrongPassword");
        in.addCommand("exit");

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Error message: Dear postgres! Your input data was incorrect!" + LINE_SEPARATOR +
                "ВАЖНО: пользователь \"postgres\" не прошёл проверку подлинности (по паролю) " +
                "(pgjdbc: autodetected server-encoding to be windows-1251, if the message is not readable, please check database logs and/or host, port, dbname, user, password, pg_hba.conf)" + LINE_SEPARATOR +
                "Try again!" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Exiting before connection to database... Good luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testHelp() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("help");
        in.addCommand("exit");

        String expected = expectedMessageAfterConnection() + LINE_SEPARATOR +
                "List of available commands:" + LINE_SEPARATOR +
                "\tconnect|database|userName|password" + LINE_SEPARATOR +
                "\t\tconnection with database under userName with password" + LINE_SEPARATOR +
                "\tlist" + LINE_SEPARATOR +
                "\t\tdisplay available tables in selected database" + LINE_SEPARATOR +
                "\tfind|tableName" + LINE_SEPARATOR +
                "\t\tdisplay table data from selected database" + LINE_SEPARATOR +
                "\tclear|tableName" + LINE_SEPARATOR +
                "\t\tdelete all data from selected table" + LINE_SEPARATOR +
                "\tinsert|tableName|col1Name|value1|col2Name|value2|...col#Name|value#" + LINE_SEPARATOR +
                "\t\tinsert entered data to selected table" + LINE_SEPARATOR +
                "\tupdateTableData|tableName" + LINE_SEPARATOR +
                "\t\tupdateTableData entry in selected table using own command interface" + LINE_SEPARATOR +
                "\tcreate" + LINE_SEPARATOR +
                "\t\tredirects to dialog to create a new table" + LINE_SEPARATOR +
                "\tdrop|tableName" + LINE_SEPARATOR +
                "\t\tdrop an existing table in a database" + LINE_SEPARATOR +
                "\texit" + LINE_SEPARATOR +
                "\t\tto exit from this session" + LINE_SEPARATOR +
                "\thelp" + LINE_SEPARATOR +
                "\t\twill display this message again... try it! )" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testWrongCommandAfterConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("hepl");
        in.addCommand("exit");

        String expected = expectedMessageAfterConnection() +
                "Command hepl doesn't exists. Use 'help' command for details" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testWrongClear() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("clear|");
        in.addCommand("clear|user");
        in.addCommand("clear|tmpusers");
        in.addCommand("no");
        in.addCommand("exit");

        String expected = expectedMessageAfterConnection() +
                "'clear' command failed because of wrong input. Use 'help' command for details" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Table user doesn't exists! See the list with available tables below:" + LINE_SEPARATOR +
                "List with available tables: [users, tmpusers]" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "You are going to delete all data from table 'tmpusers'! Are you sure? [Yes/No]" + LINE_SEPARATOR +
                "Command 'clear' was canceled..." + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testWrongInsert() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("insert|user");
        in.addCommand("insert|user|data");
        in.addCommand("exit");

        String expected = expectedMessageAfterConnection() +
                "DataSet is empty" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "'insert' command failed because of wrong input: incorrect number of elements. Use 'help' command for details" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());

    }

    @Test
    public void testWrongUpdate() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("updateTableData|user|user");
        in.addCommand("updateTableData|user");
        in.addCommand("updateTableData|tmpusers");
        in.addCommand("name");
        in.addCommand("TestName3");
        in.addCommand("password|ChuPass|oops");
        in.addCommand("exit");
        in.addCommand("exit");

        String expected = expectedMessageAfterConnection() +
                "updateTableData command failed because of wrong input. Use 'help' command for details" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Table 'user' doesn't exists! See below the list with available tables:" + LINE_SEPARATOR +
                "Available tables: [users, tmpusers]" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Please input wanted 'colName' and 'value' of the row you want to updateTableData:" + LINE_SEPARATOR +
                "Enter column name: Enter value: Now please input updateTableData data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value# or exit" + LINE_SEPARATOR +
                "Wrong input! Input must be according to the template" + LINE_SEPARATOR +
                "Try again using correct format col1Name|value1|col2Name|value2|...col#Name|value# or enter 'exit' command" + LINE_SEPARATOR +
                "Update command failed!" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    private String expectedMessageAfterConnection() {
        return "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR;
    }

    public String getData() {
        String result = "";
        try {
            result += new String(out.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

}