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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CRItems {

    public static UnifiedRegistries.Items ITEMS = UnifiedRegistries.Items.create(CombatReborn.MOD_ID);

    public static List<Supplier<Item>> QUIVERS = new ArrayList<>();

    public static final Supplier<QuiverItem> QUIVER = registerQuiver("quiver");
    public static final Supplier<QuiverItem> BLACK_QUIVER = registerQuiver("black_quiver");
    public static final Supplier<QuiverItem> BLUE_QUIVER = registerQuiver("blue_quiver");
    public static final Supplier<QuiverItem> BROWN_QUIVER = registerQuiver("brown_quiver");
    public static final Supplier<QuiverItem> CYAN_QUIVER = registerQuiver("cyan_quiver");
    public static final Supplier<QuiverItem> GRAY_QUIVER = registerQuiver("gray_quiver");
    public static final Supplier<QuiverItem> GREEN_QUIVER = registerQuiver("green_quiver");
    public static final Supplier<QuiverItem> LIGHT_BLUE_QUIVER = registerQuiver("light_blue_quiver");
    public static final Supplier<QuiverItem> LIGHT_GRAY_QUIVER = registerQuiver("light_gray_quiver");
    public static final Supplier<QuiverItem> LIME_QUIVER = registerQuiver("lime_quiver");
    public static final Supplier<QuiverItem> MAGENTA_QUIVER = registerQuiver("magenta_quiver");
    public static final Supplier<QuiverItem> ORANGE_QUIVER = registerQuiver("orange_quiver");
    public static final Supplier<QuiverItem> PINK_QUIVER = registerQuiver("pink_quiver");
    public static final Supplier<QuiverItem> PURPLE_QUIVER = registerQuiver("purple_quiver");
    public static final Supplier<QuiverItem> RED_QUIVER = registerQuiver("red_quiver");
    public static final Supplier<QuiverItem> YELLOW_QUIVER = registerQuiver("yellow_quiver");
    public static final Supplier<QuiverItem> WHITE_QUIVER = registerQuiver("white_quiver");

    public static final Supplier<QuiverItem> WEIGHTED_QUIVER = registerQuiver("weighted_quiver", QuiverHelper.WEIGHTED_QUIVER, Rarity.UNCOMMON);

    public static final Supplier<QuiverItem> SAPPHIRE_QUIVER = registerQuiver("sapphire_quiver", QuiverHelper.SAPPHIRE_QUIVER, Rarity.RARE);

    public static void init() {}

    private static @NotNull Supplier register(String name, @NotNull Function<Item.Properties, Item> function, Supplier<Item.@NotNull Properties> properties, boolean isQuiver) {
        var item = ITEMS.register(name, function, properties);
        if (isQuiver) QUIVERS.add(item);
        return item;
    }

    private static Supplier<QuiverItem> registerQuiver(String name) {
        return registerQuiver(name, QuiverHelper.QUIVER, Rarity.UNCOMMON);
    }
    private static Supplier<QuiverItem> registerQuiver(String name, String type, Rarity rarity) {
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