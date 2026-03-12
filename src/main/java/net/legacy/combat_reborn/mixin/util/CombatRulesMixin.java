package net.legacy.combat_reborn.mixin.util;

import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.util.DamageHelper;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CombatRules.class)
public class CombatRulesMixin {

    @Inject(at = @At(value = "HEAD"), method = "getDamageAfterMagicAbsorb", cancellable = true)
    private static void getDamageAfterMagicAbsorb(float damage, float protection, CallbackInfoReturnable<Float> cir) {
        if (!CRConfig.get.general.armor.armor_rebalance) return;
        cir.setReturnValue(DamageHelper.processEnchantedDamage(damage, protection));
    }
}