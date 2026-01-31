package net.legacy.combat_reborn.mixin.client;

import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.config.CRGeneralConfig;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.util.ClientTickInterface;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements ClientTickInterface {

    @Unique
    int localTick = 0;

    @Unique
    int clientTick = maxTicks;

    @Override
    public int getClientTicks() {
        return this.clientTick;
    }

    @Override
    public void setClientTicks(int ticks) {
        this.clientTick = ticks;
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void renderShieldCrosshair(CallbackInfo ci) {
        if (!CRConfig.get.general.shields.shield_overhaul || CRConfig.get.general.shields.display_style == CRGeneralConfig.ShieldDisplay.CROSSHAIR) return;
        this.localTick++;
        if (this.localTick >= 5) {
            LocalPlayer player = LocalPlayer.class.cast(this);
            if (!(player instanceof ShieldInfo info)) return;
            int damage = info.getPercentageDamage();
            if (damage == 0 && this.clientTick < maxTicks) {
                this.setClientTicks(Math.min(this.clientTick + this.localTick, maxTicks));
            }
            else if (damage > 0) {
                this.setClientTicks(0);
            }
            this.localTick = 0;
        }
    }
}