package net.rebel459.combat_reborn.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.rebel459.combat_reborn.CombatReborn;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Arrays;
import java.util.List;


@Config(name = CombatReborn.MOD_ID + "/" + "armor")
public class CRArmorConfig implements ConfigData {

    public static class Modifiers {
        public List<String> ids;
        public double defense;
        public double toughness;
        public double knockback_resistance;
        public List<CRConfig.AttributeEntry> attributes;
        public EquipmentSlotGroup slot;

        Modifiers() {
            this(List.of("example:example"), 6, 1, 1, List.of(), EquipmentSlotGroup.CHEST);
        }
        Modifiers(List<String> ids, double defense, double toughness, double knockback_resistance, List<CRConfig.AttributeEntry> attributes, EquipmentSlotGroup slot) {
            this.ids = ids;
            this.defense = defense;
            this.toughness = toughness;
            this.knockback_resistance = knockback_resistance;
            this.attributes = attributes;
            this.slot = slot;
        }
    }

    public List<Modifiers> sets = Arrays.asList(
            new Modifiers(
                    List.of("minecraft:leather_helmet"),
                    1, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.HEAD
            ),
            new Modifiers(
                    List.of("minecraft:leather_chestplate"),
                    3, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.CHEST
            ),
            new Modifiers(
                    List.of("minecraft:leather_leggings"),
                    3, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.LEGS
            ),
            new Modifiers(
                    List.of("minecraft:leather_boots"),
                    1, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.FEET
            ),
            new Modifiers(
                    List.of("minecraft:leather_horse_armor"),
                    8, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.BODY
            ),
            new Modifiers(
                    List.of("minecraft:copper_helmet"),
                    2, 1, 0,
                    List.of(),
                    EquipmentSlotGroup.HEAD
            ),
            new Modifiers(
                    List.of("minecraft:copper_chestplate"),
                    4, 1, 0,
                    List.of(),
                    EquipmentSlotGroup.CHEST
            ),
            new Modifiers(
                    List.of("minecraft:copper_leggings"),
                    3, 1, 0,
                    List.of(),
                    EquipmentSlotGroup.LEGS
            ),
            new Modifiers(
                    List.of("minecraft:copper_boots"),
                    1, 1, 0,
                    List.of(),
                    EquipmentSlotGroup.FEET
            ),
            new Modifiers(
                    List.of(
                            "minecraft:copper_horse_armor",
                            "minecraft:copper_nautilus_armor"
                    ),
                    10, 1, 0,
                    List.of(),
                    EquipmentSlotGroup.BODY
            ),
            new Modifiers(
                    List.of("minecraft:chainmail_helmet"),
                    2, 3, 1,
                    List.of(),
                    EquipmentSlotGroup.HEAD
            ),
            new Modifiers(
                    List.of("minecraft:chainmail_chestplate"),
                    4, 3, 1,
                    List.of(),
                    EquipmentSlotGroup.CHEST
            ),
            new Modifiers(
                    List.of("minecraft:chainmail_leggings"),
                    4, 3, 1,
                    List.of(),
                    EquipmentSlotGroup.LEGS
            ),
            new Modifiers(
                    List.of("minecraft:chainmail_boots"),
                    2, 3, 1,
                    List.of(),
                    EquipmentSlotGroup.FEET
            ),
            new Modifiers(
                    List.of("minecraft:iron_helmet"),
                    3, 2, 0,
                    List.of(),
                    EquipmentSlotGroup.HEAD
            ),
            new Modifiers(
                    List.of("minecraft:iron_chestplate"),
                    6, 2, 0,
                    List.of(),
                    EquipmentSlotGroup.CHEST
            ),
            new Modifiers(
                    List.of("minecraft:iron_leggings"),
                    5, 2, 0,
                    List.of(),
                    EquipmentSlotGroup.LEGS
            ),
            new Modifiers(
                    List.of("minecraft:iron_boots"),
                    2, 2, 0,
                    List.of(),
                    EquipmentSlotGroup.FEET
            ),
            new Modifiers(
                    List.of(
                            "minecraft:iron_horse_armor",
                            "minecraft:iron_nautilus_armor"
                    ),
                    16, 2, 0,
                    List.of(),
                    EquipmentSlotGroup.BODY
            ),
            new Modifiers(
                    List.of("minecraft:golden_helmet"),
                    3, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.HEAD
            ),
            new Modifiers(
                    List.of("minecraft:golden_chestplate"),
                    5, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.CHEST
            ),
            new Modifiers(
                    List.of("minecraft:golden_leggings"),
                    4, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.LEGS
            ),
            new Modifiers(
                    List.of("minecraft:golden_boots"),
                    2, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.FEET
            ),
            new Modifiers(
                    List.of(
                            "minecraft:golden_horse_armor",
                            "minecraft:golden_nautilus_armor"
                    ),
                    14, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.BODY
            ),
            new Modifiers(
                    List.of("minecraft:diamond_helmet"),
                    4, 4, 0,
                    List.of(),
                    EquipmentSlotGroup.HEAD
            ),
            new Modifiers(
                    List.of("minecraft:diamond_chestplate"),
                    7, 4, 0,
                    List.of(),
                    EquipmentSlotGroup.CHEST
            ),
            new Modifiers(
                    List.of("minecraft:diamond_leggings"),
                    6, 4, 0,
                    List.of(),
                    EquipmentSlotGroup.LEGS
            ),
            new Modifiers(
                    List.of("minecraft:diamond_boots"),
                    3, 4, 0,
                    List.of(),
                    EquipmentSlotGroup.FEET
            ),
            new Modifiers(
                    List.of(
                            "minecraft:diamond_horse_armor",
                            "minecraft:diamond_nautilus_armor"
                    ),
                    20, 4, 0,
                    List.of(),
                    EquipmentSlotGroup.BODY
            ),
            new Modifiers(
                    List.of("minecraft:netherite_helmet"),
                    4, 5, 1,
                    List.of(),
                    EquipmentSlotGroup.HEAD
            ),
            new Modifiers(
                    List.of("minecraft:netherite_chestplate"),
                    7, 5, 1,
                    List.of(),
                    EquipmentSlotGroup.CHEST
            ),
            new Modifiers(
                    List.of("minecraft:netherite_leggings"),
                    6, 5, 1,
                    List.of(),
                    EquipmentSlotGroup.LEGS
            ),
            new Modifiers(
                    List.of("minecraft:netherite_boots"),
                    3, 5, 1,
                    List.of(),
                    EquipmentSlotGroup.FEET
            ),
            new Modifiers(
                    List.of(
                            "minecraft:netherite_horse_armor",
                            "minecraft:netherite_nautilus_armor"
                    ),
                    20, 5, 1,
                    List.of(),
                    EquipmentSlotGroup.BODY
            ),
            new Modifiers(
                    List.of("minecraft:turtle_helmet"),
                    3, 2, 0,
                    List.of(),
                    EquipmentSlotGroup.HEAD
            ),
            new Modifiers(
                    List.of("minecraft:wolf_armor"),
                    20, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.BODY
            ),
            new Modifiers(
                    List.of("progression_reborn:rose_helmet"),
                    4, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.HEAD
            ),
            new Modifiers(
                    List.of("progression_reborn:rose_chestplate"),
                    6, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.CHEST
            ),
            new Modifiers(
                    List.of("progression_reborn:rose_leggings"),
                    5, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.LEGS
            ),
            new Modifiers(
                    List.of("progression_reborn:rose_boots"),
                    3, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.FEET
            ),
            new Modifiers(
                    List.of(
                            "progression_reborn:rose_horse_armor",
                            "progression_reborn:rose_nautilus_armor"
                    ),
                    18, 0, 0,
                    List.of(),
                    EquipmentSlotGroup.BODY
            ),
            new Modifiers(
                    List.of("end_reborn:remnant_helmet"),
                    4, 5, 1,
                    List.of(new CRConfig.AttributeEntry("minecraft:explosion_knockback_resistance", 0.25, AttributeModifier.Operation.ADD_VALUE)),
                    EquipmentSlotGroup.HEAD
            ),
            new Modifiers(
                    List.of("end_reborn:remnant_chestplate"),
                    7, 5, 1,
                    List.of(new CRConfig.AttributeEntry("minecraft:explosion_knockback_resistance", 0.25, AttributeModifier.Operation.ADD_VALUE)),
                    EquipmentSlotGroup.CHEST
            ),
            new Modifiers(
                    List.of("end_reborn:remnant_leggings"),
                    6, 5, 1,
                    List.of(new CRConfig.AttributeEntry("minecraft:explosion_knockback_resistance", 0.25, AttributeModifier.Operation.ADD_VALUE)),
                    EquipmentSlotGroup.LEGS
            ),
            new Modifiers(
                    List.of("end_reborn:remnant_boots"),
                    3, 5, 1,
                    List.of(new CRConfig.AttributeEntry("minecraft:explosion_knockback_resistance", 0.25, AttributeModifier.Operation.ADD_VALUE)),
                    EquipmentSlotGroup.FEET
            ),
            new Modifiers(
                    List.of(
                            "end_reborn:remnant_horse_armor",
                            "end_reborn:remnant_nautilus_armor"
                    ),
                    20, 5, 1,
                    List.of(new CRConfig.AttributeEntry("minecraft:explosion_knockback_resistance", 1.0, AttributeModifier.Operation.ADD_VALUE)),
                    EquipmentSlotGroup.BODY
            ),
            new Modifiers(
                    List.of("end_reborn:featherzeal_helmet"),
                    4, 5, 1,
                    List.of(new CRConfig.AttributeEntry("minecraft:safe_fall_distance", 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                    EquipmentSlotGroup.HEAD
            ),
            new Modifiers(
                    List.of("end_reborn:featherzeal_chestplate"),
                    7, 5, 1,
                    List.of(new CRConfig.AttributeEntry("minecraft:safe_fall_distance", 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                    EquipmentSlotGroup.CHEST
            ),
            new Modifiers(
                    List.of("end_reborn:featherzeal_leggings"),
                    6, 5, 1,
                    List.of(new CRConfig.AttributeEntry("minecraft:safe_fall_distance", 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                    EquipmentSlotGroup.LEGS
            ),
            new Modifiers(
                    List.of("end_reborn:featherzeal_boots"),
                    3, 5, 1,
                    List.of(new CRConfig.AttributeEntry("minecraft:safe_fall_distance", 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                    EquipmentSlotGroup.FEET
            ),
            new Modifiers(
                    List.of(
                            "end_reborn:featherzeal_horse_armor",
                            "end_reborn:featherzeal_nautilus_armor"
                    ),
                    20, 5, 1,
                    List.of(new CRConfig.AttributeEntry("minecraft:safe_fall_distance", 0.3 * 0.3 * 0.3 * 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)),
                    EquipmentSlotGroup.BODY
            ),
            new Modifiers(
                    List.of("legacies_and_legends:reinforced_chestplate"),
                    7, 1, 5,
                    List.of(),
                    EquipmentSlotGroup.CHEST
            ),
            new Modifiers(
                    List.of("legacies_and_legends:travelling_strides"),
                    3, 0, 0,
                    List.of(new CRConfig.AttributeEntry("minecraft:movement_speed", 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)),
                    EquipmentSlotGroup.LEGS
            ),
            new Modifiers(
                    List.of("legacies_and_legends:wanderer_boots"),
                    2, 1, 0,
                    List.of(new CRConfig.AttributeEntry("minecraft:step_height", 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)),
                    EquipmentSlotGroup.FEET
            ),
            new Modifiers(
                    List.of("enderscape:shadoline_helmet"),
                    3, 1, 0,
                    List.of(new CRConfig.AttributeEntry("enderscape:stealth", 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)),
                    EquipmentSlotGroup.HEAD
            ),
            new Modifiers(
                    List.of("enderscape:shadoline_chestplate"),
                    5, 1, 0,
                    List.of(new CRConfig.AttributeEntry("enderscape:stealth", 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)),
                    EquipmentSlotGroup.CHEST
            ),
            new Modifiers(
                    List.of("enderscape:shadoline_leggings"),
                    4, 1, 0,
                    List.of(new CRConfig.AttributeEntry("enderscape:stealth", 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)),
                    EquipmentSlotGroup.LEGS
            ),
            new Modifiers(
                    List.of("enderscape:shadoline_boots"),
                    2, 1, 0,
                    List.of(new CRConfig.AttributeEntry("enderscape:stealth", 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)),
                    EquipmentSlotGroup.FEET
            ),
            new Modifiers(
                    List.of("enderscape:drift_leggings"),
                    4, 1, 0,
                    List.of(
                            new CRConfig.AttributeEntry("minecraft:movement_speed", 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                            new CRConfig.AttributeEntry("minecraft:safe_fall_distance", 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                            new CRConfig.AttributeEntry("minecraft:gravity", -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    ),
                    EquipmentSlotGroup.LEGS
            )
    );
}