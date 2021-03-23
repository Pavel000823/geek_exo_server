package client.storage;

import client.services.Buffer;

public class StringBuffer implements Buffer {

    private StringBuilder buffer = new StringBuilder();

    public StringBuffer() {
    }

    @Override
    public void add(String data) {
        buffer.append(data).append("\n");
    }

    @Override
    public String get() {
        return buffer.toString();
    }
}
