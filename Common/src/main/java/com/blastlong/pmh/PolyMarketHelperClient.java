package com.blastlong.pmh;

import com.blastlong.pmh.gui.ItemDetailScreen;
import com.blastlong.pmh.gui.PolyMarketScreen;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

import java.util.Arrays;

public class PolyMarketHelperClient {

    private static PolyMarketHelperClient instance;

    public static Logger LOGGER = LogUtils.getLogger();

    private final PolyMarketScreen polyMarketScreen;
    private final ItemDetailScreen itemDetailScreen;

    public PolyMarketHelperClient() {
        instance = this;

        polyMarketScreen = new PolyMarketScreen();
        itemDetailScreen = new ItemDetailScreen();
    }

    public static PolyMarketHelperClient getInstance() {
        return instance;
    }

    public void init() {

    }

    public void openScreen() {
        Minecraft.getInstance().setScreen(polyMarketScreen);
    }
}
