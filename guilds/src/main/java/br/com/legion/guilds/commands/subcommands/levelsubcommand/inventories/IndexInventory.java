package br.com.legion.guilds.commands.subcommands.levelsubcommand.inventories;

import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.misc.utils.HeadTexture;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.relation.guild.GuildRelation;

public class IndexInventory extends CustomInventory {

    private static final String UP_HEAD_KEY = "58fe251a40e4167d35d081c27869ac151af96b6bd16dd2834d5dc7235f47791d";

    public IndexInventory(Guild guild, GuildRelation relation) {
        super(4 * 9, "Guilda - Level");

        ItemBuilder up = new ItemBuilder(HeadTexture.getTempHead(UP_HEAD_KEY))
                .name("&aSubir de level")
                .lore(
                        "&7Clique aqui para subir o level",
                        "&7da guilda."
                );


    }

}
