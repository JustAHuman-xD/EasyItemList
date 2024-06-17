package me.justahuman.easy_item_list.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.world.World;

import java.util.Objects;

public abstract class Hook {
    public abstract boolean alreadyAdded(ItemStack itemStack);
    public abstract void addItemStack(ItemStack itemStack);

    public void load() {
        final World world = MinecraftClient.getInstance().world;
        if (world == null || world.getRecipeManager() == null) {
            return;
        }

        world.getRecipeManager().values().forEach(recipe -> {
            if (recipe.getId().getNamespace().equals("minecraft")) {
                return;
            }

            for (Ingredient ingredient : recipe.getIngredients()) {
                for (ItemStack itemStack : ingredient.getMatchingStacks()) {
                    handleItem(itemStack);
                }
            }

            handleItem(recipe.getOutput());
        });
    }

    public void handleItem(ItemStack itemStack) {
        if (itemStack == null) {
            return;
        }

        itemStack = itemStack.copy();
        itemStack.setCount(1);

        if (alreadyAdded(itemStack)) {
            return;
        }

        if (isCustom(itemStack)) {
            addItemStack(itemStack);
        }
    }

    public boolean isCustom(ItemStack itemStack) {
        NbtCompound nbt = removeUseless(itemStack);
        NbtCompound defaultNbt = removeUseless(itemStack.getItem().getDefaultStack());
        return !Objects.equals(nbt, defaultNbt);
    }

    private static NbtCompound removeUseless(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        if (nbt == null || nbt.isEmpty()) {
            return new NbtCompound();
        }

        nbt = nbt.copy();
        nbt.remove("Damage");
        nbt.remove("Enchantments");
        nbt.remove("Patterns");
        nbt.remove("Trim");
        nbt.remove("StoredEnchantments");
        nbt.remove("EntityTag");
        nbt.remove("Fireworks");
        nbt.remove("pages");
        nbt.remove("author");
        nbt.remove("generation");
        nbt.remove("title");

        NbtCompound display = nbt.contains("display", 10) ? nbt.getCompound("display") : null;
        if (display != null) {
            display.remove("color");
        }
        return nbt;
    }
}