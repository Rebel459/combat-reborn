package net.legacy.combat_reborn.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.legacy.combat_reborn.CombatReborn;
import net.legacy.combat_reborn.item.QuiverItem;
import net.legacy.combat_reborn.util.QuiverContents;
import net.legacy.combat_reborn.util.QuiverHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomModelData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class CRItems {

    public static List<Item> QUIVERS = new ArrayList<>();

    public static final QuiverItem QUIVER = registerQuiver("quiver");
    public static final QuiverItem BLACK_QUIVER = registerQuiver("black_quiver");
    public static final QuiverItem BLUE_QUIVER = registerQuiver("blue_quiver");
    public static final QuiverItem BROWN_QUIVER = registerQuiver("brown_quiver");
    public static final QuiverItem CYAN_QUIVER = registerQuiver("cyan_quiver");
    public static final QuiverItem GRAY_QUIVER = registerQuiver("gray_quiver");
    public static final QuiverItem GREEN_QUIVER = registerQuiver("green_quiver");
    public static final QuiverItem LIGHT_BLUE_QUIVER = registerQuiver("light_blue_quiver");
    public static final QuiverItem LIGHT_GRAY_QUIVER = registerQuiver("light_gray_quiver");
    public static final QuiverItem LIME_QUIVER = registerQuiver("lime_quiver");
    public static final QuiverItem MAGENTA_QUIVER = registerQuiver("magenta_quiver");
    public static final QuiverItem ORANGE_QUIVER = registerQuiver("orange_quiver");
    public static final QuiverItem PINK_QUIVER = registerQuiver("pink_quiver");
    public static final QuiverItem PURPLE_QUIVER = registerQuiver("purple_quiver");
    public static final QuiverItem RED_QUIVER = registerQuiver("red_quiver");
    public static final QuiverItem YELLOW_QUIVER = registerQuiver("yellow_quiver");
    public static final QuiverItem WHITE_QUIVER = registerQuiver("white_quiver");

    public static final QuiverItem WEIGHTED_QUIVER = registerQuiver("weighted_quiver", QuiverHelper.WEIGHTED_QUIVER, Rarity.UNCOMMON);

    public static final QuiverItem SAPPHIRE_QUIVER = registerQuiver("sapphire_quiver", QuiverHelper.SAPPHIRE_QUIVER, Rarity.RARE);

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.addAfter(Items.CROSSBOW, QUIVER, BLACK_QUIVER, BLUE_QUIVER, BROWN_QUIVER, CYAN_QUIVER, GRAY_QUIVER, GREEN_QUIVER, LIGHT_BLUE_QUIVER, LIGHT_GRAY_QUIVER, LIME_QUIVER, MAGENTA_QUIVER, ORANGE_QUIVER, PINK_QUIVER, PURPLE_QUIVER, RED_QUIVER, YELLOW_QUIVER, WHITE_QUIVER);
            if (CombatReborn.isLegaciesAndLegendsLoaded()) entries.addAfter(QUIVER, WEIGHTED_QUIVER, SAPPHIRE_QUIVER);
        });
    }

    private static @NotNull <T extends Item> T register(String name, @NotNull Function<Item.Properties, Item> function, Item.@NotNull Properties properties) {
        return register(name, function, properties, false);
    }
    private static @NotNull <T extends Item> T register(String name, @NotNull Function<Item.Properties, Item> function, Item.@NotNull Properties properties, boolean isQuiver) {
        var item = (T) Items.registerItem(ResourceKey.create(Registries.ITEM, CombatReborn.id(name)), function, properties);
        if (isQuiver) QUIVERS.add(item);
        return item;
    }

    private static QuiverItem registerQuiver(String name) {
        return registerQuiver(name, QuiverHelper.QUIVER, Rarity.UNCOMMON);
    }
    private static QuiverItem registerQuiver(String name, String type, Rarity rarity) {
        return register(
                name,
                QuiverItem::new,
                new Item.Properties()
                        .rarity(rarity)
                        .stacksTo(1)
                        .component(CRDataComponents.QUIVER_CONTENTS, QuiverContents.empty(type))
                        .component(CRDataComponents.QUIVER_CONTENTS_SLOT, -1)
                        .equippableUnswappable(EquipmentSlot.OFFHAND)
                        .component(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of("empty"), List.of())),
                true
        );
    }
}