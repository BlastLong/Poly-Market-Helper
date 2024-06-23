package com.blastlong.pmh.gui;

import com.blastlong.pmh.PolyMarketHelperClient;
import com.blastlong.pmh.data.object.ItemObject;
import com.blastlong.pmh.data.object.LocationObject;
import com.blastlong.pmh.data.object.ShopObject;
import com.blastlong.pmh.gui.button.ArrowButton;
import com.blastlong.pmh.gui.button.IndicateLocationButton;
import com.blastlong.pmh.gui.button.ItemDetailButton;
import com.blastlong.pmh.gui.button.PageNumberButton;
import com.blastlong.pmh.gui.util.ArrowButtonFlag;
import com.blastlong.pmh.gui.util.Coordinate;
import com.blastlong.pmh.gui.util.Rect;
import com.blastlong.pmh.gui.util.TextureRect;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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

public class ItemDetailScreen extends Screen {

    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("pmh", "textures/gui/poly_market_background.png");
    public static final ResourceLocation SHOP_INFO_TEXTURE_LOCATION = new ResourceLocation("pmh", "textures/gui/shop_info.png");

    private static final Rect leftArrowButtonRect = new Rect(50, 151, 13, 6);
    private static final TextureRect leftArrowButtonDeactivatedTextureRect = new TextureRect(0, 177);
    private static final TextureRect leftArrowButtonActivatedTextureRect = new TextureRect(13, 177);
    private static final TextureRect leftArrowButtonHighlightedTextureRect = new TextureRect(26, 177);

    private static final Rect rightArrowButtonRect = new Rect(176, 151, 13, 6);
    private static final TextureRect rightArrowButtonDeactivatedTextureRect = new TextureRect(0, 183);
    private static final TextureRect rightArrowButtonActivatedTextureRect = new TextureRect(13, 183);
    private static final TextureRect rightArrowButtonHighlightedTextureRect = new TextureRect(26, 183);

    private static final Coordinate titleLabelCoordinate = new Coordinate(8, 4);

    private static final Rect labelBarRect = new Rect(5, 24, 230, 10);
    private static final TextureRect labelBarTextureRect = new TextureRect(0, 226);

    private static final Coordinate nameLabelCoordinate = new Coordinate(8, 26);
    private static final Coordinate amountLabelCoordinate = new Coordinate(104, 26);
    private static final Coordinate priceLabelCoordinate = new Coordinate(141, 26);
    private static final Coordinate unitPriceLabelCoordinate = new Coordinate(187, 26);
    private static final Coordinate stockLabelCoordinate = new Coordinate(223, 26);

    private static final Rect itemButtonRect = new Rect(5, 38, 230, 15);
    private static final TextureRect itemButtonTextureRect = new TextureRect(0, 162);
    private static final TextureRect itemButtonHighlightTextureRect = new TextureRect(0, 189);

    private static final Rect shopInfoRect = new Rect(241, 38, 100, 62);
    private static final TextureRect shopInfoTextureRect = new TextureRect(0, 0);
    private static final Coordinate shopNameLabelCoordinate = new Coordinate(50, 2);
    private static final Coordinate shopOwnerLabelCoordinate = new Coordinate(50, 13);
    private static final Coordinate locationLabelCoordinate = new Coordinate(50, 38);

    private static final Rect indicateLocationButtonRect = new Rect(2, 45, 96, 15);
    private static final TextureRect indicateLocationButtonDeactivatedTextureRect = new TextureRect(0, 62);
    private static final TextureRect indicateLocationButtonActivatedTextureRect = new TextureRect(0, 77);
    private static final TextureRect indicateLocationButtonHighlightedTextureRect = new TextureRect(0, 92);

    private static final int titleLabelSize = 10;
    private static final int labelSize = 7;

    private static final int pageNumberButtonOffset = 1;
    private static final int pageNumberSize = 5;
    private static final int pageNumberInterval = 6;

    private static final int itemButtonInterval = 3;

    private Minecraft mc;
    private PolyMarketHelperClient client;

    private final int width, height;

    private final ArrowButtonFlag leftButtonActivated = new ArrowButtonFlag();
    private final ArrowButtonFlag rightButtonActivated = new ArrowButtonFlag();

    private int maxPageNumber = 7;
    private int pageNumber = 1;
    private int pageNumberForm = -1;

    private int leftDotsX;
    private int rightDotsX;

    private final List<PageNumberButton> pageNumberButtonList = new ArrayList<>();
    private final List<ItemDetailButton> itemButtonList = new ArrayList<>();
    private IndicateLocationButton indicateLocationButton;
    private String indicateLocationButtonContent;

    private String itemName = "TESTITEM";
    private List<ItemObject> itemObjectList;

