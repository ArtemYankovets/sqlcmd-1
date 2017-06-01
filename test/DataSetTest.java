import ua.com.shtramak.model.DataSet;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;


public class DataSetTest {
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        DataSet dataSet = new DataSet();
        String name = "name";
        Object value = new Object();
        dataSet.put(name, value);
        if (dataSet.getValue(name).equals(value) && dataSet.getNames()[0].equals(name)) {
            System.out.println("put() works");
        }else{
            System.out.println("put() is incorrect");
        }
        dataSet.put(name, value);
        if (dataSet.size()==2){
            System.out.println("size() works");
        }else{
            System.out.println("size() is incorrect");
        }
        System.out.println(Arrays.toString(dataSet.getNames()));
    }
}
