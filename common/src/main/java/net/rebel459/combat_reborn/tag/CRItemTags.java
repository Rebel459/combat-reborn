package net.rebel459.combat_reborn.tag;

import net.rebel459.combat_reborn.CombatReborn;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class CRItemTags {
    public static final TagKey<Item> SHIELD = bind("shield");
    public static final TagKey<Item> QUIVER = bind("quiver");

    public static final TagKey<Item> RUBBLE_SHIELD = bindEnderscape("rubble_shields");

    public static final TagKey<Item> TRIDENT_REPAIR_MATERIALS = bindLegaciesAndLegends("trident_repair_materials");

    public static final TagKey<Item> SOUP = bindCommon("foods/soup");
    public static final TagKey<Item> POTIONS = bindCommon("potions");

    @NotNull
    private static TagKey<Item> bind(@NotNull String path) {
        return TagKey.create(Registries.ITEM, CombatReborn.id(path));
    }
    @NotNull
    private static TagKey<Item> bindCommon(@NotNull String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }
    @NotNull
    private static TagKey<Item> bindEnderscape(@NotNull String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("enderscape", path));
    }
    @NotNull
    private static TagKey<Item> bindLegaciesAndLegends(@NotNull String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("legacies_and_legends", path));
    }
}