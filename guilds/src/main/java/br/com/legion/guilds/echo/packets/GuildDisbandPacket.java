package br.com.legion.guilds.echo.packets;

import br.com.legion.guilds.framework.echo.api.EchoBufferInput;
import br.com.legion.guilds.framework.echo.api.EchoBufferOutput;
import br.com.legion.guilds.framework.echo.api.EchoPacket;
import br.com.legion.guilds.framework.echo.api.ServerPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ServerPacket
public class GuildDisbandPacket extends EchoPacket {

    private int guildId;

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeInt(this.guildId);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.guildId = buffer.readInt();
    }
}
