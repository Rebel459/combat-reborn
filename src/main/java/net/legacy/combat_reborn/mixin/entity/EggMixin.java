package net.legacy.combat_reborn.mixin.entity;

import net.legacy.combat_reborn.config.CRConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEgg.class)
public abstract class EggMixin {

    @Inject(method = "onHitEntity", at = @At(value = "TAIL"))
    private void cancelConsumption(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (!CRConfig.get.consumables.knockback_throwables) return;
        ThrownEgg egg = ThrownEgg.class.cast(this);
        knockback(entityHitResult.getEntity().asLivingEntity(), egg.damageSources().generic(), 0.2F);
    }

    @Unique
    private static void knockback(LivingEntity entity, DamageSource source, float strength) {
        double d = 0.0;
        double e = 0.0;
        if (source.getSourcePosition() != null) {
            d = source.getSourcePosition().x() - entity.getX();
            e = source.getSourcePosition().z() - entity.getZ();
        }

        entity.knockback(strength, d, e);
    }
}