package br.com.legion.guilds.framework.echo.api;

import br.com.idea.api.shared.location.SerializedLocation;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public class EchoBufferOutput {

    private ByteArrayDataOutput buffer;

    public EchoBufferOutput() {
        this.buffer = ByteStreams.newDataOutput();
    }

    public void writeBoolean(boolean b) {
        buffer.writeBoolean(b);
    }

    public void writeByte(int i) {
        buffer.writeByte(i);
    }

    public void writeShort(int i) {
        buffer.writeShort(i);
    }

    public void writeChar(int i) {
        buffer.writeChar(i);
    }

    public void writeInt(int i) {
        buffer.writeInt(i);
    }

    public void writeLong(long l) {
        buffer.writeLong(l);
    }

    public void writeFloat(float v) {
        buffer.writeFloat(v);
    }

    public void writeDouble(double v) {
        buffer.writeDouble(v);
    }

    public void writeString(String s) {
        if (s != null) {
            buffer.writeBoolean(true);
            buffer.writeUTF(s);
        } else {
            buffer.writeBoolean(false);
        }
    }

    public byte[] toByteArray() {
        return buffer.toByteArray();
    }

    public <T extends Enum<T>> void writeEnum(T value) {
        writeString(Optional.ofNullable(value).map(Enum::name).orElse(null));
    }

    public void writeUUID(UUID uuid) {
        if (uuid == null) {
            buffer.writeLong(-1);
        } else {
            buffer.writeLong(uuid.getMostSignificantBits());
            buffer.writeLong(uuid.getLeastSignificantBits());
        }
    }

    public void writeAddress(InetSocketAddress address) {
        InetAddress addr = address.getAddress();
        String str = addr == null ? address.getHostName() : addr.toString().trim();
        int ix = str.indexOf('/');
        if (ix >= 0) {
            if (ix == 0) { // missing host name; use address
                str = addr instanceof Inet6Address
                        ? "[" + str.substring(1) + "]" // bracket IPv6 addresses with
                        : str.substring(1);

            } else { // otherwise use name
                str = str.substring(0, ix);
            }
        }

        writeString(str + ":" + address.getPort());
    }

    public void writeSerializedLocation(SerializedLocation location) {
        writeString(Optional.ofNullable(location).map(SerializedLocation::toString).orElse(null));
    }

    public void writeJsonObject(JsonObject json) {
        if (json != null) {
            writeString(json.toString());
        } else {
            writeString(null);
        }
    }
}
