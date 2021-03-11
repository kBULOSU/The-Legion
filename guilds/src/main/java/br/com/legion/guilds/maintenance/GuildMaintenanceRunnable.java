package br.com.legion.guilds.maintenance;

import br.com.idea.api.shared.misc.utils.NumberUtils;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsConstants;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.misc.utils.GuildUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

@RequiredArgsConstructor
public class GuildMaintenanceRunnable implements Runnable {

    @Getter
    private final Guild guild;

    @Override
    public void run() {
        if (!guild.needMaintenance()) {
            return;
        }

        int maintenanceTaxByLevel = GuildsConstants.Config.getMaintenanceTaxByLevel(guild.getLevel());

        if (guild.getGloryPoints() < maintenanceTaxByLevel) {
            //ESPERAR RESPONDER
            return;
        }

        guild.setGloryPoints(guild.getGloryPoints() - maintenanceTaxByLevel);
        GuildsProvider.Repositories.GUILDS.provide().updateBank(guild.getId(), guild.getGloryPoints());

        ComponentBuilder builder = new ComponentBuilder("\n")
                .color(ChatColor.GREEN)
                .append("A guilda acaba de pagar a taxa de manutenção.", ComponentBuilder.FormatRetention.NONE)
                .append("Valor pago foi de " + NumberUtils.format(maintenanceTaxByLevel) + " pontos de glória.")
                .append("\n");

        GuildUtils.broadcast(
                guild.getId(),
                builder.create()
        );
    }
}
