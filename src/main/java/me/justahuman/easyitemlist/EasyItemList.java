package me.justahuman.easyitemlist;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.registry.EmiRegistryImpl;
import dev.emi.emi.registry.EmiStackList;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;

import java.util.Set;

public class EasyItemList implements ClientModInitializer, EmiPlugin {
    private static final Set<DataComponentType<?>> COMPONENTS_TO_CHECK = Set.of(DataComponentTypes.ITEM_NAME, DataComponentTypes.CUSTOM_NAME, DataComponentTypes.LORE, DataComponentTypes.FOOD, DataComponentTypes.CUSTOM_MODEL_DATA);
    private static final RegistryWrapper.WrapperLookup LOOKUP = BuiltinRegistries.createWrapperLookup();

    @Override
    public void onInitializeClient() {}

    @Override
    public void register(EmiRegistry registry) {
        registry.getRecipeManager().sortedValues().forEach(entry -> {
            if (entry.id().getNamespace().equals("minecraft") || !(entry.value() instanceof Recipe<?> recipe)) {
                return;
            }

            for (Ingredient ingredient : recipe.getIngredients()) {
                for (ItemStack itemStack : ingredient.getMatchingStacks()) {
                    handleItem(registry, itemStack);
                }
            }

            handleItem(registry, recipe.getResult(LOOKUP));
        });
    }

    private void handleItem(EmiRegistry registry, ItemStack itemStack) {
        final EmiStack emiStack = EmiStack.of(itemStack);
        for (EmiStack existingStack : EmiStackList.stacks) {
            if (Comparison.compareComponents().compare(emiStack, existingStack)) {
                return;
            }
        }

        if (itemStack.getComponents().contains(DataComponentTypes.CUSTOM_DATA)) {
            registry.setDefaultComparison(emiStack, Comparison.compareComponents());
            registry.addEmiStack(emiStack);
        } else {
            for (DataComponentType<?> componentType : COMPONENTS_TO_CHECK) {
                if (itemStack.getComponentChanges().get(componentType) != null) {
                    registry.setDefaultComparison(emiStack, Comparison.compareComponents());
                    registry.addEmiStack(emiStack);
                    break;
                }
            }
        }
    }
}
