package net.rebel459.combat_reborn.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.rebel459.combat_reborn.CombatReborn;
import net.rebel459.unified.platform.UnifiedRegistries;

public class CRAttributes {

    public static UnifiedRegistries.DeferredRegistry ATTRIBUTES = UnifiedRegistries.DeferredRegistry.create(CombatReborn.MOD_ID, BuiltInRegistries.ATTRIBUTE);

    public static final Holder<Attribute> CRITICAL_DAMAGE_BOOST = ATTRIBUTES.registerHolder("critical_damage_boost", () -> new Attribute("attribute." + CombatReborn.MOD_ID + ".critical_damage_boost", 1.5F).setSyncable(true));
    public static final Holder<Attribute> CHARGE_ATTACK_BOOST = ATTRIBUTES.registerHolder("charge_damage_boost", () -> new Attribute("attribute." + CombatReborn.MOD_ID + ".charge_damage_boost", 0F).setSyncable(true));

    public static void init() {}
}