package net.legacy.combat_reborn.mixin.entity;

import net.legacy.combat_reborn.config.CRConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEgg;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThrownEgg.class)
public abstract class EggMixin {

    @Redirect(
            method = "onHitEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean cancelConsumption(Entity entity, DamageSource damageSource, float f) {
        if (!CRConfig.get.general.misc.knockback_throwables) return false;
        f = 0F;
        if (entity instanceof Player player) {
            player.addTag("knockback_only");
            f = 1F;
        }
        return entity.hurt(damageSource, f);
    }
}