package net.legacy.combat_reborn.mixin.item;

import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CREnchantments;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.legacy.combat_reborn.util.BlockedSourceInterface;
import net.legacy.combat_reborn.util.ShieldHelper;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.Random;

@Mixin(BlocksAttacks.class)
public abstract class BlocksAttacksMixin implements BlockedSourceInterface {

    @Shadow
    @Final
    private Optional<Holder<SoundEvent>> disableSound;

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

    @Inject(method = "disable", at = @At(value = "HEAD"), cancellable = true)
    private void handleDisabling(ServerLevel serverLevel, LivingEntity entity, float duration, ItemStack stack, CallbackInfo ci) {
        if (entity.getTags().contains("should_disable_shield")) {
            entity.removeTag("should_disable_shield");
            return;
        }
        int level = CREnchantments.getLevel(stack, CREnchantments.PARRY);
        boolean shouldContinue = CRConfig.get.general.shields.shield_overhaul;
        if (level > 0) {
            if (new Random().nextInt(1, 5) <= level && entity.getTicksUsingItem() < ShieldHelper.getParryWindow(stack)) {
                shouldContinue = false;
                ci.cancel();
            }
        }
        if (shouldContinue) {
            if (entity instanceof ShieldInfo shieldInfo) {
                int percentageToIncrease = ShieldHelper.processDamage(stack, duration * 5F);
                shieldInfo.setPercentageDamageAndSync(Math.max(shieldInfo.getPercentageDamage() + percentageToIncrease, 0), (ServerPlayer) entity);
                if (shieldInfo.getPercentageDamage() >= 100) {
                    float disableTime = 15;
                    if (CRConfig.get.general.integrations.enderscape_rubble_shields && stack.is(CRItemTags.RUBBLE_SHIELD)) disableTime = 10F;
                    int disableTicks = (int) (disableTime * 20);
                    Player player = (Player) entity;
                    player.getCooldowns().addCooldown(stack, disableTicks);
                    shieldInfo.setPercentageDamageAndSync(0, (ServerPlayer) entity);
                    player.stopUsingItem();
                    this.disableSound
                            .ifPresent(
                                    holder -> serverLevel.playSound(
                                            null,
                                            player.getX(),
                                            player.getY(),
                                            player.getZ(),
                                            holder,
                                            player.getSoundSource(),
                                            0.8F,
                                            0.8F + serverLevel.random.nextFloat() * 0.4F
                                    )
                            );
                    ci.cancel();
                }
            }
        }
    }
}