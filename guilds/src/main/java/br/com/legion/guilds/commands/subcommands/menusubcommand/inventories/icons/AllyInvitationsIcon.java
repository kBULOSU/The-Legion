package br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.icons;

import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.MenuIcon;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.function.Supplier;

public class AllyInvitationsIcon extends MenuIcon {

    public AllyInvitationsIcon(User user, Supplier<CustomInventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.CHAINMAIL_CHESTPLATE)
                .name("&aPedidos de Aliança")
                .lore(
                        "&7Veja os pedidos de aliança",
                        "&7que foram enviados para a",
                        "&7sua guilda!"
                );

        GuildUserRelation relation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user.getId());
        Set<Integer> invites = GuildsProvider.Cache.Redis.ALLY_INVITATIONS.provide().getInvitations(relation.getGuildId());

        if (invites.isEmpty()) {
            builder.lore("&fPedidos pendentes: &c0");
        } else {
            builder.lore("&fPedidos pendentes: &a" + invites.size());
        }

        builder.lore(
                "",
                "&7Dica: Use /guilda relacao",
                "&eClique para visualizar."
        );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            Bukkit.getPlayerExact(user.getName()).performCommand("guilda relacao");
        };
    }

}
