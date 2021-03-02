package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8181;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            // запускаем слушатель сервера в отдельный поток

//            ServerListener listener = new ServerListener(socket);
//            Thread thread = new Thread(listener);
//            thread.setPriority(1);
//            thread.start();

            // в текущем потоке отправляем команды серверу сами
            while (true) {
//                if (in.available() != 0) {
//                    System.out.println(in.readUTF());
//                }
                String str = scanner.nextLine();
                if (str.equals("/end")) {
                    break;
                }
                out.writeUTF(str);
                String strFromServer = in.readUTF();
                System.out.println(strFromServer);
            }
        }
    }
}