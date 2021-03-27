package br.com.legion.glory.points.inventories;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.misc.utils.NumberUtils;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.inventory.PaginateInventory;
import br.com.idea.api.spigot.misc.utils.HeadTexture;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.glory.points.GloryPointsAPI;
import br.com.legion.glory.points.GloryPointsProvider;
import br.com.legion.glory.points.transactions.Transaction;
import br.com.legion.glory.points.transactions.TransactionType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class IndexInventory extends CustomInventory {

    private static final String TOP_HEAD_KEY = "";

    public IndexInventory(User user) {
        super(3 * 9, "Pontos de Glória");

        ItemBuilder info = new ItemBuilder(HeadTexture.getPlayerHead(user.getName()))
                .name("&e" + user.getName())
                .lore("&7Quantia de G.P: &f" + NumberUtils.format(GloryPointsAPI.get(user.getName())));

        setItem(4, info.make());

        ItemBuilder ranking = new ItemBuilder(HeadTexture.getTempHead(TOP_HEAD_KEY))
                .name("&eRanking de G.P")
                .lore("&7Visualiza os jogadores com mais", "&7pontos de glória na rede.");

        setItem(12, ranking.make(), event -> {
            PaginateInventory.PaginateInventoryBuilder inventory = PaginateInventory.builder();

            int i = 1;

            Set<Map.Entry<String, Double>> entries = GloryPointsProvider.Cache.Local.GLORY_POINTS.provide().getRank(28).entrySet();
            for (Map.Entry<String, Double> entry : entries) {
                String key = entry.getKey();
                Double value = entry.getValue();

                inventory.item(getHeadIcon(key, i++, value), event0 -> {
                    //nada
                });
            }

            inventory.backInventory(() -> new IndexInventory(ApiProvider.Cache.Local.USERS.provide().get(user.getId())));

            event.getWhoClicked().openInventory(inventory.build("Ranking de G.P"));
        });

        ItemBuilder transactions = new ItemBuilder(Material.BOOK_AND_QUILL)
                .name("&eExtrato")
                .lore("&7Aqui você visualiza seu extrato", "&7contando com todas as transações realizadas.");

        setItem(14, transactions.make(), event -> {
            PaginateInventory.PaginateInventoryBuilder inventory = PaginateInventory.builder();

            List<Transaction> transactions0 = GloryPointsProvider.Repositories.TRANSACTIONS.provide().fetch(user.getId());
            for (Transaction transaction : transactions0) {
                inventory.item(getTransactionIcon(transaction), event0 -> {
                    //nada
                });
            }

            inventory.backInventory(() -> new IndexInventory(ApiProvider.Cache.Local.USERS.provide().get(user.getId())));

            event.getWhoClicked().openInventory(inventory.build("Transações"));
        });
    }

    private ItemStack getTransactionIcon(Transaction transaction) {
        User source = ApiProvider.Cache.Local.USERS.provide().get(transaction.getSourceId());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(transaction.getTransactedAt().getTime());

        ItemBuilder builder = new ItemBuilder(Material.PAPER)
                .name("&eTransação")
                .lore(
                        "&7Origem: &f" + (transaction.getSourceId() == 0 ? "Servidor" : source.getName()),
                        "&7Tipo: &f" + (transaction.getType() == TransactionType.SENT ? "Enviado" : "Recebida")
                );

        builder.lore("&7Efetuada em: &f" + calendar.get(Calendar.DATE) + " de "
                + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                + ". de " + calendar.get(Calendar.YEAR)
                + ", às " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d", calendar.get(Calendar.MINUTE)) + ".");

        return builder.make();
    }

    private ItemStack getHeadIcon(String name, int index, double amount) {
        return new ItemBuilder(HeadTexture.getPlayerHead(name))
                .name("&e" + name + " " + index + "°")
                .lore("&7Quantia de G.P: &f" + NumberUtils.format(amount))
                .make();
    }

}
