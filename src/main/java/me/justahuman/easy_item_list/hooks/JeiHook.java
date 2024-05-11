package me.justahuman.easy_item_list.hooks;

import me.justahuman.easy_item_list.api.Hook;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IRuntimeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

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
    public boolean alreadyAdded(ItemStack itemStack) {
        final String id = helper.getUniqueId(itemStack, UidContext.Ingredient);
        return this.manager.getIngredientByUid(VanillaTypes.ITEM_STACK, id).isPresent();
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