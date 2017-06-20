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
                "Hello, postgres! Welcome to sqlcmd database \n" +
                "\n" +
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
                "Hello, postgres! Welcome to sqlcmd database \n" +
                "\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Here's the names of available tables: [users]\r\n"+
                "\n" +
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
                "Hello, postgres! Welcome to sqlcmd database \n" +
                "\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Wrong table name! Check if your table exists from the list below\r\n" +
                "List with available tables: [users]\r\n"+
                "\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Good Luck!\r\n";

        Main.main(new String[0]);

        assertEquals(expected, getData());
    }

    @Test
    public void testFindAfterConnection() {
        in.addCommand("connect|sqlcmd|postgres|postgres");
        in.addCommand("find|users");
        in.addCommand("exit");

        String expected = "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
                "Hello, postgres! Welcome to sqlcmd database \n" +
                "\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "----------------------------------------\r\n" +
                "| id         | name       | password   |\r\n" +
                "----------------------------------------\r\n" +
                "| 11         | shtramak   | qqq        |\r\n" +
                "----------------------------------------\r\n" +
                "| 8          | Cheburator | qwerty     |\r\n" +
                "----------------------------------------\r\n" +
                "| 1          | Chupakabra | qwerty     |\r\n" +
                "----------------------------------------\r\n"+
                "\n" +
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
                "Hello, postgres! Welcome to sqlcmd database \n" +
                "\n" +
                "Type a command or 'help' to see the command list\r\n" +
                "Hello, postgres! Welcome to postgres database \n"+
                "\n" +
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
                "Try again!\n"+
                "\r\n" +
                "Hello, user! For first connection to database please enter required input data using next format:\n" +
                "connect|database|userName|password\r\n" +
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