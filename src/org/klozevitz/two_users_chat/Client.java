package org.klozevitz.two_users_chat;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client  {
    private final String host;
    private final int port;
    private Scanner in;
    private PrintWriter out;
    private final Scanner CONSOLE = new Scanner(System.in);

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (Socket client = clientInit()) {
            ClientOutputThread sendThread = new ClientOutputThread(out);
            sendThread.start();

            while (true) {
                String fromServer = receive();
                System.out.println(fromServer);
                if (fromServer.equals("exit")) {
                    send("exit");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("ошибка подключения к серверу");
            e.printStackTrace();
        }
    }

    private Socket clientInit() {
        Socket client;
        try {
            client = new Socket(host, port);
        } catch (IOException e) {
            System.out.println("ошибка подключения к серверу");
            throw new RuntimeException(e);
        }
        inputOutputStreamsInit(client);
        return client;
    }

    private void inputOutputStreamsInit(Socket client) {
        in = inInit(client);
        out = outInit(client);
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

    private Scanner inInit(Socket client) {
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

    private void sendMessage() {
        System.out.println();
        String message = CONSOLE.nextLine();
        send(message);
    }

    private void send(String message) {
        out.println(message);
    }

     private String receive() {
        return in.nextLine();
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 9000);
        client.start();
    }

    class ClientOutputThread extends Thread {
        private final PrintWriter out;
        private final Scanner console;

        public ClientOutputThread(PrintWriter out) {
            this.out = out;
            this.console = new Scanner(System.in);
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                String message = console.nextLine();
                out.println("CLIENT> " + message);
                if (message.equals("exit")) {
                    break;
                }
            }
            console.close();
        }
    }
}
