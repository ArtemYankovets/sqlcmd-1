package ua.com.shtramak.sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.view.View;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ShowTableListTest {
    private DataBaseManager dataBaseManager;
    private View view;
    private AbstractCommand command;

    @Before
    public void init(){
        dataBaseManager = mock(DataBaseManager.class);
        view = mock(View.class);
        command = new ShowTablesList(dataBaseManager,view);
    }

    @Test
    public void testShowTableListDescription(){
        String expected = "\tlist" +
                System.lineSeparator() +
                "\t\tdisplay available tables in selected database";
        assertEquals(expected,command.description());
    }

    @Test
    public void testIsDetectedWithCorrectData(){
        assertTrue(command.isDetected("list"));
    }

    @Test
    public void testIsDetectedWithWrongtData(){
        assertFalse(command.isDetected("lict"));
    }

    @Test
    public void testShowTableListEmptyData() throws NotExecutedRequestException {
        String[] tableNames = null;
        when(dataBaseManager.getTableNames()).thenReturn(tableNames);
        command.execute("list");
        verify(view).writeln("Database is empty. Nothing to show");
    }

    @Test
    public void testShowTableListWithData() throws NotExecutedRequestException {
        String[] tableNames = new  String[]{"table1","table2"};
        when(dataBaseManager.getTableNames()).thenReturn(tableNames);
        command.execute("list");
        verify(view).writeln("Here's the names of available tables: " + Arrays.toString(dataBaseManager.getTableNames()));
    }
}
