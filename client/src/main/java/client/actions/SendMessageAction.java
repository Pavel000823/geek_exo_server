package client.actions;

import client.Chat;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class SendMessageAction implements KeyListener {
    private final Chat chat;

    public SendMessageAction(Chat chat) {
        this.chat = chat;
    }

    private void sendMessage() {
            String message = chat.getInputField().getText();
            if (message.isEmpty()) {
                chat.addMessageForClient("Поле не может быть пустым");
                return;
            }
            chat.getInputField().setText("");
            try {
                chat.write(message);
            } catch (IOException e) {
                chat.addMessageForClient("Не удалось отправить сообщение. Попробуйте еще раз");
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
