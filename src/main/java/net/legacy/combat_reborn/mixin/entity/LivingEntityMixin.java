package net.legacy.combat_reborn.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.legacy.combat_reborn.CombatReborn;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CREnchantments;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.legacy.combat_reborn.util.BlockedSourceInterface;
import net.legacy.combat_reborn.util.ShieldHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ShieldInfo, BlockedSourceInterface {

    @Unique
    private DamageSource lastBlockedSource;

    @Override
    @Nullable
    public DamageSource getLastBlockedSource() {
        return this.lastBlockedSource;
    }

    @Override
    public void setLastBlockedSource(DamageSource source) {
        this.lastBlockedSource = source;
    }

    @WrapOperation(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float lastBlockedSource(LivingEntity attacked, ServerLevel serverLevel, DamageSource damageSource, float f, Operation<Float> original) {
        this.setLastBlockedSource(damageSource);
        return original.call(attacked, serverLevel, damageSource, f);
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
    private void write(ValueOutput valueOutput, CallbackInfo ci) {
        valueOutput.putInt(CombatReborn.MOD_ID + ":percentage_damage", this.percentageDamage.get());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(ValueInput valueInput, CallbackInfo ci) {
        if (valueInput.contains(CombatReborn.MOD_ID + ":percentage_damage")) {
            this.percentageDamage = valueInput.getInt(CombatReborn.MOD_ID + ":percentage_damage");
        }
    }

    @Unique
    DamageSource damageSource;

    @Inject(method = "hurtServer", at = @At(value = "HEAD"))
    private void getDamageSource(ServerLevel level, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.damageSource = damageSource;
    }

    @WrapOperation(
            method = "hurtServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)F"
            )
    )
    private float trackShieldDamageBlocked(LivingEntity instance, ServerLevel serverLevel, DamageSource damageSource, float f, Operation<Float> original) {
        float blockedDamage = original.call(instance, serverLevel, damageSource, f);
        if (blockedDamage > 0 && CRConfig.get.combat.shield_overhaul && this.hurtOrBlockedTime == 0) {
            LivingEntity entity = LivingEntity.class.cast(this);
            ItemStack stack = entity.getUseItem();
            if (stack.is(CRItemTags.SHIELD) && entity instanceof ShieldInfo shieldInfo) {
                int percentageToIncrease = ShieldHelper.processDamage(stack, f);
                if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) percentageToIncrease /= 2;
                if (entity.getTicksUsingItem() <= ShieldHelper.getParryWindow(stack) && ShieldHelper.canBeParried(damageSource)) percentageToIncrease = (int) (percentageToIncrease / ShieldHelper.getParryBonus(stack));
                shieldInfo.setPercentageDamageAndSync(Math.max(shieldInfo.getPercentageDamage() + percentageToIncrease, 0), (ServerPlayer) entity);
                this.hurtOrBlockedTime = 10;
                this.recoveryDelay = 100;
                if (shieldInfo.getPercentageDamage() >= 100) {
                    float disableTime = 15F;
                    if (CRConfig.get.integrations.enderscape && stack.is(CRItemTags.RUBBLE_SHIELD)) disableTime = 10F;
                    stack.getComponents().get(DataComponents.BLOCKS_ATTACKS).disable(serverLevel, entity, disableTime, stack);
                    shieldInfo.setPercentageDamageAndSync(0, (ServerPlayer) entity);
                    entity.addTag("should_disable_shield");
                }
            }
        }
        return blockedDamage;
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void passiveShieldRecovery(CallbackInfo ci) {
        LivingEntity entity = LivingEntity.class.cast(this);
        if (!(entity instanceof ServerPlayer player) || !CRConfig.get.combat.shield_overhaul) return;
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

    @Shadow
    public abstract ItemStack getWeaponItem();

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

    @Inject(method = "getSecondsToDisableBlocking", at = @At(value = "TAIL"), cancellable = true)
    private void cleaving(CallbackInfoReturnable<Float> cir) {
        float disableTime = cir.getReturnValue();
        if (CRConfig.get.combat.shield_overhaul) {
            if (disableTime >= 3F) disableTime -= 2F;
        }
        if (disableTime <= 0) return;
        ItemStack stack = this.getWeaponItem();
        int level = CREnchantments.getLevel(stack, CREnchantments.CLEAVING);
        if (level > 0) {
            disableTime = disableTime + level;
        }
        if (CRConfig.get.combat.shield_overhaul) this.recoveryDelay = (int) Math.min(this.recoveryDelay + disableTime * 20, 100 + disableTime * 20);
        cir.setReturnValue(disableTime);
    }

    @Inject(method = "getItemBlockingWith", at = @At("HEAD"), cancellable = true)
    private void configureShieldDelay(CallbackInfoReturnable<ItemStack> cir) {
        LivingEntity entity = LivingEntity.class.cast(this);

        if (!entity.isUsingItem()) return;
        ItemStack stack = entity.getUseItem();
        if (!stack.is(CRItemTags.SHIELD)) return;
        BlocksAttacks blocksAttacks = stack.get(DataComponents.BLOCKS_ATTACKS);
        if (blocksAttacks != null) {
            int i = stack.getUseDuration(entity) - entity.getUseItemRemainingTicks();
            if (i >= CRConfig.get.combat.shield_delay) {
                cir.setReturnValue(stack);
            }
        }
    }

    @ModifyVariable(method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "HEAD"), index = 3, argsOnly = true)
    private float activeShieldRecovery(float value) {
        if (CRConfig.get.combat.shield_overhaul && this.damageSource.getEntity() instanceof Player player && player instanceof ShieldInfo shieldInfo && shieldInfo.getPercentageDamage() > 0 && this.hurtOrBlockedTime == 0) {
            float restoration = value / 2;
            ItemStack stack = player.getWeaponItem();
            int dueling = CREnchantments.getLevel(stack, CREnchantments.DUELING);
            restoration = restoration * (1 + dueling / 3F);
            shieldInfo.setPercentageDamageAndSync((int) Math.max(shieldInfo.getPercentageDamage() - restoration, 0), (ServerPlayer) player);
        }
        return value;
    }
}
