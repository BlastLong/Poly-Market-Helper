package com.blastlong.pmh.gui.button;

import com.blastlong.pmh.gui.util.TextureRect;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class IndicateLocationButton extends ImageButton {

    private final TextureRect deactivtedTextureRect;
    private final TextureRect activtedTextureRect;
    private final TextureRect highlightedTextureRect;

    private final boolean isActivated;

    private String content;
    private static final int contentSize = 9;

    public IndicateLocationButton(
            int x, int y,
            TextureRect deactivtedTextureRect,
            TextureRect activtedTextureRect,
            TextureRect highlightedTextureRect,
            boolean isActivated,
            String content,
            int width, int height,
            ResourceLocation resource, OnPress function) {
        super(x, y, deactivtedTextureRect.u(), deactivtedTextureRect.v(), width, height, resource, function);

        this.deactivtedTextureRect = deactivtedTextureRect;
        this.activtedTextureRect = activtedTextureRect;
        this.highlightedTextureRect = highlightedTextureRect;
        this.isActivated = isActivated;
        this.content = content;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int $$1, int $$2, float $$3) {
        Minecraft mc = Minecraft.getInstance();

        TextureRect textureRect;
        boolean isHighlighted = false;
        if (isActivated) {
            if(this.isHovered()) {
                textureRect = highlightedTextureRect;
                isHighlighted = true;
            }
            else {
                textureRect = activtedTextureRect;
            }
        }
        else {
            textureRect = deactivtedTextureRect;
        }

        guiGraphics.blit(resource,
                this.getX() - (isHighlighted ? 1 : 0), this.getY() - (isHighlighted ? 1 : 0),
                textureRect.u(), textureRect.v(),
                this.getWidth() + (isHighlighted ? 2 : 0), this.getHeight() + (isHighlighted ? 2 : 0)
        );

        // int contentWidth = (int) (mc.font.width(Component.translatable(content)) * ((float) contentSize / 9f));
        int contentHeight = contentSize / 2;
        drawCenteredString(guiGraphics,
                content,
                this.getWidth() / 2,
                this.getHeight() / 2 - contentHeight,
                contentSize
        );
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
