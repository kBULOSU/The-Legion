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
public class UserLeftGuildPacket extends EchoPacket {

    private int guildId;
    private int userId;

    private Reason reason;

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeInt(this.guildId);
        buffer.writeInt(this.userId);

        buffer.writeEnum(this.reason);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.guildId = buffer.readInt();
        this.userId = buffer.readInt();

        this.reason = buffer.readEnum(Reason.class);
    }

    public enum Reason {
        LEAVE, KICK;
    }
}
