package com.blastlong.pmh.gui.button;

import com.blastlong.pmh.gui.util.Coordinate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class ToggleButton extends ImageButton {

    private static final Coordinate contentOffset = new Coordinate(1, 1);
    public static final int contentSize = 5;

    private final int off_u;
    private final int off_v;

    private final int on_u;
    private final int on_v;

    private final int textureWidth;
    private final int textureHeight;

    private final boolean isToggled;
    private final String content;

    public ToggleButton(int x, int y,
                        int off_u, int off_v,
                        int on_u, int on_v,
                        int textureWidth, int textureHeight,
                        int width, int height,
                        boolean isToggled,
                        String content,
                        ResourceLocation resource, OnPress function) {
        super(x, y, off_u, off_v, width, height, resource, function);

        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        this.off_u = off_u;
        this.off_v = off_v;

        this.on_u = on_u;
        this.on_v = on_v;

        this.isToggled = isToggled;
        this.content = content;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int $$1, int $$2, float $$3) {
        int u, v;
        if (isToggled) {
            u = on_u;
            v = on_v;
        }
        else {
            u = off_u;
            v = off_v;
        }

        guiGraphics.blit(resource,
                this.getX(), this.getY(), u, v, textureWidth, textureHeight);

        drawString(guiGraphics, content,
                textureWidth + contentOffset.x(),
                contentOffset.y(),
                contentSize);
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
}
