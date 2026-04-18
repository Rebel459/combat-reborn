package net.rebel459.combat_reborn.mixin.entity;

import net.rebel459.combat_reborn.config.CRConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.rebel459.combat_reborn.util.CombatBooleanInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Snowball.class)
public abstract class SnowballMixin {

    @Redirect(
            method = "onHitEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"
            )
    )
    private void cancelConsumption(Entity entity, DamageSource damageSource, float f) {
        if (!CRConfig.getGeneral().misc.knockback_throwables) return;
        f = 0F;
        if (entity instanceof Player player) {
            if (player instanceof CombatBooleanInterface booleans) booleans.setKnockbackOnly(true);
            f = 1F;
        }
        entity.hurt(damageSource, f);
    }
}