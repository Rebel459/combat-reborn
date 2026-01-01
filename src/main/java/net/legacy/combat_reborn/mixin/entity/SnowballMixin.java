package net.legacy.combat_reborn.mixin.entity;

import net.legacy.combat_reborn.config.CRConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Snowball.class)
public abstract class SnowballMixin {

    @Inject(method = "onHitEntity", at = @At(value = "TAIL"))
    private void cancelConsumption(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (!CRConfig.get().general.consumables.knockback_throwables) return;
        Snowball snowball = Snowball.class.cast(this);
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof Blaze) return;
        float f = 0F;
        if (entity instanceof Player player) {
            player.addTag("knockback_only");
            f = 1F;
        }
        entity.hurt(snowball.damageSources().generic(), f);
    }
}