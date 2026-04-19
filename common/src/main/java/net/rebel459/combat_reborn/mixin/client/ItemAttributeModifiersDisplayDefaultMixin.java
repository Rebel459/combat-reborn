package net.rebel459.combat_reborn.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.rebel459.combat_reborn.client.util.ClientHelper;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.config.CRGeneralConfig;
import net.rebel459.combat_reborn.registry.CRAttributes;
import net.rebel459.combat_reborn.util.AttributeTooltipInterface;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemAttributeModifiers.Display.Default.class)
public class ItemAttributeModifiersDisplayDefaultMixin {

    @Unique
    private double displayAmount;

    @Inject(
            method = "apply",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/text/DecimalFormat;format(D)Ljava/lang/String;"
            )
    )
    private void captureDisplayAmount(Consumer<Component> consumer, @Nullable Player player, Holder<Attribute> attribute, AttributeModifier modifier, CallbackInfo ci, @Local(name = "displayAmount") double displayAmount) {
        this.displayAmount = displayAmount;
    }

    @Inject(method = "apply", at = @At(value = "TAIL"))
    private void addCriticalDamageTooltip(Consumer<Component> consumer, @Nullable Player player, Holder<Attribute> attribute, AttributeModifier modifier, CallbackInfo ci) {
        if (CRConfig.getGeneral().misc.critical_tooltip == CRGeneralConfig.CriticalTooltip.NONE) return;
        ItemAttributeModifiers itemModifiers = AttributeTooltipInterface.get();
        ItemStack stack = AttributeTooltipInterface.getStack();
        if (player == null || itemModifiers == null || !attribute.is(Attributes.ATTACK_DAMAGE)) return;
        if (!ClientHelper.hasKeyDown() && CRConfig.getGeneral().misc.critical_tooltip == CRGeneralConfig.CriticalTooltip.SHIFT) return;
        if (stack != null && (!(stack.has(DataComponents.WEAPON) || stack.has(DataComponents.TOOL)) || stack.has(DataComponents.KINETIC_WEAPON))) return;

        List<ItemAttributeModifiers.Entry> modifiers = new ArrayList<>(itemModifiers.modifiers());
        modifiers.removeIf(entry -> !entry.attribute().is(CRAttributes.CRITICAL_DAMAGE_BOOST));

        double critMultiplier = player.getAttributeBaseValue(CRAttributes.CRITICAL_DAMAGE_BOOST);
        for (ItemAttributeModifiers.Entry entry : modifiers) {
            var critModifier = entry.modifier();
            if (critModifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                critMultiplier += critModifier.amount();
            }
            else if (critModifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE) {
                critMultiplier += player.getAttributeBaseValue(CRAttributes.CRITICAL_DAMAGE_BOOST) * critModifier.amount();
            }
            else if (critModifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                critMultiplier += critMultiplier * critModifier.amount();
            }
        }

        ChatFormatting formatting = ChatFormatting.GOLD;
        if (CRConfig.getGeneral().misc.critical_tooltip == CRGeneralConfig.CriticalTooltip.ALWAYS) formatting = ChatFormatting.DARK_GREEN;

        consumer.accept(
                Component.literal(" ")
                        .append(Component.literal(ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(this.displayAmount * critMultiplier) + " "))
                        .append(Component.translatable("tooltip.combat_reborn.attribute.critical_damage"))
                        .withStyle(formatting)
        );
    }

    @WrapOperation(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/core/Holder;)Z"))
    private boolean multipliedAttributeTooltips(Holder<Attribute> attribute, Holder<Attribute> target, Operation<Boolean> original) {
        return original.call(attribute, target) || attribute.is(CRAttributes.CRITICAL_DAMAGE_BOOST) || attribute.is(CRAttributes.CHARGE_ATTACK_BOOST);
    }
}
