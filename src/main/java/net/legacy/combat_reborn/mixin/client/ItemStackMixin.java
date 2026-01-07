package net.legacy.combat_reborn.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.legacy.combat_reborn.util.QuiverHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract boolean is(Item item);

    @Shadow public abstract boolean is(TagKey<Item> tag);

    @Shadow public abstract Item getItem();

    @Inject(method = "addDetailsToTooltip", at = @At("HEAD"))
    private void addDescription(Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Player player, TooltipFlag tooltipFlag, Consumer<Component> consumer, CallbackInfo ci) {
        ItemStack stack = ItemStack.class.cast(this);
        if (!stack.is(CRItemTags.QUIVER)) return;
        consumer.accept(Component.literal(""));
        consumer.accept(Component.translatable("tooltip.combat_reborn.when_equipped").append(":").withStyle(ChatFormatting.GRAY));
        Component prefix = Component.literal(" ");
        if (QuiverHelper.getStorage(stack) > 0) consumer.accept(prefix.copy().append(Component.translatable("tooltip.combat_reborn.quiver.storage").append(": " + QuiverHelper.getStorage(stack) + " ").append(Component.translatable("tooltip.combat_reborn.quiver.stacks")).withStyle(ChatFormatting.DARK_GREEN)));
        if (QuiverHelper.getAccuracy(stack) > 1) consumer.accept(prefix.copy().append(Component.translatable("tooltip.combat_reborn.quiver.accuracy").append(": x" + QuiverHelper.getAccuracy(stack)).withStyle(ChatFormatting.DARK_GREEN)));
        if (QuiverHelper.getBowSpeed(stack) > 1) consumer.accept(prefix.copy().append(Component.translatable("tooltip.combat_reborn.quiver.speed").append(": x" + QuiverHelper.getBowSpeed(stack)).withStyle(ChatFormatting.DARK_GREEN)));
        if (QuiverHelper.getPower(stack) > 1) consumer.accept(prefix.copy().append(Component.translatable("tooltip.combat_reborn.quiver.power").append(": x" + QuiverHelper.getPower(stack)).withStyle(ChatFormatting.DARK_GREEN)));
    }
}