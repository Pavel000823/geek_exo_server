package server;

import services.ClientHandler;

public class TimeOutHandler implements Runnable {

    private final ClientHandler client;
    private int seconds;
    private boolean errorFlag = false;

    public TimeOutHandler(ClientHandler client, int seconds) {
        this.client = client;
        this.seconds = seconds;
    }

    @Override
    public void run() {
        for (int i = 0; i < seconds; i++) {
            try {
                if (client.isAuthorization()) {
                    break;
                }
                if (errorFlag) {
                    break;
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        if (!client.isAuthorization() && !errorFlag) {
            client.write("Превышено время ожидания. Вы отключены от сервера");
            client.write("/end");
            client.closeConnection();
        }
    }

    public void setErrorFlag(boolean errorFlag) {
        this.errorFlag = errorFlag;
    }
}
