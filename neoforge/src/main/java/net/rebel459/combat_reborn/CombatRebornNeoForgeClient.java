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
    }
}