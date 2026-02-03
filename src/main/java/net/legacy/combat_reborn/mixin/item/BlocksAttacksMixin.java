package net.legacy.combat_reborn.mixin.item;

import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.legacy.combat_reborn.util.BlockedSourceInterface;
import net.legacy.combat_reborn.util.ShieldHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlocksAttacks.class)
public abstract class BlocksAttacksMixin implements BlockedSourceInterface {

    @Inject(method = "onBlocked", at = @At(value = "HEAD"))
    private void handleParrying(ServerLevel serverLevel, LivingEntity attacked, CallbackInfo ci) {
        if (!CRConfig.get.general.shields.shield_overhaul || !(attacked instanceof BlockedSourceInterface blocked)) return;
        DamageSource damageSource = blocked.getLastBlockedSource();
        ItemStack stack = attacked.getUseItem();
        int useTicks = attacked.getTicksUsingItem();
        if (useTicks <= ShieldHelper.getParryWindow(stack) && stack.is(CRItemTags.SHIELD) && damageSource.getEntity() != null && damageSource.getEntity() instanceof LivingEntity attacker && ShieldHelper.canBeParried(damageSource)) {
            ShieldHelper.onParry(serverLevel, attacker, attacked, stack);
        }
    }
}