package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.Socket;

public class ServerListener implements Runnable {

    private Socket socket;
    private DataInputStream in;

    public ServerListener(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            while (true) {
                try {
                    String dataFromServer = in.readUTF();
                    System.out.println(dataFromServer);
                } catch (UTFDataFormatException e) {
                    System.out.println("Что то пошло не так");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}