package ua.com.shtramak.model;

import java.sql.*;

public class JDBCDataBaseManager implements DataBaseManager {

    private Connection connection;

    @Override
    public DataSet[] getTableData(String tableName) {

        return new DataSet[0];
    }

    @Override
    public String[] getTableNames() {
        String[] warehouse = new String[100];
        int index = 0;

        try {
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", new String[] { "TABLE" });
            while (rs.next()) {
                System.out.println(rs.getString("TABLE_NAME"));
                warehouse[index++] = rs.getString(3);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String[] result = new String[index];
        for (int i = 0; i <index ; i++) {
            result[i]=warehouse[i];
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
        System.out.printf("Hello, %s! You are welcome to %s database ", userName, database);
    }

    @Override
    public void clear(String tableName) {

    }

    @Override
    public void insert(String tableName, DataSet input) {

    }

    @Override
    public void updateById(String tableName, int id, DataSet newValue) {

    }

    @Override
    public String[] getTableColumns(String tableName) {
        return new String[0];
    }
}
