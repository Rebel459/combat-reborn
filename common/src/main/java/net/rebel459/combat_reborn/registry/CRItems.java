package net.rebel459.combat_reborn.registry;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomModelData;
import net.rebel459.combat_reborn.CombatReborn;
import net.rebel459.combat_reborn.item.QuiverItem;
import net.rebel459.combat_reborn.util.QuiverContents;
import net.rebel459.combat_reborn.util.QuiverHelper;
import net.rebel459.unified.platform.UnifiedRegistries;
import net.rebel459.unified.util.SuppliedItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CRItems {

    public static UnifiedRegistries.Items ITEMS = UnifiedRegistries.Items.create(CombatReborn.MOD_ID);

    public static List<Supplier<Item>> QUIVERS = new ArrayList<>();

    public static final SuppliedItem QUIVER = registerQuiver("quiver");
    public static final SuppliedItem BLACK_QUIVER = registerQuiver("black_quiver");
    public static final SuppliedItem BLUE_QUIVER = registerQuiver("blue_quiver");
    public static final SuppliedItem BROWN_QUIVER = registerQuiver("brown_quiver");
    public static final SuppliedItem CYAN_QUIVER = registerQuiver("cyan_quiver");
    public static final SuppliedItem GRAY_QUIVER = registerQuiver("gray_quiver");
    public static final SuppliedItem GREEN_QUIVER = registerQuiver("green_quiver");
    public static final SuppliedItem LIGHT_BLUE_QUIVER = registerQuiver("light_blue_quiver");
    public static final SuppliedItem LIGHT_GRAY_QUIVER = registerQuiver("light_gray_quiver");
    public static final SuppliedItem LIME_QUIVER = registerQuiver("lime_quiver");
    public static final SuppliedItem MAGENTA_QUIVER = registerQuiver("magenta_quiver");
    public static final SuppliedItem ORANGE_QUIVER = registerQuiver("orange_quiver");
    public static final SuppliedItem PINK_QUIVER = registerQuiver("pink_quiver");
    public static final SuppliedItem PURPLE_QUIVER = registerQuiver("purple_quiver");
    public static final SuppliedItem RED_QUIVER = registerQuiver("red_quiver");
    public static final SuppliedItem YELLOW_QUIVER = registerQuiver("yellow_quiver");
    public static final SuppliedItem WHITE_QUIVER = registerQuiver("white_quiver");

    public static final SuppliedItem WEIGHTED_QUIVER = registerQuiver("weighted_quiver", QuiverHelper.WEIGHTED_QUIVER, Rarity.UNCOMMON);

    public static final SuppliedItem SAPPHIRE_QUIVER = registerQuiver("sapphire_quiver", QuiverHelper.SAPPHIRE_QUIVER, Rarity.RARE);

    public static void init() {}

    private static @NotNull SuppliedItem register(String name, @NotNull Function<Item.Properties, Item> function, Supplier<Item.@NotNull Properties> properties, boolean isQuiver) {
        var item = ITEMS.register(name, function, properties);
        if (isQuiver) QUIVERS.add(item);
        return item;
    }

    private static SuppliedItem registerQuiver(String name) {
        return registerQuiver(name, QuiverHelper.QUIVER, Rarity.UNCOMMON);
    }
    private static SuppliedItem registerQuiver(String name, String type, Rarity rarity) {
        return register(
                name,
                QuiverItem::new,
                () -> new Item.Properties()
                        .rarity(rarity)
                        .stacksTo(1)
                        .component(CRDataComponents.QUIVER_CONTENTS.get(), QuiverContents.empty(type))
                        .component(CRDataComponents.QUIVER_CONTENTS_SLOT.get(), -1)
                        .equippableUnswappable(EquipmentSlot.OFFHAND)
                        .component(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of("empty"), List.of())),
                true
        );
    }
}