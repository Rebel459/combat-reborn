package net.legacy.combat_reborn.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.legacy.combat_reborn.config.CRConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow
    public abstract void causeFoodExhaustion(float f);

    @Shadow
    public abstract FoodData getFoodData();

    @WrapOperation(
            method = "actuallyHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"
            )
    )
    private void CR$cancelHurtExhaustion(Player player, float exhaustion, Operation<Void> original) {}

    @Inject(method = "actuallyHurt", at = @At(value = "TAIL"))
    private void CR$addHurtExhaustion(ServerLevel serverLevel, DamageSource damageSource, float f, CallbackInfo ci) {
        if (!CRConfig.get().general.food.hunger_rework) return;
        Player player = Player.class.cast(this);
        Difficulty difficulty = serverLevel.getDifficulty();
        float multiplier = 0.75F;
        if (difficulty == Difficulty.EASY) multiplier = 0.5F;
        if (difficulty == Difficulty.HARD) multiplier = 1F;
        float aboveBarrier = player.getFoodData().getFoodLevel() - CRConfig.get().general.food.hunger_barrier;
        float exhaustion = Math.max(f - aboveBarrier, 0F);
        this.causeFoodExhaustion(exhaustion * multiplier);
        this.getFoodData().setSaturation(Math.max(this.getFoodData().getSaturationLevel() - f / 2, 0));
    }

    @Inject(method = "actuallyHurt", at = @At(value = "TAIL"))
    private void cancelConsumption(ServerLevel level, DamageSource damageSource, float amount, CallbackInfo info) {
        if (!CRConfig.get().general.consumables.damage_interruptions || damageSource.getEntity() == null) return;
        Player player = Player.class.cast(this);
        ItemStack stack = player.getUseItem();
        if (stack.getComponents().has(DataComponents.FOOD) || stack.getComponents().has(DataComponents.CONSUMABLE)) player.stopUsingItem();
    }

    @WrapOperation(
            method = "actuallyHurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setHealth(F)V")
    )
    private void handleKnockbackOnly(Player instance, float v, Operation<Void> original) {
        Player player = Player.class.cast(this);
        if (player.getTags().contains("knockback_only")) {
            player.removeTag("knockback_only");
            original.call(instance, Math.min(player.getMaxHealth(), v + 1F));
        }
        else {
            original.call(instance, v);
        }
    }

    @WrapOperation(
            method = "causeFoodExhaustion",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"
            )
    )    private void reduceExhaustionWhenHealing(FoodData foodData, float f, Operation<Void> original) {
        Player player = Player.class.cast(this);
        if (!CRConfig.get().general.food.hunger_rework || player.getHealth() >= player.getMaxHealth() || foodData.getFoodLevel() <= CRConfig.get().general.food.hunger_barrier) {
            original.call(foodData, f);
            return;
        }
        original.call(foodData, f / 4);;
    }
}