package br.com.legion.guilds.framework.echo.packets;

import br.com.legion.guilds.framework.echo.api.EchoBufferInput;
import br.com.legion.guilds.framework.echo.api.EchoBufferOutput;
import br.com.legion.guilds.framework.echo.api.EchoPacket;
import br.com.legion.guilds.framework.server.ServerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BroadcastMessagePacket extends EchoPacket {

    protected BaseComponent[] components;

    protected ServerType server;

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeString(ComponentSerializer.toString(this.components));
        buffer.writeEnum(server);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.components = ComponentSerializer.parse(buffer.readString());
        this.server = buffer.readEnum(ServerType.class);
    }
}
