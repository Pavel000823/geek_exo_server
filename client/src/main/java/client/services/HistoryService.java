package client.services;

public interface HistoryService {

    void writeHistory(String history);

    String getHistory();

    Buffer getLocalHistoryBuffer();
}
