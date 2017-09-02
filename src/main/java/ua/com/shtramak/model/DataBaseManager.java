package ua.com.shtramak.model;

import ua.com.shtramak.model.exceptions.NoJDBCDriverException;
import ua.com.shtramak.model.exceptions.UnsuccessfulConnectionException;

public interface DataBaseManager {

    void connect(String host, String database, String userName, String password) throws NoJDBCDriverException, UnsuccessfulConnectionException;

    /**
     * @param tableName name of the specified table in database
     * @return arrayOf of DataSet[] with data from a table
     */

    DataSet[] getTableData(String tableName);

    /**
     * @return array with available tables in database
     */
    String[] getTableNames();

    /**
     * This method makes connection to the specified database
     *
     * @param database name of the database
     * @param userName login
     * @param password password
     */

    void connect(String database, String userName, String password) throws NoJDBCDriverException, UnsuccessfulConnectionException;

    /**
     * This method deletes all rows from the specified table
     *
     * @param tableName name of the table rows to be deleted
     */
    void clear(String tableName);

    /**
     * Creates an entry in the specified table of database
     *
     * @param tableName name of the specified table in database
     * @param input     DataSet entry to be added in database
     */
    void insert(String tableName, DataSet input);

    void createTable(String tableName, String columns);

    /**
     * @param tableName name of the specified table in database
     * @param colName   id-number of the wanted element from table to be updated
     * @param rowValue  wanted value from column
     * @param newValue  new DataSet entry for the specified row
     */
    void updateTableData(String tableName, String colName, Object rowValue, DataSet newValue);

    /**
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
     *
     * @return true if connection is available, false otherwise
     */
    boolean isConnected();

    /**
     * @param tableName name of the specified table in database
     * @return true if specified table exists in database, false otherwise
     */
    boolean hasTable(String tableName);

    /**
     * @param tableName  name of the specified table in database
     * @param columnName name of the wanted column in specified table
     * @return true if specified column presents in the table, false otherwise
     */
    boolean hasColumn(String tableName, String columnName);

    /**
     * @param tableName  name of the specified table in database
     * @param columnName name of column in specified table
     * @param value      wanted value in specified column
     * @return true if value presents in column, false otherwise
     */
    boolean hasValue(String tableName, String columnName, String value);

    void dropTable(String tableName);
}