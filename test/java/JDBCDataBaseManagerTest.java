import org.junit.Before;
import org.junit.Test;
import ua.com.shtramak.model.DataBaseManager;
import ua.com.shtramak.model.JDBCDataBaseManager;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class JDBCDataBaseManagerTest {
    private DataBaseManager manager;

    @Before
    public void setup(){
        manager = new JDBCDataBaseManager();
        manager.connect("sqlcmd", "postgres","postgres");
    }

    @Test
    public void getTableNamesTest(){
        assertEquals(Arrays.toString(manager.getTableNames()),"[users]");
    }

    @Test
    public void getTableDataTest(){
        System.out.println(Arrays.toString(manager.getTableData("users")));
    }

    @Test
    public void getTableColumnsTest(){
        assertEquals(Arrays.toString(manager.getTableColumns("users")),"[id, name, password]");
    }
}