package net.rebel459.combat_reborn.mixin.food;

import net.rebel459.combat_reborn.config.CRConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    @Shadow
    private float exhaustionLevel;

    @Shadow
    private float saturationLevel;

    @Shadow
    private int foodLevel;

    @Shadow
    private int tickTimer;

    @Shadow
    public abstract void addExhaustion(float f);

    @Shadow
    public abstract int getFoodLevel();

    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    private void CR$tick(ServerPlayer serverPlayer, CallbackInfo ci) {
        if (!CRConfig.get.general.hunger.hunger_rework) return;
        ServerLevel serverLevel = serverPlayer.level();

        Difficulty difficulty = serverLevel.getDifficulty();
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        boolean bl = serverLevel.getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION);
        if (bl && this.saturationLevel > 0.0F) {
            ++this.tickTimer;
            int requiredTicks = 20;
            float saturationConsumed = 0.5F;
            if (difficulty == Difficulty.HARD) {
                requiredTicks = 40;
                saturationConsumed = 1F;
            }
            if (difficulty == Difficulty.EASY) {
                saturationConsumed = 0.25F;
            }
            if (this.tickTimer >= requiredTicks) {
                this.saturationLevel = Math.max(this.saturationLevel - saturationConsumed, 0.0F);
                if (serverPlayer.isHurt()) serverPlayer.heal(1F);
                this.tickTimer = 0;
            }
        } else if (bl && this.foodLevel > CRConfig.get.general.hunger.hunger_barrier && serverPlayer.isHurt()) {
            ++this.tickTimer;
            int requiredTicks = 80;
            float exhaustionGained = 4F;
            if (difficulty == Difficulty.HARD) {
                requiredTicks = 160;
            }
            if (difficulty == Difficulty.EASY) {
                exhaustionGained = 2F;
            }
            if (this.tickTimer >= requiredTicks) {
                serverPlayer.heal(1.0F);
                this.addExhaustion(exhaustionGained);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                if (serverPlayer.getHealth() > 10.0F || difficulty == Difficulty.HARD || serverPlayer.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    serverPlayer.hurtServer(serverLevel, serverPlayer.damageSources().starve(), 1.0F);
                }

                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }
        ci.cancel();
    }

    @Inject(method = "addExhaustion", at = @At(value = "HEAD"), cancellable = true)
    private void CR$checkAddExhaustion(float f, CallbackInfo ci) {
        if (!CRConfig.get.general.hunger.hunger_rework || !(this.saturationLevel > 0)) return;
        ci.cancel();
    }

    @Inject(method = "hasEnoughFood", at = @At(value = "HEAD"), cancellable = true)
    private void CR$checkAddExhaustion(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.getFoodLevel() > CRConfig.get.general.hunger.hunger_barrier);
    }
}