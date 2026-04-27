package net.rebel459.combat_reborn.mixin.client;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.unified.client.util.FabricAttributeTooltipImpl;
import net.rebel459.unified.platform.UnifiedPlatform;
import net.rebel459.unified.util.LoaderType;
import org.apache.commons.lang3.function.TriConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mixin(ItemStack.class)
public class ItemStackFabricMixin {

    @Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V", at = @At("HEAD"), cancellable = true)
    private void mergeTooltipAttributeModifiers(EquipmentSlotGroup slot, TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> consumer, CallbackInfo ci) {
        if (!CRConfig.getGeneral().tooltips.merge_attributes || FabricAttributeTooltipImpl.getStack() == null || UnifiedPlatform.get().getLoader() == LoaderType.NEOFORGE) return;

        ItemStack stack = ItemStack.class.cast(this);
        Map<MergedModifierKey, MergedModifierValue> mergedModifiers = new LinkedHashMap<>();
        List<ModifierEntry> passthroughModifiers = new ArrayList<>();

        ItemAttributeModifiers itemModifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        FabricAttributeTooltipImpl.set(itemModifiers);
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
