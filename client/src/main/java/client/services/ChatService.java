package client.services;

import java.io.IOException;

public interface ChatService {

    void clearInputField();

    void addMessageForClient(String message);

    void sendMessage(String message) throws IOException;

    void setNickname(String nickname);

    String getInputFieldText();

    boolean isExit();

}
