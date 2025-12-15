package net.legacy.combat_reborn.mixin.client;

import com.mojang.logging.LogUtils;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract boolean canRenderCrosshairForSpectator(@Nullable HitResult hitResult);

    @Shadow
    @Final
    private static Identifier CROSSHAIR_SPRITE;

    @Inject(method = "renderCrosshair", at = @At(value = "HEAD", target = "Lnet/minecraft/client/Options;attackIndicator()Lnet/minecraft/client/OptionInstance;"), cancellable = true)
    private void renderShieldCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Player player = this.minecraft.player;
        if (!CRConfig.get.combat.shield_overhaul || !player.getUseItem().is(CRItemTags.SHIELD)) return;
        Options options = this.minecraft.options;
        if (options.getCameraType().isFirstPerson()) {
            if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
                if (!this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR)) {
                    guiGraphics.nextStratum();
                    guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_SPRITE, (guiGraphics.guiWidth() - 15) / 2, (guiGraphics.guiHeight() - 15) / 2, 15, 15);
                    if (player.getUseItem() != null) {
                        if (!(player instanceof ShieldInfo info)) return;

                        int percentageBlocked = info.getPercentageDamage();

                        float f = 1F - percentageBlocked / 100F;

                        int j = guiGraphics.guiHeight() / 2 - 7 + 16;
                        int k = guiGraphics.guiWidth() / 2 - 8;
                        if (f >= 1F) {
                            guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, Gui.CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, k, j, 16, 16);
                        } else {
                            int l = (int) (f * 17.0F);
                            guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, Gui.CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k, j, 16, 4);
                            guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, Gui.CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, k, j, l, 4);
                        }
                    }
                    ci.cancel();
                }

            }
        }
    }
}