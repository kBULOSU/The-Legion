package br.com.legion.missions.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter

@AllArgsConstructor
public class Mission {

    private final int id;
    private final String displayName;
    private final ItemStack icon;




}
