package me.justahuman.easy_item_list;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.client.ClientRecipeUpdateEvent;
import me.justahuman.easy_item_list.hooks.ReiHook;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

public class EasyItemList implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

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