package net.rebel459.combat_reborn.mixin.client;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.rebel459.combat_reborn.util.AttributeTooltipInterface;
import org.apache.commons.lang3.function.TriConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemAttributeModifiers.class)
public class ItemAttributeModifiersMixin {

    @Inject(method = "forEach(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V", at = @At("HEAD"))
    private void beginTooltipContext(EquipmentSlotGroup slot, TriConsumer<?, ?, ?> consumer, CallbackInfo ci) {
        AttributeTooltipInterface.set(ItemAttributeModifiers.class.cast(this));
    }

    @Inject(method = "forEach(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V", at = @At("TAIL"))
    private void endTooltipContext(EquipmentSlotGroup slot, TriConsumer<?, ?, ?> consumer, CallbackInfo ci) {
        AttributeTooltipInterface.clear();
    }
}
