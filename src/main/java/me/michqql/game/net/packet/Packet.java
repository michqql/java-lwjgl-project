package me.michqql.game.net.packet;

import me.michqql.game.net.FriendlyByteBuffer;
import me.michqql.game.net.ServerConnection;

public abstract class Packet<T extends PacketListener> {

    public static final int MAX_PACKET_LENGTH_BYTES = 1024;
    public static final int PROTOCOL_VERSION = 1;

    /*
        PACKET STRUCTURE: lengthOfPacket(4), packetId(4), payload(length - 1 - 4)
     */

    private final int packetId;
    private ServerConnection connection;

    public Packet(int packetId) {
        this.packetId = packetId;
    }

    public abstract void write(FriendlyByteBuffer out);
    public abstract void handle(T listener, FriendlyByteBuffer in);
}
