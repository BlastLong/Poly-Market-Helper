package com.blastlong.pmh.gui.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ImageButton extends Button {

    protected final int u;
    protected final int v;
    protected final ResourceLocation resource;

    public ImageButton(int x, int y, int u, int v, int width, int height, ResourceLocation resource, OnPress function) {
        super(x, y, width, height, Component.empty(), function, DEFAULT_NARRATION);
        this.u = u;
        this.v = v;
        this.resource = resource;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int $$1, int $$2, float $$3) {
        guiGraphics.blit(resource,
                this.getX(), this.getY(), this.u, this.v, this.getWidth(), this.getHeight());


        // guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        // RenderSystem.enableBlend();
        // RenderSystem.enableDepthTest();
        // guiGraphics.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
        // guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        // int $$5 = this.active ? 16777215 : 10526880;
        // this.renderString($$0, $$4.font, $$5 | Mth.ceil(this.alpha * 255.0F) << 24);
    }
}
