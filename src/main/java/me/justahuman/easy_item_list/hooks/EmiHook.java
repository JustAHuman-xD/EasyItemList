package me.justahuman.easy_item_list.hooks;

import dev.emi.emi.EmiComparisonDefaults;
import dev.emi.emi.EmiStackList;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import me.justahuman.easy_item_list.api.Hook;
import net.minecraft.item.ItemStack;

public class EmiHook extends Hook implements EmiPlugin {
    private static final Comparison NBT_COMPARISON = Comparison.builder().nbt(true).build();

    @Override
    public void register(EmiRegistry ignored) {
        load();
    }

    @Override
    public boolean alreadyAdded(ItemStack itemStack) {
        final EmiStack emiStack = EmiStack.of(itemStack).comparison(i -> NBT_COMPARISON);
        return EmiStackList.stacks.stream().anyMatch(emiStack::equals);
    }

    @Override
    public void addItemStack(ItemStack itemStack) {
        EmiStack emiStack = EmiStack.of(itemStack);
        EmiStackList.stacks.add(emiStack);
        EmiComparisonDefaults.comparisons.put(emiStack.getKey(), NBT_COMPARISON);
    }
}