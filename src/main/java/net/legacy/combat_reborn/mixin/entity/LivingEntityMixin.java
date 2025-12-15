package net.legacy.combat_reborn.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.legacy.combat_reborn.CombatReborn;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CREnchantments;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.legacy.combat_reborn.util.ShieldHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ShieldInfo {

    @Override
    public ShieldInfo getInfo() {
        return this;  // The mixin instance itself implements ShieldInfo
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

    @WrapOperation(
            method = "hurtServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)F"
            )
    )    private float trackShieldDamageBlocked(LivingEntity instance, ServerLevel serverLevel, DamageSource damageSource, float f, Operation<Float> original) {
        float blockedDamage = original.call(instance, serverLevel, damageSource, f);
        if (blockedDamage > 0) {
            LivingEntity entity = LivingEntity.class.cast(this);
            ItemStack stack = entity.getUseItem();
            if (stack.is(CRItemTags.SHIELD) && entity instanceof ShieldInfo shieldInfo) {
                int percentageToIncrease = ShieldHelper.processDamage(entity, stack, f);
                if (damageSource.getWeaponItem() != null && damageSource.getWeaponItem().is(ItemTags.AXES)) percentageToIncrease *= 2;
                if (entity.getTicksUsingItem() <= ShieldHelper.getParryWindow(stack)) percentageToIncrease = (int) (percentageToIncrease / ShieldHelper.getParryBonus(stack));
                shieldInfo.setPercentageDamageAndSync(Math.max(getPercentageDamage() + percentageToIncrease, 0), (ServerPlayer) entity);
                if (getPercentageDamage() >= 100) {
                    stack.getComponents().get(DataComponents.BLOCKS_ATTACKS).disable(serverLevel, entity, 15F, stack);
                    shieldInfo.setPercentageDamageAndSync(0, (ServerPlayer) entity);
                    entity.addTag("should_disable_shield");
                }
            }
        }
        return blockedDamage;
    }

    @Unique int localTick = 0;

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void shieldRecovery(CallbackInfo ci) {
        LivingEntity entity = LivingEntity.class.cast(this);
        if (!(entity instanceof ServerPlayer player)) return;
        this.localTick++;
        if (localTick >= 5) {
            if (entity.getUseItem().is(CRItemTags.SHIELD)) return;
            if (entity instanceof ShieldInfo shieldInfo && shieldInfo.getPercentageDamage() > 0) {
                int recoveryRate = 1;
                shieldInfo.setPercentageDamageAndSync(Math.max(shieldInfo.getPercentageDamage(), 0) - recoveryRate, player);
            }
            localTick = 0;
        }
    }

    @Shadow
    public abstract ItemStack getWeaponItem();

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
        cir.setReturnValue(disableTime);
    }

    @WrapOperation(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float trackShieldBlocking(LivingEntity attacked, ServerLevel serverLevel, DamageSource damageSource, float f, Operation<Float> original) {
        LivingEntity entity = LivingEntity.class.cast(this);
        ItemStack stack = entity.getUseItem();
        int useTicks = entity.getTicksUsingItem();
        if (useTicks <= ShieldHelper.getParryWindow(stack) && stack.is(CRItemTags.SHIELD) && damageSource.getEntity() instanceof LivingEntity attacker) {
            ShieldHelper.onParry(serverLevel, attacker, attacked, stack);
        }
        return original.call(attacked, serverLevel, damageSource, f);
    }
}
