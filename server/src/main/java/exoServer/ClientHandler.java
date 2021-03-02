package exoServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;

    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            while (server.isServerActive()) {
                try {
                    Thread.sleep(500);
                    String str = in.readUTF();
                    if (str.contains("/end")) {
                        break;
                    }
                    if (str.isEmpty()) {
                        continue;
                    }
                    out.writeUTF("Эхо : " + str);
                } catch (EOFException | SocketException e) {
                    System.out.println("Соединение с клиентом потеряно");
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
            System.out.println("Клиент завершил свою работу");
        }
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void closeConnection() {
        server.removeClient(this);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

