package net.legacy.combat_reborn.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.legacy.combat_reborn.config.CRConfig;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @WrapOperation(
            method = "checkMovementStatistics",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V",
                    ordinal = 4
            )
    )
    private void CR$noExhaustionCrouching(ServerPlayer instance, float v, Operation<Void> original) {
        if (!CRConfig.get.food.hunger_rework) original.call(instance, v);
    }

    @WrapOperation(
            method = "checkMovementStatistics",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V",
                    ordinal = 5
            )
    )
    private void CR$noExhaustionWalking(ServerPlayer instance, float v, Operation<Void> original) {
        if (!CRConfig.get.food.hunger_rework) original.call(instance, v);
    }

    @WrapOperation(
            method = "jumpFromGround",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V",
                    ordinal = 1
            )
    )
    private void CR$noExhaustionJumping(ServerPlayer instance, float v, Operation<Void> original) {
        if (!CRConfig.get.food.hunger_rework) original.call(instance, v);
    }
}