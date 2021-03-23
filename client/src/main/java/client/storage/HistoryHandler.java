package client.storage;

import client.services.Buffer;
import client.services.HistoryService;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryHandler implements HistoryService {


    private File historyFile;
    private final String username;
    private final Buffer buffer = new StringBuffer();
    private final int HISTORY_LENGTH;


    public HistoryHandler(String username) {
        this.username = username;
        HISTORY_LENGTH = 100;
    }


    public void writeHistory(String history) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFile(username), true))) {
            writer.write(history);
        } catch (IOException e) {
            System.out.println("Не удалось записать историю в файл локальной истории");
        }
    }

    @Override
    public String getHistory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(getFile(username)))) {
            StringBuilder data = new StringBuilder();
            List<String> lines = reader.lines().collect(Collectors.toList());
            int count = getFileRow(lines);
            for (int i = count; i < lines.size(); i++) {
                data.append(lines.get(i)).append("\n");
            }
            return data.toString();
        } catch (
                IOException ioException) {
            System.out.println("Не удалось прочитать файл локальной истории");
        }
        return "Не удалось найти историю";
    }

    @Override
    public Buffer getLocalHistoryBuffer() {
        return buffer;
    }

    private int getFileRow(List<String> list) {
        if (list == null) {
            throw new NullPointerException("Не удалось считать данные из файла локальной истории по пути " + getFilePath(username));
        }
        int row = 0;
        if (list.size() > HISTORY_LENGTH) {
            row = list.size() - HISTORY_LENGTH;
            return row;
        }
        return row;
    }


    private File getFile(String fileName) throws IOException {
        if (historyFile == null) {
            historyFile = new File(getFilePath(fileName));
            if (historyFile.isFile()) {
                return historyFile;
            }
            historyFile.createNewFile();
            return historyFile;
        }
        return historyFile;
    }

    private String getFilePath(String username) {
        return "C:\\Users\\Павел\\IdeaProjects\\exo_server\\client\\src\\main\\resources\\localHistory\\history_" + username + ".txt";
    }
}
