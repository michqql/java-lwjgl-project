package me.michqql.engine.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ServerConnection extends Thread {

    private final Socket clientSocket;

    public ServerConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            final InputStream in = clientSocket.getInputStream();
            int len = 0;
            while(clientSocket.isConnected()) {
                // Check for incoming messages

                // First 2 bytes will be the header saying how long the rest of the packet is
                if(len <= 0 && in.available() >= 4) {
                    len = (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | in.read();
                }

                // Check if there are enough bytes to read a packet
                if(in.available() >= len - Integer.BYTES) {
                    // Can read packet
                    byte[] data = in.readNBytes(len - Integer.BYTES);
                    parsePacket(len, data);
                    len = 0; // Reset read for read next packet
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parsePacket(int len, byte[] data) {
        // Check there are enough bytes for the packet ID
        if(len < Integer.SIZE * 2)
            return; // Discard invalid packet

        // Get the packet ID
        int packetID = read32bitInt(data, 0);

        // Make the packet from that ID
        // Use reflection? Switch? https://www.eevblog.com/forum/microcontrollers/best-practice-for-messaging-protocol-parsing-implementation/
    }

    private int read32bitInt(byte[] data, int index) {
        return (data[index] << 24) | (data[index + 1] << 16) | (data[index + 2] << 8) | data[index + 3];
    }
}
