package ua.com.shtramak.model;

public interface DataSetInterface {
    /**
     * This method adds a Data object into data field
     * @param name name of an object to be put in data field
     * @param value object references to specified field
     */
    void put(String name, Object value);

    /**
     * @return values from data field
     */
    Object[] getValues();

    /**
     * @return names from data field
     */
    String[] getNames();

    /**
     *
     * @param name name of an object wanted in data field
     * @return Object references to specified name, null otherwise
     */
    Object getValue(String name);

    /**
     * This method updates entries in data field starting from 0 up to newValue.freeIndex with newValue entries
     * @param newValue DataSet for updating a data field with entries from newValue
     */
    void updateFrom(DataSet newValue);
}
