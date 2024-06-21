package com.blastlong.pmh.gui.button;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

public class PageNumberButton extends Button {

    public int size;
    private final float sizeRatio;
    public int number;
    public boolean selected;

    public PageNumberButton(int x, int y, int size, int number, boolean selected, OnPress function) {
        super(x, y, size, size, Component.empty(), function, DEFAULT_NARRATION);
        this.size = size;
        sizeRatio = this.size / (float) 9;
        this.number = number;
        this.selected = selected;
        if (selected)
            this.active = false;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int $$1, int $$2, float $$3) {
        Minecraft mc = Minecraft.getInstance();

        MutableComponent component = Component.literal("" + number);

        if (!this.selected)
            component = component.setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
        else
            component = component.setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY));

        if (!this.selected && this.isHovered()) {
            component = component.withStyle(Style.EMPTY.withUnderlined(true));
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 0);
        guiGraphics.pose().scale(sizeRatio, sizeRatio, sizeRatio);
        guiGraphics.drawString(mc.font, component.getVisualOrderText(), 0, 0, 0, false);
        guiGraphics.pose().popPose();
    }
}
