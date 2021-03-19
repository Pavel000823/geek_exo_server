package services;

public interface ClientHandler extends Runnable {

    void write(String msg);

    String getName();

    void closeConnection();

    boolean isAuthorization();
}
