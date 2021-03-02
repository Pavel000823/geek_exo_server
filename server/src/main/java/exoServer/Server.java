package exoServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private boolean isActive = true;
    public final String SERVER_HOST;
    public final int SERVER_PORT;
    private List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        Server server = new Server("localhost", 8181);
        server.start();
    }

    public Server(String host, int port) {
        this.SERVER_HOST = host;
        this.SERVER_PORT = port;
    }


    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Сервер запущен");
            ServerConsole console = new ServerConsole(clients, this);
            // запускаем консоль сервера в 1 поток, где будут отправляться сообщения всем клиентам, которые к нам подключились
            new Thread(console).start();
            while (isServerActive()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
                System.out.println("Клиент подключен");
            }
        } catch (IOException e) {
            System.out.println("Что то пошло не так");
            e.printStackTrace();
        } finally {
            System.out.println("Сервер завершает работу");
            closeAllClientConnections();
        }
    }

    public boolean isServerActive() {
        return isActive;
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    private void closeAllClientConnections() {
        for (ClientHandler client : clients) {
            client.closeConnection();
            clients.remove(client);
        }
    }


}

