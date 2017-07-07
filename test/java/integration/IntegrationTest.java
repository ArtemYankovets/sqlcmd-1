package integration;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ua.com.shtramak.controller.Main;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    private ByteArrayOutputStream out;
    private ConsoleInputStream in;
    private static final String LINE_SEPARATOR = System.lineSeparator();

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
                "Invalid data! Your input is list:  Try again using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Exiting before connection... Good luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testAnyCommandBeforeConnect() {
        in.addCommand("help");
        in.addCommand("exit");

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Invalid data! Your input is help:  Try again using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Exiting before connection... Good luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("exit");

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
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

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Here's the names of available tables: [users]" + LINE_SEPARATOR + LINE_SEPARATOR +
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

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Wrong table name! Check the list below to see if your table exists" + LINE_SEPARATOR +
                "List with available tables: [users]" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testClear() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("clear|users");
        in.addCommand("yes");
        in.addCommand("exit");

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "You are going to delete all data from users! Are you sure? [Yes/No]" + LINE_SEPARATOR +
                "Data from users was successfully deleted" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testFindInEmptyTable() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("clear|users");
        in.addCommand("yes");
        in.addCommand("find|users");
        in.addCommand("exit");

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "You are going to delete all data from users! Are you sure? [Yes/No]" + LINE_SEPARATOR +
                "Data from users was successfully deleted" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Nothing to show! No data found. First insert data to the table using 'insert' command" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());

    }

    @Test
    public void testInsert() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("clear|users");
        in.addCommand("yes");
        in.addCommand("insert|users|id|11|name|shtramak|password|qqq");
        in.addCommand("insert|users|id|8|name|Cheburator|password|qwerty");
        in.addCommand("insert|users|id|1|name|Chupakabra|password|qwerty");
        in.addCommand("exit");

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "You are going to delete all data from users! Are you sure? [Yes/No]" + LINE_SEPARATOR +
                "Data from users was successfully deleted" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to the current table" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to the current table" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to the current table" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());


    }

    @Test
    @Ignore //TODO Переделать тест по окончанию реализации update команды
    public void testFindAndUpdate() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("clear|users");
        in.addCommand("yes");
        in.addCommand("insert|users|id|11|name|shtramak|password|qqq");
        in.addCommand("insert|users|id|8|name|Cheburator|password|qwerty");
        in.addCommand("insert|users|id|1|name|Chupakabra|password|qwerty");
        in.addCommand("find|users");
        in.addCommand("update|users");
        in.addCommand("1");
        in.addCommand("password|ChuPass");
        in.addCommand("find|users");
        in.addCommand("exit");

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "You are going to delete all data from users! Are you sure? [Yes/No]" + LINE_SEPARATOR +
                "Data from users was successfully deleted" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to the current table" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to the current table" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Data successfully added to the current table" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| id         | name       | password   |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| 11         | shtramak   | qqq        |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| 8          | Cheburator | qwerty     |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| 1          | Chupakabra | qwerty     |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Please input existing id number you want to update. Must be integer:" + LINE_SEPARATOR +
                "Now please input update data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value#" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| id         | name       | password   |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| 11         | shtramak   | qqq        |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| 8          | Cheburator | qwerty     |" + LINE_SEPARATOR +
                "----------------------------------------" + LINE_SEPARATOR +
                "| 1          | Chupakabra | ChuPass    |" + LINE_SEPARATOR +
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

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
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
                "Exiting before connection... Good luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testHelp() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("help");
        in.addCommand("exit");

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR + LINE_SEPARATOR +
                "List of available commands:" + LINE_SEPARATOR +
                "\tconnect|database|userName|password" + LINE_SEPARATOR +
                "\t\tconnection with database under userName with password" + LINE_SEPARATOR +
                "\tlist" + LINE_SEPARATOR +
                "\t\tdisplay available tables in selected database" + LINE_SEPARATOR +
                "\tfind|tableName" + LINE_SEPARATOR +
                "\t\tdisplay available tables in selected database" + LINE_SEPARATOR +
                "\tclear|tableName" + LINE_SEPARATOR +
                "\t\tdelete all data from selected table" + LINE_SEPARATOR +
                "\tinsert|tableName|col1Name|value1|col2Name|value2|...col#Name|value#" + LINE_SEPARATOR +
                "\t\tinsert entered data to selected table" + LINE_SEPARATOR +
                "\tupdate|tableName" + LINE_SEPARATOR +
                "\t\tupdate entry in selected table using own command interface" + LINE_SEPARATOR +
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

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
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
        in.addCommand("clear|users");
        in.addCommand("no");
        in.addCommand("exit");

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "clear command failed because of wrong input. Use 'help' command for details" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "You are going to delete all data from user! Are you sure? [Yes/No]" + LINE_SEPARATOR +
                "Please enter Yes or No. No other options available" + LINE_SEPARATOR +
                "Don't worry! Data is safe" + LINE_SEPARATOR + LINE_SEPARATOR +
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

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "DataSet is empty" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "'insert' command failed because of wrong input: incorrect number of elements. Use 'help' command for details" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());

    }

    @Test
    @Ignore //TODO Переделать тест по окончанию реализации update команды
    public void testWrongUpdate() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("update|user|user");
        in.addCommand("update|user");
        in.addCommand("oops");
        in.addCommand("1");
        in.addCommand("exit");
        in.addCommand("update|user");
        in.addCommand("1");
        in.addCommand("password|ChuPass|oops");
        in.addCommand("password|ChuPass");
        in.addCommand("exit");

        String expected = "Hello user! For first connection to database please enter required input data using next format:" + LINE_SEPARATOR +
                "connect|database|userName|password" + LINE_SEPARATOR +
                "Hello postgres! Welcome to sqlcmd database" + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "update command failed because of wrong input. Use 'help' command for details" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Please input existing id number you want to update. Must be integer:" + LINE_SEPARATOR +
                "The entered value is not an integer! Enter an integer value:" + LINE_SEPARATOR +
                "Now please input update data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value#" + LINE_SEPARATOR +
                "Update command failed!" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Please input existing id number you want to update. Must be integer:" + LINE_SEPARATOR +
                "Now please input update data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value#" + LINE_SEPARATOR +
                "Wrong input! Input must be according to the template described above." + LINE_SEPARATOR +
                "Try again using correct format col1Name|value1|col2Name|value2|...col#Name|value# or enter 'exit' command" + LINE_SEPARATOR +
                "Smth goes wrong... Reason: ОШИБКА: ошибка синтаксиса (примерное положение: \"user\")\n" + // This "\n" comes from postgres SQLException
                "  Позиция: 8" + LINE_SEPARATOR + LINE_SEPARATOR +
                "Type a command or 'help' to see the command list" + LINE_SEPARATOR +
                "Good Luck!" + LINE_SEPARATOR;

        Main.main(new String[0]);

        assertEquals(expected, getData());
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