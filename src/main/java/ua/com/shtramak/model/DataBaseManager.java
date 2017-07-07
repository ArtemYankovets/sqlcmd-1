package ua.com.shtramak.model;

public interface DataBaseManager {

    /**
     * @param tableName name of the specified table in database
     * @return array of DataSet[] with data from a table
     */

    DataSet[] getTableData(String tableName);

    /**
     * @return array with available tables in database
     */
    String[] getTableNames();

    /**
     * This method makes connection to the specified database
     * @param database name of the database
     * @param userName login
     * @param password password
     */

    void connect(String database, String userName, String password);

    /**
     * This method deletes all rows from the specified table
     * @param tableName name of the table rows to be deleted
     */
    void clear(String tableName);

    /**
     * Creates an entry in the specified table of database
     * @param tableName name of the specified table in database
     * @param input DataSet entry to be added in database
     */
    void insert(String tableName, DataSet input);

    /**
     *
     * @param tableName name of the specified table in database
     * @param colName id-number of the wanted element from table to be updated
     * @param rowValue wanted value from column
     * @param newValue new DataSet entry for the specified row
     */
    void update(String tableName, String colName, Object rowValue, DataSet newValue);

    /**
     *
     * @param tableName name of the specified table in database
     * @return a names of a columns in specified table
     */
    String[] getTableColumns(String tableName);

    /**
     * Provide disconnecting from current database
     */
    void disconnect();

    /**
     * Checks if connection to data base is available
     * @return true if connection is available, false otherwise
     */
    boolean isConnected();
}