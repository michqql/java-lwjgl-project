package me.michqql.engine.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {

    private ServerSocket socket;
    private final ArrayList<ServerConnection> serverConnections = new ArrayList<>();
    private boolean running = false;

    public Server() throws IOException {
        socket = new ServerSocket(2000);
    }

    @Override
    public void run() {
        running = true;
        while(running) {
            // Try and accept a connection
            try {
                Socket client = socket.accept();
                ServerConnection conn = new ServerConnection(client);
                serverConnections.add(conn);
                conn.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
