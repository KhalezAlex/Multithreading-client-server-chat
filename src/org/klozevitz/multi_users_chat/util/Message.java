package org.klozevitz.multi_users_chat.util;

public class Message {
    private final int clientId;
    private final String message;

    public Message(int clientId, String message) {
        this.clientId = clientId;
        this.message = message;
    }

    public int getClientId() {
        return clientId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Message{" + clientId + " : " + message + '}';
    }
}
