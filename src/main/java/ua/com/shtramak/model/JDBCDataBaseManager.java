package ua.com.shtramak.model;

import java.sql.*;

public class JDBCDataBaseManager implements DataBaseManager {

    private Connection connection;

    @Override
    public void connect(String database, String userName, String password) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            String message = String.format("Dear %s! Connection to database was failed. JDBC driver doesn't exist." + System.lineSeparator() +
                    "Please install required JDBC diver and try again.", userName);
            throw new UnsupportedOperationException(message);
        }

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + database + "?loggerLevel=OFF", userName, password);
            System.out.printf("Hello %s! Welcome to %s database", userName, database);
        } catch (SQLException e) {
            connection = null;
            throw new RuntimeException(String.format("Dear %s! Your input data was incorrect!" + System.lineSeparator(), userName), e);
        }

    }

    @Override
    public DataSet[] getTableData(String tableName) {

        String sql = String.format("SELECT * FROM %s", tableName);
        DataSet[] result = new DataSet[getTableSize(tableName)];

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            int index = 0;
            while (resultSet.next()) {
                DataSet entry = new DataSet();
                for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                    entry.put(rsMetaData.getColumnName(i), resultSet.getObject(i));
                }
                result[index++] = entry;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public String[] getTableNames() {

        int index = 0;
        String[] storage = new String[100];
        try {
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                storage[index++] = rs.getString(3);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String[] result = new String[index];
        System.arraycopy(storage, 0, result, 0, index);
        return result;
    }

    @Override
    public void clear(String tableName) {

        try {
            Statement statement = connection.createStatement();
            String sql = String.format("DELETE FROM %s", tableName);
            statement.execute(sql);
            System.out.println(String.format("Data from %s was successfully deleted", tableName));


        } catch (SQLException e) {
            System.out.println("An error occurred. Check if entered table exists. Reason: " + e.getMessage());
        }
    }

    @Override
    public void insert(String tableName, DataSet input) {

        String colNames = getFormattedColumnNames(input, ",");
        String colValue = getFormattedColumnData(input, ",");

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s);", tableName, colNames, colValue);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            String message = "Smth goes wrong... Reason: " + e.getMessage();
            if (e.getCause() != null)
                message += "\n" + e.getCause();
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public void updateById(String tableName, int id, DataSet newValue) {

        String sql = String.format("UPDATE %s SET %s WHERE id = ?", tableName, getFormattedColumnNames(newValue, " = ?, "));

        try (PreparedStatement prprStmt = connection.prepareStatement(sql)) {
            int index = 1;
            for (int i = 0; i < newValue.size(); i++) {
                prprStmt.setObject(index++, newValue.values()[i]);
            }
            prprStmt.setInt(index, id);
            prprStmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public String[] getTableColumns(String tableName) {
        String sql = String.format("SELECT * FROM %s", tableName);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            ResultSetMetaData rsmd = resultSet.getMetaData();

            if (!resultSet.next()) return null; //TODO

            String[] result = new String[rsmd.getColumnCount()];
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                result[i] = rsmd.getColumnName(i + 1);
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null; //TODO
        }
    }

    @Override
    public void disconnect() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    private int getTableSize(String tableName) {
        String sql = String.format("SELECT COUNT(*) FROM %s", tableName);
        int result = 0;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)
        ) {
            resultSet.next();
            result = resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getFormattedColumnData(DataSet dataSet, String format) {
        StringBuilder result = new StringBuilder();
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
        for (String colName : dataSet.names()) {
            result.append(colName);
            result.append(format);
        }
        result.deleteCharAt(result.lastIndexOf(","));
        return result.toString();
    }
}