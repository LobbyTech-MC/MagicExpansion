package io.Yomicer.magicExpansion.items.misc.magicAlter;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;

public interface RecipeProvider {
    void registerRecipes(Map<String, MagicAltarRecipe> recipes);
    void registerAltarPatterns(List<Material[][]> patternList);
}
