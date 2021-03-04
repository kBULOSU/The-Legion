package br.com.legion.guilds.commands.subcommands.banksubcommand.inventories;

import br.com.idea.api.shared.misc.utils.NumberUtils;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.idea.api.spigot.misc.utils.HeadTexture;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.listeners.PlayerChatListener;
import br.com.legion.guilds.misc.utils.GuildUtils;
import com.google.common.primitives.Doubles;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class IndexInventory extends CustomInventory {

    private static final String LIME_PLUS_HEAD_KEY = "http://textures.minecraft.net/texture/b056bc1244fcff99344f12aba42ac23fee6ef6e3351d27d273c1572531f";
    private static final String RED_MINUS_HEAD_KEY = "http://textures.minecraft.net/texture/4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46";

    public IndexInventory(Guild guild) {
        super(3 * 9, String.format("[%s] - Banco", guild.getTag()));

        ItemBuilder bankInfo = new ItemBuilder(Material.BOOK)
                .name("&eInformações")
                .lore("&7Quantia disponível: &f" + NumberUtils.toK(guild.getGloryPoints()))
                .lore("&7Limite disponível: &f" + NumberUtils.format(guild.getBankLimit()));

        setItem(4, bankInfo.make());

        ItemBuilder deposit = new ItemBuilder(HeadTexture.getTempHead(LIME_PLUS_HEAD_KEY))
                .name("&aDepositar")
                .lore("&7Clique para depositar uma quantia")
                .lore("&7de pontos de glória no banco da guilda.");

        setItem(12, deposit.make(), event -> {
            Player whoClicked = (Player) event.getWhoClicked();

            Message.EMPTY.send(whoClicked, "");
            Message.INFO.send(whoClicked, "Digite a quantia desejada para depósito no banco da guilda.");
            Message.INFO.send(whoClicked, "Caso queira cancelar esta ação, digite 'cancelar'.");
            Message.EMPTY.send(whoClicked, "");

            PlayerChatListener.on(whoClicked.getName(), chat -> {
                if (chat.getMessage().equalsIgnoreCase("cancelar")) {
                    return;
                }

                Double value = Doubles.tryParse(chat.getMessage().trim());
                if (value == null || value.isNaN() || value.isInfinite() || value < 1.0) {
                    Message.ERROR.send(whoClicked, "Quantia inválida.");
                    return;
                }

                if (guild.getGloryPoints() + value > guild.getBankLimit()) {
                    Message.ERROR.send(whoClicked, "Esta quantia ultrapassa o limite disponível do banco da guilda.");
                    return;
                }

                guild.setGloryPoints(guild.getGloryPoints() + value);

                GuildsProvider.Repositories.GUILDS.provide().updateBank(guild.getId(), guild.getGloryPoints());
                GuildsProvider.Cache.Local.GUILDS.provide().invalidateId(guild.getId());
                GuildsProvider.Cache.Local.GUILDS.provide().invalidateTag(guild.getTag());

                ComponentBuilder builder = new ComponentBuilder("")
                        .color(ChatColor.GREEN)
                        .append(whoClicked.getName())
                        .append(" depositou ", ComponentBuilder.FormatRetention.NONE)
                        .append(NumberUtils.toK(guild.getGloryPoints()))
                        .append(" pontos de glória no banco da guilda.");

                GuildUtils.broadcast(guild.getId(), builder.create());
            });

        });

        ItemBuilder withdraw = new ItemBuilder(HeadTexture.getTempHead(RED_MINUS_HEAD_KEY))
                .name("&cRetirar")
                .lore("&7Clique para retirar uma quantia de")
                .lore("&7pontos de glória do banco da guilda.");

        setItem(14, withdraw.make(), event -> {
            Player whoClicked = (Player) event.getWhoClicked();

            Message.EMPTY.send(whoClicked, "");
            Message.INFO.send(whoClicked, "Digite a quantia desejada para retirada do banco da guilda.");
            Message.INFO.send(whoClicked, "Caso queira cancelar esta ação, digite 'cancelar'.");
            Message.EMPTY.send(whoClicked, "");

            PlayerChatListener.on(whoClicked.getName(), chat -> {
                if (chat.getMessage().equalsIgnoreCase("cancelar")) {
                    return;
                }

                Double value = Doubles.tryParse(chat.getMessage().trim());
                if (value == null || value.isNaN() || value.isInfinite() || value < 1.0) {
                    Message.ERROR.send(whoClicked, "Quantia inválida.");
                    return;
                }

                if (guild.getGloryPoints() - value < 0.0) {
                    Message.ERROR.send(whoClicked, "O banco da guilda não possui esta quantia.");
                    return;
                }

                GuildsProvider.Repositories.GUILDS.provide().updateBank(guild.getId(), guild.getGloryPoints());
                GuildsProvider.Cache.Local.GUILDS.provide().invalidateId(guild.getId());
                GuildsProvider.Cache.Local.GUILDS.provide().invalidateTag(guild.getTag());

                ComponentBuilder builder = new ComponentBuilder("")
                        .color(ChatColor.GREEN)
                        .append(whoClicked.getName())
                        .append(" retirou ", ComponentBuilder.FormatRetention.NONE)
                        .append(NumberUtils.toK(guild.getGloryPoints()))
                        .append(" pontos de glória do banco da guilda.");

                GuildUtils.broadcast(guild.getId(), builder.create());
            });
        });
    }
}
