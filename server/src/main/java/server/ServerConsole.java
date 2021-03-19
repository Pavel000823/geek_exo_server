package server;

import services.ClientHandler;
import services.MessengerServer;

import java.util.Map;
import java.util.Scanner;

public class ServerConsole implements Runnable {

    private final Scanner in = new Scanner(System.in);
    private final Map<String, ClientHandler> clients;
    private final MessengerServer server;

    public ServerConsole(Map<String, ClientHandler> clients, MessengerServer server) {
        this.clients = clients;
        this.server = server;
    }

    @Override
    public void run() {
        while (server.isServerActive()) {
            String command = in.nextLine();
            if (command.isEmpty()) {
                continue;
            }
            if (!clients.isEmpty()) {
                sendMessageAllClients(command);
            } else System.out.println("Некому отправлять месседж");
        }
    }

    private void sendMessageAllClients(String message) {
        for (String client : clients.keySet()) {
            clients.get(client).write(" [" + server.getServerName() + "] : " + message);
        }
    }
}
