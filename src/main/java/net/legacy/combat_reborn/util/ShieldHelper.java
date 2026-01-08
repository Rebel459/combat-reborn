package net.legacy.combat_reborn.util;

import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CREnchantments;
import net.legacy.combat_reborn.sound.CRSounds;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
        if (CRConfig.get().general.integrations.enderscape && stack.is(CRItemTags.RUBBLE_SHIELD)) maxDamage = maxDamage - 12;
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
        if (CRConfig.get().general.integrations.enderscape && stack.is(CRItemTags.RUBBLE_SHIELD)) base += 0.5F;
        int parry = CREnchantments.getLevel(stack, CREnchantments.PARRY);
        if (!includeEnchantments) parry = 0;
        return base + parry * 0.25F;
    }

    public static int getParryWindow(ItemStack stack) {
        int parryWindow = ShieldInfo.parryWindow;
        if (CRConfig.get().general.integrations.enderscape && stack.is(CRItemTags.RUBBLE_SHIELD)) parryWindow -= 4;
        int parry = CREnchantments.getLevel(stack, CREnchantments.PARRY);
        return parryWindow - parry * 4;
    }
}
