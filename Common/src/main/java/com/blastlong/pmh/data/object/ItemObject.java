package com.blastlong.pmh.data.object;

public class ItemObject {
    public String itemName;
    public int amount;
    public float price;
    public float unitPrice;
    public int stock;
    public int priceDiscount;
    public ShopObject shop;

    // Constructor
    public ItemObject(String itemName, int amount, float price, float unitPrice, int stock, int priceDiscount, ShopObject shop) {
        this.itemName = itemName;
        this.amount = amount;
        this.price = price;
        this.unitPrice = unitPrice;
        this.stock = stock;
        this.priceDiscount = priceDiscount;
        this.shop = shop;
    }

    // toString method
    @Override
    public String toString() {
        return String.format("{%s, %d, %f, %f, %d, %d, %s}",
                itemName, amount, price, unitPrice, stock, priceDiscount, shop.toString());
    }
}
