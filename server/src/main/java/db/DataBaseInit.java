package db;


import services.DBConnection;

import java.sql.*;

public class DataBaseInit implements DBConnection {

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
            connection = DriverManager.getConnection("jdbc:sqlite:" + DataBaseInit.DATA_BASE_NAME + ".s2db");
        }
        return connection;
    }

    private static void initUserTable() throws SQLException {
        try (Statement statement = getConnectionInstance().createStatement()) {
            statement.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' text unique, 'password' text);");
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void closeConnection() {
        try {
            getConnectionInstance().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
