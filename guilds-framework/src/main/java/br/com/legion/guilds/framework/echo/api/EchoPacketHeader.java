package br.com.legion.guilds.framework.echo.api;

import lombok.*;

import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class EchoPacketHeader implements IByteSerializable {

    private UUID uniqueId;

    private String senderAppId;

    private UUID responseId;

    protected EchoPacketHeader(String senderAppId, UUID uniqueId) {
        this.senderAppId = senderAppId;
        this.uniqueId = uniqueId;
    }

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeString(this.senderAppId);
        buffer.writeUUID(this.uniqueId);
        buffer.writeUUID(this.responseId);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.senderAppId = buffer.readString();
        this.uniqueId = buffer.readUUID();
        this.responseId = buffer.readUUID();
    }
}
