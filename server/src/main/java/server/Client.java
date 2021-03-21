package server;

import services.ClientHandler;
import services.MessengerServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Client implements ClientHandler {
    private final Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private final MessengerServer server;
    private String nickName;
    private int authCount = 0;
    private final int maxAuthCount = 10;
    private boolean isAuthorization = false;
    private final int PASSWORD_MIN_LENGTH = 5;
    private final int LOGIN_MIN_LENGTH = 5;
    private final int PASSWORD_MAX_LENGTH = 20;
    private final int LOGIN_MAX_LENGTH = 20;


    public Client(Socket clientSocket, MessengerServer server) {
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
                        if (message.startsWith(Server.SEND_MESSAGE)) {
                            write(message);
                            server.sendMessageForClient(message, this);
                            continue;
                        }
                        if (message.startsWith(Server.COMMANDS_LIST)) { // enum
                            write(message);
                            write(server.getServerCommands());
                            continue;
                        }
                        if (message.startsWith(Server.EXIT)) {
                            System.out.println("disconnected");
                            break;
                        }
                        if (message.startsWith(Server.USER_LIST)) {
                            write(message);
                            write(server.getClientsNames());
                            continue;
                        }
                        if (message.startsWith(Server.USER_RENAME)) {
                            userRename(message);
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

    @Override
    public void write(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.out.println(nickName + " disconnected");
        }
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
    public String getName() {
        return nickName;
    }

    @Override
    public boolean isAuthorization() {
        return isAuthorization;
    }

    private void setIsAuthorization(boolean isAuthorization) {
        this.isAuthorization = isAuthorization;
    }

    private String getFormattedMessage(String message) {
        return "[" + getName() + "] : " + message;
    }

    private boolean isAuthorizationClient() throws IOException {
        String command = "";

        TimeOutHandler timeOutHandler = new TimeOutHandler(this, Server.SERVER_AUTHORIZATION_TIMEOUT);
        Thread thread = new Thread(timeOutHandler);
        thread.start();

        while (true) {
            try {
                if (authCount > maxAuthCount) {
                    write("Превышено количество попыток авторизации, попробуйте позднее");
                    timeOutHandler.setErrorFlag(true);
                    return false;
                }

                String str = in.readUTF().trim();
                write(str);

                if (!(str.startsWith(Server.AUTH) || str.startsWith(Server.REGISTRATION))) {
                    write("Неверный формат команды");
                    authCount++;
                    continue;
                }

                String[] data = str.split(" ");
                command = data[0];
                nickName = data[1];
                String password = data[2];

                if (!checkNickName(nickName)) {
                    write("Длинна никнейма должна быть от 5 до 20 символов");
                    continue;
                }
                if (!checkPassword(password)) {
                    write("Длинна пароля должна быть от 5 до 20 символов");
                    continue;
                }

                if (command.equals(Server.AUTH)) {
                    if (server.checkExistUser(nickName, password)) {
                        setIsAuthorization(true);
                        return true;
                    }
                    write("Неверный логин или пароль");
                    authCount++;
                    continue;
                }
                if (command.startsWith(Server.REGISTRATION)) {
                    if (server.checkExistUser(nickName, password)) {
                        write("Уже есть клиент с таким ником, придумайте другой");
                        authCount++;
                        continue;
                    }
                    server.addNewUser(nickName, password);
                    write("Вы успешно зарегестрированны");
                    setIsAuthorization(true);
                    return true;
                }

            } catch (RuntimeException e) {
                write("Неверный формат команды " + Server.REGISTRATION + " или " + Server.AUTH);
                authCount++;
            }
        }
    }

    private void userRename(String newNickName) throws IOException {
        try {
            String lastNickName = nickName;
            String[] data = newNickName.split(" ");
            String nick = data[1];
            if (!checkNickName(nick)) {
                write("Длинна никнейма должна быть от 5 до 20 символов");
                return;
            }
            if (!server.checkExistUser(nick)) {
                server.updateClient(nickName, nick);
                this.nickName = nick;
                server.removeClient(lastNickName);
                server.addClient(nickName, this);
                write("Вы успешно изменили ник");
                return;
            }
        } catch (RuntimeException e) {
            write("Неверный формат команды " + Server.USER_RENAME);
        }
        write("Уже есть клиент с таким никнеймом");
    }

    private boolean checkPassword(String password) {
        int length = password.length();
        return length >= PASSWORD_MIN_LENGTH && length <= PASSWORD_MAX_LENGTH;
    }

    private boolean checkNickName(String login) {
        int length = login.length();
        return length >= LOGIN_MIN_LENGTH && length <= LOGIN_MAX_LENGTH;
    }

}

