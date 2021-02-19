package br.com.legion.guilds.echo.packets.relation;

import br.com.idea.api.shared.echo.api.EchoBufferInput;
import br.com.idea.api.shared.echo.api.EchoBufferOutput;
import br.com.idea.api.shared.echo.api.EchoPacket;
import br.com.idea.api.shared.echo.api.ServerPacket;
import br.com.legion.guilds.relation.guild.GuildRelation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ServerPacket
@NoArgsConstructor
@AllArgsConstructor
public class GuildRelationInvitePacket extends EchoPacket {

    @Getter
    private int senderGuildId;

    @Getter
    private int targetGuildId;

    @Getter
    private GuildRelation relation;

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeInt(this.senderGuildId);
        buffer.writeInt(this.targetGuildId);
        buffer.writeEnum(relation);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.senderGuildId = buffer.readInt();
        this.targetGuildId = buffer.readInt();
        this.relation = buffer.readEnum(GuildRelation.class);
    }
}
