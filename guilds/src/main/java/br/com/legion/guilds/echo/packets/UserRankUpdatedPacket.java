package br.com.legion.guilds.echo.packets;

import br.com.idea.api.shared.echo.api.EchoBufferInput;
import br.com.idea.api.shared.echo.api.EchoBufferOutput;
import br.com.idea.api.shared.echo.api.EchoPacket;
import br.com.idea.api.shared.echo.api.ServerPacket;
import br.com.legion.guilds.relation.user.GuildRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ServerPacket
public class UserRankUpdatedPacket extends EchoPacket {

    private int targetUserId;
    private int promoterUserId;

    private GuildRole oldRank;
    private GuildRole newRank;

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeInt(this.targetUserId);
        buffer.writeInt(this.promoterUserId);

        buffer.writeEnum(this.oldRank);
        buffer.writeEnum(this.newRank);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.targetUserId = buffer.readInt();
        this.promoterUserId = buffer.readInt();

        this.oldRank = buffer.readEnum(GuildRole.class);
        this.newRank = buffer.readEnum(GuildRole.class);
    }
}
