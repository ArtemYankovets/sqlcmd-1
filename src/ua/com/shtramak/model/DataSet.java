package ua.com.shtramak.model;

public class DataSet {
    Data[] data;
    int freeIndex;
}

class Data{
    String name;
    Object value;

    public Data(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
