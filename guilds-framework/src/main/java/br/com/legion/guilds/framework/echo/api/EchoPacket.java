package br.com.legion.guilds.framework.echo.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public abstract class EchoPacket implements IByteSerializable {

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private EchoPacketHeader header;
}
