package me.michqql.game.net;

import java.net.Socket;

public class Client extends Thread {

    private Socket server;
    private boolean running;



    @Override
    public void run() {
        running = true;
        while(running) {

        }
    }
}
