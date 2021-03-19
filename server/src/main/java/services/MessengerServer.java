package services;

import server.Client;

public interface MessengerServer {

    void removeClient(String client);

    void sendMessageAllClients(String message);

    void sendMessageForClient(String message, ClientHandler client);

    void addClient(String nickName, Client client);

    boolean isContainsNickName(String nickName);

    boolean isServerActive();

    String getClientsNames();

    String getServerCommands();

    String getWelcomeMessage(String nickName);

    String getServerName();

    String getAuthMessage();
}
