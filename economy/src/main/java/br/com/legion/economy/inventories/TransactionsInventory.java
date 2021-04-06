package br.com.legion.economy.inventories;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.PaginateInventory;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.economy.EconomyProvider;
import br.com.legion.economy.transactions.Transaction;
import br.com.legion.economy.transactions.TransactionType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionsInventory extends PaginateInventory {

    public TransactionsInventory(User user) {
        super("Histórico", 6 * 9);

        List<Transaction> transactions0 = EconomyProvider.Cache.Local.TRANSACTIONS.provide().get(user.getId());
        for (Transaction transaction : transactions0) {
            addItem(getTransactionIcon(transaction));
        }

        backItem(new IndexInventory(user));
    }

    private ItemStack getTransactionIcon(Transaction transaction) {
        User source = ApiProvider.Cache.Local.USERS.provide().get(transaction.getSourceId());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(transaction.getTransactedAt().getTime());

        ItemBuilder builder = new ItemBuilder(Material.PAPER)
                .name("&eTransação")
                .lore(
                        "&7Origem: &f" + (transaction.getSourceId() == null ? "Servidor" : source.getName()),
                        "&7Tipo: &f" + (transaction.getType() == TransactionType.SENT ? "Enviado" : "Recebida")
                );

        builder.lore("&7Efetuada em: &f" + calendar.get(Calendar.DATE) + " de "
                + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                + ". de " + calendar.get(Calendar.YEAR)
                + ", às " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d", calendar.get(Calendar.MINUTE)) + ".");

        return builder.make();
    }
}
