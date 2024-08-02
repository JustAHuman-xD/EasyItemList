package me.justahuman.easy_item_list.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTransformRecipe.class)
public interface TransformRecipeAccessor {
    @Accessor Ingredient getTemplate();
    @Accessor Ingredient getBase();
    @Accessor Ingredient getAddition();
    @Accessor ItemStack getResult();
}
