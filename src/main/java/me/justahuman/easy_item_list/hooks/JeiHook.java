package me.justahuman.easy_item_list.hooks;

import me.justahuman.easy_item_list.api.Hook;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.ingredients.subtypes.SubtypeInterpreters;
import mezz.jei.common.load.registration.SubtypeRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

@JeiPlugin
public class JeiHook extends Hook implements IModPlugin {
    private IIngredientHelper<ItemStack> helper;
    private IIngredientManager manager;

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        this.manager = jeiRuntime.getIngredientManager();
        this.helper = this.manager.getIngredientHelper(VanillaTypes.ITEM_STACK);
        load();
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration iregistration) {
        if (!(iregistration instanceof SubtypeRegistration registration)) {
            return;
        }

        SubtypeInterpreters interpreters = registration.getInterpreters();
        for (Map.Entry<RegistryKey<Item>, Item> itemEntry : Registry.ITEM.getEntrySet()) {
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
        for (ItemStack ingredient : this.manager.getAllIngredients(VanillaTypes.ITEM_STACK)) {
            if (helper.getUniqueId(ingredient, UidContext.Ingredient).equals(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addItemStack(ItemStack itemStack) {
        this.manager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, Set.of(itemStack));
    }

    @Override
    public @NotNull Identifier getPluginUid() {
        return new Identifier("easy_item_list", "jei_hook");
    }
}