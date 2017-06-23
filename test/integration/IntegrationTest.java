package integration;

import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.controller.Main;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    private ByteArrayOutputStream out;
    private ConsoleInputStream in;

    public String getOut() {
        return out.toString();
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

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Good Luck!\r\n";
        assertEquals(expected, getData());
    }

    @Test
    public void testCommandBeforeConnect() {
        in.addCommand("list");
        in.addCommand("exit");

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Connection failed!\r\n" +
                "Reason: Incorrect input. Please enter required input data in format: connect|database|userName|password\r\n" +
                "Try again!\n" +
                "\r\n" +
                "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Good Luck!\r\n";

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testAnyCommandBeforeConnect() {
        in.addCommand("help");
        in.addCommand("exit");

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Connection failed!\r\n" +
                "Reason: Incorrect input. Please enter required input data in format: connect|database|userName|password\r\n" +
                "Try again!\n" +
                "\r\n" +
                "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Good Luck!\r\n";

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("exit");

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testListAfterConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("list");
        in.addCommand("exit");

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Here's the names of available tables: [users]\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testWrongFindAfterConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("find|user");
        in.addCommand("exit");

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Wrong table name! Check if your table exists from the list below\r\n" +
                "List with available tables: [users]\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testClear() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("clear|users");
        in.addCommand("yes");
        in.addCommand("exit");

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "You are going to delete all data from users! Are you sure? [Yes/No]\r\n" +
                "Data from users was successfully deleted\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

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

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "You are going to delete all data from users! Are you sure? [Yes/No]\r\n" +
                "Data from users was successfully deleted\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Nothig to show! No data find. First insert data to the table using 'insert' command\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

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

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "You are going to delete all data from users! Are you sure? [Yes/No]\r\n" +
                "Data from users was successfully deleted\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Data successfully added to current table\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Data successfully added to current table\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Data successfully added to current table\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

        Main.main(new String[0]);

        assertEquals(expected, getData());


    }

    @Test
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

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "You are going to delete all data from users! Are you sure? [Yes/No]\r\n" +
                "Data from users was successfully deleted\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Data successfully added to current table\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Data successfully added to current table\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Data successfully added to current table\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "----------------------------------------\r\n" +
                "| id         | name       | password   |\r\n" +
                "----------------------------------------\r\n" +
                "| 11         | shtramak   | qqq        |\r\n" +
                "----------------------------------------\r\n" +
                "| 8          | Cheburator | qwerty     |\r\n" +
                "----------------------------------------\r\n" +
                "| 1          | Chupakabra | qwerty     |\r\n" +
                "----------------------------------------\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Please input existing id number you want to update. Must be integer:\r\n" +
                "Now please input update data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value#\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "----------------------------------------\r\n" +
                "| id         | name       | password   |\r\n" +
                "----------------------------------------\r\n" +
                "| 11         | shtramak   | qqq        |\r\n" +
                "----------------------------------------\r\n" +
                "| 8          | Cheburator | qwerty     |\r\n" +
                "----------------------------------------\r\n" +
                "| 1          | Chupakabra | ChuPass    |\r\n" +
                "----------------------------------------\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testConnectAfterConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("connect|postgres|postgres|postgres");
        in.addCommand("exit");

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Hello, postgres! Welcome to postgres database \n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testConnectWithError() {
        in.addCommand("connect|sqlcmd|postgres|wrongPassword");
        in.addCommand("exit");

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Reason: Dear, postgres! Your input data was incorrect!\n" +
                "ВАЖНО: пользователь \"postgres\" не прошёл проверку подлинности (по паролю) (pgjdbc: autodetected server-encoding to be windows-1251, if the message is not readable, please check database logs and/or host, port, dbname, user, password, pg_hba.conf)\r\n" +
                "Try again!\n" +
                "\r\n" +
                "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Good Luck!\r\n";

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testHelp() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("help");
        in.addCommand("exit");

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n\n" +
                "Type a command or 'help' to see the command list\r\n\n" +
                "List of available commands:\r\n" +
                "\tlist\r\n" +
                "\t\tdisplay available tables in selected database\r\n" +
                "\tfind|tableName\r\n" +
                "\t\tdisplay available tables in selected database\r\n" +
                "\tclear|tableName\r\n" +
                "\t\tdelete all data from selected table\r\n" +
                "\tinsert|tableName|col1Name|value1|col2Name|value2|...col#Name|value#\r\n" +
                "\t\tinsert entered data to selected table\r\n" +
                "\tupdate|tableName\r\n" +
                "\t\tupdate entry in selected table using own command interface\r\n" +
                "\texit\r\n" +
                "\t\tto exit from this session\r\n" +
                "\thelp\r\n" +
                "\t\twill display this message again... try it! )\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testWrongCommandAfterConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("hepl");
        in.addCommand("exit");

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Command hepl doesn't exists. Use 'help' command for details\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

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

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "clear command failed because of wrong input. Use 'help' command for details\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "You are going to delete all data from user! Are you sure? [Yes/No]\r\n" +
                "Please enter Yes or No. No other options available\r\n" +
                "Don't worry! Data is in safe\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testWrongInsert() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("insert|user");
        in.addCommand("insert|user|data");
        in.addCommand("exit");

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Entered data can't be inserted to the table because of wrong format. Use 'help' command for details\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "insert command failed because of wrong input: number of elements is incorrect. Use 'help' command for details\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

        Main.main(new String[0]);

        assertEquals(expected, getData());

    }

    @Test
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

        in.addCommand("exit");

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "update command failed because of wrong input. Use 'help' command for details\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Please input existing id number you want to update. Must be integer:\r\n" +
                "Entered value is not an integer! Enter an integer value:\r\n" +
                "Now please input update data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value#\r\n" +
                "Update command failed!\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Please input existing id number you want to update. Must be integer:\r\n" +
                "Now please input update data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value#\r\n" +
                "Wrong input! Intput must be according to the template described above.\r\n" +
                "Try again using correct format col1Name|value1|col2Name|value2|...col#Name|value# or enter 'exit' command\r\n" +
                "Smth goes wrong... Reason: ОШИБКА: ошибка синтаксиса (примерное положение: \"user\")\n" +
                "  Позиция: 8\r\n\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

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