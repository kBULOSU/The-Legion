package br.com.legion.essentials.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.idea.api.spigot.misc.utils.InventoryUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Map;
import java.util.Set;

public class CompactCommand extends CustomCommand {

    private final Map<MaterialData, Result> map = Maps.newHashMap();

    public CompactCommand() {
        super("compactar", CommandRestriction.IN_GAME, "compact");

        map.put(new MaterialData(Material.IRON_INGOT, (byte) 0), new Result(new MaterialData(Material.IRON_BLOCK, (byte) 0), 9));
        map.put(new MaterialData(Material.GOLD_INGOT, (byte) 0), new Result(new MaterialData(Material.GOLD_BLOCK, (byte) 0), 9));
        map.put(new MaterialData(Material.GOLD_NUGGET, (byte) 0), new Result(new MaterialData(Material.GOLD_INGOT, (byte) 0), 9));
        map.put(new MaterialData(Material.REDSTONE, (byte) 0), new Result(new MaterialData(Material.REDSTONE_BLOCK, (byte) 0), 9));
        map.put(new MaterialData(Material.COAL, (byte) 0), new Result(new MaterialData(Material.COAL_BLOCK, (byte) 0), 9));
        map.put(new MaterialData(Material.DIAMOND, (byte) 0), new Result(new MaterialData(Material.DIAMOND_BLOCK, (byte) 0), 9));
        map.put(new MaterialData(Material.EMERALD, (byte) 0), new Result(new MaterialData(Material.EMERALD_BLOCK, (byte) 0), 9));
        map.put(new MaterialData(Material.INK_SACK, (byte) 4), new Result(new MaterialData(Material.LAPIS_BLOCK, (byte) 0), 9));
        map.put(new MaterialData(Material.SLIME_BALL, (byte) 0), new Result(new MaterialData(Material.SLIME_BLOCK, (byte) 0), 9));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Inventory inventory = InventoryUtils.copy(player.getInventory());

        Set<MaterialData> checked = Sets.newHashSet();

        int count = 0;

        for (ItemStack target : inventory.getContents()) {
            if (target == null || checked.contains(target.getData())) {
                continue;
            }

            checked.add(target.getData());

            Result targetResult = map.get(target.getData());
            if (targetResult == null) {
                continue;
            }

            int totalItems = 0;
            for (ItemStack item : inventory.getContents()) {
                if (item != null) {
                    if (item.getData().equals(target.getData())) {
                        int amount = item.getAmount();
                        totalItems += amount;
                    }
                }
            }

            if (totalItems < targetResult.getAmountNeeded()) {
                continue;
            }

            int leftOvers = Math.floorMod(totalItems, targetResult.getAmountNeeded());
            int used = totalItems - leftOvers;
            int resultProductAmount = used / targetResult.getAmountNeeded();

            int removed = 0;

            Inventory checkerInventory = InventoryUtils.copy(inventory);
            ItemStack[] checkerContents = checkerInventory.getContents();

            for (int i = 0; i < checkerContents.length; i++) {
                if (checkerContents[i] == null || !checkerContents[i].getData().equals(target.getData())) {
                    continue;
                }

                ItemStack content = checkerContents[i];

                if (content.getAmount() <= (used - removed)) {
                    checkerContents[i] = null;

                    removed += content.getAmount();

                    if (removed == used) {
                        break;
                    }

                    continue;
                }

                checkerContents[i].setAmount(content.getAmount() - (used - removed));
                break;
            }

            checkerInventory.setContents(checkerContents);

            ItemStack result = new ItemStack(
                    targetResult.getMaterialData().getItemType(),
                    resultProductAmount,
                    targetResult.getMaterialData().getData()
            );

            if (!InventoryUtils.fits(checkerInventory, result)) {
                continue;
            }

            checkerInventory.addItem(result);

            count += result.getAmount();
            inventory.setContents(checkerInventory.getContents());
        }

        if (count > 0) {
            Message.SUCCESS.send(player, "Seu inventário foi compactado.");
            player.getInventory().setContents(inventory.getContents());
        } else {
            Message.ERROR.send(player, "Nenhum item para compactar.");
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Result {

        private final MaterialData materialData;
        private final int amountNeeded;
    }
}
