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
            final long hash = EntryComparator.itemNbt().hash(ComparisonContext.EXACT, stack);
            return STACK_HASHES.contains(hash) ? hash : 0;
        });
    }

    @Override
    public boolean alreadyAdded(ItemStack itemStack) {
        return this.registry != null && this.registry.alreadyContain(EntryStacks.of(itemStack));
    }

    @Override
    public void addItemStack(ItemStack itemStack) {
        if (this.registry == null) {
            return;
        }
        this.registry.addEntries(EntryStacks.of(itemStack));
        STACK_HASHES.add(EntryComparator.itemNbt().hash(ComparisonContext.EXACT, itemStack));
    }

    public static ReiHook getInstance() {
        return instance;
    }
}
