package ua.com.shtramak.sqlcmd.model;

import ua.com.shtramak.sqlcmd.model.exceptions.NoJDBCDriverException;
import ua.com.shtramak.sqlcmd.model.exceptions.NotExecutedRequestException;
import ua.com.shtramak.sqlcmd.model.exceptions.UnsuccessfulConnectionException;
import ua.com.shtramak.sqlcmd.utils.Commands;

import java.sql.*;
import java.util.*;

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
            String message = String.format("Dear %s! Connection to database was failed. JDBC driver not found." + System.lineSeparator() +
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
    public List<DataSet> getTableData(String tableName) throws NotExecutedRequestException {
        String sql = String.format("SELECT * FROM %s", tableName);
        List<DataSet> result = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                result.add(rowDataSet(rsMetaData, resultSet));
            }

        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }

        return result;
    }

    @Override
    public Set<String> getTableNames() throws NotExecutedRequestException {
        DatabaseMetaData md;
        try {
            md = connection.getMetaData();
        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }

        Set<String> res = new TreeSet<>();
        try (ResultSet resultSet = md.getTables(null, null, "%", new String[]{"TABLE"})) {
            int tableNameIndex = 3;
            while (resultSet.next()) {
                res.add(resultSet.getString(tableNameIndex));
            }
        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }

        return res;
    }

    @Override
    public Set<String> getTableColumns(String tableName) throws NotExecutedRequestException {
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            ResultSet resultSet = dbMetaData.getColumns(null, null, tableName, "%");

            Set<String> colNames = new LinkedHashSet<>();
            while (resultSet.next()) {
                colNames.add(resultSet.getString("COLUMN_NAME"));
            }
            return colNames;
        } catch (SQLException e) {
            String message = String.format("Request to database was not executed. Reason: %s", e.getMessage());
            throw new NotExecutedRequestException(message);
        }
    }

    @Override
    public void clear(String tableName) throws NotExecutedRequestException {
        String sqlRequest = String.format("DELETE FROM %s", tableName);
        executeRequest(sqlRequest);
    }

    @Override
    public void insert(String tableName, DataSet input) throws NotExecutedRequestException {
        String colNames = getFormattedColumnNames(input);
        String colValue = getFormattedColumnData(input);
        String sqlRequest = String.format("INSERT INTO %s (%s) VALUES (%s);", tableName, colNames, colValue);
        executeRequest(sqlRequest);
    }

    @Override
    public void createTable(String tableName, String columns) throws NotExecutedRequestException {
        String parsedColumns = parseColumnsData(columns);
        String sqlRequest = String.format("CREATE TABLE %s ( %s );", tableName, parsedColumns);
        executeRequest(sqlRequest);
    }

    @Override
    public void updateTableData(String tableName, String colName, Object rowValue, DataSet newValue) throws NotExecutedRequestException {
        String updateLine = updateDataLine(newValue);
        String sqlRequest = String.format("UPDATE %s SET %s WHERE %s = '%s'", tableName, updateLine, colName, rowValue);
        executeRequest(sqlRequest);
    }

    @Override
    public void deleteTableData(String tableName, String colName, Object rowValue) throws NotExecutedRequestException {
        String sqlRequest = String.format("DELETE FROM %s WHERE %s = '%s'", tableName, colName, rowValue);
        executeRequest(sqlRequest);
    }

    @Override
    public void dropTable(String tableName) throws NotExecutedRequestException {
        String sqlRequest = String.format("DROP TABLE %s", tableName);
        executeRequest(sqlRequest);
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

    private void executeRequest(String sqlRequest) throws NotExecutedRequestException {
        try {
            Statement statement = connection.createStatement();
            statement.execute(sqlRequest);
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

    private String getFormattedColumnData(DataSet dataSet) {
        StringBuilder result = new StringBuilder();

        if (dataSet.values().size() == 0) return "";

        for (Object colName : dataSet.values()) {
            result.append("'");
            result.append(colName);
            result.append("'");
            result.append(",");
        }
        result.deleteCharAt(result.lastIndexOf(","));
        return result.toString();
    }

    private String getFormattedColumnNames(DataSet dataSet) {
        StringBuilder result = new StringBuilder();

        if (dataSet.names().size() == 0) return "";

        for (String colName : dataSet.names()) {
            result.append(colName);
            result.append(",");
        }
        result.deleteCharAt(result.lastIndexOf(","));
        return result.toString();
    }

    private String updateDataLine(DataSet newValue) {
        StringBuilder updateLine = new StringBuilder();
        if (!newValue.isEmpty()) {
            Iterator<String> names = newValue.names().iterator();
            Iterator<Object> values = newValue.values().iterator();
            while (names.hasNext()) {
                updateLine.append(names.next())
                        .append(" = '")
                        .append(values.next())
                        .append("',");
            }
            int lastCommaIndex = updateLine.lastIndexOf(",");
            updateLine.deleteCharAt(lastCommaIndex);
        } else {
            return "";
        }

        return updateLine.toString();
    }

    private DataSet rowDataSet(ResultSetMetaData rsMetaData, ResultSet resultSet) throws SQLException {
        DataSet entry = new DataSet();
        for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
            entry.put(rsMetaData.getColumnName(i), resultSet.getObject(i));
        }
        return entry;
    }
}