package com.blastlong.pmh.data;

import com.blastlong.pmh.PolyMarketHelperClient;
import com.blastlong.pmh.data.object.ItemObject;
import com.blastlong.pmh.data.object.LocationObject;
import com.blastlong.pmh.data.object.ShopObject;
import com.blastlong.pmh.gui.util.ItemNameTranslator;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DataManager {

    private static final String urlString = "http://dongle.dothome.co.kr/output.json";
    private static final String dataFileName = "output.json";
    private static final String itemMapDateFileName = "itemMap.date";
    private static final String itemMapFileName = "itemMap.json";
    private static final String baseDirectory = "PMH";

    private static String directory;
    private static String dataFilePath;
    private static String itemMapFilePath;

    private static String itemMapDate;

    public DataManager() {
        directory = System.getProperty("user.dir") + File.separator + baseDirectory;
        dataFilePath = directory + File.separator + dataFileName;
        itemMapFilePath = directory + File.separator + itemMapFileName;
    }

    public Map<String, Object>[] loadItemMap() {
        downloadData();
        Map<String, Object>[] itemMaps = readJsonToItemMap(dataFilePath);
            /*
            convertData();

            Map<String, Object> itemMap = readItemMap();
            print(itemMap.get("update_date").toString());
             */
        return itemMaps;
    }

    public List<List<ItemObject>> getItemObjectsList(Map<String, Object> itemMap) {
        List<List<ItemObject>> itemObjectsList = (List<List<ItemObject>>) itemMap.get("itemObjectsList");
        /*
        List<List<ItemObject>> itemObjectsList = new ArrayList<>();
        List<Object> objectList = (List<Object>) itemMap.get("itemObjectsList");
        for(Object object : objectList) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ItemObject>>(){}.getType();
            String jsonString = object.toString();

            itemObjectsList.add(gson.fromJson(jsonString, type));
        }
         */
        return itemObjectsList;
    }

    public String getDate(Map<String, Object> itemMap) {
        return itemMap.get("update_date").toString();
    }


    public static void downloadData() {
        String urlString = "http://dongle.dothome.co.kr/";
        String content = "output.json";

        URL url = null;
        try {
            url = new URL(urlString);

            // Combine the base URL and the relative path
            URL resourceUrl = new URL(url, content);
            PolyMarketHelperClient.LOGGER.info("Full URL: " + resourceUrl);

            // Download the resource
            downloadResource(resourceUrl, directory, dataFileName);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void convertData() throws IOException {
        Map<String, Object>[] itemMap = readJsonToItemMap(dataFilePath);
        String jsonString = mapToJson(itemMap[0]);
        saveJsonStringToFile(itemMapFilePath, jsonString);
    }

    public static Map<String, Object> readItemMap() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(itemMapFilePath), StandardCharsets.UTF_8));
        String line = reader.readLine();
        line = line.replace(" ", "V");
        PolyMarketHelperClient.LOGGER.info(line);
        JsonObject jsonObject = JsonParser.parseString(line).getAsJsonObject();
        reader.close();
        return jsonToMap(jsonObject.toString());
    }


    // List<ItemObject>
    public static Map<String, Object>[] readJsonToItemMap(String filePath) {
        Map<String, Object> itemMap = new HashMap<>();
        Map<String, Object> validItemMap = new HashMap<>();
        List<List<ItemObject>> itemObjectsList = new ArrayList<>();
        List<List<ItemObject>> validItemObjectsList = new ArrayList<>();

        try {
            FileReader reader = new FileReader(filePath);
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            String latest_file_update_date = jsonObject.getAsJsonObject("meta").get("latestfilemoddate_formatted").getAsString();
            // itemMapDate = latest_file_update_date;
            itemMap.put("update_date", latest_file_update_date);

            Map<String, List<ItemObject>> localMap = new HashMap<>();
            Map<String, List<ItemObject>> validLocalMap = new HashMap<>();
            for (JsonElement jsonElement : jsonObject.getAsJsonArray("shops")) {

                JsonObject shopObject = jsonElement.getAsJsonObject();
                String ownerName = shopObject.get("owner_name").getAsString();
                String shopName = shopObject.get("shop_name").getAsString();

                boolean isAdminShop = ownerName.equals("ADMIN");
                if (isAdminShop)
                    continue;

                boolean isAvailable = false;
                float shopX = -1;
                float shopY = -1;
                float shopZ = -1;
                if (shopObject.has("location")) {
                    isAvailable = true;
                    JsonObject locationObject = shopObject.getAsJsonObject("location");
                    shopX = locationObject.get("x").getAsFloat();
                    shopY = locationObject.get("y").getAsFloat();
                    shopZ = locationObject.get("z").getAsFloat();
                }
                LocationObject location = new LocationObject(isAvailable, shopX, shopY, shopZ);
                ShopObject shop = new ShopObject(shopName, ownerName, location);

                JsonObject offersObject = shopObject.getAsJsonObject("offers");
                Set<String> keySet = offersObject.keySet();
                for (String key : keySet) {
                    String originalName = key;

                    JsonObject itemObject = offersObject.getAsJsonObject(key);
                    JsonElement customNameElement = itemObject.get("own_name");
                    String customName = "none";
                    if (!customNameElement.isJsonNull())
                        customName = customNameElement.getAsString();

                    String itemName = customName.equals("none") ? originalName : customName;
                    itemName = ItemNameTranslator.translate(itemName);

                    int amount = itemObject.get("amount").getAsInt();
                    float price = itemObject.get("price").getAsInt();
                    float unitPrice = itemObject.get("unit_price").getAsFloat();
                    int stock = itemObject.get("stock").getAsInt();
                    int priceDiscount = itemObject.get("price_discount").getAsInt();


                    ItemObject item = new ItemObject(
                            itemName,
                            amount,
                            price,
                            unitPrice,
                            stock,
                            priceDiscount,
                            shop
                    );

                    if (!localMap.containsKey(itemName)) {
                        List<ItemObject> itemList = new ArrayList<>();
                        itemList.add(item);
                        localMap.put(itemName, itemList);
                    } else {
                        localMap.get(itemName).add(item);
                    }
                    if (stock > 0 && location.isAvailable) {
                        if (!validLocalMap.containsKey(itemName)) {
                            List<ItemObject> itemList = new ArrayList<>();
                            itemList.add(item);
                            validLocalMap.put(itemName, itemList);
                        } else {
                            validLocalMap.get(itemName).add(item);
                        }
                    }
                }
            }
            itemObjectsList.addAll(localMap.values());
            validItemObjectsList.addAll(validLocalMap.values());
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemMap.put("itemObjectsList", itemObjectsList);
        validItemMap.put("itemObjectsList", validItemObjectsList);
        // print(itemMap.toString());
        return new Map[]{itemMap, validItemMap};
    }

    public static String mapToJson(Map<String, Object> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static Map<String, Object> jsonToMap(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void saveJsonStringToFile(String fileName, String json) {
        try {
            FileWriter file = new FileWriter(fileName);
            file.write(json);
            file.flush();

            /* if file is too large
            int length = string.length();
            for (int i = 0; i < length; i += CHUNK_SIZE) {
                int end = Math.min(length, i + CHUNK_SIZE);
                file.write(string.substring(i, end));
            }
             */
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public static void print(String string) {
        PolyMarketHelperClient.LOGGER.info(string);
    }

    private static void downloadResource(URL url, String directory, String fileName) {
        try {
            InputStream in = url.openStream();
            File file = new File(directory);
            file.mkdirs();
            file = new File(directory + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            PolyMarketHelperClient.LOGGER.info("Downloaded resource to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<List<ItemObject>> getFilteredListByKeywords(List<List<ItemObject>> list, String[] keywords) {
        List<List<ItemObject>> filteredList = new ArrayList<>();
        for (List<ItemObject> itemObjectList : list) {
            boolean flag = true;
            for (String keyword : keywords) {
                if (!itemObjectList.get(0).itemName.contains(keyword)) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                filteredList.add(itemObjectList);
        }
        return filteredList;
    }
}
