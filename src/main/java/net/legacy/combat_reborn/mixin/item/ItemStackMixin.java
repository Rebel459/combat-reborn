package net.legacy.combat_reborn.mixin.item;

import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(at = @At("TAIL"), method = "getMaxStackSize", cancellable = true)
    private void overrideStackSize(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = ItemStack.class.cast(this);
        if (((CRConfig.get().general.consumables.stackable_stews && (stack.is(CRItemTags.SOUP)) || (CRConfig.get().general.consumables.stackable_potions && (stack.is(CRItemTags.POTIONS)))) && stack.getItem().getDefaultMaxStackSize() == 1 && cir.getReturnValue() == 1)) {
            cir.setReturnValue(16);
        }
    }
}