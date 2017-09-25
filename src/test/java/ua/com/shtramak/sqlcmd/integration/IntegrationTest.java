package ua.com.shtramak.sqlcmd.integration;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.com.shtramak.sqlcmd.controller.Main;
import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.JDBCDataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NoJDBCDriverException;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.model.exceptions.UnsuccessfulConnectionException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntegrationTest {

    private ByteArrayOutputStream out;
    private ConsoleInputStream in;
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @BeforeClass
    public static void initTestTable() throws NoJDBCDriverException, UnsuccessfulConnectionException, NotExecutedRequestException {
        DataBaseManager dataBaseManager = new JDBCDataBaseManager();
        dataBaseManager.connect("sqlcmd", "postgres", "postgres");
        if (dataBaseManager.hasTable("tmpusers")) {
            dataBaseManager.dropTable("tmpusers");
        }
        dataBaseManager.createTable("tmpusers", "id|serial|name|text|password|text");
        dataBaseManager.disconnect();
    }

    @AfterClass
    public static void dropTestTable() throws NoJDBCDriverException, UnsuccessfulConnectionException, NotExecutedRequestException {
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
    public void testExitWithAutomaticConnection() {
        in.addCommand("connect");
        in.addCommand("exit");

        Main.main(new String[0]);

        String expected = greetingMessage() +
                "Hello postgres! You're automatically logged in to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;
        assertEquals(expected, getData());
    }

    @Test
    public void testCommandBeforeConnect() {
        in.addCommand("list");
        in.addCommand("exit");

        String expected = greetingMessage() +
                "Incorrect input! You have 3 options:" + LINE_SEPARATOR +
                "1. For auto connect using config file enter command 'connect'" + LINE_SEPARATOR +
                "2. For connection to database using your login and password enter command in format: 'connect|database|userName|password'" + LINE_SEPARATOR +
                "3. Enter 'exit' command to stop the program" + LINE_SEPARATOR +
                "Exiting before connection to database... Good luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testAnyCommandBeforeConnect() {
        in.addCommand("help");
        in.addCommand("exit");

        String expected = greetingMessage() +
                "Incorrect input! You have 3 options:" + LINE_SEPARATOR +
                "1. For auto connect using config file enter command 'connect'" + LINE_SEPARATOR +
                "2. For connection to database using your login and password enter command in format: 'connect|database|userName|password'" + LINE_SEPARATOR +
                "3. Enter 'exit' command to stop the program" + LINE_SEPARATOR +
                "Exiting before connection to database... Good luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testManualConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("exit");

        String expected = greetingMessage() +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testListAfterConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("list");
        in.addCommand("exit");

        String expected = greetingMessage() +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Here's the names of available tables: [tmpusers, users]" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testWrongShowAfterConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("show|user");
        in.addCommand("exit");

        String expected = greetingMessage() +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Table user doesn't exists! Available tables: [tmpusers, users]" + LINE_SEPARATOR + LINE_SEPARATOR +
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

        String expected = greetingMessage() +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "You are going to delete all data from table 'tmpusers'! Are you sure? [Yes/No]" + LINE_SEPARATOR +
                "Data from 'tmpusers' was successfully deleted" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testShowInEmptyTable() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("clear|tmpusers");
        in.addCommand("yes");
        in.addCommand("show|tmpusers");
        in.addCommand("exit");

        String expected = greetingMessage() +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "You are going to delete all data from table 'tmpusers'! Are you sure? [Yes/No]" + LINE_SEPARATOR +
                "Data from 'tmpusers' was successfully deleted" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "+----+------+----------+\n" +
                "! id ! name ! password !\n" +
                "+----+------+----------+" + LINE_SEPARATOR + LINE_SEPARATOR +
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

        String expected = greetingMessage() +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
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
    public void testShowAndUpdate() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("clear|tmpusers");
        in.addCommand("yes");
        in.addCommand("insert|tmpusers|id|11|name|TestName1|password|pass1");
        in.addCommand("insert|tmpusers|id|8|name|TestName2|password|pass2");
        in.addCommand("insert|tmpusers|id|1|name|TestName3|password|pass3");
        in.addCommand("show|tmpusers");
        in.addCommand("updateTable|tmpusers");
        in.addCommand("name");
        in.addCommand("TestName1");
        in.addCommand("name|TestUpd|password|passUpd");
        in.addCommand("show|tmpusers");
        in.addCommand("exit");

        String expected = greetingMessage() +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "You are going to delete all data from table 'tmpusers'! Are you sure? [Yes/No]" + LINE_SEPARATOR +
                "Data from 'tmpusers' was successfully deleted" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to tmpusers" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to tmpusers" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to tmpusers" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "+----+-----------+----------+\n" +
                "! id ! name      ! password !\n" +
                "+----+-----------+----------+\n" +
                "! 11 ! TestName1 ! pass1    !\n" +
                "+----+-----------+----------+\n" +
                "! 8  ! TestName2 ! pass2    !\n" +
                "+----+-----------+----------+\n" +
                "! 1  ! TestName3 ! pass3    !\n" +
                "+----+-----------+----------+" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Please input wanted 'colName' and 'value' of the row you want to update:" + LINE_SEPARATOR +
                "Enter column name: " + LINE_SEPARATOR +
                "Enter value: " + LINE_SEPARATOR +
                "Now please input updateTableData data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value# or exit" + LINE_SEPARATOR +
                "Data successfully updated..." + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "+----+-----------+----------+\n" +
                "! id ! name      ! password !\n" +
                "+----+-----------+----------+\n" +
                "! 8  ! TestName2 ! pass2    !\n" +
                "+----+-----------+----------+\n" +
                "! 1  ! TestName3 ! pass3    !\n" +
                "+----+-----------+----------+\n" +
                "! 11 ! TestUpd   ! passUpd  !\n" +
                "+----+-----------+----------+" + LINE_SEPARATOR + LINE_SEPARATOR +
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

        String expected = greetingMessage() +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Disconnection from current database..." + LINE_SEPARATOR +
                "Hello postgres! Welcome to postgres database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testConnectWithError() {
        in.addCommand("connect|sqlcmd|postgres|wrongPassword");
        in.addCommand("exit");

        String expected = greetingMessage() +
                "Error message: Dear, postgres! Unsuccessful connection to sqlcmd... " +
                "Reason: ВАЖНО: пользователь \"postgres\" не прошёл проверку подлинности (по паролю) " +
                "(pgjdbc: autodetected server-encoding to be windows-1251, if the message is not readable, please " +
                "check database logs and/or host, port, dbname, user, password, pg_hba.conf)" + LINE_SEPARATOR + LINE_SEPARATOR +
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

        String expected = greetingMessage() +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR + LINE_SEPARATOR +
                "List of available commands:" + LINE_SEPARATOR +
                "\tconnect|database|userName|password" + LINE_SEPARATOR +
                "\t\tconnection with database under userName with password" + LINE_SEPARATOR +
                "\tlist" + LINE_SEPARATOR +
                "\t\tdisplay available tables in selected database" + LINE_SEPARATOR +
                "\tshow|tableName" + LINE_SEPARATOR +
                "\t\tdisplay table data from selected database" + LINE_SEPARATOR +
                "\tclear|tableName" + LINE_SEPARATOR +
                "\t\tdelete all data from selected table" + LINE_SEPARATOR +
                "\tinsert|tableName|col1Name|value1|col2Name|value2|...col#Name|value#" + LINE_SEPARATOR +
                "\t\tinsert entered data to selected table" + LINE_SEPARATOR +
                "\tupdateTable|tableName" + LINE_SEPARATOR +
                "\t\tupdates entry in selected table using own command interface" + LINE_SEPARATOR +
                "\tdeleteEntry|tableName" + LINE_SEPARATOR +
                "\t\tdeletes entry in selected table using own command interface" + LINE_SEPARATOR +
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

        String expected = greetingMessage() +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Command 'hepl' doesn't exists. Use 'help' command for details" + LINE_SEPARATOR + LINE_SEPARATOR +
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

        String expected = greetingMessage() +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "'clear' command failed because of wrong input. Use 'help' command for details" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Table user doesn't exists! See the list with available tables: [tmpusers, users]" + LINE_SEPARATOR + LINE_SEPARATOR +
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

        String expected = greetingMessage() +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Request to database was not executed. Reason: ОШИБКА: ошибка синтаксиса (примерное положение: \"user\")\n" +
                "  Позиция: 13" + LINE_SEPARATOR + LINE_SEPARATOR+
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "'insert' command failed because of wrong input: incorrect number of elements. Use 'help' command for details" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);
        String expectet = getData();

        assertEquals(expected,getData());

    }

    @Test
    public void testWrongUpdate() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("updateTable|user|user");
        in.addCommand("updateTable|user");
        in.addCommand("updateTable|tmpusers");
        in.addCommand("name");
        in.addCommand("TestName");
        in.addCommand("password|ChuPass|oops");
        in.addCommand("exit");
        in.addCommand("exit");

        String expected = greetingMessage() +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "updateTableData command failed because of wrong input. Use 'help' command for details" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Table 'user' doesn't exists! See below the list with available tables:" + LINE_SEPARATOR +
                "Available tables: [tmpusers, users]" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Please input wanted 'colName' and 'value' of the row you want to update:" + LINE_SEPARATOR +
                "Enter column name: " + LINE_SEPARATOR +
                "Enter value: " + LINE_SEPARATOR +
                "There's no value 'TestName' in column 'name'" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Command 'password|ChuPass|oops' doesn't exists. Use 'help' command for details" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    private String greetingMessage() {
        return "Hello user! For connection to database using config file, please enter command 'connect'" + LINE_SEPARATOR +
                "For connection to database using your login and password enter required input command in format: 'connect|database|userName|password'" + LINE_SEPARATOR +
                "Enter 'exit' command to leave the application. 'exit' command is always available" + LINE_SEPARATOR;
    }

    private String getData() {
        String result = "";
        try {
            result += new String(out.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}