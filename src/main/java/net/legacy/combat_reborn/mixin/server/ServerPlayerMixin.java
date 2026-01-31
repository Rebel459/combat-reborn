package net.legacy.combat_reborn.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.legacy.combat_reborn.config.CRConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    // Reduced exhaustion

    @Unique
    public void reduceExhaustionPerDifficulty(ServerPlayer instance, float v, Operation<Void> original) {
        Difficulty difficulty = instance.level().getDifficulty();
        float multiplier = 0.5F;
        if (difficulty == Difficulty.EASY) multiplier = 0.25F;
        if (difficulty == Difficulty.HARD) multiplier = 0.75F;
        original.call(instance, v * multiplier);
    }

    @WrapOperation(
            method = "checkMovementStatistics",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V",
                    ordinal = 0
            )
    )
    private void CR$reducedExhaustionSwimming(ServerPlayer instance, float v, Operation<Void> original) {
        if (!CRConfig.get.general.hunger.hunger_rework) original.call(instance, v);
        else reduceExhaustionPerDifficulty(instance, v, original);
    }
    @WrapOperation(
            method = "checkMovementStatistics",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V",
                    ordinal = 1
            )
    )
    private void CR$reducedExhaustionUnderwater(ServerPlayer instance, float v, Operation<Void> original) {
        if (!CRConfig.get.general.hunger.hunger_rework) original.call(instance, v);
        else reduceExhaustionPerDifficulty(instance, v, original);
    }
    @WrapOperation(
            method = "checkMovementStatistics",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V",
                    ordinal = 2
            )
    )
    private void CR$reducedExhaustionWater(ServerPlayer instance, float v, Operation<Void> original) {
        if (!CRConfig.get.general.hunger.hunger_rework) original.call(instance, v);
        else reduceExhaustionPerDifficulty(instance, v, original);
    }
    @WrapOperation(
            method = "checkMovementStatistics",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V",
                    ordinal = 3
            )
    )
    private void CR$reducedExhaustionSprinting(ServerPlayer instance, float v, Operation<Void> original) {
        if (!CRConfig.get.general.hunger.hunger_rework) original.call(instance, v);
        else reduceExhaustionPerDifficulty(instance, v, original);
    }
    
    // No exhaustion

    @WrapOperation(
            method = "checkMovementStatistics",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V",
                    ordinal = 4
            )
    )
    private void CR$noExhaustionCrouching(ServerPlayer instance, float v, Operation<Void> original) {
        if (!CRConfig.get.general.hunger.hunger_rework) original.call(instance, v);
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
        if (!CRConfig.get.general.hunger.hunger_rework) original.call(instance, v);
    }
}