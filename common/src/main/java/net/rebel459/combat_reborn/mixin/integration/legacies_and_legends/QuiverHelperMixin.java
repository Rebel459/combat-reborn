package net.rebel459.combat_reborn.mixin.integration.legacies_and_legends;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.rebel459.combat_reborn.tag.CRItemTags;
import net.rebel459.combat_reborn.util.QuiverHelper;
import net.rebel459.legacies_and_legends.util.AccessoryHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(QuiverHelper.class)
public abstract class QuiverHelperMixin {

    @Inject(at = @At("TAIL"), method = "getQuiver", cancellable = true)
    private static void quiverAccessory(Player player, CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue() == null) {
            ItemStack stack = AccessoryHelper.getAccessory(player);
            if (stack.is(CRItemTags.QUIVER)) {
                cir.setReturnValue(stack);
            }
        }
    }
}