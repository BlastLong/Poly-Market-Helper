package com.blastlong.pmh.gui;

import com.blastlong.pmh.gui.button.ArrowButton;
import com.blastlong.pmh.gui.button.ImageButton;
import com.blastlong.pmh.gui.button.ItemButton;
import com.blastlong.pmh.gui.button.PageNumberButton;
import com.blastlong.pmh.gui.util.ArrowButtonFlag;
import com.blastlong.pmh.gui.util.Coordinate;
import com.blastlong.pmh.gui.util.Rect;
import com.blastlong.pmh.gui.util.TextureRect;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PolyMarketScreen extends Screen {

    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("pmh", "textures/gui/poly_market_background.png");

    private static final Rect searchButtonRect = new Rect(224, 5, 9, 8);
    private static final TextureRect searchButtonTextureRect = new TextureRect(39, 172);
    private static final Coordinate searchButtonRenderOffset = new Coordinate(3, 6);

    private static final Rect leftArrowButtonRect = new Rect(50, 146, 13, 6);
    private static final TextureRect leftArrowButtonDeactivatedTextureRect = new TextureRect(0, 172);
    private static final TextureRect leftArrowButtonActivatedTextureRect = new TextureRect(13, 172);
    private static final TextureRect leftArrowButtonHighlightedTextureRect = new TextureRect(26, 172);

    private static final Rect rightArrowButtonRect = new Rect(176, 146, 13, 6);
    private static final TextureRect rightArrowButtonDeactivatedTextureRect = new TextureRect(0, 178);
    private static final TextureRect rightArrowButtonActivatedTextureRect = new TextureRect(13, 178);
    private static final TextureRect rightArrowButtonHighlightedTextureRect = new TextureRect(26, 178);

    private static final Rect labelBarRect = new Rect(5, 19, 230, 10);
    private static final TextureRect labelBarTextureRect = new TextureRect(0, 211);

    private static final Coordinate nameLabelCoordinate = new Coordinate(8, 21);
    private static final Coordinate quantityLabelCoordinate = new Coordinate(175, 21);
    private static final Coordinate priceLabelCoordinate = new Coordinate(212, 21);

    private static final Rect itemButtonRect = new Rect(5, 33, 230, 15);
    private static final TextureRect itemButtonTextureRect = new TextureRect(0, 157);
    private static final TextureRect itemButtonHighlightTextureRect = new TextureRect(0, 184);

    private static final Rect searchBoxRect = new Rect(5, 4, 230, 10);
    private static final TextureRect searchBoxTextureRect = new TextureRect(0, 201);

    private static final int labelSize = 7;

    private static final int pageNumberButtonOffset = 1;
    private static final int pageNumberSize = 5;
    private static final int pageNumberInterval = 6;

    private static final int itemButtonInterval = 3;

    private Minecraft mc;

    private final int width, height;

    private EditBox searchBox;

    private final ArrowButtonFlag leftButtonActivated = new ArrowButtonFlag();
    private final ArrowButtonFlag rightButtonActivated = new ArrowButtonFlag();

    private int maxPageNumber = 7;
    private int pageNumber = 1;
    private int pageNumberForm = -1;

    private int leftDotsX;
    private int rightDotsX;

    private final List<PageNumberButton> pageNumberButtonList = new ArrayList<>();
    private final List<ItemButton> itemButtonList = new ArrayList<>();


    public PolyMarketScreen() {
        super(Component.literal("Poly Market Screen"));
        mc = Minecraft.getInstance();

        width = 239;
        height = 157;
    }

    protected void init() {
        super.init();

        pageNumberButtonList.clear();
        itemButtonList.clear();

        addSearchButton();
        addArrowButtons();
        addPageNumberButtons();
        addItemButtons();

        searchBox = new EditBox(this.font,
                getRegularX() + searchBoxRect.x(),
                getRegularY() + searchBoxRect.y(),
                searchBoxRect.width(), searchBoxRect.height(),
                Component.translatable("pmh.search_box"));
//        searchBox.setResponder((string) -> {
//            this.renameButton.active = !$$0x.trim().isEmpty();
//        });
        searchBox.setBordered(false);
        this.addWidget(searchBox);

        updateArrowButtonsState();
    }

    private void addSearchButton() {
        this.addRenderableWidget(
                new ImageButton(
                        getRegularX() + searchButtonRect.x(),
                        getRegularY() + searchButtonRect.y(),
                        searchButtonTextureRect.u(), searchButtonTextureRect.v(),
                        searchButtonRect.width(), searchButtonRect.height(),
                        TEXTURE_LOCATION,
                        button -> {
                            onSearchButtonPressed();
                        })
        );
    }

    private void addArrowButtons() {
        this.addRenderableWidget(
                new ArrowButton(
                        getRegularX() + leftArrowButtonRect.x(),
                        getRegularY() + leftArrowButtonRect.y(),
                        leftArrowButtonDeactivatedTextureRect.u(), leftArrowButtonDeactivatedTextureRect.v(),
                        leftArrowButtonActivatedTextureRect.u(), leftArrowButtonActivatedTextureRect.v(),
                        leftArrowButtonHighlightedTextureRect.u(), leftArrowButtonHighlightedTextureRect.v(),
                        leftArrowButtonRect.width(), leftArrowButtonRect.height(),
                        TEXTURE_LOCATION,
                        button -> {
                            onLeftArrowButtonPressed();
                        },
                        leftButtonActivated)
        );

        this.addRenderableWidget(
                new ArrowButton(
                        getRegularX() + rightArrowButtonRect.x(),
                        getRegularY() + rightArrowButtonRect.y(),
                        rightArrowButtonDeactivatedTextureRect.u(), rightArrowButtonDeactivatedTextureRect.v(),
                        rightArrowButtonActivatedTextureRect.u(), rightArrowButtonActivatedTextureRect.v(),
                        rightArrowButtonHighlightedTextureRect.u(), rightArrowButtonHighlightedTextureRect.v(),
                        rightArrowButtonRect.width(), rightArrowButtonRect.height(),
                        TEXTURE_LOCATION,
                        button -> {
                            onRightArrowButtonPressed();
                        },
                        rightButtonActivated)
        );
    }

    private void addPageNumberButtons() {
        if (maxPageNumber < 10) {
            pageNumberForm = -1;

            float numberWidth = stringLength(0);
            float startX;
            if (maxPageNumber % 2 == 0)
                startX = (float) width / 2 - (int) (maxPageNumber / 2) * (numberWidth + pageNumberInterval) + (float) pageNumberInterval / 2;
            else
                startX = (float) width / 2 - (int) (maxPageNumber / 2) * (numberWidth + pageNumberInterval) - numberWidth / 2;

            for (int i = 0; i < maxPageNumber; i++) {
                int number = i + 1;
                addPageNumberButton(
                        (int) startX + (int) (numberWidth + pageNumberInterval) * i,
                        leftArrowButtonRect.y() + 2,
                        number
                );
            }
        } else {
            addPageNumberButton(    // firstPageNumberButton
                    leftArrowButtonRect.x() + leftArrowButtonRect.width() + pageNumberInterval,
                    leftArrowButtonRect.y() + pageNumberButtonOffset,
                    1);

            addPageNumberButton(
                    rightArrowButtonRect.x() - pageNumberInterval - stringLength(maxPageNumber),
                    rightArrowButtonRect.y() + pageNumberButtonOffset,
                    maxPageNumber
            );

            float maxPageNumberWidth = rightArrowButtonRect.x() - (leftArrowButtonRect.x() + leftArrowButtonRect.width());
            maxPageNumberWidth -= stringLength(1) + stringLength(maxPageNumber) + pageNumberInterval * 2;
            // maxPageNumberWidth -= 2 * (stringLength("...") * pageNumberInterval);
            int ableNumberCount = 0;
            int width = 0;
            int leftNumber = pageNumber;
            int rightNumber = pageNumber;
            boolean isLeftEnd = false;
            boolean isRightEnd = false;

            if (pageNumber != 1 && pageNumber != maxPageNumber)
                width += (stringLength(pageNumber) + pageNumberInterval);

            List<Integer> pageNumberList = new ArrayList<>();
            System.out.println();
            while (true) {
                if (isLeftEnd) {
                    rightNumber += 1;
                    if (width + (pageNumberInterval + stringLength(rightNumber)) >=
                            maxPageNumberWidth - (stringLength("...") + pageNumberInterval * 2)) {
                        pageNumberForm = 0;
                        break;
                    }
                    pageNumberList.add(rightNumber);
                    width += (stringLength(rightNumber) + pageNumberInterval);
                } else if (isRightEnd) {
                    leftNumber -= 1;
                    if (width + (stringLength(leftNumber) + pageNumberInterval) >=
                            maxPageNumberWidth - (stringLength("...") + pageNumberInterval * 2)) {
                        pageNumberForm = 2;
                        break;
                    }
                    pageNumberList.add(leftNumber);
                    width += (stringLength(leftNumber) + pageNumberInterval);
                } else {
                    if (leftNumber <= 2) {
                        isLeftEnd = true;
                        continue;
                    } else if (rightNumber >= maxPageNumber - 1) {
                        isRightEnd = true;
                        continue;
                    }

                    leftNumber -= 1;
                    rightNumber += 1;
//                    System.out.printf("%d, %d, %d, %d, %d, %d <= %f \n",
//                            width, leftNumber, stringLength(leftNumber), rightNumber, stringLength(rightNumber),
//                            width + (stringLength(leftNumber) + pageNumberInterval) + (stringLength(rightNumber) + pageNumberInterval), maxPageNumberWidth);
                    if (width + (stringLength(leftNumber) + pageNumberInterval) + (pageNumberInterval + stringLength(rightNumber)) >=
                            maxPageNumberWidth - 2 * (stringLength("...") + pageNumberInterval * 2)) {
                        pageNumberForm = 1;
                        break;
                    }

                    pageNumberList.add(leftNumber);
                    pageNumberList.add(rightNumber);
                    width += (stringLength(leftNumber) + pageNumberInterval) + (stringLength(rightNumber) + pageNumberInterval);
                }
            }
//            StringBuilder output = new StringBuilder();
//            for (Integer integer : pageNumberList) {
//                output.append(integer).append(", ");
//            }
//            System.out.println(output);
            if (pageNumber != 1 && pageNumber != maxPageNumber)
                pageNumberList.add(pageNumber);

            if (isLeftEnd) {
                pageNumberList.sort(Comparator.naturalOrder());
                int x = leftArrowButtonRect.x() + leftArrowButtonRect.width() + stringLength(1) + pageNumberInterval;
                for (Integer number : pageNumberList) {
                    x += pageNumberInterval;
                    addPageNumberButton(
                            x,
                            leftArrowButtonRect.y() + pageNumberButtonOffset,
                            number
                    );
                    x += stringLength(number);
                }
                rightDotsX = (x + rightArrowButtonRect.x() - stringLength(maxPageNumber) - pageNumberInterval) / 2;
            } else if (isRightEnd) {
                pageNumberList.sort(Comparator.reverseOrder());
                int x = rightArrowButtonRect.x() - stringLength(maxPageNumber) - pageNumberInterval;
                for (Integer number : pageNumberList) {
                    x -= pageNumberInterval;
                    x -= stringLength(number);
                    addPageNumberButton(
                            x,
                            leftArrowButtonRect.y() + pageNumberButtonOffset,
                            number
                    );
                }
                leftDotsX = (x + leftArrowButtonRect.x() + leftArrowButtonRect.width() + stringLength(maxPageNumber) + pageNumberInterval) / 2;
            } else {
                pageNumberList.sort(Comparator.naturalOrder());
                int totalNumberWidth = 0;
                for (Integer number : pageNumberList) {
                    totalNumberWidth += stringLength(number);
                }
                totalNumberWidth += pageNumberInterval * (pageNumberList.size() - 1);
                int x = (leftArrowButtonRect.x() + leftArrowButtonRect.width() + stringLength(1)
                        + rightArrowButtonRect.x() - stringLength(maxPageNumber)) / 2 - totalNumberWidth / 2;

                leftDotsX = (x + leftArrowButtonRect.x() + leftArrowButtonRect.width() + stringLength(maxPageNumber) + pageNumberInterval) / 2;

                for (Integer number : pageNumberList) {
                    addPageNumberButton(
                            x,
                            leftArrowButtonRect.y() + pageNumberButtonOffset,
                            number
                    );
                    x += stringLength(number);
                    x += pageNumberInterval;
                }
                x -= stringLength(pageNumberList.get(pageNumberList.size() - 1));
                x -= pageNumberInterval;
                rightDotsX = (x + rightArrowButtonRect.x() - stringLength(maxPageNumber) - pageNumberInterval) / 2;
            }
        }
    }

    private void addPageNumberButton(int x, int y, int number) {
        PageNumberButton pageNumberButton = new PageNumberButton(
                getRegularX() + x,
                getRegularY() + y,
                pageNumberSize,
                number,
                pageNumber == number,
                button -> {
                    onPageNumberButtonPressed(number);
                });
        this.addRenderableWidget(pageNumberButton);
        pageNumberButtonList.add(pageNumberButton);
    }

    private void addItemButtons() {
        for (int i = 0; i < 6; i++) {
            final int finalI = i;
            ItemButton itemButton = new ItemButton(
                    getRegularX() + itemButtonRect.x(),
                    getRegularY() + itemButtonRect.y() + (itemButtonRect.height() + itemButtonInterval) * i,
                    itemButtonTextureRect.u(), itemButtonTextureRect.v(),
                    itemButtonHighlightTextureRect.u(), itemButtonHighlightTextureRect.v(),
                    itemButtonRect.width(), itemButtonRect.height(),
                    "ItemName" + i,
                    (i + 1) * 20,
                    (i + 1) * 7,
                    TEXTURE_LOCATION,
                    button -> {
                        onItemButtonPressed(finalI);
                    }
            );
            addRenderableWidget(itemButton);
            itemButtonList.add(itemButton);
        }
    }

    private int stringLength(String string) {
        return (int) (mc.font.width(string) * pageNumberSize / 9f);
    }

    private int stringLength(int number) {
        return (int) (mc.font.width("" + number) * pageNumberSize / 9f);
    }

    private void clearPageNumberButtons() {
        for (PageNumberButton pageNumberButton : pageNumberButtonList) {
            removeWidget(pageNumberButton);
        }
    }

    private void updatePageNumberButtons() {
        clearPageNumberButtons();
        addPageNumberButtons();
    }

    private void clearItemButtons() {
        for (ItemButton itemButton : itemButtonList) {
            removeWidget(itemButton);
        }
    }

    private void updateItemButtons() {
        clearItemButtons();
        addItemButtons();
    }

    public void render(@NotNull GuiGraphics guiGraphics, int p_98283_, int p_98284_, float p_98285_) {
        this.renderBackground(guiGraphics);
        renderSearchBox(guiGraphics, p_98283_, p_98284_, p_98285_);
        super.render(guiGraphics, p_98283_, p_98284_, p_98285_);

        renderLabels(guiGraphics);
        renderDots(guiGraphics);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics) {
        // super.renderBackground(guiGraphics);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        guiGraphics.blit(TEXTURE_LOCATION, getRegularX(), getRegularY(), 0, 0, width, height);
    }

    private void renderSearchBox(GuiGraphics guiGraphics, int a, int b, float c) {
        guiGraphics.blit(
                TEXTURE_LOCATION,
                getRegularX() + searchBoxRect.x(),
                getRegularY() + searchBoxRect.y(),
                searchBoxTextureRect.u(), searchBoxTextureRect.v(),
                searchBoxRect.width(), searchBoxRect.height()
        );

        float sizeRatio = searchBoxRect.height() / 20f;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(sizeRatio, sizeRatio, sizeRatio);
        guiGraphics.pose().translate(
                (1/sizeRatio) * (1 - sizeRatio) * (getRegularX() + searchBoxRect.x() + searchButtonRenderOffset.x()),
                (1/sizeRatio) * (1 - sizeRatio) * (getRegularY() + searchBoxRect.y() + searchButtonRenderOffset.y()),
                0
        );
        searchBox.render(guiGraphics, a, b, c);
        guiGraphics.pose().popPose();
    }

    private void renderLabels(GuiGraphics guiGraphics) {
        guiGraphics.blit(TEXTURE_LOCATION,
                getRegularX() + labelBarRect.x(),
                getRegularY() + labelBarRect.y(),
                labelBarTextureRect.u(), labelBarTextureRect.v(),
                labelBarRect.width(), labelBarRect.height());

        drawString(guiGraphics, "pmh.label.name", nameLabelCoordinate.x(), nameLabelCoordinate.y(), labelSize);
        drawCenteredString(guiGraphics, "pmh.label.quantity", quantityLabelCoordinate.x(), quantityLabelCoordinate.y(), labelSize);
        drawCenteredString(guiGraphics, "pmh.label.price", priceLabelCoordinate.x(), priceLabelCoordinate.y(), labelSize);
    }

    private void renderDots(GuiGraphics guiGraphics) {
        if (pageNumberForm == 0) {
            drawCenteredDots(guiGraphics,
                    getRegularX() + rightDotsX,
                    getRegularY() + leftArrowButtonRect.y() + pageNumberButtonOffset);
        } else if (pageNumberForm == 2) {
            drawCenteredDots(guiGraphics,
                    getRegularX() + leftDotsX,
                    getRegularY() + leftArrowButtonRect.y() + pageNumberButtonOffset);
        } else if (pageNumberForm == 1) {
            drawCenteredDots(guiGraphics,
                    getRegularX() + leftDotsX,
                    getRegularY() + leftArrowButtonRect.y() + pageNumberButtonOffset);
            drawCenteredDots(guiGraphics,
                    getRegularX() + rightDotsX,
                    getRegularY() + leftArrowButtonRect.y() + pageNumberButtonOffset);
        }
    }

    public void resize(@NotNull Minecraft m, int a, int b) {
        String temp = searchBox.getValue();
        this.init(m, a, b);
        searchBox.setValue(temp);
    }

    public void tick() {
        searchBox.tick();
    }

    private void drawCenteredDots(GuiGraphics guiGraphics, int x, int y) {
        MutableComponent component = Component.literal("...");
        component = component.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));

        x -= stringLength("...") / 2;

        float sizeRatio = pageNumberSize / 9f;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.pose().scale(sizeRatio, sizeRatio, sizeRatio);
        guiGraphics.drawString(mc.font, component.getVisualOrderText(), 0, 0, 0, false);
        guiGraphics.pose().popPose();
    }

    private void drawString(GuiGraphics guiGraphics, String content, int x, int y, int size) {
        MutableComponent component = Component.translatable(content);
        component = component.setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));

        x += getRegularX();
        y += getRegularY();

        float sizeRatio = size / 9f;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.pose().scale(sizeRatio, sizeRatio, sizeRatio);
        guiGraphics.drawString(mc.font, component.getVisualOrderText(), 0, 0, 0, false);
        guiGraphics.pose().popPose();
    }

    private void drawCenteredString(GuiGraphics guiGraphics, String content, int x, int y, int size) {
        x -= (int) ((mc.font.width(Component.translatable(content)) * ((float) size / 9f)) / 2);
        drawString(guiGraphics, content, x, y, size);
    }

    public boolean mouseScrolled(double p_94734_, double p_94735_, double p_94736_) {
        super.mouseScrolled(p_94734_, p_94735_, p_94736_);
        return true;
    }

    public boolean mouseClicked(double x, double y, int mouseButton) {
        super.mouseClicked(x, y, mouseButton);
        if (mouseButton == 0) {

        }
        return true;
    }

    private void onSearchButtonPressed() {
    }

    private void onLeftArrowButtonPressed() {
        pageNumber -= 1;
        pageNumber = Math.max(pageNumber, 1);
        updateArrowButtonsState();
        updatePageNumberButtons();
    }

    private void onRightArrowButtonPressed() {
        pageNumber += 1;
        pageNumber = Math.min(pageNumber, maxPageNumber);
        updateArrowButtonsState();
        updatePageNumberButtons();
    }

    private void updateArrowButtonsState() {
        leftButtonActivated.flag = true;
        rightButtonActivated.flag = true;
        if(pageNumber <= 1) {
            leftButtonActivated.flag = false;
        }
        if(pageNumber >= maxPageNumber) {
            rightButtonActivated.flag = false;
        }
    }

    private void onPageNumberButtonPressed(int number) {
        pageNumber = number;
        updateArrowButtonsState();
        updatePageNumberButtons();
    }

    private void onItemButtonPressed(int index) {
    }

    int getRegularX() {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        return screenWidth / 2 - width / 2;
    }

    int getRegularY() {
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        return screenHeight / 2 - height / 2;
    }
}
