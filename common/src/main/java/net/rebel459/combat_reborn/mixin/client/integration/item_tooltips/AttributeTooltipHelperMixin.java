package net.rebel459.combat_reborn.mixin.client.integration.item_tooltips;

import net.rebel459.combat_reborn.client.util.AttributeTooltipHelper;
import net.rebel459.item_tooltips.util.ScreenHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AttributeTooltipHelper.class)
public class AttributeTooltipHelperMixin {

    @Inject(method = "hasKeyDown", at = @At(value = "HEAD"), cancellable = true)
    private static void hasConfiguredKeyDown(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(ScreenHelper.Tooltip.hasKeyDown());
    }
}