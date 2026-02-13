package net.rebel459.combat_reborn.sound;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.rebel459.combat_reborn.CombatReborn;
import net.rebel459.unified.platform.UnifiedRegistries;

import java.util.function.Supplier;

public class CRSounds {

    public static UnifiedRegistries.SoundEvents SOUNDS = UnifiedRegistries.SoundEvents.create(CombatReborn.MOD_ID);

	public static final Supplier<SoundEvent> SHIELD_PARRY = SOUNDS.register("item.shield.parry");

	public static void init() {}
}