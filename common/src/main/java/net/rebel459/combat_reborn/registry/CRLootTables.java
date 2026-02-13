package net.rebel459.combat_reborn.registry;

import net.rebel459.combat_reborn.config.CRConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.rebel459.unified.platform.UnifiedHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CRLootTables {
    public CRLootTables() {}

    public static final ResourceKey<LootTable> BIRCH_RUINS = registerLegaciesAndLegends("chests/forest_ruins/birch");
    public static final ResourceKey<LootTable> CHERRY_RUINS = registerLegaciesAndLegends("chests/forest_ruins/cherry");
    public static final ResourceKey<LootTable> MAPLE_RUINS = registerLegaciesAndLegends("chests/forest_ruins/maple");
    public static final ResourceKey<LootTable> GOLDEN_BIRCH_RUINS = registerLegaciesAndLegends("chests/forest_ruins/golden_birch");
    public static final ResourceKey<LootTable> DEEP_RUINS = registerLegaciesAndLegends("chests/deep_ruins/deep");
    public static final ResourceKey<LootTable> SCULK_RUINS = registerLegaciesAndLegends("chests/deep_ruins/sculk");
    public static final ResourceKey<LootTable> PALE_CABIN = registerLegaciesAndLegends("chests/pale_cabin/chest");
    public static final ResourceKey<LootTable> PALE_CABIN_SECRET = registerLegaciesAndLegends("chests/pale_cabin/secret");
    public static final ResourceKey<LootTable> RUINED_AETHER_PORTAL = registerLegaciesAndLegends("chests/ruined_aether_portal");
    public static final ResourceKey<LootTable> RUINED_LIBRARY = registerLegaciesAndLegends("chests/ruined_library");
    public static final ResourceKey<LootTable> END_RUINS = registerLegaciesAndLegends("chests/end_ruins");
    public static final ResourceKey<LootTable> SWAMP_HUT = registerLegaciesAndLegends("chests/swamp_hut");
    public static final ResourceKey<LootTable> RUINS = registerLegaciesAndLegends("chests/ruins");
    public static final ResourceKey<LootTable> UNDERGROUND_CABIN = registerLegaciesAndLegends("chests/cabin/underground");
    public static final ResourceKey<LootTable> DEEP_CABIN = registerLegaciesAndLegends("chests/cabin/deep");
    public static final ResourceKey<LootTable> SPIRE = registerLegaciesAndLegends("chests/spire");
    public static final ResourceKey<LootTable> SPIRE_BASE = registerLegaciesAndLegends("chests/spire_base");

    public static final ResourceKey<LootTable> DUNGEON_CHEST = registerLegaciesAndLegends("chests/dungeon/chest");
    public static final ResourceKey<LootTable> DUNGEON_CHEST_SIMPLE = registerLegaciesAndLegends("chests/dungeon/simple/chest");
    public static final ResourceKey<LootTable> DUNGEON_CHEST_DEEP = registerLegaciesAndLegends("chests/dungeon/deep/chest");
    public static final ResourceKey<LootTable> DUNGEON_CHEST_ARID = registerLegaciesAndLegends("chests/dungeon/arid/chest");
    public static final ResourceKey<LootTable> DUNGEON_CHEST_FROZEN = registerLegaciesAndLegends("chests/dungeon/frozen/chest");
    public static final ResourceKey<LootTable> DUNGEON_CHEST_VERDANT = registerLegaciesAndLegends("chests/dungeon/verdant/chest");
    public static final ResourceKey<LootTable> DUNGEON_CHEST_INFERNAL = registerLegaciesAndLegends("chests/dungeon/infernal/chest");

    public static void init() {
        var lootTables = UnifiedHelpers.LootTables.get();
            if (CRConfig.get.general.quivers.enable_quivers && CRConfig.get.general.quivers.lootable) {
                lootTables.addPool(
                        BuiltInLootTables.SIMPLE_DUNGEON,
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(EmptyLootItem.emptyItem().setWeight(2))
                                .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                );
                lootTables.addPool(
                        BuiltInLootTables.JUNGLE_TEMPLE,
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(EmptyLootItem.emptyItem().setWeight(5))
                                .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                );
                lootTables.addPool(
                        BuiltInLootTables.VILLAGE_FLETCHER,
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(EmptyLootItem.emptyItem().setWeight(5))
                                .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                );
                lootTables.addPool(
                        List.of(
                                BIRCH_RUINS,
                                CHERRY_RUINS,
                                MAPLE_RUINS,
                                GOLDEN_BIRCH_RUINS
                        ),
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(EmptyLootItem.emptyItem().setWeight(5))
                                .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                );
                lootTables.addPool(
                        UNDERGROUND_CABIN,
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(EmptyLootItem.emptyItem().setWeight(8))
                                .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                );
                lootTables.addPool(
                        List.of(
                                DUNGEON_CHEST_SIMPLE,
                                DUNGEON_CHEST_ARID,
                                DUNGEON_CHEST_FROZEN,
                                DUNGEON_CHEST_VERDANT
                        ),
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(EmptyLootItem.emptyItem().setWeight(11))
                                .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                );
                lootTables.addPool(
                        BuiltInLootTables.JUNGLE_TEMPLE,
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(EmptyLootItem.emptyItem().setWeight(5))
                                .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                );
                lootTables.addPool(
                        BuiltInLootTables.JUNGLE_TEMPLE,
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(EmptyLootItem.emptyItem().setWeight(5))
                                .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                );
                lootTables.addPool(
                        BuiltInLootTables.JUNGLE_TEMPLE,
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                .add(EmptyLootItem.emptyItem().setWeight(5))
                                .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                );

                if (CRConfig.get.general.integrations.lal_quiver_variants) {
                    lootTables.addPool(
                            SPIRE,
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                    .add(EmptyLootItem.emptyItem().setWeight(11))
                                    .add(LootItem.lootTableItem(CRItems.WEIGHTED_QUIVER.get()))
                    );
                    lootTables.addPool(
                            List.of(
                                    DEEP_RUINS,
                                    DEEP_CABIN
                            ),
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                    .add(EmptyLootItem.emptyItem().setWeight(5))
                                    .add(LootItem.lootTableItem(CRItems.WEIGHTED_QUIVER.get()))
                    );
                    lootTables.addPool(
                            List.of(
                                    DUNGEON_CHEST_DEEP,
                                    DUNGEON_CHEST_INFERNAL
                            ),
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                    .add(EmptyLootItem.emptyItem().setWeight(14))
                                    .add(LootItem.lootTableItem(CRItems.WEIGHTED_QUIVER.get()))
                    );
                }
            }
    }

    private static @NotNull ResourceKey<LootTable> registerLegaciesAndLegends(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("legacies_and_legends", path));
    }
}