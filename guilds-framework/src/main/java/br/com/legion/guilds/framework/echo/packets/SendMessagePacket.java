package br.com.legion.guilds.framework.echo.packets;

import br.com.legion.guilds.framework.echo.api.EchoBufferInput;
import br.com.legion.guilds.framework.echo.api.EchoBufferOutput;
import br.com.legion.guilds.framework.echo.api.EchoPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessagePacket extends EchoPacket {

    protected int[] usersIds;
    protected BaseComponent[] components;

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeInt(usersIds == null ? 0 : usersIds.length);

        if (this.usersIds != null) {
            for (int i : usersIds) {
                buffer.writeInt(i);
            }
        }

        buffer.writeString(TextComponent.toLegacyText(this.components));
    }

    @Override
    public void read(EchoBufferInput buffer) {
        int size = buffer.readInt();

        if (size > 0) {
            this.usersIds = new int[size];

            for (int i = 0; i < size; i++) {
                this.usersIds[i] = buffer.readInt();
            }
        }

        this.components = TextComponent.fromLegacyText(buffer.readString());
    }
}
