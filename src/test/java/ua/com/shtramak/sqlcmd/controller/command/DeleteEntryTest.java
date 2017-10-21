package ua.com.shtramak.sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.view.View;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DeleteEntryTest {
    private DataBaseManager dataBaseManager;
    private View view;
    private AbstractCommand command;

    @Before
    public void init() {
        dataBaseManager = mock(DataBaseManager.class);
        view = mock(View.class);
        command = new DeleteEntry(dataBaseManager, view);
    }

    @Test
    public void testDeleteEntryDescription() {
        String expected = "\tdeleteEntry|tableName" +
                System.lineSeparator() +
                "\t\tdeletes entry in selected table using own command interface";
        assertEquals(expected, command.description());
    }

    @Test
    public void testIsDetectedWithCorrectData() {
        assertTrue(command.isDetected("deleteEntry|"));
    }

    @Test
    public void testIsDetectedWithWrongData() {
        assertFalse(command.isDetected("delete|"));
    }

    @Test
    public void testDeleteEntryWithWrongNumberOfParameters() {
        String inputCommand = "deleteEntry|tableName|col1Name|col1Value";
        command.execute(inputCommand);
        verify(view).writeln("'deleteEntry' command failed because of wrong input. Use 'help' command for details");
    }

    @Test
    public void testDeleteEntryOfUnexistTable() throws NotExecutedRequestException {
        String tableName = "UnexistingTable";
        Set<String> existingTables = new TreeSet<>(Arrays.asList(new String[]{"table1", "table2"}));
        when(dataBaseManager.hasTable(tableName)).thenReturn(false);
        when(dataBaseManager.getTableNames()).thenReturn(existingTables);
        command.execute("deleteEntry|" + tableName);
        verify(view).writeln(String.format("Table '%s' doesn't exists! See below the list with available tables:", tableName));
        verify(view).writeln("Available tables: " + dataBaseManager.getTableNames());
    }

    @Test
    public void testDeleteEntryOfExistingTableWithWrongColumnName() throws NotExecutedRequestException {
        String tableName = "ExistingTable";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        String colName = "UnexistingColumnName";
        when(view.read()).thenReturn(colName);
        when(dataBaseManager.hasColumn(tableName, colName)).thenReturn(false);
        Set<String> tableColumns = new LinkedHashSet<>(Arrays.asList(new String[]{"col1", "col2", "col3"}));
        when(dataBaseManager.getTableColumns(tableName)).thenReturn(tableColumns);
        command.execute("deleteEntry|" + tableName);
        verify(view).writeln("Please input wanted 'colName' and 'value' of the row you want to update:");
        verify(view).write("Enter column name: ");
        verify(view).writeln(String.format("Column '%s' doesn't exists! See below the list with available columns of table %s:", colName, tableName));
        verify(view).writeln("Available columns: " + dataBaseManager.getTableColumns(tableName));
    }

    @Test
    public void testDeleteEntryOfExistingTableUsingUnexistValue() throws NotExecutedRequestException {
        String tableName = "ExistTable";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        String colName = "col1";
        String value = "UnexistValue";
        when(view.read()).thenReturn(colName).thenReturn(value);
        when(dataBaseManager.hasColumn(tableName, colName)).thenReturn(true);
        when(dataBaseManager.hasValue(tableName, colName, value)).thenReturn(false);
        command.execute("deleteEntry|" + tableName);
        verify(view).writeln("Please input wanted 'colName' and 'value' of the row you want to update:");
        verify(view).write("Enter column name: ");
        verify(view, times(2)).writeln("");
        verify(view).write("Enter value: ");
        verify(view).writeln(String.format("There's no value '%s' in column '%s'", value, colName));
    }

    @Test
    public void testUpdateTableDataOfExistingTableWithFullCorrectData() throws NotExecutedRequestException {
        String tableName = "ExistTable";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        String colName = "col1";
        String value = "value1";
        when(view.read()).thenReturn(colName).thenReturn(value).thenReturn("exit");
        when(dataBaseManager.hasColumn(tableName, colName)).thenReturn(true);
        when(dataBaseManager.hasValue(tableName, colName, value)).thenReturn(true);
        command.execute("deleteEntry|" + tableName);
        verify(view).writeln("Please input wanted 'colName' and 'value' of the row you want to update:");
        verify(view).write("Enter column name: ");
        verify(view, times(2)).writeln("");
        verify(view).write("Enter value: ");
        verify(dataBaseManager).deleteTableData(tableName, colName, value);
        verify(view).writeln("Entry successfully deleted...");
    }
}
