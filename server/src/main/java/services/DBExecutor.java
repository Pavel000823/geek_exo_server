package services;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DBExecutor {

    void execute(String query) throws SQLException;

    ResultSet getResult(String query) throws SQLException;

    boolean isResult(String query) throws SQLException;

    void closeConnection();


}
