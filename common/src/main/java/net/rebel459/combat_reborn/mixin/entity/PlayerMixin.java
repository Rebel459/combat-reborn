package net.rebel459.combat_reborn.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.registry.CRAttributes;
import net.rebel459.combat_reborn.registry.CRDataComponents;
import net.rebel459.combat_reborn.util.*;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.CustomModelData;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin implements QuiverInterface {

    @Unique
    private ItemStack quiver;

    @Override
    @Nullable
    public ItemStack getQuiver() {
        return this.quiver;
    }

    @Override
    public void setQuiver(ItemStack quiver) {
        this.quiver = quiver;
    }

    @Shadow
    public abstract void causeFoodExhaustion(float f);

    @Shadow
    public abstract FoodData getFoodData();

    @WrapOperation(method = "getProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getItem(I)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack inventoryQuiver(Inventory inventory, int i, Operation<ItemStack> original) {
        Player player = Player.class.cast(this);
        ItemStack quiverStack = QuiverHelper.getStack(player);
        if (quiverStack != null) {
            quiverStack.applyComponents(DataComponentPatch.builder()
                    .set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of("hidden"), List.of()))
                    .build()
            );
        }
        ItemStack stack = inventory.getItem(i);
        if (stack.has(CRDataComponents.QUIVER_CONTENTS.get()) && !stack.get(CRDataComponents.QUIVER_CONTENTS.get()).items.isEmpty()) {
            var quiver = stack.get(CRDataComponents.QUIVER_CONTENTS.get());
            this.setQuiver(stack);
            ItemStack arrow = quiver.items.get(Math.max(stack.get(CRDataComponents.QUIVER_CONTENTS_SLOT.get()), 0)).copy();
            return arrow;
        }
        this.setQuiver(null);
        return original.call(inventory, i);
    }

    @Inject(method = "getProjectile", at = @At("HEAD"), cancellable = true)
    private void activeQuiver(ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
        Player player = Player.class.cast(this);
        ItemStack stack = QuiverHelper.getStack(player);
        if (stack != null) {
            stack.applyComponents(DataComponentPatch.builder()
                    .set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of("hidden"), List.of()))
                    .build()
            );
        }

        if (!(itemStack.getItem() instanceof ProjectileWeaponItem)) return;

        ItemStack quiverStack = QuiverHelper.getQuiver(player);

        if (quiverStack != null) {
            Integer rawSlot = quiverStack.get(CRDataComponents.QUIVER_CONTENTS_SLOT.get());
            int selectedSlot = (rawSlot != null && rawSlot >= 0) ? rawSlot : 0;

            QuiverContents contents = quiverStack.getOrDefault(CRDataComponents.QUIVER_CONTENTS.get(), QuiverContents.empty(QuiverHelper.getType(quiverStack)));
            if (selectedSlot < contents.size()) {
                ItemStack arrow = contents.getItemUnsafe(selectedSlot).copy();
                if (!arrow.isEmpty()) {
                    this.setQuiver(quiverStack);

                    cir.setReturnValue(arrow);
                }
            }
        }
    }

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
        if (!CRConfig.getGeneral().hunger.hunger_rework) return;
        Player player = Player.class.cast(this);
        Difficulty difficulty = serverLevel.getDifficulty();
        float multiplier = 0.75F;
        if (difficulty == Difficulty.EASY) multiplier = 0.5F;
        if (difficulty == Difficulty.HARD) multiplier = 1F;
        float aboveBarrier = player.getFoodData().getFoodLevel() - CRConfig.getGeneral().hunger.hunger_barrier;
        float exhaustion = Math.max(f - aboveBarrier, 0F);
        this.causeFoodExhaustion(exhaustion * multiplier);
        this.getFoodData().setSaturation(Math.max(this.getFoodData().getSaturationLevel() - f / 2, 0));
    }

    @Inject(method = "actuallyHurt", at = @At(value = "TAIL"))
    private void cancelConsumption(ServerLevel level, DamageSource damageSource, float amount, CallbackInfo info) {
        if (!CRConfig.getGeneral().misc.damage_interruptions || damageSource.getEntity() == null) return;
        Player player = Player.class.cast(this);
        ItemStack stack = player.getUseItem();
        if (stack.getComponents().has(DataComponents.FOOD) || stack.getComponents().has(DataComponents.CONSUMABLE)) player.stopUsingItem();
    }

    @WrapOperation(
            method = "actuallyHurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F")
    )
    private float handleKnockbackOnly(Player player, DamageSource damageSource, float damage, Operation<Float> original) {
        if (player instanceof CombatBooleanInterface booleans && booleans.getKnockbackOnly()) {
            damage = Math.max(damage - 1F, 0F);
        }
        return original.call(player, damageSource, damage);
    }

    @WrapOperation(
            method = "actuallyHurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setHealth(F)V")
    )
    private void handleKnockbackOnly(Player player, float damage, Operation<Void> original) {
        if (player instanceof CombatBooleanInterface booleans && booleans.getKnockbackOnly()) {
            damage = Math.min(player.getMaxHealth(), damage + 1F);
            booleans.setKnockbackOnly(false);
        }
        original.call(player, damage);
    }

    @WrapOperation(
            method = "causeFoodExhaustion",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"
            )
    )
    private void reduceExhaustionWhenHealing(FoodData foodData, float f, Operation<Void> original) {
        Player player = Player.class.cast(this);
        if (!CRConfig.getGeneral().hunger.hunger_rework || player.getHealth() >= player.getMaxHealth() || foodData.getFoodLevel() <= CRConfig.getGeneral().hunger.hunger_barrier) {
            original.call(foodData, f);
            return;
        }
        original.call(foodData, f / 4);;
    }

    @WrapOperation(
            method = "blockUsingItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/component/BlocksAttacks;disable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;FLnet/minecraft/world/item/ItemStack;)V"
            )
    )
    private void handleDisabling(BlocksAttacks blocksAttacks, ServerLevel serverLevel, LivingEntity livingEntity, float f, ItemStack itemStack, Operation<Void> original) {
        Player player = Player.class.cast(this);
        ShieldHelper.handleDisabling(serverLevel, player, livingEntity, f, itemStack);
    }

    @ModifyConstant(
            method = "attack",
            constant = @Constant(floatValue = 1.5F)
    )
    private float modifyCritMultiplier(float original) {
        Player player = Player.class.cast(this);
        if (!player.getAttributes().hasAttribute(CRAttributes.CRITICAL_DAMAGE_BOOST)) return original;
        else return (float) Player.class.cast(this).getAttributeValue(CRAttributes.CRITICAL_DAMAGE_BOOST);
    }

    @ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder addCRAttributes(AttributeSupplier.Builder builder) {
        return builder.add(CRAttributes.CRITICAL_DAMAGE_BOOST);
    }
}
