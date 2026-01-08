package net.legacy.combat_reborn.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.util.QuiverHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BowItem.class)
public abstract class BowItemMixin {

    @Unique
    Player player;

    @Inject(at = @At(value = "HEAD"), method = "releaseUsing")
    private void getPlayer(ItemStack itemStack, Level level, LivingEntity livingEntity, int i, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity instanceof Player player) this.player = player;
    }

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BowItem;getPowerForTime(I)F"), method = "releaseUsing")
    private float quiver(int i, Operation<Float> original) {
        float base = original.call(i);
        if (CRConfig.get().general.quivers.ranged_rebalance) base *= 0.9F;
        ItemStack stack = QuiverHelper.getStack(this.player);
        if (stack != null) {
            return base * QuiverHelper.getBowSpeed(stack);
        }
        return base;
    }
}