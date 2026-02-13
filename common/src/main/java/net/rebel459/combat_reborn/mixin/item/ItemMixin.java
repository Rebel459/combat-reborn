package net.rebel459.combat_reborn.mixin.item;

import net.rebel459.combat_reborn.registry.CRDataComponents;
import net.rebel459.combat_reborn.util.QuiverContents;
import net.rebel459.combat_reborn.util.QuiverHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(at = @At("HEAD"), method = "releaseUsing")
    private static void quiver(ItemStack itemStack, Level level, LivingEntity livingEntity, int i, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity instanceof Player player) {
            ItemStack stack = QuiverHelper.getStack(player);
            if (stack != null) {
                QuiverContents.Mutable mutable = new QuiverContents.Mutable(stack.get(CRDataComponents.QUIVER_CONTENTS.get()));
                QuiverHelper.updateFullness(stack, mutable);
            }
        }
    }
}