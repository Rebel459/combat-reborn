package net.rebel459.combat_reborn.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.tag.CRItemTags;
import net.rebel459.combat_reborn.client.util.AttributeTooltipHelper;
import net.rebel459.combat_reborn.util.QuiverHelper;
import net.rebel459.combat_reborn.util.ShieldHelper;
import org.apache.commons.lang3.function.TriConsumer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Unique
    Component prefix = Component.literal(" ");

    @Inject(method = "addDetailsToTooltip", at = @At("TAIL"))
    private void shieldTooltip(Item.TooltipContext context, TooltipDisplay display, Player player, TooltipFlag tooltipFlag, Consumer<Component> consumer, CallbackInfo ci) {
        ItemStack stack = ItemStack.class.cast(this);
        if (!stack.is(CRItemTags.SHIELD) || !CRConfig.getGeneral().shields.shield_overhaul || !CRConfig.getGeneral().shields.show_tooltips) return;
        consumer.accept(Component.literal(""));
        consumer.accept(Component.translatable("tooltip.combat_reborn.when_blocking").append(":").withStyle(ChatFormatting.GRAY));

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

        consumer.accept(prefix.copy().append(Component.translatable("tooltip.combat_reborn.shield.strength").append(": ").withStyle(ChatFormatting.DARK_GREEN).append(Component.literal(String.valueOf(strength)).withStyle(strengthColor))));
        consumer.accept(prefix.copy().append(Component.translatable("tooltip.combat_reborn.shield.parry").append(": ").withStyle(ChatFormatting.DARK_GREEN).append(Component.literal("x" + parryBonus).withStyle(parryBonusColor))));
    }

    @Unique
    private void addQuiverTooltip(Consumer<Component> consumer, ItemStack stack) {
        consumer.accept(Component.literal(""));
        consumer.accept(Component.translatable("tooltip.combat_reborn.when_equipped").append(":").withStyle(ChatFormatting.GRAY));
        if (QuiverHelper.getStorage(stack) == 1) consumer.accept(prefix.copy().append(Component.translatable("tooltip.combat_reborn.quiver.storage").append(": " + QuiverHelper.getStorage(stack) + " ").append(Component.translatable("tooltip.combat_reborn.quiver.stack")).withStyle(ChatFormatting.DARK_GREEN)));
        if (QuiverHelper.getStorage(stack) > 1) consumer.accept(prefix.copy().append(Component.translatable("tooltip.combat_reborn.quiver.storage").append(": " + QuiverHelper.getStorage(stack) + " ").append(Component.translatable("tooltip.combat_reborn.quiver.stacks")).withStyle(ChatFormatting.DARK_GREEN)));
        if (QuiverHelper.getAccuracy(stack) != 1) consumer.accept(prefix.copy().append(Component.translatable("tooltip.combat_reborn.quiver.accuracy").append(": x" + QuiverHelper.getAccuracy(stack)).withStyle(ChatFormatting.DARK_GREEN)));
        if (QuiverHelper.getBowSpeed(stack) != 1) consumer.accept(prefix.copy().append(Component.translatable("tooltip.combat_reborn.quiver.speed").append(": x" + QuiverHelper.getBowSpeed(stack)).withStyle(ChatFormatting.DARK_GREEN)));
        if (QuiverHelper.getPower(stack) != 1) consumer.accept(prefix.copy().append(Component.translatable("tooltip.combat_reborn.quiver.power").append(": x" + QuiverHelper.getPower(stack)).withStyle(ChatFormatting.DARK_GREEN)));
    }

    @Inject(method = "addDetailsToTooltip", at = @At("TAIL"))
    private void quiverTooltip(Item.TooltipContext context, TooltipDisplay display, Player player, TooltipFlag tooltipFlag, Consumer<Component> consumer, CallbackInfo ci) {
        ItemStack stack = ItemStack.class.cast(this);
        if (!stack.is(CRItemTags.QUIVER) || !CRConfig.getGeneral().quivers.show_tooltips) return;
        addQuiverTooltip(consumer, stack);
    }

    @Inject(method = "addAttributeTooltips", at = @At("HEAD"))
    private void beginAttributeTooltip(Consumer<Component> consumer, TooltipDisplay display, @Nullable Player player, CallbackInfo ci) {
        AttributeTooltipHelper.setStack(ItemStack.class.cast(this));
    }

    @Inject(method = "addAttributeTooltips", at = @At("TAIL"))
    private void endAttributeTooltip(Consumer<Component> consumer, TooltipDisplay display, @Nullable Player player, CallbackInfo ci) {
        AttributeTooltipHelper.clear();
    }

    @Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V", at = @At("HEAD"), cancellable = true)
    private void mergeTooltipAttributeModifiers(EquipmentSlotGroup slot, TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> consumer, CallbackInfo ci) {
        if (!CRConfig.getGeneral().tooltips.merge_attributes || AttributeTooltipHelper.getStack() == null) return;

        ItemStack stack = ItemStack.class.cast(this);
        Map<MergedModifierKey, MergedModifierValue> mergedModifiers = new LinkedHashMap<>();
        List<ModifierEntry> passthroughModifiers = new ArrayList<>();

        ItemAttributeModifiers itemModifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        AttributeTooltipHelper.set(itemModifiers);
        for (ItemAttributeModifiers.Entry entry : itemModifiers.modifiers()) {
            if (!entry.slot().equals(slot)) continue;
            if (entry.display().type() != ItemAttributeModifiers.Display.attributeModifiers().type()) {
                passthroughModifiers.add(new ModifierEntry(entry.attribute(), entry.modifier(), entry.display()));
                continue;
            }

            this.mergeModifier(mergedModifiers, entry.attribute(), entry.modifier());
        }

        EnchantmentHelper.forEachModifier(stack, slot, (attribute, modifier) -> this.mergeModifier(mergedModifiers, attribute, modifier));

        for (ModifierEntry entry : passthroughModifiers) {
            consumer.accept(entry.attribute(), entry.modifier(), entry.display());
        }

        for (Map.Entry<MergedModifierKey, MergedModifierValue> entry : mergedModifiers.entrySet()) {
            MergedModifierKey key = entry.getKey();
            MergedModifierValue value = entry.getValue();
            consumer.accept(
                    key.attribute(),
                    new AttributeModifier(value.id(), value.amount(), key.operation()),
                    ItemAttributeModifiers.Display.attributeModifiers()
            );
        }

        ci.cancel();
    }

    @Unique
    private void mergeModifier(Map<MergedModifierKey, MergedModifierValue> mergedModifiers, Holder<Attribute> attribute, AttributeModifier modifier) {
        MergedModifierKey key = new MergedModifierKey(attribute, modifier.operation());
        MergedModifierValue merged = mergedModifiers.get(key);
        if (merged == null) {
            mergedModifiers.put(key, new MergedModifierValue(modifier.id(), modifier.amount()));
            return;
        }

        mergedModifiers.put(key, new MergedModifierValue(merged.id(), merged.amount() + modifier.amount()));
    }

    @Unique
    private record ModifierEntry(Holder<Attribute> attribute, AttributeModifier modifier, ItemAttributeModifiers.Display display) {}

    @Unique
    private record MergedModifierKey(Holder<Attribute> attribute, AttributeModifier.Operation operation) {}

    @Unique
    private record MergedModifierValue(net.minecraft.resources.Identifier id, double amount) {}
}
