package ua.com.shtramak.sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.DataSet;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.view.View;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UpdateTableDataTest {
    private DataBaseManager dataBaseManager;
    private View view;
    private AbstractCommand command;

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
                "\t\tupdates entry in selected table using own command interface";
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
    public void testUpdateTableDataOfUnexistTable() throws NotExecutedRequestException {
        String tableName = "UnexistingTable";
        Set<String> existingTables = new TreeSet<>(Arrays.asList(new String[]{"table1","table2"}));
        when(dataBaseManager.hasTable(tableName)).thenReturn(false);
        when(dataBaseManager.getTableNames()).thenReturn(existingTables);
        command.execute("updateTable|"+tableName);
        verify(view).writeln(String.format("Table '%s' doesn't exists! See below the list with available tables:", tableName));
        verify(view).writeln("Available tables: " + dataBaseManager.getTableNames());
    }

    @Test
    public void testUpdateTableDataOfExistingTableWithWrongColumnName() throws NotExecutedRequestException {
        String tableName = "ExistingTable";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        String colName = "UnexistingColumnName";
        when(view.read()).thenReturn(colName);
        when(dataBaseManager.hasColumn(tableName, colName)).thenReturn(false);
        Set<String> tableColumns = new LinkedHashSet<>(Arrays.asList(new String[] {"col1","col2","col3"}));
        when(dataBaseManager.getTableColumns(tableName)).thenReturn(tableColumns);
        command.execute("updateTable|"+tableName);
        verify(view).writeln("Please input wanted 'colName' and 'value' of the row you want to update:");
        verify(view).write("Enter column name: ");
        verify(view).writeln(String.format("Column '%s' doesn't exists! See below the list with available columns of table %s:", colName, tableName));
        verify(view).writeln("Available columns: " + dataBaseManager.getTableColumns(tableName));
    }

    @Test
    public void testUpdateTableDataOfExistingTableUsingUnexistValue() throws NotExecutedRequestException {
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

    @Test
    public void testUpdateTableDataOfExistingTableWithWrongDataFormatInput() throws NotExecutedRequestException {
        String tableName = "ExistTable";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        String colName = "col1";
        String value = "value1";
        String inputUpdateData = "col1Name|value1|col2Name";
        when(view.read()).thenReturn(colName).thenReturn(value).thenReturn(inputUpdateData).thenReturn("exit");
        when(dataBaseManager.hasColumn(tableName, colName)).thenReturn(true);
        when(dataBaseManager.hasValue(tableName,colName,value)).thenReturn(true);
        command.execute("updateTable|"+tableName);
        verify(view).writeln("Please input wanted 'colName' and 'value' of the row you want to update:");
        verify(view).write("Enter column name: ");
        verify(view,times(2)).writeln("");
        verify(view).write("Enter value: ");
        verify(view).writeln("Now please input updateTableData data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value# or exit");
        verify(view).writeln("Wrong input! Input must be according to the template");
        verify(view).writeln("Try again using correct format col1Name|value1|col2Name|value2|...col#Name|value# or enter 'exit' command");
        verify(view).writeln("Update command failed!");
    }

    @Test
    public void testUpdateTableDataOfExistingTableWithFullCorrectData() throws NotExecutedRequestException {
        String tableName = "ExistTable";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        String colName = "col1";
        String value = "value1";
        String inputUpdateData = "col1Name|value1|col2Name|value2";
        when(view.read()).thenReturn(colName).thenReturn(value).thenReturn(inputUpdateData).thenReturn("exit");
        when(dataBaseManager.hasColumn(tableName, colName)).thenReturn(true);
        when(dataBaseManager.hasValue(tableName,colName,value)).thenReturn(true);
        DataSet updateData = new DataSet();
        updateData.put("col1Name","value1");
        updateData.put("col2Name","value2");
        command.execute("updateTable|"+tableName);
        verify(view).writeln("Please input wanted 'colName' and 'value' of the row you want to update:");
        verify(view).write("Enter column name: ");
        verify(view,times(2)).writeln("");
        verify(view).write("Enter value: ");
        verify(view).writeln("Now please input updateTableData data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value# or exit");
        verify(dataBaseManager).updateTableData(tableName, colName, value, updateData);
        verify(view).writeln("Data successfully updated...");
    }

    @Test
    public void testUpdateTableDataOfExistingTableWithDataBaseManagerException() throws NotExecutedRequestException {
        String tableName = "ExistTable";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        String colName = "col1";
        String value = "value1";
        String inputUpdateData = "col1Name|value1|col2Name|value2";
        when(view.read()).thenReturn(colName).thenReturn(value).thenReturn(inputUpdateData).thenReturn("exit");
        when(dataBaseManager.hasColumn(tableName, colName)).thenReturn(true);
        when(dataBaseManager.hasValue(tableName,colName,value)).thenReturn(true);
        DataSet updateData = new DataSet();
        updateData.put("col1Name","value1");
        updateData.put("col2Name","value2");
        String message = "Exception message";
        doThrow(new NotExecutedRequestException(message)).when(dataBaseManager).updateTableData(tableName, colName, value, updateData);
        command.execute("updateTable|"+tableName);
        verify(view).writeln("Please input wanted 'colName' and 'value' of the row you want to update:");
        verify(view).write("Enter column name: ");
        verify(view,times(2)).writeln("");
        verify(view).write("Enter value: ");
        verify(view).writeln("Now please input updateTableData data for this entry in format: col1Name|value1|col2Name|value2|...col#Name|value# or exit");
        verify(dataBaseManager).updateTableData(tableName, colName, value, updateData);
        verify(view).writeln(message);

    }

}
