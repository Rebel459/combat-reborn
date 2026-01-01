package net.legacy.combat_reborn.mixin.item;

import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(at = @At("TAIL"), method = "getEnchantmentValue", cancellable = true)
    private void overrideEnchantmentValue(CallbackInfoReturnable<Integer> cir) {
        Item item = Item.class.cast(this);
        if (CRConfig.get().general.combat.shield_overhaul && item.getDefaultInstance().is(CRItemTags.SHIELD) && cir.getReturnValue() == 0) {
            cir.setReturnValue(10);
        }
    }
}