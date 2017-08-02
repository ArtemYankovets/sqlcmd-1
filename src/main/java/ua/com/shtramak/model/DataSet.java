package ua.com.shtramak.model;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class DataSet implements DataSetInterface {
    private Data[] data = new Data[100];
    private int freeIndex;

    @Override
    public void put(String name, Object value) {
        if (value == null) value = "";
        for (int i = 0; i < data.length; i++) {
            if (data[i] == null) {
                freeIndex = i;
                break;
            }
        }
        data[freeIndex] = new Data(name, value);
    }

    @Override
    public Object[] values() {
        if (isEmpty()) throw new RuntimeException("DataSet is empty");

        Object[] result = new Object[freeIndex + 1];
        for (int i = 0; i <= freeIndex; i++) {
            result[i] = data[i].getValue();
        }
        return result;
    }

    @Override
    public String[] names() {
        if (isEmpty()) throw new RuntimeException("DataSet is empty");

        String[] result = new String[freeIndex + 1];
        for (int i = 0; i <= freeIndex; i++) {
            result[i] = data[i].getName();
        }
        return result;
    }

    @Override
    public Object value(String name) {
        if (isEmpty()) throw new RuntimeException("DataSet is empty");

        for (int i = 0; i <= freeIndex; i++) {
            if (data[i].getName().equals(name)) {
                return data[i].getValue();
            }
        }

        throw new NoSuchElementException("There's no such name in DataSet");
    }

    int size() {
        return freeIndex + 1;
    }

    boolean isEmpty(){
        return data[0]==null;
    }

    @Override
    public String toString() {
        if (data[0] == null) return "";
        StringBuilder result = new StringBuilder("[");
        int index = 0;
        String[] names = names();
        Object[] values = values();
        for (int i = 0; i < data.length; i++) {
            if (data[i] == null) break;
            result.append("{");
            result.append(names[i]);
            result.append(", ");
            result.append(values[i]);
            result.append("},");
        }
        result.deleteCharAt(result.lastIndexOf(","));
        result.append("]");
        return result.toString();
    }

    public String[] getStringValues() {
        String[] result = new String[freeIndex + 1];
        for (int i = 0; i <= freeIndex; i++) {
            result[i] = data[i].getValue().toString();
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSet dataSet = (DataSet) o;

        if (freeIndex != dataSet.freeIndex) return false;
        return Arrays.equals(data, dataSet.data);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(data);
        result = 31 * result + freeIndex;
        return result;
    }
}

class Data {
    private String name;
    private Object value;

    Data(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    String getName() {
        return name;
    }

    Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Data data = (Data) o;

        if (!name.equals(data.name)) return false;
        return value.equals(data.value);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}