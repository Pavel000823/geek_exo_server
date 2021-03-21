package db;


import services.DBExecutor;

import java.sql.*;

public class DataBaseExecutor implements DBExecutor {

    public static final String DATA_BASE_NAME = "messenger";
    private static Connection connection;

    static {
        try {
            init();
            initUserTable();
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке класса org.sqlite.JDBC");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Ошибка при инициализации таблицы Users");
            e.printStackTrace();
        }
    }

    private static void init() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
    }

    private static Connection getConnectionInstance() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DataBaseExecutor.DATA_BASE_NAME + ".s2db");
        }
        return connection;
    }

    private static void initUserTable() throws SQLException {
        try (Statement statement = getConnectionInstance().createStatement()) {
            statement.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' text unique, 'password' text);");
        }
    }

    @Override
    public boolean isResult(String query) throws SQLException {
        int size = 0;
        try (Statement statement = getConnectionInstance().createStatement()) {
            statement.execute(query);
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                size++;
            }
        }
        return size > 0;
    }

    @Override
    public void execute(String query) throws SQLException {
        try (Statement statement = getConnectionInstance().createStatement()) {
            statement.executeUpdate(query);
        }
    }

    @Override
    public ResultSet getResult(String query) throws SQLException {
        try (Statement statement = getConnectionInstance().createStatement()) {
            return statement.executeQuery(query);
        }
    }


    @Override
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
