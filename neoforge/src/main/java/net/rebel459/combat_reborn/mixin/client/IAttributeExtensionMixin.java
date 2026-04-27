package net.rebel459.combat_reborn.mixin.client;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import net.rebel459.combat_reborn.registry.CRAttributes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IAttributeExtension.class)
public interface IAttributeExtensionMixin {

    @Inject(method = "toValueComponent", at = @At("HEAD"), cancellable = true)
    private void percentageAttributeTooltips(@Nullable AttributeModifier.Operation op, double value, TooltipFlag flag, CallbackInfoReturnable<MutableComponent> cir) {
        Attribute attribute = (Attribute) this;
        if ((attribute == CRAttributes.CRITICAL_DAMAGE_BOOST.value() || attribute == CRAttributes.CHARGE_ATTACK_BOOST.value()) && IAttributeExtension.isNullOrAddition(op)) {
            cir.setReturnValue(((IAttributeExtension) this).toValueComponent(AttributeModifier.Operation.ADD_MULTIPLIED_BASE, value, flag));
        }
    }
}