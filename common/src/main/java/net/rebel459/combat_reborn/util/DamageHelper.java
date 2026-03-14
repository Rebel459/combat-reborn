package net.rebel459.combat_reborn.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.config.CRGeneralConfig;

public class DamageHelper {

    public static float getDamageAfterArmorAbsorb(LivingEntity entity, DamageSource damageSource, float damage) {
        if (!damageSource.is(DamageTypeTags.BYPASSES_ARMOR)) {
            entity.hurtArmor(damageSource, damage);
            damage = processDamage(entity, damage, damageSource, entity.getArmorValue(), (float) entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        }

        return damage;
    }

    public static float processDamage(LivingEntity livingEntity, float damage, DamageSource damageSource, float defense, float toughness) {
        float damageReduction = calculateDamageReduction(livingEntity, damage, defense, toughness);
        ItemStack itemStack = damageSource.getWeaponItem();
        float checkedDamageReduction;
        if (itemStack != null && livingEntity.level() instanceof ServerLevel serverLevel) {
            checkedDamageReduction = Mth.clamp(EnchantmentHelper.modifyArmorEffectiveness(serverLevel, itemStack, livingEntity, damageSource, damageReduction), 0.0F, 1.0F);
        } else {
            checkedDamageReduction = damageReduction;
        }

        float damageMultiplier = 1.0F - checkedDamageReduction;
        return damage * damageMultiplier;
    }

    public static float calculateDamageReduction(LivingEntity entity, float damage, float defense, float toughness) {
        float toughnessToUse = 0F;
        if (CRConfig.get.general.armor.toughness.toughness_type == CRGeneralConfig.ToughnessMechanics.DURABILITY) {
            toughnessToUse = getDurabilityToughness(entity, toughness);
        } else if (CRConfig.get.general.armor.toughness.toughness_type == CRGeneralConfig.ToughnessMechanics.DAMAGE) {
            float damageMultiplier = Math.max(Math.min(damage / 50, 1), 0);
            toughnessToUse = toughness * damageMultiplier;
        }
        return damageReductionFormula(defense + toughnessToUse * Math.max(CRConfig.get.general.armor.toughness.multiplier, 0F));
    }

    public static float getDurabilityToughness(LivingEntity entity, float toughness) {
        float durabilityToughness = toughness;
        for (EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
            ItemStack stack = entity.getItemBySlot(equipmentSlot);
            if (stack != ItemStack.EMPTY && stack.getComponents().has(DataComponents.ATTRIBUTE_MODIFIERS) && equipmentSlot.isArmor() && stack.has(DataComponents.MAX_DAMAGE)) {
                var modifiers = stack.getComponents().get(DataComponents.ATTRIBUTE_MODIFIERS);
                if (modifiers != null) {
                    for (ItemAttributeModifiers.Entry attributes : modifiers.modifiers()) {
                        if (attributes.attribute().is(Attributes.ARMOR_TOUGHNESS)) {
                            float attributeToughness = (float) attributes.modifier().amount();
                            float durabilityLost = stack.getDamageValue();
                            float maxDurability = stack.getMaxDamage();
                            float percentDamaged = durabilityLost / maxDurability;
                            durabilityToughness -= Math.min(Math.max(attributeToughness * percentDamaged, 0F), attributeToughness);
                            break;
                        }
                    }
                }
            }
        }
        return durabilityToughness;
    }

    public static float processEnchantedDamage(float damage, float protection) {
        float h = Mth.clamp(protection * Math.max(CRConfig.get.general.armor.protection.multiplier, 0), 0.0F, Math.min(Math.max(CRConfig.get.general.armor.protection.max_percentage, 0), 100) / 4);
        return damage * (1.0F - h / 25.0F);
    }

    public static float damageReductionFormula(float points) {
        float maxPercentage = Math.min(Math.max(CRConfig.get.general.armor.formula.max_percentage, 0), 100);
        float middlePercentage = Math.min(Math.max(CRConfig.get.general.armor.formula.middle_percentage, 0), maxPercentage);
        return damageReductionFormula(points, Math.max(CRConfig.get.general.armor.formula.middle_points, 0), middlePercentage, Math.max(CRConfig.get.general.armor.formula.max_points, 0), maxPercentage, Math.min(Math.max(CRConfig.get.general.armor.formula.gradient, 0), 2), Math.max(CRConfig.get.general.armor.formula.multiplier, 0)) / 100F;
    }
    private static float damageReductionFormula(float points, float middlePoints, float middlePercentage, float maxPoints, float maxPercentage, float gradient, float multiplier) {
        if (points <= 0) return 0F;

        gradient = 1 + (gradient - 1) / 2;

        points = points * multiplier;
        middlePoints = middlePoints * multiplier;
        maxPoints = maxPoints * multiplier;

        if (points <= middlePoints) {
            float k_low = (float) (middlePoints * Math.pow((100.0 / middlePercentage - 1.0), 1.0 / gradient));
            float modDefPow = (float) Math.pow(points, gradient);
            float kPow = (float) Math.pow(k_low, gradient);
            return 100 * modDefPow / (modDefPow + kPow);
        } else {
            float excessModified = points - middlePoints;
            float excessMax = maxPoints - middlePoints;
            float addPercentage = maxPercentage - middlePercentage;

            float slope = gradient * middlePercentage * (100 - middlePercentage) / (100f * middlePoints);

            float b = addPercentage / (slope * excessMax);
            float addP;
            if (b > 0 && b < 1 && slope > 0) {
                float kHigh = b * excessMax / (1 - b);
                float cap = slope * kHigh;
                addP = cap * excessModified / (excessModified + kHigh);
            } else {
                float kHigh = (float) (excessMax * Math.pow((100.0 / addPercentage - 1.0), 1.0 / gradient));
                float excessPow = (float) Math.pow(excessModified, gradient);
                float kPow = (float) Math.pow(kHigh, gradient);
                addP = 100 * excessPow / (excessPow + kPow);
            }
            return middlePercentage + addP;
        }
    }
}
