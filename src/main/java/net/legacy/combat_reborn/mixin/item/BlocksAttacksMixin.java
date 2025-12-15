package net.legacy.combat_reborn.mixin.item;

import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CREnchantments;
import net.legacy.combat_reborn.util.ShieldHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlocksAttacks.class)
public abstract class BlocksAttacksMixin {

    @Inject(method = "disable", at = @At(value = "HEAD"), cancellable = true)
    private void parry(ServerLevel serverLevel, LivingEntity entity, float duration, ItemStack stack, CallbackInfo ci) {
        ItemStack itemStack = entity.getItemBlockingWith();
        if (itemStack == null) return;
        if (entity.getTags().contains("should_disable_shield")) {
            entity.removeTag("should_disable_shield");
            return;
        }
        int level = CREnchantments.getLevel(itemStack, CREnchantments.PARRY);
        if (level > 0) {
            if (new Random().nextInt(1, 6) <= level && entity.getTicksUsingItem() < ShieldHelper.getParryWindow(stack)) {
                ci.cancel();
            }
        }
    }
}