package ua.com.shtramak.sqlcmd.model;

import ua.com.shtramak.sqlcmd.model.exceptions.NoJDBCDriverException;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.model.exceptions.UnsuccessfulConnectionException;

import java.util.List;
import java.util.Set;

public interface DataBaseManager {

    void connect(String host, String database, String userName, String password) throws NoJDBCDriverException, UnsuccessfulConnectionException;

    /**
     * @param tableName name of the specified table in database
     * @return arrayOf of DataSet[] with data from a table
     */

    List<DataSet> getTableData(String tableName) throws NotExecutedRequestException;

    /**
     * @return array with available tables in database
     */
    Set<String> getTableNames() throws NotExecutedRequestException;

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
    void clear(String tableName) throws NotExecutedRequestException;

    /**
     * Creates an entry in the specified table of database
     *
     * @param tableName name of the specified table in database
     * @param input     DataSet entry to be added in database
     */
    void insert(String tableName, DataSet input) throws NotExecutedRequestException;

    void createTable(String tableName, String columns) throws NotExecutedRequestException;

    /**
     * @param tableName name of the specified table in database
     * @param colName   id-number of the wanted element from table to be updated
     * @param rowValue  wanted value from column
     * @param newValue  new DataSet entry for the specified row
     */
    void updateTableData(String tableName, String colName, Object rowValue, DataSet newValue) throws NotExecutedRequestException;

    /**
     * @param tableName name of the specified table in database
     * @return a names of a columns in specified table
     */
    String[] getTableColumns(String tableName) throws NotExecutedRequestException;

    /**
     * Provide disconnecting from current database
     */
    void disconnect() throws NotExecutedRequestException;

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
    boolean hasTable(String tableName) throws NotExecutedRequestException;

    /**
     * @param tableName  name of the specified table in database
     * @param columnName name of the wanted column in specified table
     * @return true if specified column presents in the table, false otherwise
     */
    boolean hasColumn(String tableName, String columnName) throws NotExecutedRequestException;

    /**
     * @param tableName  name of the specified table in database
     * @param columnName name of column in specified table
     * @param value      wanted value in specified column
     * @return true if value presents in column, false otherwise
     */
    boolean hasValue(String tableName, String columnName, String value) throws NotExecutedRequestException;

    void dropTable(String tableName) throws NotExecutedRequestException;
}