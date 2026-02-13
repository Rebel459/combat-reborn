package net.rebel459.combat_reborn.tag;

import net.rebel459.combat_reborn.CombatReborn;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class CREnchantmentTags {

    public static final TagKey<Enchantment> NOT_ON_AXES = register("not_on_axes");

    @NotNull
    private static TagKey<Enchantment> register(@NotNull String path) {
        return TagKey.create(Registries.ENCHANTMENT, CombatReborn.id(path));
    }
}