package br.com.legion.guilds.echo.packets;

import br.com.idea.api.shared.echo.api.EchoBufferInput;
import br.com.idea.api.shared.echo.api.EchoBufferOutput;
import br.com.idea.api.shared.echo.api.EchoPacket;
import br.com.idea.api.shared.echo.api.ServerPacket;
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
