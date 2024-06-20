package me.justahuman.easy_item_list.hooks;

import me.justahuman.easy_item_list.api.Hook;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IRuntimeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.ingredients.subtypes.SubtypeInterpreters;
import mezz.jei.library.load.registration.SubtypeRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

@JeiPlugin
public class JeiHook extends Hook implements IModPlugin {
    private IIngredientHelper<ItemStack> helper;
    private IIngredientManager manager;

    @Override
    public void registerRuntime(IRuntimeRegistration registration) {
        this.manager = registration.getIngredientManager();
        this.helper = this.manager.getIngredientHelper(VanillaTypes.ITEM_STACK);
        load();
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration iregistration) {
        if (!(iregistration instanceof SubtypeRegistration registration)) {
            return;
        }

        SubtypeInterpreters interpreters = registration.getInterpreters();
        for (Map.Entry<RegistryKey<Item>, Item> itemEntry : Registries.ITEM.getEntrySet()) {
            Item item = itemEntry.getValue();
            interpreters.get(VanillaTypes.ITEM_STACK, item.getDefaultStack()).ifPresentOrElse(
                    interpreter -> {
                        interpreters.addInterpreter(VanillaTypes.ITEM_STACK, item, (stack, context) -> {
                            return isCustom(stack) ? String.valueOf(stack.getOrCreateNbt().hashCode()) : interpreter.apply(stack, context);
                        });
                    },
                    () -> {
                        interpreters.addInterpreter(VanillaTypes.ITEM_STACK, item, (stack, context) -> {
                            return isCustom(stack) ? String.valueOf(stack.getOrCreateNbt().hashCode()) : IIngredientSubtypeInterpreter.NONE;
                        });
                    }
            );
        }
    }

    @Override
    public boolean alreadyAdded(ItemStack itemStack) {
        final String id = helper.getUniqueId(itemStack, UidContext.Ingredient);
        return ITEM_STACKS.stream().anyMatch(stack -> helper.getUniqueId(stack, UidContext.Ingredient).equals(id));
    }

    @Override
    public void addItemStacks() {
        this.manager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, ITEM_STACKS);
    }

    @Override
    public @NotNull Identifier getPluginUid() {
        return new Identifier("easy_item_list", "jei_hook");
    }
}
