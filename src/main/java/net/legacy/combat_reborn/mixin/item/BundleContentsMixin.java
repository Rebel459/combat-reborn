package net.legacy.combat_reborn.mixin.item;

import net.legacy.combat_reborn.tag.CRItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContents.class)
public abstract class BundleContentsMixin {

    @Inject(at = @At("TAIL"), method = "canItemBeInBundle", cancellable = true)
    private static void quiverCheck(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() && !itemStack.is(CRItemTags.QUIVER));
    }
}