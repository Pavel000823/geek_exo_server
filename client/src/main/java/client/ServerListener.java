package client;

import client.services.ChatService;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerListener implements Runnable {

    private final Socket socket;
    private final ChatService chatService;

    public ServerListener(Socket socket, ChatService chatService) {
        this.socket = socket;
        this.chatService = chatService;
    }

    // ждем ответ от сервера, если есть то выводим на экран
    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
            isAuthorization(in);
            while (chatService.isExit()) {
                String dataFromServer = in.readUTF();
                chatService.addMessageForClient(dataFromServer);
                Thread.sleep(1000);
            }
        } catch (InterruptedException | IOException e) {
            chatService.addMessageForClient("Соединение разорвано");
        }
    }

    public void isAuthorization(DataInputStream in) throws IOException {
        while (true) {
            String data = in.readUTF();
            if (data.startsWith("/true")) {//контракт который установлен с сервером на событие успешной авторизации.Сервер обязуется отдать в
                //    случае авторизации клиентом / true никнейм
                String nickname = data.split(" ")[1];
                chatService.setNickname(nickname);
                return;
            }
            chatService.addMessageForClient(data);
        }
    }
}