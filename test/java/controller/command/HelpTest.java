package controller.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ua.com.shtramak.controller.command.AbstractCommand;
import ua.com.shtramak.controller.command.Help;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.view.View;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HelpTest {
    private View view;
    private AbstractCommand command;

    @Before
    public void init() {
        DataBaseManager dataBaseManager = mock(DataBaseManager.class);
        view = mock(View.class);
        command = new Help(dataBaseManager, view);
    }

    @Test
    public void testHelpIsDetected() {
        assertTrue(command.isDetected("help"));
    }

    @Test
    public void testWrongHelpIsDetected() {
        assertFalse(command.isDetected("hellp"));
    }

    @Test
    public void testHelpDescription() {
        String expected = "\thelp" +
                System.lineSeparator() +
                "\t\twill display this message again... try it! )";
        assertEquals(expected, command.description());
    }

    @Test
    public void testHelpExecute() {
        String lineSeparator = System.lineSeparator();
        String expected = lineSeparator + "List of available commands:\tconnect|database|userName|password" + lineSeparator +
                "\t\tconnection with database under userName with password\tlist" + lineSeparator +
                "\t\tdisplay available tables in selected database\tshow|tableName" + lineSeparator +
                "\t\tdisplay table data from selected database\tclear|tableName" + lineSeparator +
                "\t\tdelete all data from selected table\tinsert|tableName|col1Name|value1|col2Name|value2|...col#Name|value#" + lineSeparator +
                "\t\tinsert entered data to selected table\tupdateTable|tableName" + lineSeparator +
                "\t\tupdate entry in selected table using own command interface\tcreate" + lineSeparator +
                "\t\tredirects to dialog to create a new table\tdrop|tableName" + lineSeparator +
                "\t\tdrop an existing table in a database\texit" + lineSeparator +
                "\t\tto exit from this session\thelp" + lineSeparator +
                "\t\twill display this message again... try it! )";
        command.execute("help");
        ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeln(arg.capture());
        List<String> descriptionsList = arg.getAllValues();
        StringBuilder result = new StringBuilder();
        for (String description : descriptionsList) {
            result.append(description);
        }
        assertEquals(expected, result.toString());
    }
}
