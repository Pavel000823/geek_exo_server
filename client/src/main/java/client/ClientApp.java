package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8181;
    public static boolean end = true;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            // запускаем слушатель сервера в отдельный поток
            ServerListener listener = new ServerListener(socket);
            Thread thread = new Thread(listener);
            thread.start();

            // в текущем потоке отправляем команды серверу сами
            while (true) {
                String str = scanner.nextLine();
                if (str.equals("/end")) {
                    out.writeUTF("/end");
                    end = false;
                    break;
                }
                out.writeUTF(str);
                String strFromServer = in.readUTF();
                System.out.println(strFromServer);
            }
        } catch (ConnectException e) {
            System.out.println("Не удалось подключиться к серверу, попробуйте позднее");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Клиент завершил свою работу");
    }
}