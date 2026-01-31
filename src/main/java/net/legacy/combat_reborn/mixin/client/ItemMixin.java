package net.legacy.combat_reborn.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.legacy.combat_reborn.util.ShieldHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(Item.class)
public abstract class ItemMixin {

    @Unique
    Component prefix = Component.literal(" ");

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void shieldTooltip(ItemStack itemStack, Item.TooltipContext context, List<Component> list, TooltipFlag tooltipFlag, CallbackInfo ci) {
        ItemStack stack = ItemStack.class.cast(this);
        if (!stack.is(CRItemTags.SHIELD) || !CRConfig.get.general.shields.shield_overhaul || !CRConfig.get.general.shields.show_tooltips) return;
        list.add(Component.literal(""));
        list.add(Component.translatable("tooltip.combat_reborn.when_blocking").append(":").withStyle(ChatFormatting.GRAY));

        int strength = (int) ShieldHelper.getMaxDamage(stack, false);
        float parryBonus = ShieldHelper.getParryBonus(stack, false);
        int enchantedStrength = (int) ShieldHelper.getMaxDamage(stack);
        float enchantedParryBonus = ShieldHelper.getParryBonus(stack);
        ChatFormatting strengthColor = ChatFormatting.DARK_GREEN;
        ChatFormatting parryBonusColor = ChatFormatting.DARK_GREEN;

        if (enchantedStrength > strength) {
            strength = enchantedStrength;
            strengthColor = ChatFormatting.BLUE;
        }
        else if (enchantedStrength < strength) {
            strength = enchantedStrength;
            strengthColor = ChatFormatting.RED;
        }
        if (enchantedParryBonus > parryBonus) {
            parryBonus = enchantedParryBonus;
            parryBonusColor = ChatFormatting.BLUE;
        }
        else if (enchantedParryBonus < parryBonus) {
            parryBonus = enchantedParryBonus;
            parryBonusColor = ChatFormatting.RED;
        }

        list.add(prefix.copy().append(Component.translatable("tooltip.combat_reborn.shield.strength").append(": ").withStyle(ChatFormatting.DARK_GREEN).append(Component.literal(String.valueOf(strength)).withStyle(strengthColor))));
        list.add(prefix.copy().append(Component.translatable("tooltip.combat_reborn.shield.parry").append(": ").withStyle(ChatFormatting.DARK_GREEN).append(Component.literal("x" + parryBonus).withStyle(parryBonusColor))));
    }
}