package net.legacy.combat_reborn.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.legacy.combat_reborn.CombatReborn;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;
import java.util.List;

@Config(name = CombatReborn.MOD_ID + "/" + "weapons")
public class CRWeaponConfig implements ConfigData {

    public static class Modifiers {
        public List<String> ids;
        public double damage;
        public double speed;
        public double reach;
        public List<Triple<String, Double, AttributeModifier.Operation>> attributes;

        Modifiers() {
            this(List.of("example:example"), 1, 4, 3, List.of());
        }
        Modifiers(List<String> ids, double damage, double speed, double reach, List<Triple<String, Double, AttributeModifier.Operation>> attributes) {
            this.ids = ids;
            this.damage = damage;
            this.speed = speed;
            this.reach = reach;
            this.attributes = attributes;
        }
    }

    public List<Modifiers> sets = Arrays.asList(

            // Swords
            new Modifiers(List.of(
                    "minecraft:wooden_sword",
                    "minecraft:stone_sword"
            ), 4, 2, 3, List.of()),
            new Modifiers(List.of(
                    "progression_reborn:copper_sword",
                    "minecraft:golden_sword"
            ), 5, 2, 3, List.of()),
            new Modifiers(List.of(
                    "minecraft:iron_sword",
                    "progression_reborn:rose_sword"
            ), 6, 2, 3, List.of()),
            new Modifiers(List.of(
                    "minecraft:diamond_sword"
            ), 7, 2, 3, List.of()),
            new Modifiers(List.of(
                    "minecraft:netherite_sword",
                    "end_reborn:remnant_sword",
                    "end_reborn:featherzeal_sword"
            ), 8, 2, 3, List.of()),

            // Axes
            new Modifiers(List.of(
                    "minecraft:wooden_axe",
                    "minecraft:stone_axe"
            ), 7, 0.8, 3, List.of()),
            new Modifiers(List.of(
                    "progression_reborn:copper_axe",
                    "minecraft:golden_axe"
            ), 7, 1, 3, List.of()),
            new Modifiers(List.of(
                    "minecraft:iron_axe",
                    "progression_reborn:rose_axe"
            ), 8, 1, 3, List.of()),
            new Modifiers(List.of(
                    "minecraft:diamond_axe"
            ), 8, 1.2, 3, List.of()),
            new Modifiers(List.of(
                    "minecraft:netherite_axe",
                    "end_reborn:remnant_axe",
                    "end_reborn:featherzeal_axe"
            ), 9, 1.2, 3, List.of()),

            // Pickaxes
            new Modifiers(List.of(
                    "minecraft:wooden_pickaxe",
                    "minecraft:stone_pickaxe"
            ), 3, 0.8, 3, List.of()),
            new Modifiers(List.of(
                    "progression_reborn:copper_pickaxe",
                    "minecraft:golden_pickaxe"
            ), 4, 0.8, 3, List.of()),
            new Modifiers(List.of(
                    "minecraft:iron_pickaxe",
                    "progression_reborn:rose_pickaxe"
            ), 5, 0.8, 3, List.of()),
            new Modifiers(List.of(
                    "minecraft:diamond_pickaxe"
            ), 6, 0.8, 3, List.of()),
            new Modifiers(List.of(
                    "minecraft:netherite_pickaxe",
                    "end_reborn:remnant_pickaxe",
                    "end_reborn:featherzeal_pickaxe"
            ), 7, 0.8, 3, List.of()),

            // Shovels
            new Modifiers(List.of(
                    "minecraft:wooden_shovel",
                    "minecraft:stone_shovel"
            ), 2, 1, 3, List.of()),
            new Modifiers(List.of(
                    "progression_reborn:copper_shovel",
                    "minecraft:golden_shovel"
            ), 3, 1, 3, List.of()),
            new Modifiers(List.of(
                    "minecraft:iron_shovel",
                    "progression_reborn:rose_shovel"
            ), 4, 1, 3, List.of()),
            new Modifiers(List.of(
                    "minecraft:diamond_shovel"
            ), 5, 1, 3, List.of()),
            new Modifiers(List.of(
                    "minecraft:netherite_shovel",
                    "end_reborn:remnant_shovel",
                    "end_reborn:featherzeal_shovel"
            ), 6, 1, 3, List.of()),

            // Hoes
            new Modifiers(List.of(
                    "minecraft:wooden_hoe",
                    "minecraft:stone_hoe"
            ), 1, 2, 3.5, List.of()),
            new Modifiers(List.of(
                    "progression_reborn:copper_hoe",
                    "minecraft:golden_hoe"
            ), 1, 2.5, 3.5, List.of()),
            new Modifiers(List.of(
                    "minecraft:iron_hoe",
                    "progression_reborn:rose_hoe"
            ), 1, 3, 3.5, List.of()),
            new Modifiers(List.of(
                    "minecraft:diamond_hoe"
            ), 1, 3.5, 3.5, List.of()),
            new Modifiers(List.of(
                    "minecraft:netherite_hoe",
                    "end_reborn:remnant_hoe",
                    "end_reborn:featherzeal_hoe"
            ), 1, 4, 3.5, List.of()),

            // Mace
            new Modifiers(List.of(
                    "minecraft:mace"
            ), 6, 0.9, 3, List.of()),

            // Trident
            new Modifiers(List.of(
                    "minecraft:trident"
            ), 9, 1.6, 3.5, List.of()),

            // Katana
            new Modifiers(List.of(
                    "remnants:katana"
            ), 6, 2.4, 3, List.of()),

            // Knives
            new Modifiers(List.of(
                    "farmersdelight:flint_knife"
            ), 2, 2.8, 2.5, List.of()),
            new Modifiers(List.of(
                    "farmersknives:copper_knife",
                    "farmersdelight:golden_knife"
            ), 3, 2.8, 2.5, List.of()),
            new Modifiers(List.of(
                    "farmersdelight:iron_knife",
                    "farmersknives:rose_knife"
            ), 4, 2.8, 2.5, List.of()),
            new Modifiers(List.of(
                    "farmersdelight:diamond_knife"
            ), 5, 2.8, 2.5, List.of()),
            new Modifiers(List.of(
                    "farmersdelight:netherite_knife",
                    "farmersknives:remnant_knife",
                    "farmersknives:featherzeal_knife"
            ), 6, 2.8, 2.5, List.of())
    );
}