package ua.com.shtramak.model;

import java.util.Arrays;

public class DataSet implements DataSetInterface {
    private Data[] data = new Data[100];
    private int freeIndex;

    @Override
    public void put(String name, Object value) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == null) {
                freeIndex = i;
                break;
            }
        }
        data[freeIndex] = new Data(name, value);
    }

    @Override
    public Object[] getValues() {
        if (data == null || data.length == 0) return null;

        Object[] result = new Object[freeIndex + 1];
        for (int i = 0; i <= freeIndex; i++) {
            result[i] = data[i].getValue();
        }
        return result;
    }

    @Override
    public String[] getNames() {
        if (data == null || data.length == 0) return null;

        String[] result = new String[freeIndex + 1];
        for (int i = 0; i <= freeIndex; i++) {
            result[i] = data[i].getName();
        }
        return result;
    }

    @Override
    public Object getValue(String name) {
        if (data == null || data.length == 0) return null;

        for (int i = 0; i <= freeIndex; i++) {
            if (data[i].getName().equals(name)) {
                return data[i].getValue();
            }
        }
        return null;
    }

    @Override
    public void updateFrom(DataSet newValue) {
        if (newValue.size() > this.size()) {
            freeIndex = newValue.size();
        }
        String[] names = newValue.getNames();
        Object[] values = newValue.getValues();
        for (int i = 0; i < newValue.size(); i++) {
            data[i] = new Data(names[i], values[i]);
        }
    }

    public int size() {
        return freeIndex + 1;
    }

    @Override
    public String toString() {
        if (data[0] == null) return null;
            StringBuilder result = new StringBuilder("[");
        int index = 0;
        String[] names = getNames();
        Object[] values = getValues();
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
