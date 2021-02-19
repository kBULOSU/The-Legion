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
public class UserInvitedGuildPacket extends EchoPacket {

    private int guildId;

    private int targetUserId;
    private int senderUserId;

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeInt(this.guildId);
        buffer.writeInt(this.targetUserId);
        buffer.writeInt(this.senderUserId);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.guildId = buffer.readInt();
        this.targetUserId = buffer.readInt();
        this.senderUserId = buffer.readInt();
    }

}
