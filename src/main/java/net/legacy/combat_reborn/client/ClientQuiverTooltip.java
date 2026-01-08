package net.legacy.combat_reborn.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.legacy.combat_reborn.registry.CRDataComponents;
import net.legacy.combat_reborn.util.QuiverContents;
import net.legacy.combat_reborn.util.QuiverHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ClientQuiverTooltip implements ClientTooltipComponent {
	private static final Identifier PROGRESSBAR_BORDER_SPRITE = Identifier.withDefaultNamespace("container/bundle/bundle_progressbar_border");
	private static final Identifier PROGRESSBAR_FILL_SPRITE = Identifier.withDefaultNamespace("container/bundle/bundle_progressbar_fill");
	private static final Identifier PROGRESSBAR_FULL_SPRITE = Identifier.withDefaultNamespace("container/bundle/bundle_progressbar_full");
	private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_highlight_back");
	private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_highlight_front");
	private static final Identifier SLOT_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_background");
	private static final int SLOT_MARGIN = 4;
	private static final int SLOT_SIZE = 24;
	private static final int GRID_WIDTH = 96;
	private static final int PROGRESSBAR_HEIGHT = 13;
	private static final int PROGRESSBAR_WIDTH = 96;
	private static final int PROGRESSBAR_BORDER = 1;
	private static final int PROGRESSBAR_FILL_MAX = 94;
	private static final int PROGRESSBAR_MARGIN_Y = 4;
	private static final Component QUIVER_FULL_TEXT = Component.translatable("item.minecraft.bundle.full");
	private static final Component QUIVER_EMPTY_TEXT = Component.translatable("item.minecraft.bundle.empty");
	private static final Component QUIVER_EMPTY_DESCRIPTION = Component.translatable("item.combat_reborn.quiver.empty.desc");

    private final ItemStack quiverStack;
    private final QuiverContents contents;

    public ClientQuiverTooltip(ItemStack quiverStack) {
        this.quiverStack = quiverStack;
        this.contents = quiverStack.getOrDefault(CRDataComponents.QUIVER_CONTENTS, QuiverContents.empty(QuiverHelper.getType(quiverStack)));
    }
	@Override
	public int getHeight(Font font) {
		return this.contents.isEmpty() ? getEmptyBundleBackgroundHeight(font) : this.backgroundHeight();
	}

	@Override
	public int getWidth(Font font) {
		return 96;
	}

	@Override
	public boolean showTooltipWithItemInHand() {
		return true;
	}

	private static int getEmptyBundleBackgroundHeight(Font font) {
		return getEmptyBundleDescriptionTextHeight(font) + 13 + 8;
	}

	private int backgroundHeight() {
		return this.itemGridHeight() + 13 + 8;
	}

	private int itemGridHeight() {
		return this.gridSizeY() * 24;
	}

	private int getContentXOffset(int i) {
		return (i - 96) / 2;
	}

	private int gridSizeY() {
		return Mth.positiveCeilDiv(this.slotCount(), 4);
	}

	private int slotCount() {
        int quiverSlots = QuiverHelper.getStorage(this.quiverStack);
        int visibleSlots = 4;
        if (quiverSlots > 4) visibleSlots = 8;
        if (quiverSlots > 8) visibleSlots = 12;
		return Math.min(visibleSlots, this.contents.size());
	}

	@Override
	public void renderImage(Font font, int i, int j, int k, int l, GuiGraphics guiGraphics) {
		if (this.contents.isEmpty()) {
			this.renderEmptyBundleTooltip(font, i, j, k, l, guiGraphics);
		} else {
			this.renderBundleWithItemsTooltip(font, i, j, k, l, guiGraphics);
		}
	}

	private void renderEmptyBundleTooltip(Font font, int i, int j, int k, int l, GuiGraphics guiGraphics) {
		drawEmptyBundleDescriptionText(i + this.getContentXOffset(k), j, font, guiGraphics);
		this.drawProgressbar(i + this.getContentXOffset(k), j + getEmptyBundleDescriptionTextHeight(font) + 4, font, guiGraphics);
	}

	private void renderBundleWithItemsTooltip(Font font, int i, int j, int k, int l, GuiGraphics guiGraphics) {
		boolean bl = this.contents.size() > 12;
		List<ItemStack> list = this.getShownItems(this.contents.getNumberOfItemsToShow());
		int m = i + this.getContentXOffset(k) + 96;
		int n = j + this.gridSizeY() * 24;
		int o = 1;

		for (int p = 1; p <= this.gridSizeY(); p++) {
			for (int q = 1; q <= 4; q++) {
				int r = m - q * 24;
				int s = n - p * 24;
				if (shouldRenderSurplusText(bl, q, p)) {
					renderCount(r, s, this.getAmountOfHiddenItems(list), font, guiGraphics);
				} else if (shouldRenderItemSlot(list, o)) {
					this.renderSlot(o, r, s, list, o, font, guiGraphics);
					o++;
				}
			}
		}

		this.drawSelectedItemTooltip(font, guiGraphics, i, j, k);
		this.drawProgressbar(i + this.getContentXOffset(k), j + this.itemGridHeight() + 4, font, guiGraphics);
	}

	private List<ItemStack> getShownItems(int i) {
		int j = Math.min(this.contents.size(), i);
		return this.contents.itemCopyStream().toList().subList(0, j);
	}

	private static boolean shouldRenderSurplusText(boolean bl, int i, int j) {
		return bl && i * j == 1;
	}

	private static boolean shouldRenderItemSlot(List<ItemStack> list, int i) {
		return list.size() >= i;
	}

	private int getAmountOfHiddenItems(List<ItemStack> list) {
		return this.contents.itemCopyStream().skip(list.size()).mapToInt(ItemStack::getCount).sum();
	}

	private void renderSlot(int i, int j, int k, List<ItemStack> list, int l, Font font, GuiGraphics guiGraphics) {
		int m = list.size() - i;
		boolean bl = m == Math.max(0, this.quiverStack.get(CRDataComponents.QUIVER_CONTENTS_SLOT));
		ItemStack itemStack = (ItemStack)list.get(m);
		if (bl) {
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, j, k, 24, 24);
		} else {
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, j, k, 24, 24);
		}

		guiGraphics.renderItem(itemStack, j + 4, k + 4, l);
		guiGraphics.renderItemDecorations(font, itemStack, j + 4, k + 4);
		if (bl) {
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, j, k, 24, 24);
		}
	}

	private static void renderCount(int i, int j, int k, Font font, GuiGraphics guiGraphics) {
		guiGraphics.drawCenteredString(font, "+" + k, i + 12, j + 10, -1);
	}

    private void drawSelectedItemTooltip(Font font, GuiGraphics guiGraphics, int x, int y, int width) {
        Integer rawSlot = this.quiverStack.get(CRDataComponents.QUIVER_CONTENTS_SLOT);
        int selectedSlot = (rawSlot != null) ? rawSlot : QuiverContents.NO_SELECTED_ITEM_INDEX;

        if (selectedSlot >= 0 && selectedSlot < this.contents.size()) {
            ItemStack selectedItem = this.contents.getItemUnsafe(selectedSlot);
            Component name = selectedItem.getStyledHoverName();
            int nameWidth = font.width(name.getVisualOrderText());
            int tooltipX = x + width / 2 - 12;
            int tooltipY = y - 15;

            guiGraphics.renderTooltip(
                    font,
                    List.of(ClientTooltipComponent.create(name.getVisualOrderText())),
                    tooltipX - nameWidth / 2,
                    tooltipY,
                    DefaultTooltipPositioner.INSTANCE,
                    selectedItem.get(DataComponents.TOOLTIP_STYLE)
            );
        }
    }

	private void drawProgressbar(int i, int j, Font font, GuiGraphics guiGraphics) {
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.getProgressBarTexture(), i + 1, j, this.getProgressBarFill(), 13);
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESSBAR_BORDER_SPRITE, i, j, 96, 13);
		Component component = this.getProgressBarFillText();
		if (component != null) {
			guiGraphics.drawCenteredString(font, component, i + 48, j + 3, -1);
		}
	}

	private static void drawEmptyBundleDescriptionText(int i, int j, Font font, GuiGraphics guiGraphics) {
		guiGraphics.drawWordWrap(font, QUIVER_EMPTY_DESCRIPTION, i, j, 96, -5592406);
	}

	private static int getEmptyBundleDescriptionTextHeight(Font font) {
		return font.split(QUIVER_EMPTY_DESCRIPTION, 96).size() * 9;
	}

	private int getProgressBarFill() {
		return Mth.clamp(Mth.mulAndTruncate(this.contents.weight(), 94), 0, 94);
	}

	private Identifier getProgressBarTexture() {
		return this.contents.weight().compareTo(Fraction.ONE) >= 0 ? PROGRESSBAR_FULL_SPRITE : PROGRESSBAR_FILL_SPRITE;
	}

	@Nullable
	private Component getProgressBarFillText() {
		if (this.contents.isEmpty()) {
			return QUIVER_EMPTY_TEXT;
		} else {
			return this.contents.weight().compareTo(Fraction.ONE) >= 0 ? QUIVER_FULL_TEXT : null;
		}
	}
}