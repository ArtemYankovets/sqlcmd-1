package ua.com.shtramak.sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NoJDBCDriverException;
import ua.com.shtramak.sqlcmd.model.exceptions.UnsuccessfulConnectionException;
import ua.com.shtramak.sqlcmd.utils.Commands;
import ua.com.shtramak.sqlcmd.view.View;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ConnectToDBTest {
    private DataBaseManager dataBaseManager;
    private View view;
    private AbstractCommand command;

    @Before
    public void init() {
        dataBaseManager = mock(DataBaseManager.class);
        view = mock(View.class);
        command = new ConnectToDB(dataBaseManager, view);
    }

    @Test
    public void testConnectIsDetected() {
        assertTrue(command.isDetected("connect|"));
    }

    @Test
    public void testWrongConnectIsDetected() {
        assertFalse(command.isDetected("conect"));
    }

    @Test
    public void testConnectDescription() {
        String expected = "\tconnect|database|userName|password" +
                System.lineSeparator() +
                "\t\tconnection with database under userName with password";
        assertEquals(expected, command.description());
    }

    @Test
    public void testConnectAfterConnectionWithDisconnection() {
        when(dataBaseManager.isConnected()).thenReturn(true);
        when(view.read()).thenReturn("Yes");
        command.execute("connect|database|userName|password");
        verify(view).writeln("You are going to disconnect from current database. Proceed? [Yes/No]");
        verify(view).writeln("Disconnection from current database...");
        verify(view).writeln("Hello userName! Welcome to database database");

    }
    @Test
    public void testConnectAfterConnectionWithoutDisconnection() {
        when(dataBaseManager.isConnected()).thenReturn(true);
        when(view.read()).thenReturn("No");
        command.execute("connect|database|userName|password");
        verify(view).writeln("You are going to disconnect from current database. Proceed? [Yes/No]");
        verify(view).writeln("Connection failed!");
        verify(view).writeln("Error message: Incorrect input. Please enter required input data in format: connect|database|userName|password");
    }

    @Test
    public void testWrongConnectCommandLength() {
        command.execute("connect|database|userName");
        verify(view).writeln("Connection failed!");
    }

    @Test
    public void testConnectWithFullCommand() throws NoJDBCDriverException, UnsuccessfulConnectionException {
        String fullCommand = "connect|database|userName|userPassword";
        String[] commands = Commands.arrayOf(fullCommand);
        command.execute(fullCommand);
        String dbName = commands[1];
        String userName = commands[2];
        String userPassword = commands[3];
        verify(dataBaseManager).connect(dbName, userName, userPassword);
        verify(view).writeln(String.format("Hello %s! Welcome to %s database", userName, dbName));
    }

}
