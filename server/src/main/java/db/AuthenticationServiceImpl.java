package db;

import services.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationServiceImpl implements AuthenticationService {

    DBConnection dbConnection;

    public AuthenticationServiceImpl(DBConnection connection) {
        this.dbConnection = connection;
    }
    @Override
    public synchronized boolean checkUser(String nickName) {
        try (PreparedStatement preparedStatement = dbConnection.getConnection().prepareStatement("select * from users where name = ?;")) {
            preparedStatement.setString(1, nickName);
            ResultSet resultSet = preparedStatement.executeQuery();
            return checkResult(resultSet);
        } catch (SQLException throwable) {
            System.out.println("Произошла ошибка при выполнении запроса " + throwable.getMessage());
            return false;
        }
    }

    @Override
    public synchronized boolean checkUser(String nickName, String password) {
        try (PreparedStatement preparedStatement = dbConnection.getConnection().prepareStatement("select * from users where name = ? and password = ?;")) {
            preparedStatement.setString(1, nickName);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return checkResult(resultSet);
        } catch (SQLException throwable) {
            System.out.println("Произошла ошибка при выполнении запроса " + throwable.getMessage());
            return false;
        }
    }

    @Override
    public synchronized void addUser(String nickName, String password) {
        try (PreparedStatement preparedStatement = dbConnection.getConnection().prepareStatement("INSERT INTO 'users' ('name', 'password') VALUES (?, ?);")) {
            preparedStatement.setString(1, nickName);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        } catch (SQLException throwable) {
            System.out.println("Произошла ошибка при выполнении запроса " + throwable.getMessage());
        }
    }

    @Override
    public synchronized void updateClient(String lastNick, String newNick) {
        try (PreparedStatement preparedStatement = dbConnection.getConnection().prepareStatement("update users set name = ? where name = ?")) {
            preparedStatement.setString(1, newNick);
            preparedStatement.setString(2, lastNick);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private boolean checkResult(ResultSet resultSet) throws SQLException {
        int size = 0;
        while (resultSet.next()) {
            size++;
        }
        return size > 0;
    }
}
