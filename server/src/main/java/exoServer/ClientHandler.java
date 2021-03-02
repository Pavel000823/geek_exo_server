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

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            while (true) {
                try {
                    Thread.sleep(500);
                    if (!ServerConsole.isServerActive()) {
                        break;
                    }
                    String str = in.readUTF();
                    if (str.contains("/end")) {
                        out.writeUTF("Приходите к нам еще:)");
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
        }
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void closeConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
