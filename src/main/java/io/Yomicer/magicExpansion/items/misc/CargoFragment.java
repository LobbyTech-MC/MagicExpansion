package io.Yomicer.magicExpansion.items.misc;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

public class CargoFragment extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable {

    public CargoFragment(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);



    }

    @Override
    public @NotNull ItemUseHandler getItemHandler() {
        return e->{
            e.setUseItem(Event.Result.DENY);
            e.setUseBlock(Event.Result.DENY);
        };
    }


}

