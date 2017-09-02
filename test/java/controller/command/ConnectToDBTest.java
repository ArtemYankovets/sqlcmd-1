package controller.command;

import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.controller.command.AbstractCommand;
import ua.com.shtramak.controller.command.ConnectToDB;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.exceptions.NoJDBCDriverException;
import ua.com.shtramak.model.exceptions.UnsuccessfulConnectionException;
import ua.com.shtramak.view.View;

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
    public void testConnectAfterConnection() {
        when(dataBaseManager.isConnected()).thenReturn(true);
        command.isDetected("connect|");
        verify(view).writeln("Disconnection from current database...");
    }

    @Test
    public void testWrongConnectCommandLength() {
        command.execute("connect|database|userName");
        verify(view).writeln("Connection failed!");
    }

    @Test
    public void testConnectWithFullCommand() throws NoJDBCDriverException, UnsuccessfulConnectionException {
        String fullCommand = "connect|database|userName|userPassword";
        command.execute(fullCommand);
        String dbName = fullCommand.split("\\|")[1];
        String userName = fullCommand.split("\\|")[2];
        String userPassword = fullCommand.split("\\|")[3];
        verify(dataBaseManager).connect(dbName, userName, userPassword);
        verify(view).writeln(String.format("Hello %s! Welcome to %s database", userName, dbName));
    }

}
