package ua.com.shtramak.sqlcmd.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.stream.Collectors.toList;

public class DataSet {
    private List<Data> data = new ArrayList<>();

    public void put(String name, Object value) {
        if (value == null) value = "";
        data.add(new Data(name, value));
    }

    public List<Object> values() {
        if (data.isEmpty()) return new ArrayList<>(Arrays.asList(new String[]{""}));
        return data.stream().map(Data::getValue).collect(toList());
    }

    public List<String> names() {
        if (data.isEmpty()) return new ArrayList<>();
        return data.stream().map(Data::getName).collect(toList());
    }

    public Object value(String name) {
        if (data.isEmpty()) throw new RuntimeException("DataSet is empty");
        return data.stream().filter(d -> d.getName().equals(name)).map(Data::getValue).findFirst()
                .orElseThrow(() -> new NoSuchElementException("There's no such name in DataSet"));
    }

    public String toString() {
        if (data.isEmpty()) return "";
        StringBuilder result = new StringBuilder("[");
        data.forEach(el -> result.append("{").append(el.getName()).append(", ").append(el.getValue()).append("},"));
        result.replace(result.lastIndexOf(","), result.lastIndexOf(",") + 1, "]");
        return result.toString();
    }

    public List<String> stringValues() {
        return data.stream().map(d->d.getValue().toString()).collect(toList());
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