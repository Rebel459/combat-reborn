package net.rebel459.combat_reborn.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.rebel459.combat_reborn.registry.CRAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemAttributeModifiers.Display.Default.class)
public class ItemAttributeModifiersDisplayDefaultFabricMixin {

    @WrapOperation(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/core/Holder;)Z"))
    private boolean multipliedAttributeTooltips(Holder<Attribute> attribute, Holder<Attribute> target, Operation<Boolean> original) {
        return original.call(attribute, target) || attribute.is(CRAttributes.CRITICAL_DAMAGE_BOOST) || attribute.is(CRAttributes.CHARGE_ATTACK_BOOST);
    }
}
