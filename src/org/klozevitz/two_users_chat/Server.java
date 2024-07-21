package org.klozevitz;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Server {
    private final int BACKLOG = 4; // количество человек на сервере
    private final List<Client> users; // список подключенных клиентов
    private final String host;
    private final int port;
    private Scanner in;
    private PrintWriter out;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        this.users = new ArrayList<>();
    }

    public void start() {
        try (
                ServerSocket server = serverInit();
                Socket client = clientInit(server)
        ) {
            Thread sendThread = new ServerOutputThread(out);
            sendThread.start();

            while (true) {
                String fromClient = receive();
                if (fromClient.contains("exit")) {
                    send("exit", out);
                    break;
                }
                System.out.println(fromClient);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
            out.close();
        }
    }

    private ServerSocket serverInit() {
        System.out.println("server starting...");
        try {
            return new ServerSocket(port, 10, InetAddress.getByName(host));
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
        Scanner sc = new Scanner(System.in);
        System.out.println();
        String message = sc.nextLine();
        send(message, out);
    }

    private void send(String message, PrintWriter clientOutputStream) {
        clientOutputStream.println(message);
    }

    private String receive() {
        try {
            return in.nextLine();
        } catch (NoSuchElementException e) {
            return "exit";
        }
    }

    public static void main(String[] args) {
        Server server = new Server("25.7.187.76", 9000);
        server.start();
    }

    class ServerOutputThread extends Thread {
        private final PrintWriter out;
        private final Scanner console;

        public ServerOutputThread(PrintWriter out) {
            this.out = out;
            this.console = new Scanner(System.in);
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                String message = console.nextLine();
                out.println("SERVER> " + message);
                if (message.equals("exit")) {
                    break;
                }
            }
            console.close();
        }
    }
}
