package server;

import services.ClientHandler;
import services.MessengerServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server implements MessengerServer {

    public final String SERVER_HOST;
    public final int SERVER_PORT;
    public static final int SERVER_AUTHORIZATION_TIMEOUT = 120;
    private boolean isActive = true;
    private final Map<String, ClientHandler> clients = new HashMap<>();
    private static final HashMap<String, String> allCommands = new HashMap<>();
    private final StringBuilder serverCommands = new StringBuilder();

    static {
        allCommands.put("/ls", "список всех доступных команд");
        allCommands.put("/end", "выйти из чата");
        allCommands.put("/w", "отправить личное сообщение - пример (/w nickname Привет)");
        allCommands.put("/list", "Список всех участников");
        allCommands.put("/rename", "Изменить имя");
    }

    public static void main(String[] args) {
        Server server = new Server("localhost", 8181);
        server.start();
    }

    public Server(String host, int port) {
        this.SERVER_HOST = host;
        this.SERVER_PORT = port;
        initializationServerCommands();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("server started");
            ServerConsole console = new ServerConsole(clients, this);
            // запускаем консоль сервера в 1 поток, где будут отправляться сообщения всем клиентам, которые к нам подключились
            new Thread(console).start();
            while (isServerActive()) {
                // ждем клиента - слушатель
                Socket socket = serverSocket.accept();
                ClientHandler client = new Client(socket, this);
                new Thread(client).start();
                System.out.println("client connected");
            }
        } catch (IOException e) {
            System.out.println("server error: ");
            e.printStackTrace();
        } finally {
            System.out.println("server stopped");
            closeAllClientConnections();
        }
    }

    @Override
    public boolean isServerActive() {
        return isActive;
    }

    @Override
    public synchronized void removeClient(String client) {
        clients.remove(client);
    }

    @Override
    public synchronized void sendMessageAllClients(String message) {
        for (String client : clients.keySet()) {
            clients.get(client).write(message);
        }
    }

    @Override
    public synchronized void sendMessageForClient(String message, ClientHandler client) {
        try {
            message = message.replaceAll("/w", "");
            String[] data = message.trim().split(" ");
            String nickName = data[0];
            String localMessage = data[1];
            if (clients.containsKey(nickName)) {
                ClientHandler clientOut = clients.get(nickName);
                clientOut.write("ls - [" + client.getName() + "] :" + localMessage);
                return;
            }
            client.write("Нет получателя с ником " + nickName + ", либо он вышел из чата");
        } catch (RuntimeException e) {
            client.write("Неверный формат команды /w");
        }
    }

    @Override
    public void addClient(String nickName, Client client) {
        clients.put(nickName, client);
    }

    @Override
    public boolean isContainsNickName(String nickName) {
        return clients.containsKey(nickName);
    }

    @Override
    public String getClientsNames() {
        StringBuilder builder = new StringBuilder();
        builder.append("Список участников:" + "\n");
        List<String> list = new ArrayList<>(clients.keySet());
        list.sort(new SortComparator());
        for (String nick : list) {
            builder.append(nick).append("\n");
        }
        return builder.toString();
    }

    @Override
    public String getWelcomeMessage(String nickName) {
        return "Приветствуем Вас в нашем чате, " + nickName + "\n" + getServerCommands();
    }

    @Override
    public String getAuthMessage() {
        return "Приветствуем Вас в нашем чате. Для авторизации введите ваш никнейм в поле ввода в формате \n" +
                " /auth nickname и нажмите Enter";
    }

    @Override
    public String getServerName() {
        return "Chat_server";
    }

    @Override
    public String getServerCommands() {
        return serverCommands.toString();
    }

    private void initializationServerCommands() {
        serverCommands.append("Список команд:" + "\n");
        for (String key : allCommands.keySet()) {
            serverCommands.append(key).append(" - ").append(allCommands.get(key)).append("\n");
        }
    }

    private void closeAllClientConnections() {
        for (String client : clients.keySet()) {
            clients.get(client).closeConnection();
            clients.remove(client);
        }
    }
}