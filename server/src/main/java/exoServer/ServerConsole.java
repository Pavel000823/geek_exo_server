package exoServer;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ServerConsole implements Runnable {

    private Scanner in = new Scanner(System.in);
    private List<ClientHandler> clients;
    private Server server;

    public ServerConsole(List<ClientHandler> clients, Server server) {
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
                sendMessage(command);
            } else System.out.println("Некому отправлять месседж");
        }
    }

    public void sendMessage(String message) {
        for (ClientHandler client : clients) {
            try {
                client.getOut().writeUTF(message);
            } catch (IOException e) {
                System.out.println("Потеряно соединение с клиентом");
            }
        }
    }
}
