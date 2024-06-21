package com.blastlong.pmh.gui.button;

import com.blastlong.pmh.gui.util.ArrowButtonFlag;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ArrowButton extends ImageButton {

    private final int deactivatedU;
    private final int deactivatedV;

    private final int activatedU;
    private final int activatedV;

    private final int highlightedU;
    private final int highlightedV;

    public final ArrowButtonFlag activateFlag;

    public ArrowButton(int x, int y,
                       int deactivatedU, int deactivatedV,
                       int activatedU, int activatedV,
                       int highlightedU, int highlightedV,
                       int width, int height, ResourceLocation resource, OnPress function,
                       ArrowButtonFlag activateFlag) {
        super(x, y, deactivatedU, deactivatedV, width, height, resource, function);

        this.deactivatedU = deactivatedU;
        this.deactivatedV = deactivatedV;

        this.activatedU = activatedU;
        this.activatedV = activatedV;

        this.highlightedU = highlightedU;
        this.highlightedV = highlightedV;

        this.activateFlag = activateFlag;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int $$1, int $$2, float $$3) {
        int u, v;
        if (this.activateFlag.flag) {
            if(this.isHovered()) {
                u = this.highlightedU;
                v = this.highlightedV;
            }
            else {
                u = this.activatedU;
                v = this.activatedV;
            }
        }
        else {
            u = this.deactivatedU;
            v = this.deactivatedV;
        }

        guiGraphics.blit(resource,
                this.getX(), this.getY(), u, v, this.getWidth(), this.getHeight());
    }
}
