package net.legacy.combat_reborn.util;

import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CREnchantments;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ShieldHelper {

    public static void onParry(ServerLevel serverLevel, LivingEntity attacker, LivingEntity attacked, ItemStack stack) {
        handleKnockback(attacker, attacked.damageSources().generic(), 0.6F);
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

    public static boolean canBeParried(DamageSource source) {
        return source.isDirect() && !source.is(DamageTypeTags.IS_PROJECTILE);
    }

    public static int processDamage(ItemStack stack, float f) {
        float maxDamage = ShieldInfo.defaultMaxBlockDamage;
        if (CRConfig.get.integrations.enderscape && stack.is(CRItemTags.RUBBLE_SHIELD)) maxDamage = maxDamage / 2;
        int endurance = CREnchantments.getLevel(stack, CREnchantments.ENDURANCE);
        maxDamage = maxDamage * (1 + endurance / 3F);
        f = f / maxDamage;
        return (int) (f * 100F);
    }

    public static float getParryBonus(ItemStack stack) {
        float base = 1.25F;
        if (CRConfig.get.integrations.enderscape && stack.is(CRItemTags.RUBBLE_SHIELD)) base += 0.5F;
        int parry = CREnchantments.getLevel(stack, CREnchantments.PARRY);
        return base + parry * 0.25F;
    }

    public static int getParryWindow(ItemStack stack) {
        int parryWindow = ShieldInfo.parryWindow;
        if (CRConfig.get.integrations.enderscape && stack.is(CRItemTags.RUBBLE_SHIELD)) parryWindow -= 4;
        int parry = CREnchantments.getLevel(stack, CREnchantments.PARRY);
        return parryWindow - parry * 4;
    }
}
