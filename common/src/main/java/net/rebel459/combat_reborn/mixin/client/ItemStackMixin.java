package net.rebel459.combat_reborn.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.tag.CRItemTags;
import net.rebel459.combat_reborn.util.QuiverHelper;
import net.rebel459.combat_reborn.util.ShieldHelper;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = ItemStack.class, priority = 500)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Unique
    private static final Component PREFIX = Component.literal(" ");

    @Inject(method = "addDetailsToTooltip", at = @At(value = "TAIL"))
    private void shieldTooltip(Item.TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag tooltipFlag, Consumer<Component> builder, CallbackInfo ci) {
        ItemStack stack = ItemStack.class.cast(this);
        if (!stack.is(CRItemTags.SHIELD) || !CRConfig.getGeneral().shields.shield_overhaul || !CRConfig.getGeneral().shields.show_tooltips) return;
        builder.accept(Component.literal(""));
        builder.accept(Component.translatable("tooltip.combat_reborn.when_blocking").append(":").withStyle(ChatFormatting.GRAY));

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

        builder.accept(PREFIX.copy().append(Component.translatable("tooltip.combat_reborn.shield.strength").append(": ").withStyle(ChatFormatting.DARK_GREEN).append(Component.literal(String.valueOf(strength)).withStyle(strengthColor))));
        builder.accept(PREFIX.copy().append(Component.translatable("tooltip.combat_reborn.shield.parry").append(": ").withStyle(ChatFormatting.DARK_GREEN).append(Component.literal("x" + parryBonus).withStyle(parryBonusColor))));
    }

    @Unique
    private void addQuiverTooltip(Consumer<Component> builder, ItemStack stack) {
        builder.accept(Component.literal(""));
        builder.accept(Component.translatable("tooltip.combat_reborn.when_equipped").append(":").withStyle(ChatFormatting.GRAY));
        if (QuiverHelper.getStorage(stack) == 1) builder.accept(PREFIX.copy().append(Component.translatable("tooltip.combat_reborn.quiver.storage").append(": " + QuiverHelper.getStorage(stack) + " ").append(Component.translatable("tooltip.combat_reborn.quiver.stack")).withStyle(ChatFormatting.DARK_GREEN)));
        if (QuiverHelper.getStorage(stack) > 1) builder.accept(PREFIX.copy().append(Component.translatable("tooltip.combat_reborn.quiver.storage").append(": " + QuiverHelper.getStorage(stack) + " ").append(Component.translatable("tooltip.combat_reborn.quiver.stacks")).withStyle(ChatFormatting.DARK_GREEN)));
        if (QuiverHelper.getAccuracy(stack) != 1) builder.accept(PREFIX.copy().append(Component.translatable("tooltip.combat_reborn.quiver.accuracy").append(": x" + QuiverHelper.getAccuracy(stack)).withStyle(ChatFormatting.DARK_GREEN)));
        if (QuiverHelper.getBowSpeed(stack) != 1) builder.accept(PREFIX.copy().append(Component.translatable("tooltip.combat_reborn.quiver.speed").append(": x" + QuiverHelper.getBowSpeed(stack)).withStyle(ChatFormatting.DARK_GREEN)));
        if (QuiverHelper.getPower(stack) != 1) builder.accept(PREFIX.copy().append(Component.translatable("tooltip.combat_reborn.quiver.power").append(": x" + QuiverHelper.getPower(stack)).withStyle(ChatFormatting.DARK_GREEN)));
    }

    @Inject(method = "addDetailsToTooltip", at = @At("TAIL"))
    private void quiverTooltip(Item.TooltipContext context, TooltipDisplay display, Player player, TooltipFlag tooltipFlag, Consumer<Component> builder, CallbackInfo ci) {
        ItemStack stack = ItemStack.class.cast(this);
        if (!stack.is(CRItemTags.QUIVER) || !CRConfig.getGeneral().quivers.show_tooltips) return;
        addQuiverTooltip(builder, stack);
    }
}
