package net.rebel459.combat_reborn.mixin.util;

import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.util.DamageHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CombatRules.class)
public class CombatRulesMixin {

    @Inject(at = @At(value = "HEAD"), method = "getDamageAfterAbsorb", cancellable = true)
    private static void getDamageAfterAbsorb(LivingEntity entity, float damage, DamageSource source, float defense, float toughness, CallbackInfoReturnable<Float> cir) {
        if (!CRConfig.getGeneral().armor.armor_rebalance) return;
        cir.setReturnValue(DamageHelper.processDamage(entity, damage, source, defense, toughness));
    }

    @Inject(at = @At(value = "HEAD"), method = "getDamageAfterMagicAbsorb", cancellable = true)
    private static void getDamageAfterMagicAbsorb(float damage, float protection, CallbackInfoReturnable<Float> cir) {
        if (!CRConfig.getGeneral().armor.armor_rebalance) return;
        cir.setReturnValue(DamageHelper.processEnchantedDamage(damage, protection));
    }
}