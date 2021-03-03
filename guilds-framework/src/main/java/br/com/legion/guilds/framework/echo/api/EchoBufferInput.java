package br.com.legion.guilds.framework.echo.api;

import br.com.idea.api.shared.location.SerializedLocation;
import com.google.common.base.Enums;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.InetSocketAddress;
import java.util.UUID;

public class EchoBufferInput {

    private final ByteArrayDataInput buffer;

    public EchoBufferInput(byte[] bytes) {
        this.buffer = ByteStreams.newDataInput(bytes);
    }

    public boolean readBoolean() {
        return buffer.readBoolean();
    }

    public byte readByte() {
        return buffer.readByte();
    }

    public int readUnsignedByte() {
        return buffer.readUnsignedByte();
    }

    public short readShort() {
        return buffer.readShort();
    }

    public int readUnsignedShort() {
        return buffer.readUnsignedShort();
    }

    public char readChar() {
        return buffer.readChar();
    }

    public int readInt() {
        return buffer.readInt();
    }

    public long readLong() {
        return buffer.readLong();
    }

    public float readFloat() {
        return buffer.readFloat();
    }

    public double readDouble() {
        return buffer.readDouble();
    }

    public String readString() {
        boolean valid = buffer.readBoolean();

        if (!valid) {
            return null;
        }

        return buffer.readUTF();
    }


    public <T extends Enum<T>> T readEnum(Class<T> clazz) {
        return readEnum(clazz, null);
    }

    public <T extends Enum<T>> T readEnum(Class<T> clazz, T deft) {
        String str = readString();

        if (str != null) {
            com.google.common.base.Optional<T> optional = Enums.getIfPresent(clazz, str);

            if (deft != null) {
                return optional.or(deft);
            }

            return optional.orNull();
        }

        return deft;
    }

    public UUID readUUID() {
        long msb = buffer.readLong();

        if (msb == -1) {
            return null;
        }

        return new UUID(msb, buffer.readLong());
    }

    public InetSocketAddress readAddress() {
        String value = readString();

        if (value == null) {
            return null;
        }

        if (value.startsWith("[")) {
            // bracketed IPv6 (with port number)

            int i = value.lastIndexOf(']');
            if (i == -1) {
                return null;
            }

            int j = value.indexOf(':', i);
            int port = j > -1 ? Integer.parseInt(value.substring(j + 1)) : 0;
            return new InetSocketAddress(value.substring(0, i + 1), port);
        } else {
            int i = value.indexOf(':');
            if (i != -1 && value.indexOf(':', i + 1) == -1) {
                // host:port
                int port = Integer.parseInt(value.substring(i + 1));
                return new InetSocketAddress(value.substring(0, i), port);
            } else {
                // host or unbracketed IPv6, without port number
                return new InetSocketAddress(value, 0);
            }
        }
    }

    public SerializedLocation readSerializedLocation() {
        String str = readString();

        if (str != null) {
            return SerializedLocation.of(str);
        }

        return null;
    }

    public JsonObject readJsonObject() {
        String str = readString();

        if (str == null) {
            return null;
        }

        return new JsonParser().parse(str).getAsJsonObject();
    }

}
