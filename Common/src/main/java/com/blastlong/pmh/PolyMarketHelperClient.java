package com.blastlong.pmh;

import com.blastlong.pmh.data.DataManager;
import com.blastlong.pmh.data.object.ItemObject;
import com.blastlong.pmh.gui.PolyMarketScreen;
import com.blastlong.pmh.visaul.LocationIndicator;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PolyMarketHelperClient {

    private static PolyMarketHelperClient instance;

    public static Logger LOGGER = LogUtils.getLogger();

    private final PolyMarketScreen polyMarketScreen;

    private final DataManager dataManager = new DataManager();
    private final LocationIndicator locationIndicator = new LocationIndicator();

    private Map<String, Object> itemMap;
    private Map<String, Object> validItemMap;
    private List<List<ItemObject>> itemObjectsList = new ArrayList<>();
    private List<List<ItemObject>> validItemObjectsList = new ArrayList<>();
    private String updateDate;

    public PolyMarketHelperClient() {
        instance = this;

        polyMarketScreen = new PolyMarketScreen();
        // PolyMarketHelperClient.LOGGER.info("Last update date: " + itemMap.get("update_date").toString());

        loadItemMap();
    }

    public static PolyMarketHelperClient getInstance() {
        return instance;
    }

    public void init() {

    }

    public void openScreen() {
        Minecraft.getInstance().setScreen(polyMarketScreen);
    }

    public void loadItemMap() {
        Map<String, Object>[] itemMaps = dataManager.loadItemMap();

        itemMap = itemMaps[0];
        validItemMap = itemMaps[1];

        itemObjectsList = dataManager.getItemObjectsList(itemMap);
        validItemObjectsList = dataManager.getItemObjectsList(validItemMap);
        updateDate = dataManager.getDate(itemMap);
    }

    public List<List<ItemObject>> getItemObjectsList() {
        return itemObjectsList;
    }
    public List<List<ItemObject>> getValidItemObjectsList() {
        return validItemObjectsList;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public LocationIndicator getLocationIndicator() {
        return locationIndicator;
    }
}
