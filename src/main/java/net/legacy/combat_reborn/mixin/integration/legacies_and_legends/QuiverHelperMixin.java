package net.legacy.combat_reborn.mixin.integration.legacies_and_legends;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.legacy.combat_reborn.util.QuiverHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(QuiverHelper.class)
public abstract class QuiverHelperMixin {

    @Inject(at = @At("TAIL"), method = "getQuiver", cancellable = true)
    private static void quiverAccessory(Player player, CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue() == null) {
            if (TrinketsApi.getTrinketComponent(player).isPresent()) {
                var trinkets = TrinketsApi.getTrinketComponent(player).get().getAllEquipped();
                ItemStack stack = null;
                for (net.minecraft.util.Tuple<SlotReference, ItemStack> slotReferenceItemStackTuple : trinkets) {
                    ItemStack trinket = slotReferenceItemStackTuple.getB();
                    if (trinket.is(CRItemTags.QUIVER)) {
                        stack = trinket;
                        break;
                    }
                }
                cir.setReturnValue(stack);
            }
        }
    }
}