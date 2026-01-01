package net.legacy.combat_reborn.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.Arrays;
import java.util.List;


@Config(name = "modifiers")
public class CRModifierConfig implements ConfigData {

    public static class Modifiers {
        public List<String> ids;
        public double damage;
        public double speed;
        public double reach;

        Modifiers() {
            this(List.of("example:example"), 1, 4, 3);
        }
        Modifiers(List<String> ids, double damage, double speed, double reach) {
            this.ids = ids;
            this.damage = damage;
            this.speed = speed;
            this.reach = reach;
        }
    }

    public List<Modifiers> sets = Arrays.asList(

            // Swords
            new Modifiers(List.of(
                    "minecraft:wooden_sword",
                    "minecraft:stone_sword",
                    "legacies_and_legends:verdant_sword"
            ), 4, 2, 3),
            new Modifiers(List.of(
                    "minecraft:copper_sword",
                    "minecraft:golden_sword"
            ), 5, 2, 3),
            new Modifiers(List.of(
                    "minecraft:iron_sword",
                    "progression_reborn:rose_sword"
            ), 6, 2, 3),
            new Modifiers(List.of(
                    "minecraft:diamond_sword"
            ), 7, 2, 3),
            new Modifiers(List.of(
                    "minecraft:netherite_sword",
                    "end_reborn:remnant_sword",
                    "end_reborn:featherzeal_sword"
            ), 8, 2, 3),

            // Axes
            new Modifiers(List.of(
                    "minecraft:wooden_axe",
                    "minecraft:stone_axe"
            ), 7, 0.8, 3),
            new Modifiers(List.of(
                    "minecraft:copper_axe",
                    "minecraft:golden_axe"
            ), 7, 1, 3),
            new Modifiers(List.of(
                    "minecraft:iron_axe",
                    "progression_reborn:rose_axe",
                    "legacies_and_legends:cleaving_battleaxe"
            ), 8, 1, 3),
            new Modifiers(List.of(
                    "minecraft:diamond_axe"
            ), 8, 1.2, 3),
            new Modifiers(List.of(
                    "minecraft:netherite_axe",
                    "end_reborn:remnant_axe",
                    "end_reborn:featherzeal_axe"
            ), 9, 1.2, 3),

            // Spears
            new Modifiers(List.of(
                    "minecraft:wooden_spear",
                    "minecraft:stone_spear",
                    "legacies_and_legends:frosted_spear"
            ), 2, 1.6, 3),
            new Modifiers(List.of(
                    "minecraft:copper_spear",
                    "minecraft:golden_spear"
            ), 3, 1.4, 3),
            new Modifiers(List.of(
                    "minecraft:iron_spear",
                    "progression_reborn:rose_spear"
            ), 4, 1.2, 3),
            new Modifiers(List.of(
                    "minecraft:diamond_spear"
            ), 5, 1.0, 3),
            new Modifiers(List.of(
                    "minecraft:netherite_spear",
                    "end_reborn:remnant_spear",
                    "end_reborn:featherzeal_spear"
            ), 6, 0.8, 3),

            // Pickaxes
            new Modifiers(List.of(
                    "minecraft:wooden_pickaxe",
                    "minecraft:stone_pickaxe"
            ), 3, 0.8, 3),
            new Modifiers(List.of(
                    "minecraft:copper_pickaxe",
                    "minecraft:golden_pickaxe"
            ), 4, 0.8, 3),
            new Modifiers(List.of(
                    "minecraft:iron_pickaxe",
                    "progression_reborn:rose_pickaxe",
                    "legacies_and_legends:molten_pickaxe"
            ), 5, 0.8, 3),
            new Modifiers(List.of(
                    "minecraft:diamond_pickaxe"
            ), 6, 0.8, 3),
            new Modifiers(List.of(
                    "minecraft:netherite_pickaxe",
                    "end_reborn:remnant_pickaxe",
                    "end_reborn:featherzeal_pickaxe"
            ), 7, 0.8, 3),

            // Shovels
            new Modifiers(List.of(
                    "minecraft:wooden_shovel",
                    "minecraft:stone_shovel"
            ), 2, 1, 3),
            new Modifiers(List.of(
                    "minecraft:copper_shovel",
                    "minecraft:golden_shovel"
            ), 3, 1, 3),
            new Modifiers(List.of(
                    "minecraft:iron_shovel",
                    "progression_reborn:rose_shovel",
                    "legacies_and_legends:prospector_shovel"
            ), 4, 1, 3),
            new Modifiers(List.of(
                    "minecraft:diamond_shovel"
            ), 5, 1, 3),
            new Modifiers(List.of(
                    "minecraft:netherite_shovel",
                    "end_reborn:remnant_shovel",
                    "end_reborn:featherzeal_shovel"
            ), 6, 1, 3),

            // Hoes
            new Modifiers(List.of(
                    "minecraft:wooden_hoe",
                    "minecraft:stone_hoe",
                    "legacies_and_legends:withered_hoe"
            ), 1, 2, 3.5),
            new Modifiers(List.of(
                    "minecraft:copper_hoe",
                    "minecraft:golden_hoe"
            ), 1, 2.5, 3.5),
            new Modifiers(List.of(
                    "minecraft:iron_hoe",
                    "progression_reborn:rose_hoe"
            ), 1, 3, 3.5),
            new Modifiers(List.of(
                    "minecraft:diamond_hoe"
            ), 1, 3.5, 3.5),
            new Modifiers(List.of(
                    "minecraft:netherite_hoe",
                    "end_reborn:remnant_hoe",
                    "end_reborn:featherzeal_hoe"
            ), 1, 4, 3.5),

            // Mace
            new Modifiers(List.of(
                    "minecraft:mace"
            ), 6, 0.9, 3),

            // Trident
            new Modifiers(List.of(
                    "minecraft:trident"
            ), 9, 1.6, 3.5),

            // Boomerang
            new Modifiers(List.of(
                    "legacies_and_legends:boomerang"
            ), 4, 3, 2.5),

            // Hook
            new Modifiers(List.of(
                    "legacies_and_legends:hook"
            ), 9, 0.9, 3.5),

            // Katana
            new Modifiers(List.of(
                    "remnants:katana"
            ), 6, 2.4, 3),

            // Knives
            new Modifiers(List.of(
                    "farmersdelight:flint_knife"
            ), 2, 2.8, 2.5),
            new Modifiers(List.of(
                    "farmersdelight:copper_knife",
                    "farmersdelight:golden_knife"
            ), 3, 2.8, 2.5),
            new Modifiers(List.of(
                    "farmersdelight:iron_knife",
                    "farmersknives:rose_knife"
            ), 4, 2.8, 2.5),
            new Modifiers(List.of(
                    "farmersdelight:diamond_knife"
            ), 5, 2.8, 2.5),
            new Modifiers(List.of(
                    "legacies_and_legends:knife"
            ), 5, 3, 2.5),
            new Modifiers(List.of(
                    "farmersdelight:netherite_knife",
                    "farmersknives:remnant_knife",
                    "farmersknives:featherzeal_knife"
            ), 6, 2.8, 2.5)
    );
}