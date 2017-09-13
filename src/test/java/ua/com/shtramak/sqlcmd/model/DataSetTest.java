package ua.com.shtramak.sqlcmd.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;


public class DataSetTest {
    private DataSet dataSet;

    @Before
    public void setup() {
        dataSet = new DataSet();
        fillWithTestData(dataSet);
    }

    @Test
    public void testPutWithRegularData() {
        String expected = "[{String, string},{Integer, 1},{Double, 1.5}]";
        assertEquals(expected, dataSet.toString());
    }

    @Test
    public void testValuesWithRegularData() {
        String expected = "[string, 1, 1.5]";
        assertEquals(expected, dataSet.values().toString());
    }

    @Test
    public void testValuesFromEmptyDataSet(){
        dataSet=new DataSet();
        String expected = "[]";
        assertEquals(expected, dataSet.values().toString());
    }

    @Test
    public void testNamesWithRegularData() {
        String expected = "[String, Integer, Double]";
        assertEquals(expected,dataSet.names().toString());
    }

    @Test
    public void testNamesFromEmptyDataSet(){
        dataSet=new DataSet();
        String expected = "[]";
        assertEquals(expected, dataSet.names().toString());    }

    @Test
    public void testValueWithExistingElement(){
        String expected = "string";
        assertEquals(expected,dataSet.value("String"));
    }
    @Test(expected = NoSuchElementException.class)
    public void testValueWithUnexistingElement(){
        dataSet.value("Fake");
    }

    @Test(expected = RuntimeException.class)
    public void testValueWithFromEmptyDataSet(){
        dataSet = new DataSet();
        dataSet.value("Fake");
    }

    private void fillWithTestData(DataSet dataSet) {
        dataSet.put("String", "string");
        dataSet.put("Integer", 1);
        dataSet.put("Double", 1.5);
    }
}
