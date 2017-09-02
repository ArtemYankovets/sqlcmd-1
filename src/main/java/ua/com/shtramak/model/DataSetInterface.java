package ua.com.shtramak.model;

interface DataSetInterface {
    /**
     * This method adds a Data object into data field
     * @param name name of an object to be put in data field
     * @param value object references to specified field
     */
    void put(String name, Object value);

    /**
     * @return values from data field
     */
    Object[] values();

    /**
     * @return names from data field
     */
    String[] names();

    /**
     *
     * @param name name of an object wanted in data field
     * @return Object references to specified name, null otherwise
     */
    Object value(String name);
}
