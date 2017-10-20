package ua.com.shtramak.sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.view.View;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DropTableTest {
    private DataBaseManager dataBaseManager;
    private View view;
    private AbstractCommand command;

    @Before
    public void init() {
        dataBaseManager = mock(DataBaseManager.class);
        view = mock(View.class);
        command = new DropTable(dataBaseManager, view);
    }

    @Test
    public void testDropIsDetected() {
        assertTrue(command.isDetected("drop|"));
    }

    @Test
    public void testWrongDropIsDetected() {
        assertFalse(command.isDetected("drap"));
    }

    @Test
    public void testDropDescription() {
        String expected = "\tdrop|tableName" +
                System.lineSeparator() +
                "\t\tdrop an existing table in a database";
        assertEquals(expected, command.description());
    }

    @Test
    public void testWrongDropTable() {
        command.execute("drop|");
        verify(view).writeln("Incorrect usage of 'drop' command. Use 'help' command for details");
    }

    @Test
    public void testDropUnexistingTable() throws NotExecutedRequestException {
        String tableName = "tableName";
        when(dataBaseManager.hasTable(tableName)).thenReturn(false);
        Set<String> tables = new TreeSet<>(Arrays.asList(new String[]{"table1", "table2"}));
        when(dataBaseManager.getTableNames()).thenReturn(tables);
        command.execute("drop|tableName");
        verify(view).writeln(String.format("Table %s doesn't exists! See the list with available tables below:", tableName));
        verify(view).writeln("ShowTablesList with available tables: " + dataBaseManager.getTableNames());
    }

    @Test
    public void testDropTableWithYesAnswer() throws NotExecutedRequestException {
        String tableName = "tableName";
        when(view.read()).thenReturn("yes");
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        command.execute("drop|tableName");
        verify(dataBaseManager).dropTable(tableName);
        verify(view).writeln(String.format("Table %s was successfully dropped from database",tableName));
    }

    @Test
    public void testDropTableWithNoAnswer() throws NotExecutedRequestException {
        String tableName = "tableName";
        when(view.read()).thenReturn("no");
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        command.execute("drop|tableName");
        verify(view).writeln("Command 'drop' was canceled...");
    }
}
