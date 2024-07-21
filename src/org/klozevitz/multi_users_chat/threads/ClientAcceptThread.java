package org.klozevitz.multi_users_chat.threads;

import org.klozevitz.multi_users_chat.util.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class ClientAcceptThread extends Thread {
    private final ServerSocket server;
    private final List<Socket> users;
    private final List<Thread> readerThreads;
    private final Queue<Message> messages;

    public ClientAcceptThread(ServerSocket server, List<Socket> users, List<Thread> readerThreads, Queue<Message> messages) {
        this.server = server;
        this.users = users;
        this.readerThreads = readerThreads;
        this.messages = messages;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket client = server.accept();
                users.add(client);
                ReaderThread readerThread = new ReaderThread(generateId(), client, messages);
                readerThreads.add(readerThread);
                readerThread.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int generateId() {
        return new Random().nextInt(users.size() * 10000, (users.size() + 10) * 10000);
    }
}
