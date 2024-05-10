package me.justahuman.easy_item_list.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.Set;

public abstract class Hook {
    public static final Set<DataComponentType<?>> COMPONENTS_TO_CHECK = Set.of(DataComponentTypes.ITEM_NAME, DataComponentTypes.CUSTOM_NAME, DataComponentTypes.LORE, DataComponentTypes.FOOD, DataComponentTypes.CUSTOM_MODEL_DATA);
    public static final RegistryWrapper.WrapperLookup LOOKUP = BuiltinRegistries.createWrapperLookup();

    public abstract boolean alreadyAdded(ItemStack itemStack);
    public abstract void addItemStack(ItemStack itemStack);

    public void load() {
        final World world = MinecraftClient.getInstance().world;
        if (world == null || world.getRecipeManager() == null) {
            return;
        }

        world.getRecipeManager().sortedValues().forEach(entry -> {
            if (entry.id().getNamespace().equals("minecraft") || !(entry.value() instanceof Recipe<?> recipe)) {
                return;
            }

            for (Ingredient ingredient : recipe.getIngredients()) {
                for (ItemStack itemStack : ingredient.getMatchingStacks()) {
                    handleItem(itemStack);
                }
            }

            handleItem(recipe.getResult(LOOKUP));
        });
    }

    public void handleItem(ItemStack itemStack) {
        if (alreadyAdded(itemStack)) {
            return;
        }

        if (itemStack.getComponents().contains(DataComponentTypes.CUSTOM_DATA)) {
            addItemStack(itemStack);
        } else {
            for (DataComponentType<?> componentType : COMPONENTS_TO_CHECK) {
                if (itemStack.getComponentChanges().get(componentType) != null) {
                    addItemStack(itemStack);
                    break;
                }
            }
        }
    }
}
