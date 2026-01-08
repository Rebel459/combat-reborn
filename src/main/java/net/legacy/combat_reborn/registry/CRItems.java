package net.legacy.combat_reborn.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.legacy.combat_reborn.CombatReborn;
import net.legacy.combat_reborn.item.QuiverItem;
import net.legacy.combat_reborn.util.QuiverContents;
import net.legacy.combat_reborn.util.QuiverHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public final class CRItems {

    public static final QuiverItem QUIVER = register("quiver",
            QuiverItem::new,
            new Item.Properties()
                    .rarity(Rarity.UNCOMMON)
                    .stacksTo(1)
                    .component(CRDataComponents.QUIVER_CONTENTS, QuiverContents.empty(QuiverHelper.QUIVER))
                    .component(CRDataComponents.QUIVER_CONTENTS_SLOT, -1)
    );

    public static final QuiverItem WEIGHTED_QUIVER = register("weighted_quiver",
            QuiverItem::new,
            new Item.Properties()
                    .rarity(Rarity.UNCOMMON)
                    .stacksTo(1)
                    .component(CRDataComponents.QUIVER_CONTENTS, QuiverContents.empty(QuiverHelper.WEIGHTED_QUIVER))
                    .component(CRDataComponents.QUIVER_CONTENTS_SLOT, -1)
    );

    public static final QuiverItem SAPPHIRE_QUIVER = register("sapphire_quiver",
            QuiverItem::new,
            new Item.Properties()
                    .rarity(Rarity.RARE)
                    .stacksTo(1)
                    .component(CRDataComponents.QUIVER_CONTENTS, QuiverContents.empty(QuiverHelper.SAPPHIRE_QUIVER))
                    .component(CRDataComponents.QUIVER_CONTENTS_SLOT, -1)
    );

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.addAfter(Items.CROSSBOW, QUIVER);
            if (CombatReborn.isLegaciesAndLegendsLoaded) entries.addAfter(QUIVER, WEIGHTED_QUIVER, SAPPHIRE_QUIVER);
        });
    }

    private static @NotNull <T extends Item> T register(String name, @NotNull Function<Item.Properties, Item> function, Item.@NotNull Properties properties) {
        return (T) Items.registerItem(ResourceKey.create(Registries.ITEM, CombatReborn.id(name)), function, properties);
    }
}