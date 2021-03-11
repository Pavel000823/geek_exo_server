package server;

import services.ClientHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Client implements ClientHandler, Runnable {
    private final Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private final Server server;
    private String nickName;
    private int authCount = 0;
    private boolean isAuthorization = false;

    public Client(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            write(server.getAuthMessage());

            if (!isAuthorizationClient()) {
                return;
            }
            write(server.getWelcomeMessage(nickName));
            // добавляем клиента в список клиентов
            server.addClient(nickName, this);

            while (server.isServerActive()) {
                try {
                    Thread.sleep(500);
                    String message = in.readUTF();
                    if (message.isEmpty()) {
                        continue;
                    }
                    // переписать, вынести
                    if (message.startsWith("/")) {
                        if (message.startsWith("/w")) {
                            out.writeUTF(message);
                            server.sendMessageForClient(message, this);
                            continue;
                        }
                        if (message.startsWith("/ls")) { // enum
                            out.writeUTF(message);
                            out.writeUTF(server.getServerCommands());
                            continue;
                        }
                        if (message.startsWith("/end")) {
                            System.out.println("disconnected");
                            break;
                        }
                        if (message.startsWith("/list")) {
                            out.writeUTF(message);
                            out.writeUTF(server.getClientsNames());
                            continue;
                        }
                    }
                    server.sendMessageAllClients(getFormattedMessage(message));
                } catch (SocketException | EOFException e) {
                    System.out.println("disconnected");
                    break;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.out.println("Error - " + e.getMessage());
        } finally {
            closeConnection();
            System.out.println(nickName + " disconnected");
        }
    }


    private boolean isAuthorizationClient() throws IOException {

        TimeOutHandler timeOutHandler = new TimeOutHandler(this,120);
        Thread thread = new Thread(timeOutHandler);
        thread.start();

        while (true) {
            try {
                if (authCount > 4) {
                    out.writeUTF("Превышено количество попыток авторизации, попробуйте позднее");
                    timeOutHandler.setErrorFlag(true);
                    return false;
                }
                String str = in.readUTF().trim();
                if (!str.startsWith("/auth")) {
                    out.writeUTF("Неверный формат команды");
                    authCount++;
                    continue;
                }
                nickName = str.split(" ")[1];
                if (server.isContainsNickName(nickName)) {
                    out.writeUTF("Уже есть клиент с таким ником");
                    authCount++;
                    continue;
                }
                setIsAuthorization(true);
                return true;
            } catch (RuntimeException e) {
                out.writeUTF("Неверный формат команды /auth");
                authCount++;
            }
        }
    }

    private void setIsAuthorization(boolean isAuthorization) {
        this.isAuthorization = isAuthorization;
    }

    @Override
    public boolean isAuthorization() {
        return isAuthorization;
    }

    private String getFormattedMessage(String message) {
        return "[" + getName() + "] : " + message;
    }

    @Override
    public void closeConnection() {
        server.removeClient(nickName);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.out.println(nickName + " disconnected");
        }
    }

    @Override
    public String getName() {
        return nickName;
    }
}

