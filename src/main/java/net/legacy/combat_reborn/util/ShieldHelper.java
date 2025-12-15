package net.legacy.combat_reborn.util;

import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CREnchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ShieldHelper {

    public static void onParry(ServerLevel serverLevel, LivingEntity attacker, LivingEntity attacked, ItemStack stack) {
        handleKnockback(attacker, attacked.damageSources().generic(), 0.8F);
        boolean stagger = CREnchantments.getLevel(stack, CREnchantments.STAGGER) > 0;
        if (stagger) {
            attacker.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60));
            attacker.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60));
            attacker.hurtServer(serverLevel, attacked.damageSources().generic(), 1);
        }
        serverLevel.playSound(
                null,
                attacked.getX(),
                attacked.getY(),
                attacked.getZ(),
                SoundEvents.ANVIL_LAND,
                attacked.getSoundSource(),
                0.8F,
                0.8F + serverLevel.random.nextFloat() * 0.4F
        );
    }

    public static void handleKnockback(LivingEntity attacker, DamageSource source, float strength) {
        double d = 0.0;
        double e = 0.0;
        if (source.getSourcePosition() != null) {
            d = source.getSourcePosition().x() - attacker.getX();
            e = source.getSourcePosition().z() - attacker.getZ();
        }

        attacker.knockback(strength, d, e);
    }

    public static int processDamage(LivingEntity entity, ItemStack stack, float f) {
        float maxDamage = ShieldInfo.defaultMaxBlockDamage;
        int endurance = CREnchantments.getLevel(stack, CREnchantments.ENDURANCE);
        maxDamage += endurance * 8;
        f = f / maxDamage;
        return (int) (f * 100F);
    }

    public static float getParryBonus(ItemStack stack) {
        int parry = CREnchantments.getLevel(stack, CREnchantments.PARRY);
        return 1.4F + parry * 0.2F;
    }

    public static int getParryWindow(ItemStack stack) {
        return ShieldInfo.parryWindow;
    }
}
