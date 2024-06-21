package com.blastlong.pmh.gui.button;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class ItemDetailButton extends ImageButton {

    private final static int stringY = 5;
    private final static int itemNameX = 3;
    private final static int quantityX = 125;
    private final static int priceX = 162;
    private final static int unitPriceX = 206;

    private final int textureU;
    private final int textureV;

    private final int highlightU;
    private final int highlightV;

    private final String itemName;
    private final int quantity;
    private final int price;
    private final float unitPrice;

    public ItemDetailButton(int x, int y,
                            int textureU, int textureV,
                            int highlightU, int highlightV,
                            int width, int height,
                            String itemName, int quantity, int price, float unitPrice,
                            ResourceLocation resource, OnPress function) {
        super(x, y, textureU, textureV, width, height, resource, function);

        this.textureU = textureU;
        this.textureV = textureV;

        this.highlightU = highlightU;
        this.highlightV = highlightV;

        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.unitPrice = unitPrice;
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
        drawCenteredString(guiGraphics, "" + price, priceX, stringY, 7);
        drawCenteredString(guiGraphics, String.format("%.1f", unitPrice), unitPriceX, stringY, 7);
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