    private boolean showShopInfo = false;
    private ShopObject showingShopObject = null;
    private boolean isIndicateLocationButtonAvailable = true;

    public ItemDetailScreen(List<ItemObject> itemObjectList) {
        super(Component.literal("Item Detail Screen"));
        mc = Minecraft.getInstance();
        client = PolyMarketHelperClient.getInstance();

        width = 239;
        height = 162;

        this.itemObjectList = itemObjectList;
        itemName = itemObjectList.get(0).itemName;
    }

    protected void init() {
        super.init();

        pageNumberButtonList.clear();

        updateMaxPageNumber();

        addArrowButtons();
        addPageNumberButtons();
        addItemButtons();

        updateArrowButtonsState();

        if (indicateLocationButton != null)
            addIndicateLocationButton();
    }

    private void updateMaxPageNumber() {
        maxPageNumber = (int) Math.ceil((double) itemObjectList.size() / 6);
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
            PolyMarketHelperClient.LOGGER.info("");
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
//            PolyMarketHelperClient.LOGGER.info(output);
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
        int size = itemObjectList.size();
        int itemButtonCount = Math.min(size - (pageNumber - 1) * 6, 6);
        for (int i = 0; i < itemButtonCount; i++) {
            ItemObject itemObject = itemObjectList.get((pageNumber - 1) * 6 + i);
            String itemName = itemObject.itemName;
            int amount = itemObject.amount;
            int stock = itemObject.stock;
            int price = (int) itemObject.price;
            float unitPrice = itemObject.unitPrice;

            final int finalI = i;
            ItemDetailButton itemButton = new ItemDetailButton(
                    getRegularX() + itemButtonRect.x(),
                    getRegularY() + itemButtonRect.y() + (itemButtonRect.height() + itemButtonInterval) * i,
                    itemButtonTextureRect.u(), itemButtonTextureRect.v(),
                    itemButtonHighlightTextureRect.u(), itemButtonHighlightTextureRect.v(),
                    itemButtonRect.width(), itemButtonRect.height(),
                    itemName,
                    amount,
                    price,
                    unitPrice,
                    stock,
                    TEXTURE_LOCATION,
                    button -> {
                        onItemButtonPressed(finalI);
                    }
            );
            addRenderableWidget(itemButton);
            itemButtonList.add(itemButton);
        }
    }

    private void addIndicateLocationButton() {
        indicateLocationButton = new IndicateLocationButton(
                getRegularX() + shopInfoRect.x() + indicateLocationButtonRect.x(),
                getRegularY() + shopInfoRect.y() + indicateLocationButtonRect.y(),
                indicateLocationButtonDeactivatedTextureRect,
                indicateLocationButtonActivatedTextureRect,
                indicateLocationButtonHighlightedTextureRect,
                isIndicateLocationButtonAvailable,
                indicateLocationButtonContent,
                indicateLocationButtonRect.width(),
                indicateLocationButtonRect.height(),
                SHOP_INFO_TEXTURE_LOCATION,
                button -> {
                    onIndicateLocationButtonPressed();
                }
        );
        addRenderableWidget(indicateLocationButton);
    }

    private void removeIndicateLocationButton() {
        removeWidget(indicateLocationButton);
        indicateLocationButton = null;
    }

    private void updateIndicateLocationButton() {
        removeIndicateLocationButton();
        addIndicateLocationButton();
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
        updateItemButtons();
    }

    private void clearItemButtons() {
        for (ItemDetailButton itemButton : itemButtonList) {
            removeWidget(itemButton);
        }
    }

    private void updateItemButtons() {
        clearItemButtons();
        addItemButtons();
    }

