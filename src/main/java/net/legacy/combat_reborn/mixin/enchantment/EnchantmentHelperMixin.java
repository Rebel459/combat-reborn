package net.legacy.combat_reborn.mixin.enchantment;

import com.google.common.collect.Lists;
import net.legacy.combat_reborn.CombatReborn;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.tag.CREnchantmentTags;
import net.minecraft.core.Holder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "getAvailableEnchantmentResults", at = @At(value = "HEAD"), cancellable = true)
    private static void CR$getAvailableEnchantmentResults(int level, ItemStack stack, Stream<Holder<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (CombatReborn.hasEnchantsAndExpeditions() || !stack.is(ItemTags.AXES) || !CRConfig.get.general.shields.shield_overhaul) return;
        List<EnchantmentInstance> list = Lists.<EnchantmentInstance>newArrayList();
        boolean bl = stack.is(Items.BOOK);
        possibleEnchantments.filter(holder -> holder.value().isPrimaryItem(stack) || bl).forEach(holder -> {
            Enchantment enchantment = holder.value();

            for (int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); j--) {
                if ((level >= enchantment.getMinCost(j) && level <= enchantment.getMaxCost(j)) && !(stack.is(ItemTags.AXES) && holder.is(CREnchantmentTags.NOT_ON_AXES))) {
                    list.add(new EnchantmentInstance(holder, j));
                    break;
                }
            }
        });
        cir.setReturnValue(list);
    }
}