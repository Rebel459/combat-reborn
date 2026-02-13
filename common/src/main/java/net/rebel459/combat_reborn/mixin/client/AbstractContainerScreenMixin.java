package net.rebel459.combat_reborn.mixin.client;

import net.rebel459.combat_reborn.client.QuiverMouseActions;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void addQuiver(CallbackInfo ci) {
        AbstractContainerScreen screen = AbstractContainerScreen.class.cast(this);
        screen.addItemSlotMouseAction(new QuiverMouseActions(((ScreenAccessor)((AbstractContainerScreen<?>) screen)).getMinecraft()));
    }
}