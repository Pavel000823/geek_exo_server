package client;

import java.io.DataInputStream;
import java.io.IOException;
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
            while (ClientApp.end) {
                    if (in.available() > 0) {
                        String dataFromServer = in.readUTF();
                        System.out.println(dataFromServer);
                    }
                    Thread.sleep(1000);
                }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}