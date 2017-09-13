package ua.com.shtramak.sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.DataSet;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.view.View;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ShowTableDataTest {
    private DataBaseManager dataBaseManager;
    private View view;
    private AbstractCommand command;

    @Before
    public void init() {
        dataBaseManager = mock(DataBaseManager.class);
        view = mock(View.class);
        command = new ShowTableData(dataBaseManager, view);
    }

    @Test
    public void testDescription() {
        String expected = "\tshow|tableName" +
                System.lineSeparator() +
                "\t\tdisplay table data from selected database";
        assertEquals(expected, command.description());
    }

    @Test
    public void testIsDetectedWithCorrectData() {
        assertTrue(command.isDetected("show|"));
    }

    @Test
    public void testIsDetectedWithWrongData() {
        assertFalse(command.isDetected("shou|"));
    }

    @Test
    public void testShowTableDataWithUnexistingTable() throws NotExecutedRequestException {
        String tableName = "tableName";
        when(dataBaseManager.hasTable(tableName)).thenReturn(false);
        Set<String> names = new TreeSet<>(Arrays.asList(new String[]{"users", "cars"}));
        when(dataBaseManager.getTableNames()).thenReturn(names);
        command.execute("show|" + tableName);
        verify(view, times(1)).write(String.format("Table %s doesn't exists! Available tables: ", tableName));
        verify(view, times(1)).writeln(dataBaseManager.getTableNames().toString());
        assertEquals(names, dataBaseManager.getTableNames());
    }

    @Test
    public void testShowTableDataEmtyTable() throws NotExecutedRequestException {
        String tableName = "tableName";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        Set<String> columnsNames = new LinkedHashSet<>(Arrays.asList(new String[]{"id", "name", "password"}));
        when(dataBaseManager.getTableColumns(tableName)).thenReturn(columnsNames);
        List<DataSet> tableData = new ArrayList<>();
        when(dataBaseManager.getTableData(tableName)).thenReturn(tableData);
        command.execute("show|" + tableName);
        ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeln(arg.capture());
        List<String> expected = Arrays.asList(
                "----------------------------------------",
                "| id         | name       | password   |",
                "----------------------------------------");
        assertArrayEquals(expected.toArray(), arg.getAllValues().toArray());
    }

    @Test
    public void testShowTableDataCorrectTable() throws NotExecutedRequestException {
        String tableName = "tableName";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);

        Set<String> columnsNames = new LinkedHashSet<>(Arrays.asList(new String[]{"id", "name", "password"}));
        when(dataBaseManager.getTableColumns(tableName)).thenReturn(columnsNames);

        DataSet value1 = new DataSet();
        value1.put("id", 1);
        value1.put("name", "testName");
        value1.put("password", "testPassword1");
        DataSet value2 = new DataSet();
        value2.put("id", 2);
        value2.put("name", "testName");
        value2.put("password", "testPassword2");
        List<DataSet> tableData = new ArrayList<>(Arrays.asList(new DataSet[]{value1, value2}));
        when(dataBaseManager.getTableData(tableName)).thenReturn(tableData);

        command.execute("show|" + tableName);
        ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeln(arg.capture());
        List<String> expected = Arrays.asList(
                "----------------------------------------",
                "| id         | name       | password   |",
                "----------------------------------------",
                "| 1          | testName   | testPassword1 |",
                "----------------------------------------",
                "| 2          | testName   | testPassword2 |",
                "----------------------------------------");
        assertArrayEquals(expected.toArray(), arg.getAllValues().toArray());
    }
}
