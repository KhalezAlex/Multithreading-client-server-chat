package org.klozevitz.multi_users_chat;

import org.klozevitz.multi_users_chat.threads.ClientAcceptThread;
import org.klozevitz.multi_users_chat.util.Message;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class MultiUsersServer {
    private static final int BACKLOG = 4; // количество человек на сервере
    private static final List<Socket> USERS = new ArrayList<>(); // список подключенных клиентов
    private static final List<Thread> READER_THREADS = new ArrayList<>(); // список потоков чтения данных
    private static final Queue<Message> MESSAGES = new ArrayDeque<>(); // очередь сообщений, которая будет разгребаться в отдельном потоке
    private final String host;
    private final int port;


    public MultiUsersServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (ServerSocket server = serverInit()) {
            ClientAcceptThread acceptThread = new ClientAcceptThread(server, USERS, READER_THREADS, MESSAGES);
            acceptThread.start();

            Thread.sleep(600_000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {

        }
    }

    private ServerSocket serverInit() {
        System.out.println("server starting...");
        try {
            return new ServerSocket(port, BACKLOG, InetAddress.getByName(host));
        } catch (IOException e) {
            System.out.println("ошибка при инициализации серверного сокета");
            throw new RuntimeException(e);
        }
    }

    private Socket clientInit(ServerSocket server) {
        Socket client = null;
        try {
            client = server.accept();
        } catch (IOException e) {
            System.out.println("ошибка при подключении клиентского сокета");
            throw new RuntimeException(e);
        }
        inputOutputStreamsInit(client);
        return client;
    }

    private void inputOutputStreamsInit(Socket client) {
//        in = inInit(client);
//        out = outInit(client);
    }

    private PrintWriter outInit(Socket client) {
        try {
            return new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    client.getOutputStream()
                            )
                    ), true
            );
        } catch (IOException e) {
            System.out.println("ошибка при инициализации потока вывода сервера");
            throw new RuntimeException(e);
        }
    }

    private void sendMessage() {
        Scanner sc = new Scanner(System.in);
        System.out.println();
        String message = sc.nextLine();
//        send(message, out);
    }

    private void send(String message, PrintWriter clientOutputStream) {
        clientOutputStream.println(message);
    }

    public static void main(String[] args) {
        MultiUsersServer server = new MultiUsersServer("127.0.0.1", 9000);
        server.start();
    }
}
