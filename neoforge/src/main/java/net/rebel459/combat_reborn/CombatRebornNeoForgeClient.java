package net.rebel459.combat_reborn;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.rebel459.combat_reborn.client.QuiverMouseActions;
import net.rebel459.combat_reborn.mixin.client.ScreenAccessor;
import net.rebel459.combat_reborn.tag.CRItemTags;

@Mod(value = CombatReborn.MOD_ID, dist = Dist.CLIENT)
public class CombatRebornNeoForgeClient {

    public CombatRebornNeoForgeClient(IEventBus modEventBus) {
        CombatRebornClient.initClient();
        NeoForge.EVENT_BUS.addListener(CombatRebornNeoForgeClient::onMouseScroll);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onMouseScroll(ScreenEvent.MouseScrolled.Pre event) {
        if (!(event.getScreen() instanceof InventoryScreen inventoryScreen)) {
            return;
        }

        var mouseActions = (ItemSlotMouseAction) new QuiverMouseActions(event.getScreen().getMinecraft());

        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();

        Slot hoveredSlot = inventoryScreen.getSlotUnderMouse();

        if (hoveredSlot != null && mouseActions.matches(hoveredSlot)) {
            event.setCanceled(mouseActions.onMouseScrolled(mouseX, mouseY, hoveredSlot.index, hoveredSlot.getItem()));
        }
    }
}