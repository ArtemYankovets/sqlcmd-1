package controller.command;

import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.controller.command.Command;
import ua.com.shtramak.controller.command.CreateTable;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.util.Commands;
import ua.com.shtramak.view.View;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CreateTableTest {

    private DataBaseManager dataBaseManager;
    private View view;
    private Command command;

    @Before
    public void init() {
        dataBaseManager = mock(DataBaseManager.class);
        view = mock(View.class);
        command = new CreateTable(dataBaseManager, view);
    }

    @Test
    public void testCreateIsDetected() {
        assertTrue(command.isDetected("create"));
    }

    @Test
    public void testWrongCreateIsDetected() {
        assertFalse(command.isDetected("klear|"));
    }

    @Test
    public void testCreateDescription() {
        String expected = "\tcreate" +
                System.lineSeparator() +
                "\t\tredirects to dialog to create a new table";
        assertEquals(expected, command.description());
    }

    @Test
    public void testCreateExistingTable(){
        String tableName = "tableName";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        when(view.read()).thenReturn(tableName);
        command.execute("create");
        verify(view).write("Please enter a name of table you want to create: ");
        verify(view).writeln("Such table already exists...");
    }

    @Test
    public void testCreateCorrectTable(){
        String tableName = "tableName";
        when(dataBaseManager.hasTable(tableName)).thenReturn(false);
        String columnData = "col1Name|dataType1|col2Name|dataType2";
        when(view.read()).thenReturn(tableName).thenReturn(columnData);
        command.execute("create");
        verify(view).write("Please enter a name of table you want to create: ");
        verify(view).writeln("Please enter columns data in format: col1Name|dataType1|col2Name|dataType2...col#Name|dataType#");
        verify(dataBaseManager).createTable(tableName,columnData);
        verify(view).writeln("Table successfully created...");
    }

    @Test
    public void testCreateWrongTableData(){
        String tableName = "tableName";
        when(dataBaseManager.hasTable(tableName)).thenReturn(false);
        String columnsData = "col1Name|dataType1|col2Name|dataType2|col3Name";
        when(view.read()).thenReturn(tableName).thenReturn(columnsData);
        command.execute("create");
        verify(view).write("Please enter a name of table you want to create: ");
        verify(view).writeln("Please enter columns data in format: col1Name|dataType1|col2Name|dataType2...col#Name|dataType#");
        verify(view).writeln(String.format("Wrong input! Number of elements must be even, but entered %s", Commands.sizeOf(columnsData)));
    }

}