    public void render(@NotNull GuiGraphics guiGraphics, int p_98283_, int p_98284_, float p_98285_) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, p_98283_, p_98284_, p_98285_);

        renderTitle(guiGraphics);
        renderLabels(guiGraphics);
        renderDots(guiGraphics);

        if(showShopInfo) {
            renderShowInfo(guiGraphics);
            if (indicateLocationButton != null)
                indicateLocationButton.render(guiGraphics, p_98283_, p_98284_, p_98284_);
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics) {
        // super.renderBackground(guiGraphics);

        // RenderSystem.setShader(GameRenderer::getPositionTexShader);
        guiGraphics.blit(TEXTURE_LOCATION, getRegularX(), getRegularY(), 0, 0, width, height);
    }

    private void renderTitle(GuiGraphics guiGraphics) {
        Component suffix = Component.translatable("pmh.label.title_suffix");
        String title = itemName + suffix.getString();
        drawRawString(guiGraphics, title, titleLabelCoordinate.x(), titleLabelCoordinate.y(), titleLabelSize);
    }

    private void renderLabels(GuiGraphics guiGraphics) {
        guiGraphics.blit(TEXTURE_LOCATION,
                getRegularX() + labelBarRect.x(),
                getRegularY() + labelBarRect.y(),
                labelBarTextureRect.u(), labelBarTextureRect.v(),
                labelBarRect.width(), labelBarRect.height());

        drawString(guiGraphics, "pmh.label.name", nameLabelCoordinate.x(), nameLabelCoordinate.y(), labelSize);
        drawCenteredString(guiGraphics, "pmh.label.amount", amountLabelCoordinate.x(), amountLabelCoordinate.y(), labelSize);
        drawCenteredString(guiGraphics, "pmh.label.price", priceLabelCoordinate.x(), priceLabelCoordinate.y(), labelSize);
        drawCenteredString(guiGraphics, "pmh.label.unit_price", unitPriceLabelCoordinate.x(), unitPriceLabelCoordinate.y(), labelSize);
        drawCenteredString(guiGraphics, "pmh.label.stock", stockLabelCoordinate.x(), stockLabelCoordinate.y(), labelSize);
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

    private void renderShowInfo(GuiGraphics guiGraphics) {
        int baseX = shopInfoRect.x();
        int baseY = shopInfoRect.y();
        guiGraphics.blit(SHOP_INFO_TEXTURE_LOCATION,
                getRegularX() + baseX,
                getRegularY() + baseY,
                shopInfoTextureRect.u(), shopInfoTextureRect.v(),
                shopInfoRect.width(), shopInfoRect.height()
        );

        drawCenteredString(guiGraphics,
                showingShopObject.shopName,
                baseX + shopNameLabelCoordinate.x(),
                baseY + shopNameLabelCoordinate.y(),
                9
        );
        drawCenteredString(guiGraphics,
                "by " + showingShopObject.ownerName,
                baseX + shopOwnerLabelCoordinate.x(),
                baseY + shopOwnerLabelCoordinate.y(),
                5
        );

        LocationObject locationObject = showingShopObject.location;
        if(locationObject.isAvailable) {
            drawCenteredString(guiGraphics,
                    locationObject.toString(),
                    baseX + locationLabelCoordinate.x(),
                    baseY + locationLabelCoordinate.y(),
                    6
            );
        }
        else {
            drawCenteredString(guiGraphics,
                    "pmh.label.no_location",
                    baseX + locationLabelCoordinate.x(),
                    baseY + locationLabelCoordinate.y(),
                    5
            );
        }
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        // super.keyPressed($$0, $$1, $$2);

        if ($$0 == 256) {
            if (showShopInfo) {
                showShopInfo = false;
                showingShopObject = null;
                if (indicateLocationButton != null) {
                    removeWidget(indicateLocationButton);
                    indicateLocationButton = null;
                    isIndicateLocationButtonAvailable = true;
                    // 삭제 및 update 함수 만들기
                }
            }
            else {
                PolyMarketHelperClient.getInstance().openScreen();
            }
        }
        return true;
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

    private void drawRawString(GuiGraphics guiGraphics, String content, int x, int y, int size) {
        MutableComponent component = Component.literal(content);
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

    private void onLeftArrowButtonPressed() {
        // leftButtonActivated.flag = !leftButtonActivated.flag;

        pageNumber -= 1;
        pageNumber = Math.max(pageNumber, 1);
        updateArrowButtonsState();
        updatePageNumberButtons();
    }

    private void onRightArrowButtonPressed() {
        // rightButtonActivated.flag = !rightButtonActivated.flag;

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
        removeIndicateLocationButton();
        int itemObjectIndex = (pageNumber - 1) * 6 + index;
        ItemObject itemObject = itemObjectList.get(itemObjectIndex);

        showShopInfo = true;
        showingShopObject = itemObject.shop;
        if (showingShopObject.location.isAvailable) {
            indicateLocationButtonContent = "pmh.button.indicate_location.activated";
            isIndicateLocationButtonAvailable = true;
        }
        else {
            indicateLocationButtonContent = "pmh.button.indicate_location.unavailable";
            isIndicateLocationButtonAvailable = false;

        }
        addIndicateLocationButton();
        if (showingShopObject.location.isAvailable)
            indicateLocationButton.active = true;
        else
            indicateLocationButton.active = false;
    }

    private void onIndicateLocationButtonPressed() {
        client.getLocationIndicator().indicateLocation(showingShopObject.location);

        isIndicateLocationButtonAvailable = false;
        indicateLocationButtonContent = "pmh.button.indicate_location.deactivated";
        updateIndicateLocationButton();
        indicateLocationButton.active = false;
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
