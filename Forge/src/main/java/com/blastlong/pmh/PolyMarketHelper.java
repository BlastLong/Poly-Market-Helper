package com.blastlong.pmh;

import com.blastlong.pmh.input.KeyBindings;
import net.minecraftforge.fml.common.Mod;


@Mod(PolyMarketHelper.MODID)
public class PolyMarketHelper {
	public static final String MODID = "pmh";

	private PolyMarketHelperClient client;
	private KeyBindings keyMapping;

	public PolyMarketHelper() {
		client = new PolyMarketHelperClient();
		client.init();

		keyMapping = new KeyBindings();
		keyMapping.register();
	}
}
