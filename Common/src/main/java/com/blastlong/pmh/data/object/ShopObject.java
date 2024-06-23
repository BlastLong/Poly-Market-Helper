package com.blastlong.pmh.data.object;

public class ShopObject {
    public String shopName;
    public String ownerName;
    public LocationObject location;

    // Constructor
    public ShopObject(String shopName, String ownerName, LocationObject location) {
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.location = location;
    }

    // toString method
    @Override
    public String toString() {
        return String.format("{%s, %s, %s}", shopName, ownerName, location.toString());
    }
}

