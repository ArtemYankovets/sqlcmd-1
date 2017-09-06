package controller.command;

import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.controller.command.AbstractCommand;
import ua.com.shtramak.controller.command.Exit;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExitTest {
    private View view;
    private AbstractCommand command;

    @Before
    public void init() {
        DataBaseManager dataBaseManager = mock(DataBaseManager.class);
        view = mock(View.class);
        command = new Exit(dataBaseManager, view);
    }

    @Test
    public void testExitIsDetected() {
        assertTrue(command.isDetected("exit"));
    }

    @Test
    public void testWrongExitIsDetected() {
        assertFalse(command.isDetected("ekzit"));
    }

    @Test
    public void testExitDescription() {
        String expected = "\texit" +
                System.lineSeparator() +
                "\t\tto exit from this session";
        assertEquals(expected, command.description());
    }

    @Test
    public void testExitExecute() {
        command.execute("exit");
        verify(view).writeln("Good Luck!");
    }
}
