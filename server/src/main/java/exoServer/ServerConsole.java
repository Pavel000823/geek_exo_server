package exoServer;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ServerConsole implements Runnable {

    private static boolean isActive;
    private Scanner in = new Scanner(System.in);
    private List<ClientHandler> clients;

    public ServerConsole(List<ClientHandler> clients) {
        this.clients = clients;
    }

    @Override
    public void run() {
        isActive = true;
        while (isActive) {
            String command = in.nextLine();
            if (command.isEmpty()) {
                continue;
            }
            if (command.startsWith("/stop")) {
                isActive = false;
                break;
            }
            if(!clients.isEmpty()){
            sendMessage(command);
        }else System.out.println("Некому отправлять месседж");}
    }

    public void sendMessage(String message) {
        for (ClientHandler client : clients) {
            try {
                client.getOut().writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isServerActive() {
        return isActive;
    }
}
