package services;

public interface ClientHandler {

    void write(String msg);

    String getName();

    void closeConnection();
}
