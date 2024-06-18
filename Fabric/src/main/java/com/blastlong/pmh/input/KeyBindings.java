package com.blastlong.pmh.input;

import com.blastlong.pmh.PolyMarketHelperClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class KeyBindings {
    public static KeyMapping openScreenKey = new KeyMapping(
            "key.pmh.open_screen",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_G,
            "key.pmh.category");

    public void register() {
        PolyMarketHelperClient client = PolyMarketHelperClient.getInstance();

        System.out.println("Registered!");
        register(openScreenKey, client::openScreen);
    }

    private void register(KeyMapping keyMapping, KeyBehavior behavior) {
        keyMapping = KeyBindingHelper.registerKeyBinding(keyMapping);

        KeyMapping finalKeyMapping = keyMapping;
        ClientTickEvents.END_CLIENT_TICK.register(m -> {
            while(finalKeyMapping.consumeClick()) {
                behavior.action();
            }
        });
    }

    interface KeyBehavior {
        void action();
    }
}
