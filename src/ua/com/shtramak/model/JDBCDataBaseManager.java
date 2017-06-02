package ua.com.shtramak.model;

import java.sql.*;

public class JDBCDataBaseManager implements DataBaseManager {

    private Connection connection;

    @Override
    public DataSet[] getTableData(String tableName) {

        String sql = "SELECT * FROM " + tableName;
        DataSet[] result = new DataSet[getTableSize(tableName)];

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            int index = 0;
            while (resultSet.next()) {
                DataSet entry = new DataSet();
                for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
                    entry.put(rsMetaData.getCatalogName(i), resultSet.getObject(i));
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
        String[] warehouse = new String[100];
        int index = 0;

        try {
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                System.out.println(rs.getString("TABLE_NAME"));
                warehouse[index++] = rs.getString(3);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String[] result = new String[index];
        for (int i = 0; i < index; i++) {
            result[i] = warehouse[i];
        }
        return result;
    }

    @Override
    public void connect(String database, String userName, String password) {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/" + database, userName, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.printf("Dear, %s! Connection to database was failed. JDBC driver doesn't exist");
            ;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.printf("Hello, %s! You are welcome to %s database \n", userName, database);
    }

    @Override
    public void clear(String tableName) {

        try {
            Statement statement = connection.createStatement();
            String sql = "DELETE FROM " + tableName;
            statement.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(String tableName, DataSet input) {

    }

    @Override
    public void updateById(String tableName, int id, DataSet newValue) {

    }

    @Override
    public String[] getTableColumns(String tableName) {
        String sql = "SELECT * FROM " + tableName;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            ResultSetMetaData rsmd = resultSet.getMetaData();

            if (!resultSet.next()) return null;

            String[] result = new String[rsmd.getColumnCount()];
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                result[i] = rsmd.getColumnName(i+1);
            }

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getTableSize(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        int result = 0;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql);
        ) {
            resultSet.next();
            result = resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}