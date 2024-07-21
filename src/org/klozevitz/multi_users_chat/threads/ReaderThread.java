package org.klozevitz.multi_users_chat.threads;

import java.net.Socket;

public class ReaderThread extends Thread {
    private final int id;
    private final Socket client;

    public ReaderThread(int id, Socket client) {
        this.id = id;
        this.client = client;
    }

    @Override
    public void run() {

    }
}
