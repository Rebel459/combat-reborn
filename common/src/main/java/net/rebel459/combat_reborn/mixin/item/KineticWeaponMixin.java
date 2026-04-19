package net.rebel459.combat_reborn.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.KineticWeapon;
import net.rebel459.combat_reborn.registry.CRAttributes;
import net.rebel459.combat_reborn.registry.CRDataComponents;
import net.rebel459.combat_reborn.util.QuiverContents;
import net.rebel459.combat_reborn.util.QuiverHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KineticWeapon.class)
public abstract class KineticWeaponMixin {

    @Unique
    private LivingEntity livingEntity;

    @Inject(at = @At("HEAD"), method = "damageEntities")
    private void getStack(ItemStack stack, int ticksRemaining, LivingEntity livingEntity, EquipmentSlot equipmentSlot, CallbackInfo ci) {
        this.livingEntity = livingEntity;
    }

    @ModifyExpressionValue(
            method = "damageEntities",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/item/component/KineticWeapon;damageMultiplier:F"
            )
    )
    private float modifyMultiplier(float original) {
        if (!this.livingEntity.getAttributes().hasAttribute(CRAttributes.CHARGE_ATTACK_BOOST)) return original;
        else return original + (float) this.livingEntity.getAttributeValue(CRAttributes.CHARGE_ATTACK_BOOST);
}
}