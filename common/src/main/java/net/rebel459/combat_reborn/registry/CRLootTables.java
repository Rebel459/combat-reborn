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
import net.rebel459.unified.platform.UnifiedEvents;
import net.rebel459.unified.platform.UnifiedHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CRLootTables {
    public CRLootTables() {}

    public static final ResourceKey<LootTable> BIRCH_RUINS = registerLaL("chests/forest_ruins/birch");
    public static final ResourceKey<LootTable> CHERRY_RUINS = registerLaL("chests/forest_ruins/cherry");
    public static final ResourceKey<LootTable> MAPLE_RUINS = registerLaL("chests/forest_ruins/maple");
    public static final ResourceKey<LootTable> GOLDEN_BIRCH_RUINS = registerLaL("chests/forest_ruins/golden_birch");
    public static final ResourceKey<LootTable> DEEP_RUINS = registerLaL("chests/deep_ruins/deep");
    public static final ResourceKey<LootTable> SCULK_RUINS = registerLaL("chests/deep_ruins/sculk");
    public static final ResourceKey<LootTable> PALE_CABIN = registerLaL("chests/pale_cabin/chest");
    public static final ResourceKey<LootTable> PALE_CABIN_SECRET = registerLaL("chests/pale_cabin/secret");
    public static final ResourceKey<LootTable> RUINED_AETHER_PORTAL = registerLaL("chests/ruined_aether_portal");
    public static final ResourceKey<LootTable> RUINED_LIBRARY = registerLaL("chests/ruined_library");
    public static final ResourceKey<LootTable> END_RUINS = registerLaL("chests/end_ruins");
    public static final ResourceKey<LootTable> SWAMP_HUT = registerLaL("chests/swamp_hut");
    public static final ResourceKey<LootTable> RUINS = registerLaL("chests/ruins");
    public static final ResourceKey<LootTable> UNDERGROUND_CABIN = registerLaL("chests/cabin/underground");
    public static final ResourceKey<LootTable> DEEP_CABIN = registerLaL("chests/cabin/deep");
    public static final ResourceKey<LootTable> SPIRE = registerLaL("chests/spire");
    public static final ResourceKey<LootTable> SPIRE_BASE = registerLaL("chests/spire_base");

    public static final ResourceKey<LootTable> DUNGEON_CHEST = registerLaL("chests/dungeon/chest");
    public static final ResourceKey<LootTable> DUNGEON_CHEST_SIMPLE = registerLaL("chests/dungeon/simple/chest");
    public static final ResourceKey<LootTable> DUNGEON_CHEST_DEEP = registerLaL("chests/dungeon/deep/chest");
    public static final ResourceKey<LootTable> DUNGEON_CHEST_ARID = registerLaL("chests/dungeon/arid/chest");
    public static final ResourceKey<LootTable> DUNGEON_CHEST_FROZEN = registerLaL("chests/dungeon/frozen/chest");
    public static final ResourceKey<LootTable> DUNGEON_CHEST_VERDANT = registerLaL("chests/dungeon/verdant/chest");
    public static final ResourceKey<LootTable> DUNGEON_CHEST_INFERNAL = registerLaL("chests/dungeon/infernal/chest");

    public static void init() {
        if (CRConfig.getGeneral().quivers.enable_quivers && CRConfig.getGeneral().quivers.lootable) {
            UnifiedEvents.LootTables.modify(((table, key, provider) -> {
                if (key == BuiltInLootTables.SIMPLE_DUNGEON) {
                    table.addPool(
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                    .add(EmptyLootItem.emptyItem().setWeight(2))
                                    .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                    );
                }
                if (key == BuiltInLootTables.JUNGLE_TEMPLE) {
                    table.addPool(
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                    .add(EmptyLootItem.emptyItem().setWeight(5))
                                    .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                    );
                }
                if (key == BuiltInLootTables.VILLAGE_FLETCHER) {
                    table.addPool(
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                    .add(EmptyLootItem.emptyItem().setWeight(5))
                                    .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                    );
                }
                if (key == BIRCH_RUINS || key == CHERRY_RUINS || key == MAPLE_RUINS || key == GOLDEN_BIRCH_RUINS) {
                    table.addPool(
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                    .add(EmptyLootItem.emptyItem().setWeight(5))
                                    .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                    );
                }
                if (key == UNDERGROUND_CABIN) {
                    table.addPool(
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                    .add(EmptyLootItem.emptyItem().setWeight(8))
                                    .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                    );
                }
                if (key == DUNGEON_CHEST_SIMPLE ||  key == DUNGEON_CHEST_ARID || key == DUNGEON_CHEST_FROZEN || key == DUNGEON_CHEST_VERDANT) {
                    table.addPool(
                            LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                    .add(EmptyLootItem.emptyItem().setWeight(11))
                                    .add(LootItem.lootTableItem(CRItems.QUIVER.get()))
                    );
                }

                if (CRConfig.getGeneral().integrations.lal_quiver_variants) {
                    if (key == SPIRE) {
                        table.addPool(
                                LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                        .add(EmptyLootItem.emptyItem().setWeight(11))
                                        .add(LootItem.lootTableItem(CRItems.WEIGHTED_QUIVER.get()))
                        );
                    }
                    if (key == DEEP_CABIN || key == DEEP_RUINS) {
                        table.addPool(
                                LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                        .add(EmptyLootItem.emptyItem().setWeight(5))
                                        .add(LootItem.lootTableItem(CRItems.WEIGHTED_QUIVER.get()))
                        );
                    }
                    if (key == DUNGEON_CHEST_DEEP || key == DUNGEON_CHEST_INFERNAL) {
                        table.addPool(
                                LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                        .add(EmptyLootItem.emptyItem().setWeight(14))
                                        .add(LootItem.lootTableItem(CRItems.WEIGHTED_QUIVER.get()))
                        );
                    }
                }
            }));
        }
    }

    private static @NotNull ResourceKey<LootTable> registerLaL(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("legacies_and_legends", path));
    }
}