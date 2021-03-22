package services;

import java.sql.Connection;

public interface DBConnection {

    Connection getConnection();
    void closeConnection();
}
