package me.justahuman.easy_item_list.hooks;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import me.justahuman.easy_item_list.api.Hook;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;

public class ReiHook extends Hook implements REIClientPlugin {
    private static final LongSet STACK_HASHES = new LongOpenHashSet();
    private EntryRegistry registry;

    @Override
    public void registerEntries(EntryRegistry registry) {
        this.registry = registry;
        load();
    }

    @Override
    public void registerItemComparators(ItemComparatorRegistry registry) {
        registry.registerGlobal((context, stack) -> {
            final long exact = EntryStacks.hashExact(EntryStacks.of(stack));
            return STACK_HASHES.contains(exact) ? exact : 0;
        });
    }

    @Override
    public boolean alreadyAdded(ItemStack itemStack) {
        return this.registry.alreadyContain(EntryStacks.of(itemStack));
    }

    @Override
    public void addItemStack(ItemStack itemStack) {
        final EntryStack<?> stack = EntryStacks.of(itemStack);
        this.registry.addEntries(stack);
        STACK_HASHES.add(EntryStacks.hashExact(stack));
    }
}
