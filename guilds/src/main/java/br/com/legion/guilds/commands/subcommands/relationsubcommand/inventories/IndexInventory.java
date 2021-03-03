package br.com.legion.guilds.commands.subcommands.relationsubcommand.inventories;

import br.com.idea.api.shared.misc.utils.Plural;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.ConfirmInventory;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.inventory.PaginateInventory;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.misc.utils.GuildUtils;
import br.com.legion.guilds.relation.guild.GuildRelation;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Set;

public class IndexInventory extends CustomInventory {

    public IndexInventory(Guild guild, User user, boolean isLeader) {
        super(isLeader ? 45 : 27, String.format("Relações de %s", guild.getTag()));

        {
            ItemBuilder alliesIcon = new ItemBuilder(Material.BANNER)
                    .name("&aAliados")
                    .color(DyeColor.LIME)
                    .flags(ItemFlag.HIDE_POTION_EFFECTS);

            Set<Integer> allies = GuildsProvider.Cache.Local.GUILDS_RELATIONS
                    .provide().getRelationships(guild.getId(), GuildRelation.ALLY);

            if (allies.isEmpty()) {
                alliesIcon.lore(String.format(
                        "%s não possui aliados.",
                        guild.getDisplayName()
                ));
            } else {
                alliesIcon.lore(String.format(
                        "%s possui %s %s.",
                        guild.getDisplayName(),
                        allies.size(),
                        Plural.of(allies.size(), "aliado", "aliados")
                ), "", "&eClique para ver!");
            }

            this.setItem(13, alliesIcon.make(), event -> {
                if (!allies.isEmpty()) {

                    PaginateInventory.PaginateInventoryBuilder alliesInventory = PaginateInventory.builder();

                    allies.forEach(ally -> {
                        Guild allyGuild = GuildsProvider.Cache.Local.GUILDS.provide().getById(ally);

                        ItemBuilder allyIcon = GuildUtils.getBanner(allyGuild);

                        if (isLeader) {
                            allyIcon.lore("", "&eClique para desfazer a aliança!");
                        }

                        alliesInventory.item(allyIcon.make(), event0 -> {

                            if (isLeader) {
                                ConfirmInventory confirmInventory = ConfirmInventory.of(event1 -> {
                                    ((Player) event1.getWhoClicked()).performCommand("guilda aliada " + allyGuild.getTag() + " desfazer");
                                    event1.getWhoClicked().closeInventory();
                                }, event1 -> {
                                    event1.getWhoClicked().openInventory(alliesInventory.build("Aliados"));
                                }, GuildUtils.getBanner(allyGuild, user).make());

                                event.getWhoClicked().openInventory(confirmInventory.make("&cVocê irá desfazer a", "&caliança com a guilda", allyGuild
                                        .getDisplayName() + "&c."));
                            }
                        });
                    });

                    alliesInventory.backInventory(() -> new IndexInventory(guild, user, isLeader));

                    event.getWhoClicked().openInventory(alliesInventory.build("Aliados"));
                }
            });
        }

        if (isLeader) {
            Set<Integer> invitations = GuildsProvider.Cache.Redis.ALLY_INVITATIONS.provide().getInvitations(guild.getId());

            ItemBuilder invitationsIcon = new ItemBuilder(Material.BOOK)
                    .name("&ePedidos de aliança");

            int amount = invitations.size();

            if (invitations.isEmpty()) {
                invitationsIcon.lore("&7Nenhum pedido pendente.");
            } else {
                invitationsIcon.lore(
                        String.format("&f%s %s.", amount, Plural.of(amount, "pedido pendente", "pedidos pendentes")),
                        "",
                        "&eClique para ver!"
                );
            }

            this.setItem(31, invitationsIcon.make(), event -> {
                if (!invitations.isEmpty()) {
                    PaginateInventory.PaginateInventoryBuilder invitationsInventory = PaginateInventory.builder();

                    invitations.forEach(neutral -> {
                        Guild neutralGuild = GuildsProvider.Cache.Local.GUILDS.provide().getById(neutral);

                        invitationsInventory.item(GuildUtils.getBanner(neutralGuild, user).make(), event0 -> {
                            event0.getWhoClicked().openInventory(ConfirmInventory.of(event1 -> {
                                ((Player) event1.getWhoClicked()).performCommand("guilda aliada " + neutralGuild.getTag());
                                event1.getWhoClicked().closeInventory();
                            }, event1 -> {

                            }, null).make("&aAceitar convite."));
                        });
                    });

                    invitationsInventory.backInventory(() -> new IndexInventory(guild, user, isLeader));

                    event.getWhoClicked().openInventory(invitationsInventory.build("Convites de aliança"));
                }
            });
        }
    }
}
