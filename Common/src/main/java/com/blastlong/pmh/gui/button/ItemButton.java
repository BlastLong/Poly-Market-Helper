package com.blastlong.pmh.gui.button;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class ItemButton extends ImageButton {

    private final static int stringY = 5;
    private final static int itemNameX = 3;
    private final static int quantityX = 170;
    private final static int priceX = 207;

    private final static int maxItemNameLength = 140;

    private final int textureU;
    private final int textureV;

    private final int highlightU;
    private final int highlightV;

    private final String itemName;
    private final int quantity;
    private final float price;

    public ItemButton(int x, int y,
                      int textureU, int textureV,
                      int highlightU, int highlightV,
                      int width, int height,
                      String itemName, int quantity, float price,
                      ResourceLocation resource, OnPress function) {
        super(x, y, textureU, textureV, width, height, resource, function);

        this.textureU = textureU;
        this.textureV = textureV;

        this.highlightU = highlightU;
        this.highlightV = highlightV;

        itemName = setLimit(itemName, 7, maxItemNameLength);
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    private String setLimit(String content, int size, int limitLength) {
        Minecraft mc = Minecraft.getInstance();

        boolean isOutOfLimit = false;
        int componentLength = (int) ((mc.font.width(content) * ((float) size / 9f)));
        if (componentLength > limitLength)
            isOutOfLimit = true;

        while(componentLength > limitLength) {
            content = content.substring(0, content.length() - 1);
            componentLength = (int) ((mc.font.width(content + "...") * ((float) size / 9f)));
        }

        if (isOutOfLimit)
            content += "...";
        return content;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int $$1, int $$2, float $$3) {
        if (this.isHovered()) {
            guiGraphics.blit(resource,
                    this.getX() - 1, this.getY() - 1, highlightU, highlightV, this.getWidth() + 2, this.getHeight() + 2);
        }

        guiGraphics.blit(resource,
                this.getX(), this.getY(), textureU, textureV, this.getWidth(), this.getHeight());

        drawString(guiGraphics, itemName, itemNameX, stringY, 7);
        drawCenteredString(guiGraphics, "" + quantity, quantityX, stringY, 7);
        drawCenteredString(guiGraphics, String.format("%.1f", price), priceX, stringY, 7);
    }

    private void drawString(GuiGraphics guiGraphics, String content, int x, int y, int size) {
        Minecraft mc = Minecraft.getInstance();

        MutableComponent component = Component.translatable(content);
        component = component.setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));

        x += getX();
        y += getY();

        float sizeRatio = size / 9f;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.pose().scale(sizeRatio, sizeRatio, sizeRatio);
        guiGraphics.drawString(mc.font, component.getVisualOrderText(), 0, 0, 0, false);
        guiGraphics.pose().popPose();
    }

    private void drawCenteredString(GuiGraphics guiGraphics, String content, int x, int y, int size) {
        Minecraft mc = Minecraft.getInstance();

        x -= (int) ((mc.font.width(Component.translatable(content)) * ((float) size / 9f)) / 2);
        drawString(guiGraphics, content, x, y, size);
    }
}
