package controller.command;

import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.controller.command.Command;
import ua.com.shtramak.controller.command.ConnectToDB;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConnectToDBTest {
    private DataBaseManager dataBaseManager;
    private View view;
    private Command command;

    @Before
    public void init(){
        dataBaseManager = mock(DataBaseManager.class);
        view = mock(View.class);
        command = new ConnectToDB(dataBaseManager,view);
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
    public void testConnectAfterConnection(){
        when(dataBaseManager.isConnected()).thenReturn(true);
        command.isDetected("connect|");
        verify(view).writeln("Disconnection from current database...");
    }

}
