package ua.com.shtramak.sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.sqlcmd.model.DataBaseManager;
import ua.com.shtramak.sqlcmd.model.DataSet;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.utils.TableFormatter;
import ua.com.shtramak.sqlcmd.view.View;

import java.io.IOException;
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
    public void testShowDescription() {
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
    public void testShowTableDataEmtyTable() throws NotExecutedRequestException, IOException {
        String tableName = "tableName";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);
        Set<String> headers = new LinkedHashSet<>(Arrays.asList(new String[]{"id", "name", "password"}));
        when(dataBaseManager.getTableColumns(tableName)).thenReturn(headers);
        List<DataSet> tableData = new ArrayList<>();
        when(dataBaseManager.getTableData(tableName)).thenReturn(tableData);
        command.execute("show|" + tableName);
        String expected = "+----+------+----------+\n" +
                "! id ! name ! password !\n" +
                "+----+------+----------+";
        String result = TableFormatter.formattedTableRow(headers, headers.size());
        verify(view).writeln(result);
        assertEquals(expected, result);
    }

    @Test
    public void testShowTableDataCorrectTable() throws NotExecutedRequestException, IOException {
        String tableName = "tableName";
        when(dataBaseManager.hasTable(tableName)).thenReturn(true);

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
        String expected = "+----+----------+---------------+\n" +
                "! id ! name     ! password      !\n" +
                "+----+----------+---------------+\n" +
                "! 1  ! testName ! testPassword1 !\n" +
                "+----+----------+---------------+\n" +
                "! 2  ! testName ! testPassword2 !\n" +
                "+----+----------+---------------+";
        int size = tableData.get(0).size();
        List<String> data = dataSetListToString(tableData);
        String result = TableFormatter.formattedTableRow(data, size);
        verify(view).writeln(result);
        assertEquals(expected, result);
    }

    @SuppressWarnings("unchecked")
    private static List<String> dataSetListToString(List<DataSet> dataSets) {
        List<String> result = new ArrayList<>();
        result.addAll(dataSets.get(0).names());
        for (DataSet dataSet : dataSets) {
            List tmp = dataSet.values();
            result.addAll(tmp);
        }
        return result;
    }

}
