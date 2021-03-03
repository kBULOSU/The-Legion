package br.com.legion.guilds.echo.packets.relation;

import br.com.legion.guilds.framework.echo.api.EchoBufferInput;
import br.com.legion.guilds.framework.echo.api.EchoBufferOutput;
import br.com.legion.guilds.framework.echo.api.EchoPacket;
import br.com.legion.guilds.framework.echo.api.ServerPacket;
import br.com.legion.guilds.relation.guild.GuildRelation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ServerPacket
@NoArgsConstructor
@AllArgsConstructor
public class GuildRelationCreatedPacket extends EchoPacket {

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
