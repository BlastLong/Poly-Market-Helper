package com.blastlong.pmh;

import com.blastlong.pmh.input.KeyBindings;
import net.fabricmc.api.ModInitializer;

public class PolyMarketHelper implements ModInitializer {

    public static final String MODID = "pmh";

    private PolyMarketHelperClient client;
    private KeyBindings keyBindings;

    @Override
    public void onInitialize() {
        client = new PolyMarketHelperClient();
        keyBindings = new KeyBindings();

        client.init();
        keyBindings.register();
    }
}
