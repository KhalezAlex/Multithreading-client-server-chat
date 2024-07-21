package org.klozevitz.multi_users_chat.threads;

import org.klozevitz.multi_users_chat.util.Message;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;

public class ReaderThread extends Thread {
    private final int id;
    private final Socket client;
    private final Scanner in;
    private final Queue<Message> messages;

    public ReaderThread(int id, Socket client, Queue<Message> messages) {
        this.id = id;
        this.client = client;
        this.messages = messages;
        this.in = inInit();
    }

    @Override
    public void run() {
        while (true) {
            String fromClient = receive();
            if (fromClient.contains("exit")) {
                closeResources();
                break;
            } else {
                synchronized (messages) {
                    messages.add(new Message(id, fromClient));
                }
            }
        }
    }

    private Scanner inInit() {
        try {
            return new Scanner(
                    new InputStreamReader(
                            client.getInputStream()
                    )
            );
        } catch (IOException e) {
            System.out.println("ошибка инициализации потока ввода");
            throw new RuntimeException(e);
        }
    }

    private String receive() {
        try {
            return in.nextLine();
        } catch (NoSuchElementException e) {
            return "exit";
        }
    }

    private void closeResources() {
        try {
            in.close();
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
