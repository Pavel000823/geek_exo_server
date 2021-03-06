package client;

import client.actions.SendMessageAction;
import client.services.ChatService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Chat extends JFrame implements ChatService {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8181;
    public boolean exit = true;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;

    private String nickname = "";


    private JTextArea messageArea;
    private JTextField inputField;


    // создаем окно
    public void start() {
        setTitle("Chat");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.out.println("Соединение разорвано");
                exit = !exit;
                closeAllConnections();
                exit();
            }
        });
        setScreenSize();
        setResizable(true);
        initializationPanels();
        setVisible(true);
        connection();
    }

    @Override
    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
    }

    public void clearInputField() {
        inputField.setText("");
    }

    @Override
    public void addMessageForClient(String message) {
        messageArea.append(message + "\n");
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String getNickName() {
        return nickname;
    }

    @Override
    public String getInputFieldText() {
        return inputField.getText();
    }


    // создаем наши панели
    private void initializationPanels() {
        setInputMessageFiled();
        setArea();
    }

    // надстройка тектовой области
    private void setArea() {
        messageArea = new JTextArea(8, 20);
        messageArea.setEditable(false);
        ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
        scrollPane.add(messageArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    // надстройка полей ввода
    private void setInputMessageFiled() {
        // поле ввода
        JPanel underPanel = new JPanel();
        inputField = new JTextField();
        inputField.addKeyListener(new SendMessageAction(this));
        underPanel.setLayout(new GridLayout(2, 15));
        underPanel.add(new JLabel("Введите сообщение"));
        this.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                inputField.requestFocus();
            }
        });
        underPanel.add(inputField);
        add(underPanel, BorderLayout.SOUTH);
    }

    // формируем размер окна в зависимости от экрана
    private void setScreenSize() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        setBounds(0, 0, screenWidth / 3, screenHeight / 3);
    }

    private void closeAllConnections() {
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void connection() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // запускаем слушатель в отдельный поток
            Runnable serverListener = new ServerListener(socket, this);
            Thread thread = new Thread(serverListener);
            thread.start();

        } catch (IOException e) {
            addMessageForClient("Не удалось установить соединение с сервером, попробуйте позже");
            try {
                Thread.sleep(5000);
                exit();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }

    @Override
    public boolean isExit() {
        return exit;
    }

    private void exit() {
        System.exit(0);
    }
}
