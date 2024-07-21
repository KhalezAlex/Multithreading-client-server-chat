package org.klozevitz;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MultiUsersServer {
    private final int BACKLOG = 4; // количество человек на сервере
    /**
     * TODO избавиться от двух следующих списков и использовать только список сокетов для доступа к потока ввода и вывода
     * */
    private final List<Socket> users; // список подключенных клиентов
    private final List<PrintWriter> outs; // список потоков вывода подключенных клиентов
    private final List<Scanner> ins; // список потоков ввода подключенных клиентов
    private final String host;
    private final int port;

    public MultiUsersServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.users = new ArrayList<>();
        this.ins = new ArrayList<>();
        this.outs = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket server = serverInit()) {
            ClientAcceptThread acceptThread = new ClientAcceptThread(server, users);
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
//        send(message, out);
    }

    private void send(String message, PrintWriter clientOutputStream) {
        clientOutputStream.println(message);
    }

    private String receive() {
        try {
//            return in.nextLine();
            return null;
        } catch (NoSuchElementException e) {
            return "exit";
        }
    }

    public static void main(String[] args) {
        MultiUsersServer server = new MultiUsersServer("25.7.187.76", 9000);
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

    class ClientAcceptThread extends Thread {
        private final ServerSocket server;
        private final List<Socket> users;

        public ClientAcceptThread(ServerSocket server, List<Socket> users) {
            this.server = server;
            this.users = users;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Socket client = server.accept();
                    users.add(client);
                    System.out.println("кто-то подключился - " + users.size() + " человек в чате");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
