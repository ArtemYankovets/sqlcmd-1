package ua.com.shtramak.sqlcmd.model;

import ua.com.shtramak.sqlcmd.model.exceptions.NoJDBCDriverException;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.model.exceptions.UnsuccessfulConnectionException;
import ua.com.shtramak.sqlcmd.utils.Commands;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCDataBaseManager implements DataBaseManager {

    private Connection connection;

    @Override
    public void connect(String database, String userName, String password) throws NoJDBCDriverException, UnsuccessfulConnectionException {
        String host = "//localhost:5432/";
        connect(host, database, userName, password);
    }

    @Override
    public void connect(String host, String database, String userName, String password) throws NoJDBCDriverException, UnsuccessfulConnectionException {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            String message = String.format("Dear %s! Connection to database was failed. JDBC driver doesn't exist." + System.lineSeparator() +
                    "Please install required JDBC diver and try again.", userName);
            throw new NoJDBCDriverException(message);
        }

        try {
            connection = DriverManager.getConnection("jdbc:postgresql:" + host + database + "?loggerLevel=OFF", userName, password);
        } catch (SQLException e) {
            connection = null;
            String message = String.format("Dear, %s! Unsuccessful connection to %s... Reason: %s" + System.lineSeparator(), userName, database, e.getMessage());
            throw new UnsuccessfulConnectionException(message);
        }
    }

    @Override
    public DataSet[] getTableData(String tableName) throws NotExecutedRequestException {

        String sql = String.format("SELECT * FROM %s", tableName);
        DataSet[] result = new DataSet[getTableSize(tableName)];

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            int index = 0;
            while (resultSet.next()) {
                result[index++] = rowDataSet(rsMetaData, resultSet);
            }

        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }

        return result;
    }

    private DataSet rowDataSet(ResultSetMetaData rsMetaData, ResultSet resultSet) throws SQLException {
        DataSet entry = new DataSet();
        for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
            entry.put(rsMetaData.getColumnName(i), resultSet.getObject(i));
        }
        return entry;
    }

    @Override
    public String[] getTableNames() throws NotExecutedRequestException {
        DatabaseMetaData md;
        try {
            md = connection.getMetaData();
        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }

        String[] res;
        try (ResultSet resultSet = md.getTables(null, null, "%", new String[]{"TABLE"})) {
            resultSet.last();
            int resultSetSize = resultSet.getRow();
            resultSet.beforeFirst();
            res = new String[resultSetSize];

            int index = 0;
            int tableNameIndex = 3;
            while (resultSet.next()) {
                res[index++] = resultSet.getString(tableNameIndex);
            }
        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }

        return res;
    }

    @Override
    public void clear(String tableName) throws NotExecutedRequestException {

        try {
            Statement statement = connection.createStatement();
            String sql = String.format("DELETE FROM %s", tableName);
            statement.execute(sql);


        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }
    }

    @Override
    public void insert(String tableName, DataSet input) throws NotExecutedRequestException {

        String colNames = getFormattedColumnNames(input, ",");
        String colValue = getFormattedColumnData(input, ",");

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s);", tableName, colNames, colValue);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }
    }

    @Override
    public void createTable(String tableName, String columns) throws NotExecutedRequestException {
        String parsedColumns = parseColumnsData(columns);
        String sql = String.format("CREATE TABLE %s ( %s );", tableName, parsedColumns);

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }
    }

    private String parseColumnsData(String columns) {
        String[] colArray = Commands.arrayOf(columns);

        StringBuilder colDataBuilder = new StringBuilder();
        for (int i = 0; i < colArray.length; i++) {
            colDataBuilder
                    .append(colArray[i])
                    .append(" ")
                    .append(colArray[++i])
                    .append(", ");
        }
        int lastCommaIndex = colDataBuilder.lastIndexOf(",");
        colDataBuilder.deleteCharAt(lastCommaIndex);
        return colDataBuilder.toString().trim();
    }

    @Override
    public void updateTableData(String tableName, String colName, Object rowValue, DataSet newValue) throws NotExecutedRequestException {
        String updateLine = updateDataLine(newValue);
        String sql = String.format("UPDATE %s SET %s WHERE %s = '%s'", tableName, updateLine, colName, rowValue);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }
    }

    private String updateDataLine(DataSet newValue) {
        StringBuilder updateLine = new StringBuilder();
        if (!newValue.isEmpty()) {
            String[] names = newValue.names();
            Object[] values = newValue.values();
            for (int i = 0; i < newValue.size(); i++) {
                updateLine.append(names[i])
                        .append(" = '")
                        .append(values[i])
                        .append("',");
            }
            int lastCommaIndex = updateLine.lastIndexOf(",");
            updateLine.deleteCharAt(lastCommaIndex);
        } else {
            return "";
        }

        return updateLine.toString();
    }

    @Override
    public String[] getTableColumns(String tableName) throws NotExecutedRequestException {
        try {
            DatabaseMetaData dbmt = connection.getMetaData();
            ResultSet resultSet = dbmt.getColumns(null, null, tableName, "%");

            List<String> colNames = new ArrayList<>();
            while (resultSet.next()) {
                colNames.add(resultSet.getString("COLUMN_NAME"));
            }
            String[] result = new String[colNames.size()];
            return colNames.toArray(result);
        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }
    }

    @Override
    public void disconnect() throws NotExecutedRequestException {
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new NotExecutedRequestException("Request to database was not executed. " + e.getMessage());
        }
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public boolean hasTable(String tableName) throws NotExecutedRequestException {
        for (String name : getTableNames()) {
            if (name.equals(tableName)) return true;
        }

        return false;
    }

    @Override
    public boolean hasColumn(String tableName, String columnName) throws NotExecutedRequestException {

        if (!hasTable(tableName)) return false;

        for (String element : getTableColumns(tableName)) {
            if (element.equals(columnName)) return true;
        }

        return false;
    }

    @Override
    public boolean hasValue(String tableName, String columnName, String value) throws NotExecutedRequestException {

        if (!hasColumn(tableName, columnName)) return false;

        String sql = String.format("SELECT %s FROM %s where %1$s = '%s'", columnName, tableName, value);
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet.next();
        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }
    }

    @Override
    public void dropTable(String tableName) throws NotExecutedRequestException {
        String sql = String.format("DROP TABLE %s", tableName);

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }
    }

    private int getTableSize(String tableName) throws NotExecutedRequestException {
        String sql = String.format("SELECT COUNT(*) FROM %s", tableName);
        int result = 0;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)
        ) {
            resultSet.next();
            result = resultSet.getInt(1);
        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }
        return result;
    }

    private String getFormattedColumnData(DataSet dataSet, String format) {
        StringBuilder result = new StringBuilder();

        if (dataSet.values().length==0) return "";

        for (Object colName : dataSet.values()) {
            result.append("'");
            result.append(colName);
            result.append("'");
            result.append(format);
        }
        result.deleteCharAt(result.lastIndexOf(format));
        return result.toString();
    }

    private String getFormattedColumnNames(DataSet dataSet, String format) {
        StringBuilder result = new StringBuilder();

        if (dataSet.names().length == 0) return "";

        for (String colName : dataSet.names()) {
            result.append(colName);
            result.append(format);
        }
        result.deleteCharAt(result.lastIndexOf(","));
        return result.toString();
    }
}