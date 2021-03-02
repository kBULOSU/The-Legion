package br.com.legion.guilds.inventory;

import br.com.idea.api.spigot.inventory.PaginateInventory;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.misc.utils.GuildUtils;

public class GuildsListInventory extends PaginateInventory {

    public GuildsListInventory() {
        super("Lista de Guildas");

        GuildsProvider.Cache.Local.GUILDS.provide().fetchAll().forEach(guild -> {
            this.addItem(
                    GuildUtils.getBanner(guild)
                            .make()
            );
        });
    }
}
