package com.blastlong.pmh.gui.input;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.Objects;

public class SearchBox extends EditBox {

    public SearchBox(Font font, int x, int y, int width, int height, Component backgroundComponent) {
        super(font, x, y, width, height, backgroundComponent);
    }
    /*
    public void renderWidget(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        if (this.isVisible()) {
            int $$5;
            if (this.isBordered()) {
                $$5 = this.isFocused() ? -1 : -6250336;
                $$0.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, $$5);
                $$0.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
            }

            $$5 = this.isEditable ? this.textColor : this.textColorUneditable;
            int $$6 = this.cursorPos - this.displayPos;
            int $$7 = this.highlightPos - this.displayPos;
            String $$8 = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            boolean $$9 = $$6 >= 0 && $$6 <= $$8.length();
            boolean $$10 = this.isFocused() && this.frame / 6 % 2 == 0 && $$9;
            int $$11 = this.bordered ? this.getX() + 4 : this.getX();
            int $$12 = this.bordered ? this.getY() + (this.height - 8) / 2 : this.getY();
            int $$13 = $$11;
            if ($$7 > $$8.length()) {
                $$7 = $$8.length();
            }

            if (!$$8.isEmpty()) {
                String $$14 = $$9 ? $$8.substring(0, $$6) : $$8;
                $$13 = $$0.drawString(this.font, (FormattedCharSequence)this.formatter.apply($$14, this.displayPos), $$13, $$12, $$5);
            }

            boolean $$15 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
            int $$16 = $$13;
            if (!$$9) {
                $$16 = $$6 > 0 ? $$11 + this.width : $$11;
            } else if ($$15) {
                --$$16;
                --$$13;
            }

            if (!$$8.isEmpty() && $$9 && $$6 < $$8.length()) {
                $$0.drawString(this.font, (FormattedCharSequence)this.formatter.apply($$8.substring($$6), this.cursorPos), $$13, $$12, $$5);
            }

            if (this.hint != null && $$8.isEmpty() && !this.isFocused()) {
                $$0.drawString(this.font, this.hint, $$13, $$12, $$5);
            }

            if (!$$15 && this.suggestion != null) {
                $$0.drawString(this.font, this.suggestion, $$16 - 1, $$12, -8355712);
            }

            int var10003;
            int var10004;
            int var10005;
            if ($$10) {
                if ($$15) {
                    RenderType var10001 = RenderType.guiOverlay();
                    var10003 = $$12 - 1;
                    var10004 = $$16 + 1;
                    var10005 = $$12 + 1;
                    Objects.requireNonNull(this.font);
                    $$0.fill(var10001, $$16, var10003, var10004, var10005 + 9, -3092272);
                } else {
                    $$0.drawString(this.font, "_", $$16, $$12, $$5);
                }
            }

            if ($$7 != $$6) {
                int $$17 = $$11 + this.font.width($$8.substring(0, $$7));
                var10003 = $$12 - 1;
                var10004 = $$17 - 1;
                var10005 = $$12 + 1;
                Objects.requireNonNull(this.font);
                this.renderHighlight($$0, $$16, var10003, var10004, var10005 + 9);
            }

        }
    }
    */
}
