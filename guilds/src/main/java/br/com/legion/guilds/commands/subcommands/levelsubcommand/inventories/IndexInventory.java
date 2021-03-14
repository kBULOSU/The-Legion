package br.com.legion.guilds.commands.subcommands.levelsubcommand.inventories;

import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.idea.api.spigot.misc.utils.HeadTexture;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsConstants;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.misc.utils.GuildUtils;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

public class IndexInventory extends CustomInventory {

    private static final String UP_HEAD_KEY = "58fe251a40e4167d35d081c27869ac151af96b6bd16dd2834d5dc7235f47791d";

    public IndexInventory(Guild guild, GuildUserRelation relation) {
        super(9, "Guilda - Level");

        ItemBuilder up = new ItemBuilder(HeadTexture.getTempHead(UP_HEAD_KEY))
                .name("&aSubir de level")
                .lore(
                        "&7Clique aqui para subir o level",
                        "&7da guilda."
                );

        setItem(4, up.make(), event -> {
            Player player = (Player) event.getWhoClicked();

            player.closeInventory();

            if (!relation.getRole().isSameOrHigher(GuildRole.CAPTAIN)) {
                Message.ERROR.send(player, "Você precisa ser capitão ou superior para fazer isto.");
                return;
            }

            if (guild.getLevel() == 3) {
                Message.ERROR.send(player, "A guilda já está em seu level máximo.");
                return;
            }

            double price = GuildsConstants.Config.LEVEL_UPGRADE_PRICE_II;
            if (guild.getLevel() == 2) {
                price = GuildsConstants.Config.LEVEL_UPGRADE_PRICE_III;
            }

            if (guild.getGloryPoints() < price) {
                Message.ERROR.send(player, "A guilda não possui pontos de glória suficiente.");
                return;
            }

            int level = guild.getLevel() + 1;

            guild.setGloryPoints(guild.getGloryPoints() - price);
            guild.upgradeLevel();

            GuildsProvider.Cache.Local.GUILDS.provide().invalidateId(guild.getId());
            GuildsProvider.Repositories.GUILDS.provide().updateBank(guild.getId(), guild.getGloryPoints());
            GuildsProvider.Repositories.GUILDS.provide().updateLevel(guild.getId(), level);

            Message.SUCCESS.send(player, "Yeah! O level da guilda foi aumentado e agora é " + level + "!");

            ComponentBuilder builder = new ComponentBuilder("")
                    .append(player.getName())
                    .append(" atualizou a guilda para o level ", ComponentBuilder.FormatRetention.NONE)
                    .append("" + level)
                    .color(ChatColor.GREEN)
                    .append(".", ComponentBuilder.FormatRetention.NONE)
                    .color(ChatColor.GREEN);

            GuildUtils.broadcast(
                    guild.getId(),
                    builder.create()
            );
        });
    }

}
