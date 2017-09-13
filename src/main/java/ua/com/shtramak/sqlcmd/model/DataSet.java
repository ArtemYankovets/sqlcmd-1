package ua.com.shtramak.sqlcmd.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class DataSet {
    private List<Data> data = new ArrayList<>();

    public void put(String name, Object value) {
        if (value == null) value = "";
        data.add(new Data(name, value));
    }

    public List<Object> values() {
        if (data.isEmpty()) return new ArrayList<>(Arrays.asList(new String[]{""}));

        List<Object> result = new ArrayList<>();
        for (Data dataItem : data) {
            result.add(dataItem.getValue());
        }
        return result;
    }

    public List<String> names() {
        List<String> result = new ArrayList<>();
        if (data.isEmpty()) return result;

        for (Data dataElement : data) {
            result.add(dataElement.getName());
        }
        return result;
    }

    public Object value(String name) {
        if (data.isEmpty()) throw new RuntimeException("DataSet is empty");

        for (Data dataElement : data) {
            if (dataElement.getName().equals(name)) {
                return dataElement.getValue();
            }
        }

        throw new NoSuchElementException("There's no such name in DataSet");
    }

    public String toString() {
        if (data.isEmpty()) return "";
        StringBuilder result = new StringBuilder("[");
        for (Data dataElement : data) {
            result.append("{");
            result.append(dataElement.getName());
            result.append(", ");
            result.append(dataElement.getValue());
            result.append("},");
        }
        result.deleteCharAt(result.lastIndexOf(","));
        result.append("]");
        return result.toString();
    }

    public List<String> stringValues() {
        List<String> result = new ArrayList<>();
        for (Data dataElement : data) {
            result.add(dataElement.getValue().toString());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSet)) return false;

        DataSet dataSet = (DataSet) o;

        return data != null ? data.equals(dataSet.data) : dataSet.data == null;
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int size() {
        return data.size();
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

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Data data = (Data) o;

            return (name.equals(data.name) && value.equals(data.value));
        }

        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + value.hashCode();
            return result;
        }
    }
}