package me.justahuman.easy_item_list.hooks;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import me.justahuman.easy_item_list.api.Hook;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.comparison.ComparisonContext;
import me.shedaniel.rei.api.common.entry.comparison.EntryComparator;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;

public class ReiHook extends Hook implements REIClientPlugin {
    private static final LongSet STACK_HASHES = new LongOpenHashSet();
    private static ReiHook instance = null;
    private EntryRegistry registry = null;

    public ReiHook() {
        instance = this;
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void registerItemComparators(ItemComparatorRegistry registry) {
        registry.registerGlobal((context, stack) -> {
            final long hash = hash(stack);
            return STACK_HASHES.contains(hash) ? hash : 0;
        });
    }

    @Override
    public boolean alreadyAdded(ItemStack itemStack) {
        final long hash = hash(itemStack);
        return this.registry != null && ITEM_STACKS.stream().anyMatch(stack -> hash(stack) == hash);
    }

    @Override
    public void addItemStacks() {
        if (this.registry == null) {
            return;
        }
        this.registry.addEntries(ITEM_STACKS.stream().map(EntryStacks::of).toList());
        STACK_HASHES.addAll(ITEM_STACKS.stream().map(this::hash).toList());
    }

    private long hash(ItemStack stack) {
        return EntryComparator.itemComponents().hash(ComparisonContext.EXACT, stack);
    }

    public static ReiHook getInstance() {
        return instance;
    }
}
