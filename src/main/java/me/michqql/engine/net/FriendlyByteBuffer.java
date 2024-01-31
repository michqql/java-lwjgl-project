package me.michqql.engine.net;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class FriendlyByteBuffer {

    private static final int MAX_STR_LENGTH = Short.MAX_VALUE;
    private static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;

    private final ByteBuffer buffer;

    public FriendlyByteBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public UUID readUuid() {
        long most = buffer.getLong();
        long least = buffer.getLong();
        return new UUID(most, least);
    }

    public void writeUuid(UUID uuid) {
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
    }

    public int readVarInt() {
        int varint = 0;
        int bytesUsed = 0;

        byte currentByte;
        do {
            currentByte = buffer.get();
            varint |= (currentByte & 127) << bytesUsed++ * 7;
            if (bytesUsed > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while((currentByte & 128) == 128);

        return varint;
    }

    public void writeVarInt(int varint) {
        while((varint & -128) != 0) {
            buffer.put((byte) (varint & 127 | 128));
            varint >>>= 7;
        }

        buffer.put((byte) varint);
    }

    public long readVarLong() {
        long varlong = 0L;
        int bytesUsed = 0;

        byte currentByte;
        do {
            currentByte = buffer.get();
            varlong |= (long)(currentByte & 127) << bytesUsed++ * 7;
            if (bytesUsed > 10) {
                throw new RuntimeException("VarLong too big");
            }
        } while((currentByte & 128) == 128);

        return varlong;
    }

    public void writeVarLong(long varlong) {
        while((varlong & -128L) != 0L) {
            buffer.put((byte) (varlong & 127L | 128));
            varlong >>>= 7;
        }

        buffer.put((byte) varlong);
    }

    public String readUtf() {
        return readUtf(MAX_STR_LENGTH);
    }

    public String readUtf(int maxLength) {
        int i = getMaxEncodedUtfLength(maxLength);
        int varint = readVarInt();
        if (varint > i) {
            throw new RuntimeException("The received encoded string buffer length is longer than maximum allowed (" + varint + " > " + i + ")");
        } else if (varint < 0) {
            throw new RuntimeException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            String str = decodeString(buffer.position(), varint, UTF8_CHARSET);
            buffer.position(buffer.position() + varint);
            if (str.length() > maxLength) {
                throw new RuntimeException("The received string length is longer than maximum allowed (" + str.length() + " > " + maxLength + ")");
            } else {
                return str;
            }
        }
    }

    public void writeUtf(String str) {
        writeUtf(str, MAX_STR_LENGTH);
    }

    public void writeUtf(String string, int maxLength) {
        if (string.length() > maxLength) {
            throw new RuntimeException("String too big (was " + string.length() + " characters, max " + maxLength + ")");
        } else {
            byte[] strBytes = string.getBytes(StandardCharsets.UTF_8);
            int i = getMaxEncodedUtfLength(maxLength);
            if (strBytes.length > i) {
                throw new RuntimeException("String too big (was " + strBytes.length + " bytes encoded, max " + i + ")");
            } else {
                writeVarInt(strBytes.length);
                buffer.put(strBytes);
            }
        }
    }

    public <T> void writeOptional(Optional<T> optional, BiConsumer<FriendlyByteBuffer, T> presentConsumer) {
        if(optional.isPresent()) {
            buffer.put((byte) 1);
            presentConsumer.accept(this, optional.get());
        } else {
            buffer.put((byte) 0);
        }
    }

    public <T> Optional<T> readOptional(Function<FriendlyByteBuffer, T> presentFunction) {
        byte present = buffer.get();
        if(present > 0) {
            return Optional.of(presentFunction.apply(this));
        }
        return Optional.empty();
    }

    public boolean readBoolean() {
        return buffer.get() > 0;
    }

    public void writeBoolean(boolean b) {
        buffer.put((byte) (b ? 1 : 0));
    }

    public byte readByte() {
        return buffer.get();
    }

    public void writeByte(byte b) {
        buffer.put(b);
    }

    public short readShort() {
        return buffer.getShort();
    }

    public void writeShort(short s) {
        buffer.putShort(s);
    }

    public int readInt() {
        return buffer.getInt();
    }

    public void writeInt(int i) {
        buffer.putInt(i);
    }

    public long readLong() {
        return buffer.getLong();
    }

    public void writeLong(long l) {
        buffer.putLong(l);
    }

    public float readFloat() {
        return buffer.getFloat();
    }

    public void writeFloat(float f) {
        buffer.putFloat(f);
    }

    public double readDouble() {
        return buffer.getDouble();
    }

    public void writeDouble(double d) {
        buffer.putDouble(d);
    }

    private int getMaxEncodedUtfLength(int maxLength) {
        return maxLength * 3;
    }

    private String decodeString(int startIndex, int length, Charset charset) {
        if (length == 0) {
            return "";
        }

        final byte[] array;
        final int offset;

        if (buffer.hasArray()) {
            array = buffer.array();
            offset = buffer.arrayOffset() + startIndex;
        } else {
            array = new byte[length];
            offset = 0;
            final int position = buffer.position();

            // Read the bytes
            buffer.position(startIndex);
            for(int i = 0; i < array.length; i++) {
                array[i] = buffer.get();
            }

            // Set buffer position back to where it was
            buffer.position(position);
        }
        if (StandardCharsets.US_ASCII.equals(charset)) {
            // Fast-path for US-ASCII which is used frequently.
            return new String(array, 0, offset, length);
        }
        return new String(array, offset, length, charset);
    }
}
