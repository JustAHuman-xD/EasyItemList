package me.justahuman.easy_item_list.hooks;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.registry.EmiComparisonDefaults;
import dev.emi.emi.registry.EmiStackList;
import me.justahuman.easy_item_list.api.Hook;
import net.minecraft.item.ItemStack;

public class EmiHook extends Hook implements EmiPlugin {
    @Override
    public void register(EmiRegistry ignored) {
        load();
    }

    @Override
    public boolean alreadyAdded(ItemStack itemStack) {
        final EmiStack emiStack = EmiStack.of(itemStack);
        return EmiStackList.stacks.stream().anyMatch(stack -> Comparison.compareComponents().compare(emiStack, stack));
    }

    @Override
    public void addItemStack(ItemStack itemStack) {
        EmiStack emiStack = EmiStack.of(itemStack).setAmount(1);
        EmiStackList.stacks.add(emiStack);
        EmiComparisonDefaults.comparisons.put(emiStack, Comparison.compareComponents());
    }
}
