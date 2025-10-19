package io.Yomicer.magicExpansion.utils.itemUtils;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;

public class sfItemUtils {

    public static ItemStack sfItemAmount(SlimefunItemStack slimefunItem, int amount) {

        return new SlimefunItemStack(slimefunItem,amount);
    }

}
