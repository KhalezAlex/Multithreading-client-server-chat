package org.klozevitz.multi_users_chat.threads;

import java.io.PrintWriter;
import java.util.Scanner;

public class ServerOutputThread extends Thread {
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
