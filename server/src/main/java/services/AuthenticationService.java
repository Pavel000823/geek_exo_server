package services;


public interface AuthenticationService {


    boolean checkUser(String nickName);

    boolean checkUser(String nickName, String password);

    void addUser(String nickName, String password);

    void updateClient(String lastNick, String newNick);
}
