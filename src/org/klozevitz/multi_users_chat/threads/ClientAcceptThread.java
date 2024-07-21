package org.klozevitz.multi_users_chat.threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ClientAcceptThread extends Thread {
    private final ServerSocket server;
    private final List<Socket> users;
    private final List<Thread> readingThreads;

    public ClientAcceptThread(ServerSocket server, List<Socket> users, List<Thread> readingThreads) {
        this.server = server;
        this.users = users;
        this.readingThreads = readingThreads;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket client = server.accept();
                users.add(client);
                
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
