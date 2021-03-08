package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerListener implements Runnable {

    private final Socket socket;
    private final Chat chat;
    private DataInputStream in;

    public ServerListener(Socket socket, Chat chat) {
        this.socket = socket;
        this.chat = chat;
    }

    // ждем ответ от сервера, если есть то выводим на экран
    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            while (Chat.isEnd) {
                String dataFromServer = in.readUTF();
                chat.addMessageForClient(dataFromServer);
                Thread.sleep(1000);
            }
        } catch (InterruptedException | IOException e) {
            chat.addMessageForClient("Соединение разорвано");
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}