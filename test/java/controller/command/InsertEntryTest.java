package controller.command;

import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.controller.command.AbstractCommand;
import ua.com.shtramak.controller.command.InsertEntry;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.DataSet;
import ua.com.shtramak.view.View;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InsertEntryTest {
    private View view;
    private AbstractCommand command;
    private DataBaseManager dataBaseManager;

    @Before
    public void init() {
        dataBaseManager = mock(DataBaseManager.class);
        view = mock(View.class);

        command = new InsertEntry(dataBaseManager, view);
    }

    @Test
    public void testInsertIsDetected() {
        assertTrue(command.isDetected("insert|"));
    }

    @Test
    public void testWrongInsertIsDetected() {
        assertFalse(command.isDetected("incert|"));
    }

    @Test
    public void testInsertDescription() {
        String expected = "\tinsert|tableName|col1Name|value1|col2Name|value2|...col#Name|value#" +
                System.lineSeparator() +
                "\t\tinsert entered data to selected table";
        assertEquals(expected, command.description());
    }

    @Test
    public void testWrongInsertCommandSize() {
        command.execute("insert|tableName|col1Name|value1|col2Name");
        verify(view, times(1)).writeln("'insert' command failed because of wrong input: incorrect number of elements. Use 'help' command for details");
    }

    @Test
    public void testInsertWithCorrectData() {
        command.execute("insert|tableName|col1Name|value1|col2Name|value2");
        DataSet insertData = new DataSet();
        insertData.put("col1Name", "value1");
        insertData.put("col2Name", "value2");
        String tableName = "tableName";
        verify(dataBaseManager).insert(tableName, insertData);
        verify(view, times(1)).writeln(String.format("Data successfully added to %s", tableName));
    }

    @Test
    public void testInsertWithWrongData() {
        DataSet insertData = new DataSet();
        insertData.put("col1Name", "value1");
        insertData.put("col2Name", "value2");
        String tableName = "tableName";
        doThrow(new IllegalArgumentException("Exception message")).when(dataBaseManager).insert(tableName, insertData);
        command.execute("insert|tableName|col1Name|value1|col2Name|value2");
        verify(dataBaseManager).insert(tableName, insertData);
        verify(view, times(1)).writeln("Exception message");
    }
}