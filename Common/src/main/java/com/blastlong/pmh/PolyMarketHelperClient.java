package com.blastlong.pmh;

import com.blastlong.pmh.gui.PolyMarketScreen;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

import java.util.Arrays;

public class PolyMarketHelperClient {

    private static PolyMarketHelperClient instance;

    public static Logger LOGGER = LogUtils.getLogger();

    private final PolyMarketScreen polyMarketScreen;

    public PolyMarketHelperClient() {
        instance = this;

        polyMarketScreen = new PolyMarketScreen();
    }

    public static PolyMarketHelperClient getInstance() {
        return instance;
    }

    public void init() {

    }

    public void openScreen() {

    }
}
