package controller.command;

import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.controller.command.Command;
import ua.com.shtramak.controller.command.UpdateTableData;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UpdateTableDataTest {
    private DataBaseManager dataBaseManager;
    private View view;
    private Command command;

    @Before
    public void init(){
        dataBaseManager = mock(DataBaseManager.class);
        view = mock(View.class);
        command = new UpdateTableData(dataBaseManager,view);
    }

    @Test
    public void testShowTableListDescription(){
        String expected = "\tupdateTable|tableName" +
                System.lineSeparator() +
                "\t\tupdate entry in selected table using own command interface";
        assertEquals(expected,command.description());
    }

    @Test
    public void testIsDetectedWithCorrectData(){
        assertTrue(command.isDetected("updateTable|"));
    }

    @Test
    public void testIsDetectedWithWrongData(){
        assertFalse(command.isDetected("update|"));
    }

    @Test
    public void testUpdateTableDataWithWrongNumberOfParameters(){
        String inputCommand = "updateTable|tableName|col1Name|col1Value";
        command.execute(inputCommand);
        verify(view).writeln("updateTableData command failed because of wrong input. Use 'help' command for details");
    }

    @Test
    public void testUpdateTableDataOfUnexistTable(){
        String tableName = "UnexistingTable";
        String[] existingTables = new String[]{"table1","table2"};
        when(dataBaseManager.hasTable(tableName)).thenReturn(false);
        when(dataBaseManager.getTableNames()).thenReturn(existingTables);
        command.execute("updateTable|"+tableName);
        verify(view).writeln(String.format("Table '%s' doesn't exists! See below the list with available tables:", tableName));
        verify(view).writeln("Available tables: " + Arrays.toString(dataBaseManager.getTableNames()));
    }

    @Test
    public void testUpdateTableDataOfExistingTableWithWrongColumnName(){
        String tableName = "ExistingTable";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        String colName = "UnexistingColumnName";
        when(view.read()).thenReturn(colName);
        when(dataBaseManager.hasColumn(tableName, colName)).thenReturn(false);
        String[] tableColumns = new String[] {"col1","col2","col3"};
        when(dataBaseManager.getTableColumns(tableName)).thenReturn(tableColumns);
        command.execute("updateTable|"+tableName);
        verify(view).writeln("Please input wanted 'colName' and 'value' of the row you want to update:");
        verify(view).write("Enter column name: ");
        verify(view).writeln(String.format("Column '%s' doesn't exists! See below the list with available columns of table %s:", colName, tableName));;
        verify(view).writeln("Available columns: " + Arrays.toString(dataBaseManager.getTableColumns(tableName)));
    }

    @Test
    public void testUpdateTableDataOfExistingTableUsingUnexistValue(){
        String tableName = "ExistTable";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        String colName = "col1";
        String value = "UnexistValue";
        when(view.read()).thenReturn(colName).thenReturn(value);
        when(dataBaseManager.hasColumn(tableName, colName)).thenReturn(true);
        when(dataBaseManager.hasValue(tableName,colName,value)).thenReturn(false);
        command.execute("updateTable|"+tableName);
        verify(view).writeln("Please input wanted 'colName' and 'value' of the row you want to update:");
        verify(view).write("Enter column name: ");
        verify(view,times(2)).writeln("");
        verify(view).write("Enter value: ");
        verify(view).writeln(String.format("There's no value '%s' in column '%s'",value, colName));
    }

}
