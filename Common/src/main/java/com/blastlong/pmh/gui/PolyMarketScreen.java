package com.blastlong.pmh.gui;

import com.blastlong.pmh.PolyMarketHelperClient;
import com.blastlong.pmh.data.DataManager;
import com.blastlong.pmh.data.object.ItemObject;
import com.blastlong.pmh.gui.button.*;
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

    private static final Rect searchButtonRect = new Rect(212, 5, 9, 8);
    private static final TextureRect searchButtonTextureRect = new TextureRect(39, 177);
    private static final Coordinate searchButtonRenderOffset = new Coordinate(3, 6);

    private static final Rect reloadButtonRect = new Rect(225, 4, 10, 10);
    private static final TextureRect reloadButtonTextureRect = new TextureRect(48, 177);

    private static final Rect leftArrowButtonRect = new Rect(50, 151, 13, 6);
    private static final TextureRect leftArrowButtonDeactivatedTextureRect = new TextureRect(0, 177);
    private static final TextureRect leftArrowButtonActivatedTextureRect = new TextureRect(13, 177);
    private static final TextureRect leftArrowButtonHighlightedTextureRect = new TextureRect(26, 177);

    private static final Rect rightArrowButtonRect = new Rect(176, 151, 13, 6);
    private static final TextureRect rightArrowButtonDeactivatedTextureRect = new TextureRect(0, 183);
    private static final TextureRect rightArrowButtonActivatedTextureRect = new TextureRect(13, 183);
    private static final TextureRect rightArrowButtonHighlightedTextureRect = new TextureRect(26, 183);

    private static final Rect labelBarRect = new Rect(5, 24, 230, 10);
    private static final TextureRect labelBarTextureRect = new TextureRect(0, 216);

    private static final Coordinate nameLabelCoordinate = new Coordinate(8, 26);
    private static final Coordinate amountLabelCoordinate = new Coordinate(175, 26);
    private static final Coordinate priceLabelCoordinate = new Coordinate(212, 26);

    private static final Rect itemButtonRect = new Rect(5, 38, 230, 15);
    private static final TextureRect itemButtonTextureRect = new TextureRect(0, 162);
    private static final TextureRect itemButtonHighlightTextureRect = new TextureRect(0, 189);

    private static final Rect searchBoxRect = new Rect(5, 4, 218, 10);
    private static final TextureRect searchBoxTextureRect = new TextureRect(0, 206);

    private static final Rect validOnlyToggleButtonRect = new Rect(6, 16, 6, 6);
    private static final TextureRect validOnlyToggleButtonOffTextureRect = new TextureRect(69, 177);
    private static final TextureRect validOnlyToggleButtonOnTextureRect = new TextureRect(75, 177);

    private static final Coordinate updateDateLabelCoordinate = new Coordinate(234, 16);

    private static final int labelSize = 7;
    private static final int dateLabelSize = 5;

    private static final int pageNumberButtonOffset = 1;
    private static final int pageNumberSize = 5;
    private static final int pageNumberInterval = 6;

    private static final int itemButtonInterval = 3;

    private final Minecraft mc;
    private final PolyMarketHelperClient client;

    private final int width, height;

    private EditBox searchBox;

    private final ArrowButtonFlag leftButtonActivated = new ArrowButtonFlag();
    private final ArrowButtonFlag rightButtonActivated = new ArrowButtonFlag();

    private boolean isValidOnly = false;

    private boolean isFiltered = false;
    private List<List<ItemObject>> filteredList = null;
    private String filterLine = "";

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
        client = PolyMarketHelperClient.getInstance();

        width = 239;
        height = 162;
    }

    protected void init() {
        super.init();

        pageNumberButtonList.clear();
        itemButtonList.clear();

        updateMaxPageNumber();

        addSearchButton();
        addReloadButton();
        addValidOnlyToggleButton();
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

        if (!filterLine.isEmpty())
            searchBox.setValue(filterLine);

        updateArrowButtonsState();
    }

    private void reload() {
        client.loadItemMap();
        reloadWidgets();
        filterLine = "";
        searchBox.setValue("");
        clearSearch();
        search();       // to reload the related to search
    }

    private void reloadWidgets() {
        pageNumber = 1;
        clearWidgets();
        init();
        clearSearch();
        search();
    }


    private void updateMaxPageNumber() {
        List<List<ItemObject>> itemObjectsList = getItemObjectsList();
        maxPageNumber = (int) Math.ceil((double) itemObjectsList.size() / 6);
    }

    private void search() {
        filterLine = searchBox.getValue();
        clearSearch();

        if (!filterLine.isEmpty()) {
            String[] keywords = filterLine.split(" ");
            filteredList = DataManager.getFilteredListByKeywords(getItemObjectsList(), keywords);
            isFiltered = true;
        }

        updateMaxPageNumber();
        pageNumber = 1;
        updatePageNumberButtons();
        updateArrowButtonsState();
        updateItemButtons();
    }

    private void clearSearch() {
        isFiltered = false;
        filteredList = null;
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

    private void addReloadButton() {
        this.addRenderableWidget(
                new ImageButton(
                        getRegularX() + reloadButtonRect.x(),
                        getRegularY() + reloadButtonRect.y(),
                        reloadButtonTextureRect.u(), reloadButtonTextureRect.v(),
                        reloadButtonRect.width(), reloadButtonRect.height(),
                        TEXTURE_LOCATION,
                        button -> {
                            onReloadButtonPressed();
                        })
        );
    }

    private void addValidOnlyToggleButton() {
        String content = "pmh.label.valid_only";
        Component component = Component.translatable(content);
        int contentWidth = (int) (mc.font.width(component) * ToggleButton.contentSize / 9f);

        this.addRenderableWidget(
                new ToggleButton(
                        getRegularX() + validOnlyToggleButtonRect.x(),
                        getRegularY() + validOnlyToggleButtonRect.y(),
                        validOnlyToggleButtonOffTextureRect.u(), validOnlyToggleButtonOffTextureRect.v(),
                        validOnlyToggleButtonOnTextureRect.u(), validOnlyToggleButtonOnTextureRect.v(),
                        validOnlyToggleButtonRect.width(), validOnlyToggleButtonRect.height(),
                        validOnlyToggleButtonRect.width() + contentWidth, validOnlyToggleButtonRect.height(),
                        isValidOnly,
                        content,
                        TEXTURE_LOCATION,
                        button -> {
                            onValidOnlyToggleButtonPressed();
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
        List<List<ItemObject>> itemObjectsList = getItemObjectsList();

        int size = itemObjectsList.size();
        int itemButtonCount = Math.min(size - (pageNumber - 1) * 6, 6);
        for (int i = 0; i < itemButtonCount; i++) {
            List<ItemObject> itemObjectList = itemObjectsList.get((pageNumber - 1) * 6 + i);
            String itemName = null;
            int amount = 0;
            float leastUnitPrice = Float.MAX_VALUE;
            for (Object object : itemObjectList) {
                ItemObject itemObject = (ItemObject) object;
                itemName = itemObject.itemName;
                amount += itemObject.amount;
                if (leastUnitPrice > itemObject.unitPrice)
                    leastUnitPrice = itemObject.unitPrice;
            }

            final int finalI = i;
            ItemButton itemButton = new ItemButton(
                    getRegularX() + itemButtonRect.x(),
                    getRegularY() + itemButtonRect.y() + (itemButtonRect.height() + itemButtonInterval) * i,
                    itemButtonTextureRect.u(), itemButtonTextureRect.v(),
                    itemButtonHighlightTextureRect.u(), itemButtonHighlightTextureRect.v(),
                    itemButtonRect.width(), itemButtonRect.height(),
                    itemName,
                    amount,
                    leastUnitPrice,
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
        updateItemButtons();
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
        renderUpdateDateLabel(guiGraphics);
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
        drawCenteredString(guiGraphics, "pmh.label.amount", amountLabelCoordinate.x(), amountLabelCoordinate.y(), labelSize);
        drawCenteredString(guiGraphics, "pmh.label.price", priceLabelCoordinate.x(), priceLabelCoordinate.y(), labelSize);
    }

    private void renderUpdateDateLabel(GuiGraphics guiGraphics) {
        String content = client.getUpdateDate();
        int x = updateDateLabelCoordinate.x() - (int) ((mc.font.width(Component.translatable(content)) * ((float) dateLabelSize / 9f)));
        drawString(guiGraphics, content, x, updateDateLabelCoordinate.y(), dateLabelSize);
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

    @Override
    public void resize(@NotNull Minecraft m, int a, int b) {
        filterLine = searchBox.getValue();
        this.init(m, a, b);
        searchBox.setValue(filterLine);
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
        search();
    }

    private void onReloadButtonPressed() {
        reload();
    }

    private void onValidOnlyToggleButtonPressed() {
        isValidOnly = !isValidOnly;
        reloadWidgets();
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
        int itemObjectListIndex = (pageNumber - 1) * 6 + index;
        mc.setScreen(new ItemDetailScreen(getItemObjectsList().get(itemObjectListIndex)));
    }

    private List<List<ItemObject>> getItemObjectsList() {
        if (isFiltered)
            return filteredList;
        if (!isValidOnly)
            return client.getItemObjectsList();
        else
            return client.getValidItemObjectsList();
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
