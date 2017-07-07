package ua.com.shtramak.model;

import java.util.NoSuchElementException;

public class DataSet implements DataSetInterface {
    private Data[] data = new Data[100];
    private int freeIndex;

    @Override
    public void put(String name, Object value) {
        if (value == null) throw new RuntimeException("An attempt to add 'null' in DataSet. 'null' is not permitted");
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

}