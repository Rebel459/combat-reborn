package net.legacy.combat_reborn.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.legacy.combat_reborn.CombatReborn;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CREnchantments;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.legacy.combat_reborn.util.ShieldHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ShieldInfo {

    @Unique
    private DamageSource lastBlockedSource;

    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
    private boolean lastBlockedSource(LivingEntity entity, DamageSource damageSource, Operation<Boolean> original) {
        this.lastBlockedSource = damageSource;
        return original.call(entity, damageSource);
    }

    @Inject(method = "blockUsingShield", at = @At(value = "HEAD"))
    private void handleParrying(LivingEntity attacker, CallbackInfo ci) {
        if (!CRConfig.get().general.combat.shield_overhaul) return;
        LivingEntity attacked = LivingEntity.class.cast(this);
        DamageSource damageSource = this.lastBlockedSource;
        ItemStack stack = attacked.getUseItem();
        int useTicks = attacked.getTicksUsingItem();
        if (useTicks <= ShieldHelper.getParryWindow(stack) && damageSource.getEntity() != null && ShieldHelper.canBeParried(damageSource)) {
            ServerLevel serverLevel = attacked.level().getServer().getLevel(attacked.level().dimension());
            ShieldHelper.onParry(serverLevel, attacker, attacked, stack);
        }
    }

    @Unique int localTick = 0;
    @Unique int recoveryDelay = 0;
    @Unique int hurtOrBlockedTime = 0;

    @Override
    public ShieldInfo getInfo() {
        return this;
    }

    @Unique
    private Optional<Integer> percentageDamage = Optional.of(0);

    @Override
    public int getPercentageDamage() {
        return this.percentageDamage.get();
    }

    @Override
    public void setPercentageDamage(int value) {
        this.percentageDamage = Optional.of(value);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void write(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putInt(CombatReborn.MOD_ID + ":percentage_damage", this.percentageDamage.get());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(CompoundTag compoundTag, CallbackInfo ci) {
        if (compoundTag.contains(CombatReborn.MOD_ID + ":percentage_damage")) {
            this.percentageDamage = Optional.of(compoundTag.getInt(CombatReborn.MOD_ID + ":percentage_damage"));
        }
    }

    @Unique
    DamageSource damageSource;

    @Inject(method = "hurt", at = @At(value = "HEAD"))
    private void getDamageSource(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        this.damageSource = damageSource;
    }

    @WrapOperation(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V"
            )
    )
    private void trackShieldDamageBlocked(ServerPlayer player, ResourceLocation resourceLocation, int i, Operation<Void> original) {
        float blockedDamage = i / 10F;
        if (blockedDamage > 0 && CRConfig.get().general.combat.shield_overhaul && this.hurtOrBlockedTime == 0) {
            LivingEntity entity = LivingEntity.class.cast(this);
            ItemStack stack = entity.getUseItem();
            if (stack.is(CRItemTags.SHIELD) && entity instanceof ShieldInfo shieldInfo) {
                int percentageToIncrease = ShieldHelper.processDamage(stack, blockedDamage);
                if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) percentageToIncrease /= 2;
                if (damageSource.getWeaponItem() != null && damageSource.getWeaponItem().is(ItemTags.AXES)) percentageToIncrease *= 2;
                if (entity.getTicksUsingItem() <= ShieldHelper.getParryWindow(stack) && ShieldHelper.canBeParried(damageSource)) percentageToIncrease = (int) (percentageToIncrease / ShieldHelper.getParryBonus(stack));
                shieldInfo.setPercentageDamageAndSync(Math.max(shieldInfo.getPercentageDamage() + percentageToIncrease, 0), (ServerPlayer) entity);
                this.hurtOrBlockedTime = 10;
                this.recoveryDelay = 100;
                if (shieldInfo.getPercentageDamage() >= 100) {
                    float disableTime = 15F;
                    if (CRConfig.get().general.integrations.enderscape && stack.is(CRItemTags.RUBBLE_SHIELD)) disableTime = 10F;
                    if (stack.getItem() instanceof ShieldItem) {
                        player.getCooldowns().addCooldown(Items.SHIELD, (int) (disableTime * 20));
                        player.stopUsingItem();
                        player.level().broadcastEntityEvent(player, (byte) 30);
                    }
                    shieldInfo.setPercentageDamageAndSync(0, (ServerPlayer) entity);
                    entity.addTag("should_disable_shield");
                }
            }
        }
    }

    @Shadow
    public int hurtTime;

    @Inject(method = "baseTick", at = @At(value = "HEAD"))
    private void decreaseHurtOrBlockedTime(CallbackInfo ci) {
        if (this.hurtOrBlockedTime > 0) {
            this.hurtOrBlockedTime--;
        }
    }
    @Inject(method = "handleDamageEvent", at = @At(value = "HEAD"))
    private void setHurtOrBlockedTime(CallbackInfo ci) {
        this.hurtOrBlockedTime = this.hurtTime;
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void passiveShieldRecovery(CallbackInfo ci) {
        LivingEntity entity = LivingEntity.class.cast(this);
        if (!(entity instanceof ServerPlayer player) || !CRConfig.get().general.combat.shield_overhaul) return;
        if (player.getTags().contains("stop_shield_recharge")) {
            int disableTime = 3;
            if (player.getTags().contains("stop_shield_recharge_1")) {
                disableTime = 4;
                player.removeTag("stop_shield_recharge_1");
            }
            if (player.getTags().contains("stop_shield_recharge_2")) {
                disableTime = 5;
                player.removeTag("stop_shield_recharge_2");
            }
            if (player.getTags().contains("stop_shield_recharge_3")) {
                disableTime = 6;
                player.removeTag("stop_shield_recharge_3");
            }
            this.recoveryDelay = Math.min(this.recoveryDelay + disableTime * 20, 100 + disableTime * 20);
            player.removeTag("stop_shield_recharge");
        }
        this.localTick++;
        if (localTick >= 5) {
            if (this.recoveryDelay == 0) {
                if (entity instanceof ShieldInfo shieldInfo && shieldInfo.getPercentageDamage() > 0) {
                    shieldInfo.setPercentageDamageAndSync(Math.max(shieldInfo.getPercentageDamage(), 0) - 1, player);
                }
            }
            else {
                this.recoveryDelay = Math.max(this.recoveryDelay - this.localTick, 0);
            }
            localTick = 0;
        }
    }

    @ModifyConstant(method = "isBlocking", constant = @Constant(intValue = 5))
    private int removeShieldDelay(int original) {
        return CRConfig.get().general.combat.shield_delay;
    }

    @ModifyVariable(method = "hurt", at = @At(value = "HEAD"), index = 2, argsOnly = true)
    private float activeShieldRecovery(float value) {
        if (CRConfig.get().general.combat.shield_overhaul && this.damageSource.getEntity() instanceof Player player && player instanceof ShieldInfo shieldInfo && shieldInfo.getPercentageDamage() > 0 && this.hurtOrBlockedTime == 0) {
            float restoration = value / 2;
            ItemStack stack = player.getWeaponItem();
            int dueling = CREnchantments.getLevel(stack, CREnchantments.DUELING);
            restoration = restoration * (1 + dueling / 3F);
            if (player instanceof ServerPlayer serverPlayer) shieldInfo.setPercentageDamageAndSync((int) Math.max(shieldInfo.getPercentageDamage() - restoration, 0), serverPlayer);
        }
        return value;
    }
}
