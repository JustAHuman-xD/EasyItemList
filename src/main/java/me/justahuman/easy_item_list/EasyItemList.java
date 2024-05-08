package me.justahuman.easy_item_list;

import dev.architectury.event.events.client.ClientRecipeUpdateEvent;
import me.justahuman.easy_item_list.hooks.ReiHook;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class EasyItemList implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientRecipeUpdateEvent.EVENT.register(manager -> {
            if (FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
                final ReiHook hook = ReiHook.getInstance();
                if (hook != null) {
                    hook.load();
                }
            }
        });
    }
}