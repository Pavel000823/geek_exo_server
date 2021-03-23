package services;

public interface MessengerServer extends ConsoleCommands {

    void removeClient(String client);

    void sendMessageAllClients(String message);

    void sendMessageForClient(String message, ClientHandler client);

    void addClient(String nickName, ClientHandler client);

    boolean isServerActive();

    String getClientsNames();

    String getServerCommands();

    String getWelcomeMessage(String nickName);

    String getServerName();

    String getAuthMessage();

    AuthenticationService getAuthService();
}
