package client;

import client.services.ChatService;
import client.services.HistoryService;
import client.storage.HistoryHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerListener implements Runnable {

    private final Socket socket;
    private final ChatService chatService;
    private HistoryService historyService;

    public ServerListener(Socket socket, ChatService chatService) {
        this.socket = socket;
        this.chatService = chatService;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
            isAuthorization(in);
            chatService.addMessageForClient(historyService.getHistory());
            while (chatService.isExit()) {
                String dataFromServer = in.readUTF();
                historyService.getLocalHistoryBuffer().add(dataFromServer);
                chatService.addMessageForClient(dataFromServer);
                Thread.sleep(1000);
            }
        } catch (InterruptedException | IOException e) {
            chatService.addMessageForClient("Соединение разорвано");
        } finally {
            historyService.writeHistory(historyService.getLocalHistoryBuffer().get());
        }
    }

    public void isAuthorization(DataInputStream in) throws IOException {
        while (true) {
            String data = in.readUTF();
            if (data.startsWith("/true")) {//контракт который установлен с сервером на событие успешной авторизации.Сервер обязуется отдать в
                String nickname = data.split(" ")[1];
                chatService.setNickname(nickname);
                historyService = new HistoryHandler(nickname);
                return;
            }
            chatService.addMessageForClient(data);
        }
    }

}