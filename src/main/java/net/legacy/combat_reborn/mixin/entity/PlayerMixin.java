package net.legacy.combat_reborn.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CREnchantments;
import net.legacy.combat_reborn.util.ShieldHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

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
    private void CR$addHurtExhaustion(DamageSource damageSource, float f, CallbackInfo ci) {
        if (!CRConfig.get.general.hunger.hunger_rework) return;
        Player player = Player.class.cast(this);
        Difficulty difficulty = player.level().getDifficulty();
        float multiplier = 0.75F;
        if (difficulty == Difficulty.EASY) multiplier = 0.5F;
        if (difficulty == Difficulty.HARD) multiplier = 1F;
        float aboveBarrier = player.getFoodData().getFoodLevel() - 6;
        float exhaustion = Math.max(f - aboveBarrier, 0F);
        this.causeFoodExhaustion(exhaustion * multiplier);
        this.getFoodData().setSaturation(Math.max(this.getFoodData().getSaturationLevel() - f / 2, 0));
    }

    @Inject(method = "actuallyHurt", at = @At(value = "TAIL"))
    private void cancelConsumption(DamageSource damageSource, float f, CallbackInfo ci) {
        if (!CRConfig.get.general.misc.damage_interruptions || damageSource.getEntity() == null) return;
        Player player = Player.class.cast(this);
        ItemStack stack = player.getUseItem();
        if (stack.getComponents().has(DataComponents.FOOD) || stack.getComponents().has(DataComponents.POTION_CONTENTS)) player.stopUsingItem();
    }

    @WrapOperation(
            method = "actuallyHurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F")
    )
    private float handleKnockbackOnly(Player instance, DamageSource damageSource, float v, Operation<Float> original) {
        Player player = Player.class.cast(this);
        if (player.getTags().contains("knockback_only")) {
            original.call(instance, damageSource, Math.min(v - 1F, 0F));
        }
        else {
            original.call(instance, damageSource, v);
        }
        return v;
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
        if (!CRConfig.get.general.hunger.hunger_rework || player.getHealth() >= player.getMaxHealth() || foodData.getFoodLevel() <= 6) {
            original.call(foodData, f);
            return;
        }
        original.call(foodData, f / 4);;
    }

    @Shadow
    public abstract ItemStack getWeaponItem();

    @Shadow
    public abstract ItemCooldowns getCooldowns();

    @Inject(method = "disableShield", at = @At(value = "HEAD"), cancellable = true)
    private void handleDisabling(CallbackInfo ci) {
        Player player = Player.class.cast(this);
        float disableDuration = 5F;
        if (CRConfig.get.general.shields.shield_overhaul) {
            disableDuration -= 2F;
        }
        ItemStack stack = this.getWeaponItem();
        int cleaving = CREnchantments.getLevel(stack, CREnchantments.CLEAVING);
        if (cleaving > 0) {
            disableDuration += cleaving;
        }
        player.addTag("stop_shield_recharge");
        if (CRConfig.get.general.shields.shield_overhaul && cleaving == 1) player.addTag("stop_shield_recharge_1");
        if (CRConfig.get.general.shields.shield_overhaul && cleaving == 2) player.addTag("stop_shield_recharge_2");
        if (CRConfig.get.general.shields.shield_overhaul && cleaving == 3) player.addTag("stop_shield_recharge_3");

        if (player.getTags().contains("should_disable_shield")) {
            player.removeTag("should_disable_shield");
            return;
        }
        int parry = CREnchantments.getLevel(stack, CREnchantments.PARRY);
        boolean shouldContinue = CRConfig.get.general.shields.shield_overhaul;
        if (parry > 0) {
            if (new Random().nextInt(1, 5) <= parry && player.getTicksUsingItem() < ShieldHelper.getParryWindow(stack)) {
                shouldContinue = false;
            }
        }
        if (shouldContinue) {
            if (player instanceof ShieldInfo shieldInfo) {
                int percentageToIncrease = ShieldHelper.processDamage(stack, disableDuration * 5F);
                shieldInfo.setPercentageDamageAndSync(Math.max(shieldInfo.getPercentageDamage() + percentageToIncrease, 0), (ServerPlayer) player);
                if (shieldInfo.getPercentageDamage() >= 100) {
                    disableDuration = ShieldHelper.getDisableDuration(stack);
                    int disableTicks = (int) (disableDuration * 20);
                    player.getCooldowns().addCooldown(stack.getItem(), disableTicks);
                    shieldInfo.setPercentageDamageAndSync(0, (ServerPlayer) player);
                    player.stopUsingItem();
                    player.level().broadcastEntityEvent(player, (byte) 30);
                    ci.cancel();
                    return;
                }
            }
        }

        this.getCooldowns().addCooldown(Items.SHIELD, (int) (disableDuration * 20));
        player.stopUsingItem();
        player.level().broadcastEntityEvent(player, (byte) 30);

        ci.cancel();
    }

    @Unique
    public void reduceExhaustionPerDifficulty(Player instance, float f, Operation<Void> original) {
        Difficulty difficulty = instance.level().getDifficulty();
        float multiplier = 0.5F;
        if (difficulty == Difficulty.EASY) multiplier = 0.25F;
        if (difficulty == Difficulty.HARD) multiplier = 0.75F;
        original.call(instance, f * multiplier);
    }

    @WrapOperation(
            method = "jumpFromGround",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V",
                    ordinal = 0
            )
    )
    private void CR$reducedExhaustionSprintJumping(Player instance, float f, Operation<Void> original) {
        if (!CRConfig.get.general.hunger.hunger_rework) original.call(instance, f);
        else reduceExhaustionPerDifficulty(instance, f, original);
    }

    @WrapOperation(
            method = "jumpFromGround",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V",
                    ordinal = 1
            )
    )
    private void CR$noExhaustionJumping(Player instance, float f, Operation<Void> original) {
        if (!CRConfig.get.general.hunger.hunger_rework) original.call(instance, f);
    }
}