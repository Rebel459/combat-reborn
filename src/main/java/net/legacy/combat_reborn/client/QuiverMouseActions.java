package net.legacy.combat_reborn.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.legacy.combat_reborn.item.QuiverItem;
import net.legacy.combat_reborn.network.SelectQuiverItemPacket;
import net.legacy.combat_reborn.registry.CRDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;

@Environment(EnvType.CLIENT)
public class QuiverMouseActions implements ItemSlotMouseAction {
	private final Minecraft minecraft;
	private final ScrollWheelHandler scrollWheelHandler;

	public QuiverMouseActions(Minecraft minecraft) {
		this.minecraft = minecraft;
		this.scrollWheelHandler = new ScrollWheelHandler();
	}

	@Override
	public boolean matches(Slot slot) {
		return slot.getItem().getItem() instanceof QuiverItem;
	}

	@Override
	public boolean onMouseScrolled(double d, double e, int i, ItemStack itemStack) {
		int j = QuiverItem.getNumberOfItemsToShow(itemStack);
		if (j == 0) {
			return false;
		} else {
			Vector2i vector2i = this.scrollWheelHandler.onMouseScroll(d, e);
			int k = vector2i.y == 0 ? -vector2i.x : vector2i.y;
			if (k != 0) {
				int l = Math.max(itemStack.get(CRDataComponents.QUIVER_CONTENTS_SLOT), 0);
				int m = ScrollWheelHandler.getNextScrollWheelSelection(k, l, j);
				if (l != m) {
					this.toggleSelectedQuiverItem(itemStack, i, m);
				}
			}

			return true;
		}
	}

	@Override
	public void onStopHovering(Slot slot) {
		//this.unselectedQuiverItem(slot.getItem(), slot.index);
	}

	@Override
	public void onSlotClicked(Slot slot, ClickType clickType) {
		if (clickType == ClickType.QUICK_MOVE || clickType == ClickType.SWAP) {
			this.unselectedQuiverItem(slot.getItem(), slot.index);
		}
	}

    private void toggleSelectedQuiverItem(ItemStack itemStack, int slotId, int selectedSlot) {
        if (this.minecraft.getConnection() != null && selectedSlot < QuiverItem.getNumberOfItemsToShow(itemStack)) {
            QuiverItem.toggleSelectedItem(itemStack, selectedSlot);
            itemStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT, Math.max(0, selectedSlot));
            SelectQuiverItemPacket packet = new SelectQuiverItemPacket(slotId, selectedSlot);
            this.minecraft.getConnection().send(packet.toVanillaPacket());
        }
    }

	public void unselectedQuiverItem(ItemStack itemStack, int i) {
		this.toggleSelectedQuiverItem(itemStack, i, -1);
	}
}