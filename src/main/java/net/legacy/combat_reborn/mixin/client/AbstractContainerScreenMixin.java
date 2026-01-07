package net.legacy.combat_reborn.mixin.client;

import net.legacy.combat_reborn.client.QuiverMouseActions;
import net.minecraft.client.gui.BundleMouseActions;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Inject(method = "init", at = @At(value = "TAIL"))
    private void addQuiver(CallbackInfo ci) {
        AbstractContainerScreen screen = AbstractContainerScreen.class.cast(this);
        ((AbstractContainerScreen<?>) screen).addItemSlotMouseAction(new QuiverMouseActions(((AbstractContainerScreen<?>) screen).minecraft));
    }
}