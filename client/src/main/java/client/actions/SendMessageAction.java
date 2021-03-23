package client.actions;

import client.services.ChatService;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class SendMessageAction implements KeyListener {
    private final ChatService chatService;

    public SendMessageAction(ChatService chatService) {
        this.chatService = chatService;
    }

    private void sendMessage() {
            String message = chatService.getInputFieldText();
            if (message.isEmpty()) {
                chatService.addMessageForClient("Поле не может быть пустым");
                return;
            }
            chatService.clearInputField();
            try {
                chatService.sendMessage(message);
            } catch (IOException e) {
                chatService.addMessageForClient("Не удалось отправить сообщение. Попробуйте еще раз");
            }
        }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            sendMessage();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            sendMessage();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        }
    }
}
