package net.legacy.combat_reborn.mixin.food;

import net.legacy.combat_reborn.config.CRConfig;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    private void CR$tick(Player player, CallbackInfo ci) {
        if (!CRConfig.get.food.hunger_rework) return;
        Level level = player.level();

        Difficulty difficulty = level.getDifficulty();
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        boolean bl = player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        if (bl && this.saturationLevel > 0.0F) {
            ++this.tickTimer;
            int requiredTicks = 20;
            float saturationConsumed = 0.5F;
            if (difficulty == Difficulty.HARD) {
                requiredTicks = 40;
            }
            if (difficulty == Difficulty.EASY) {
                saturationConsumed = 0.25F;
            }
            if (this.tickTimer >= requiredTicks) {
                this.saturationLevel = Math.max(this.saturationLevel - saturationConsumed, 0.0F);
                if (player.isHurt()) player.heal(1F);
                this.tickTimer = 0;
            }
        } else if (bl && this.foodLevel > 6 && player.isHurt()) {
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
                player.heal(1.0F);
                this.addExhaustion(exhaustionGained);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.tickTimer;
            if (this.tickTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.hurt(player.damageSources().starve(), 1.0F);
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
        if (!CRConfig.get.food.hunger_rework || !(this.saturationLevel > 0)) return;
        ci.cancel();
    }
}