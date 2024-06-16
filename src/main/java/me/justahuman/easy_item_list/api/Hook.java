package me.justahuman.easy_item_list.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.HashSet;
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
                    handleItem(itemStack.copyWithCount(1));
                }
            }

            handleItem(recipe.getResult(LOOKUP).copyWithCount(1));
        });
    }

    public void handleItem(ItemStack itemStack) {
        if (alreadyAdded(itemStack)) {
            return;
        }

        if (isCustom(itemStack)) {
            addItemStack(itemStack);
        }
    }

    public boolean isCustom(ItemStack itemStack) {
        final ComponentMap components = itemStack.getComponents();
        if (components.contains(DataComponentTypes.CUSTOM_DATA)) {
            final NbtComponent customData = components.get(DataComponentTypes.CUSTOM_DATA);
            final NbtCompound nbt = customData.copyNbt();
            for (String key : new HashSet<>(nbt.getKeys())) {
                if (key.contains("VVIProtocol")) {
                    nbt.remove(key);
                }
            }

            if (nbt.contains("display", NbtElement.COMPOUND_TYPE)) {
                final NbtCompound display = nbt.getCompound("display");
                if (display.contains("Name", NbtElement.STRING_TYPE)) {
                    final String rawName = display.getString("Name");
                    final Text name = Text.Serialization.fromJson(rawName, LOOKUP);
                    if (name == null) {
                        display.remove("Name");
                    } else {
                        if (name.getString().equals(itemStack.getName().getString())) {
                            display.remove("Name");
                        }
                    }
                }

                if (display.isEmpty()) {
                    nbt.remove("display");
                }
            }

            if (!nbt.isEmpty()) {
                return true;
            }
        }

        for (DataComponentType<?> componentType : COMPONENTS_TO_CHECK) {
            if (itemStack.getComponentChanges().get(componentType) != null) {
                return true;
            }
        }
        return false;
    }
}
