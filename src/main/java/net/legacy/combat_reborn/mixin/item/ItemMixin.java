package net.legacy.combat_reborn.mixin.item;

import net.legacy.combat_reborn.config.CRConfig;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "getUseDuration", at = @At("TAIL"), cancellable = true)
    private void fasterDrinking(ItemStack itemStack, LivingEntity livingEntity, CallbackInfoReturnable<Integer> cir) {
        int original = cir.getReturnValue();
        if (!CRConfig.get.consumables.faster_drinking) cir.setReturnValue(original);
        if (itemStack.is(Items.POTION) && original > 16) {
            cir.setReturnValue(Math.max(16, original / 2));
        }
    }
}