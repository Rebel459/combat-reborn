package net.rebel459.combat_reborn.registry;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.rebel459.combat_reborn.CombatReborn;
import net.rebel459.unified.platform.UnifiedHelpers;

public class CRCreativeInventory {

    public static void init() {
        UnifiedHelpers.CREATIVE_ENTRIES.addAfter(
                CreativeModeTabs.COMBAT,
                Items.CROSSBOW,
                CRItems.QUIVER.get(),
                CRItems.BLACK_QUIVER.get(),
                CRItems.BLUE_QUIVER.get(),
                CRItems.BROWN_QUIVER.get(),
                CRItems.CYAN_QUIVER.get(),
                CRItems.GRAY_QUIVER.get(),
                CRItems.GREEN_QUIVER.get(),
                CRItems.LIGHT_BLUE_QUIVER.get(),
                CRItems.LIGHT_GRAY_QUIVER.get(),
                CRItems.LIME_QUIVER.get(),
                CRItems.MAGENTA_QUIVER.get(),
                CRItems.ORANGE_QUIVER.get(),
                CRItems.PINK_QUIVER.get(),
                CRItems.PURPLE_QUIVER.get(),
                CRItems.RED_QUIVER.get(),
                CRItems.YELLOW_QUIVER.get(),
                CRItems.WHITE_QUIVER.get()
        );
        if (CombatReborn.hasLegaciesAndLegends()) UnifiedHelpers.CREATIVE_ENTRIES.addAfter(
                CreativeModeTabs.COMBAT,
                CRItems.QUIVER.get(),
                CRItems.WEIGHTED_QUIVER.get(),
                CRItems.SAPPHIRE_QUIVER.get()
        );
    }
}
