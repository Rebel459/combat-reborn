package net.legacy.combat_reborn.mixin.item;

import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.config.CRWeaponConfig;
import net.legacy.combat_reborn.item.ItemAttributeModifierCallback;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {

    @Inject(at = @At("HEAD"), method = "createAttributes", cancellable = true)
    private static void tridentModifier(CallbackInfoReturnable<ItemAttributeModifiers> cir) {
        Optional<CRWeaponConfig.Modifiers> optionalToolsModifier = CRConfig.get.weapons.sets.stream()
                .filter(modifier -> modifier.ids.contains("minecraft:trident"))
                .findFirst();
        if (optionalToolsModifier.isEmpty()) return;
        cir.setReturnValue(
                ItemAttributeModifierCallback.createAttributeModifiers(
                        optionalToolsModifier.get().damage - ItemAttributeModifierCallback.DEFAULT_ATTACK_DAMAGE,
                        optionalToolsModifier.get().speed - ItemAttributeModifierCallback.DEFAULT_ATTACK_SPEED,
                        optionalToolsModifier.get().reach - ItemAttributeModifierCallback.DEFAULT_ATTACK_RANGE,
                        optionalToolsModifier.get().attributes
                )
        );
    }
}