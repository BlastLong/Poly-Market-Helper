package com.blastlong.pmh;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.Arrays;

public class PolyMarketHelperClient {

    private static PolyMarketHelperClient instance;

    public static Logger LOGGER = LogUtils.getLogger();

    public PolyMarketHelperClient() {
        instance = this;
    }

    public static PolyMarketHelperClient getInstance() {
        return instance;
    }

    public void init() {

    }
}
