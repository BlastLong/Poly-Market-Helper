package com.blastlong.pmh.input;

import com.blastlong.pmh.PolyMarketHelperClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class KeyBindings {

    public static KeyMapping openScreenKey = new KeyMapping(
            "key.pmh.open_screen",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_G,
            "key.pmh.category");

    public static KeyMapping stopIndicatingKey = new KeyMapping(
            "key.pmh.stop_indicating",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_H,
            "key.pmh.category");

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(KeyBindings::registerKeyBindings);
    }

    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        PolyMarketHelperClient.LOGGER.info("Key Registered!");
        event.register(openScreenKey);
        event.register(stopIndicatingKey);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        PolyMarketHelperClient client = PolyMarketHelperClient.getInstance();

        if(event.phase == TickEvent.Phase.END) {
            while(openScreenKey.consumeClick()) {
                client.openScreen();
            }

            while(stopIndicatingKey.consumeClick()) {
                client.getLocationIndicator().stopIndicating();
            }
        }
    }
}
