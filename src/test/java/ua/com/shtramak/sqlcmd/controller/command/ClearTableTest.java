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

public class ClearTableTest {
    private DataBaseManager dataBaseManager;
    private View view;
    private AbstractCommand command;

    @Before
    public void init() {
        dataBaseManager = mock(DataBaseManager.class);
        view = mock(View.class);
        command = new ClearTable(dataBaseManager, view);
    }

    @Test
    public void testClearIsDetected() {
        assertTrue(command.isDetected("clear|"));
    }

    @Test
    public void testWrongClearIsDetected() {
        assertFalse(command.isDetected("klear|"));
    }

    @Test
    public void testClearDescription() {
        String expected = "\tclear|tableName" +
                System.lineSeparator() +
                "\t\tdelete all data from selected table";
        assertEquals(expected, command.description());
    }

    @Test
    public void testClearExecuteWithAnswerYes() throws NotExecutedRequestException {
        when(view.read()).thenReturn("yes");
        String tableName = "user";
        correctExecuting(tableName);
        verify(dataBaseManager).clear(tableName);
        verify(view).writeln(String.format("Data from '%s' was successfully deleted", tableName));
    }

    @Test
    public void testClearExecuteWithAnswerNo() throws NotExecutedRequestException {
        when(view.read()).thenReturn("no");
        correctExecuting("user");
        verify(view).writeln("Command 'clear' was canceled...");
    }

    @Test
    public void testClearWrongTable() throws NotExecutedRequestException {
        String tableName="wrongName";
        when((dataBaseManager).hasTable(tableName)).thenReturn(false);
        Set<String> tables = new TreeSet<>(Arrays.asList(new String[]{"users"}));
        when(dataBaseManager.getTableNames()).thenReturn(tables);
        command.execute("clear|"+tableName);
        verify(view).writeln(String.format("Table %s doesn't exists! See the list with available tables: %s", tableName, tables));
    }

    @Test
    public void testClearWrongNumberOfParameters(){
        command.execute("clear|");
        verify(view).writeln("'clear' command failed because of wrong input. Use 'help' command for details");
    }

    private void correctExecuting(String tableName) throws NotExecutedRequestException {
        when((dataBaseManager).hasTable(tableName)).thenReturn(true);
        command.execute("clear|" + tableName);
        verify(view).writeln(String.format("You are going to delete all data from table '%s'! Are you sure? [Yes/No]", tableName));
    }
}
