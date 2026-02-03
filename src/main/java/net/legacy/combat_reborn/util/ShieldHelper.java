package net.legacy.combat_reborn.util;

import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CREnchantments;
import net.legacy.combat_reborn.sound.CRSounds;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;

import java.util.Random;

public class ShieldHelper {

    public static void onParry(ServerLevel serverLevel, LivingEntity attacker, LivingEntity attacked, ItemStack stack) {
        handleKnockback(attacker, attacked, 0.6F);
        boolean stagger = CREnchantments.getLevel(stack, CREnchantments.STAGGER) > 0;
        float f = 0F;
        if (attacker instanceof Player player) {
            player.addTag("knockback_only");
            f = 1F;
        }
        if (stagger) {
            attacker.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60));
            attacker.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60));
            f += 1F;
        }
        attacker.hurt(attacked.damageSources().generic(), f);
        serverLevel.playSound(
                null,
                attacked.getX(),
                attacked.getY(),
                attacked.getZ(),
                CRSounds.SHIELD_PARRY,
                attacked.getSoundSource(),
                1F,
                1F
        );
    }

    public static void handleKnockback(LivingEntity attacker, LivingEntity attacked, float strength) {
        double d = attacked.getX() - attacker.getX();
        double e = attacked.getZ() - attacker.getZ();

        attacker.knockback(strength, d, e);
    }

    public static void onDisable(ServerLevel serverLevel, LivingEntity attacked, LivingEntity attacker, float duration, ItemStack stack, boolean strengthDepleted) {}

    public static void handleDisabling(ServerLevel serverLevel, LivingEntity attacked, LivingEntity attacker, float duration, ItemStack stack) {
        BlocksAttacks blocksAttacks = stack.get(DataComponents.BLOCKS_ATTACKS);
        boolean shouldContinue = true;
        if (blocksAttacks == null) return;
        if (!attacked.getTags().contains("should_disable_shield")) {
            int level = CREnchantments.getLevel(stack, CREnchantments.PARRY);
            if (level > 0) {
                if (new Random().nextInt(1, 5) <= level && attacked.getTicksUsingItem() < ShieldHelper.getParryWindow(stack)) {
                    return;
                }
            }
        }
        else {
            attacked.removeTag("should_disable_shield");
        }
        if (CRConfig.get.general.shields.shield_overhaul) {
            if (attacked instanceof ShieldInfo shieldInfo) {
                int percentageToIncrease = ShieldHelper.processDamage(stack, duration * 5F);
                shieldInfo.setPercentageDamageAndSync(Math.max(shieldInfo.getPercentageDamage() + percentageToIncrease, 0), (ServerPlayer) attacked);
                if (shieldInfo.getPercentageDamage() >= 100) {
                    onDisable(serverLevel, attacked, attacker, duration, stack, true);
                    float disableDuration = ShieldHelper.getDisableDuration(stack);
                    int disableTicks = (int) (disableDuration * 20);
                    Player player = (Player) attacked;
                    player.getCooldowns().addCooldown(stack, disableTicks);
                    player.stopUsingItem();
                    blocksAttacks.disableSound()
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
                    shieldInfo.setPercentageDamageAndSync(0, (ServerPlayer) attacked);
                    shouldContinue = false;
                }
            }
        }
        if (shouldContinue) {
            onDisable(serverLevel, attacked, attacker, duration, stack, false);
            blocksAttacks.disable(serverLevel, attacked, duration, stack);
        }
    }

    public static boolean canBeParried(DamageSource source) {
        return source.isDirect() && !source.is(DamageTypeTags.IS_PROJECTILE);
    }

    public static int processDamage(ItemStack stack, float f) {
        float maxDamage = getMaxDamage(stack);
        f = f / maxDamage;
        return (int) (f * 100F);
    }

    public static float getMaxDamage(ItemStack stack) {
        return getMaxDamage(stack, true);
    }
    public static float getMaxDamage(ItemStack stack, boolean includeEnchantments) {
        float maxDamage = ShieldInfo.defaultMaxBlockDamage;
        if (CRConfig.get.general.integrations.enderscape_rubble_shields && stack.is(CRItemTags.RUBBLE_SHIELD)) maxDamage -= 12;
        int endurance = CREnchantments.getLevel(stack, CREnchantments.ENDURANCE);
        if (!includeEnchantments) endurance = 0;
        maxDamage = maxDamage * (1 + endurance / 3F);
        return maxDamage;
    }

    public static float getParryBonus(ItemStack stack) {
        return getParryBonus(stack, true);
    }
    public static float getParryBonus(ItemStack stack, boolean includeEnchantments) {
        float base = 1.25F;
        if (CRConfig.get.general.integrations.enderscape_rubble_shields && stack.is(CRItemTags.RUBBLE_SHIELD)) base += 0.5F;
        int parry = CREnchantments.getLevel(stack, CREnchantments.PARRY);
        if (!includeEnchantments) parry = 0;
        return base + parry * 0.25F;
    }

    public static int getParryWindow(ItemStack stack) {
        int parryWindow = ShieldInfo.parryWindow;
        if (CRConfig.get.general.integrations.enderscape_rubble_shields && stack.is(CRItemTags.RUBBLE_SHIELD)) parryWindow -= 4;
        int parry = CREnchantments.getLevel(stack, CREnchantments.PARRY);
        return parryWindow - parry * 4;
    }

    public static float getDisableDuration(ItemStack stack) {
        float disableDuration = 15F;
        if (CRConfig.get.general.integrations.enderscape_rubble_shields && stack.is(CRItemTags.RUBBLE_SHIELD)) disableDuration = 10F;
        return disableDuration;
    }
}
