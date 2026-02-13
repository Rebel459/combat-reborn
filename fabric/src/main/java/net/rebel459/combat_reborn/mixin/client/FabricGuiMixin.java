package net.rebel459.combat_reborn.mixin.client;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.rebel459.combat_reborn.CombatReborn;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.config.CRGeneralConfig;
import net.rebel459.combat_reborn.network.ShieldInfo;
import net.rebel459.combat_reborn.tag.CRItemTags;
import net.rebel459.combat_reborn.util.ClientTickInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class FabricGuiMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private static final Identifier HOTBAR_SHIELD_INDICATOR_FULL = CombatReborn.id("hud/hotbar_shield_indicator_full");
    @Unique
    private static final Identifier HOTBAR_SHIELD_INDICATOR_BACKGROUND = CombatReborn.id("hud/hotbar_shield_indicator_background");
    @Unique
    private static final Identifier HOTBAR_SHIELD_INDICATOR_PROGRESS = CombatReborn.id("hud/hotbar_shield_indicator_progress");

    @Unique
    private static final RenderPipeline SHIELD_INDICATOR = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET).withLocation("pipeline/shield_indicator").withBlend(BlendFunction.TRANSLUCENT).build()
    );
    @Unique
    private static final RenderPipeline SHIELD_INDICATOR_BACKGROUND = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET).withLocation("pipeline/shield_indicator_background").withBlend(BlendFunction.OVERLAY).build()
    );

    @Inject(method = "renderHotbarAndDecorations", at = @At(value = "HEAD"))
    private void renderShieldHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!CRConfig.get.general.shields.shield_overhaul || CRConfig.get.general.shields.display_style != CRGeneralConfig.ShieldDisplay.HOTBAR || this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) return;
        guiGraphics.nextStratum();
        Player player = this.minecraft.player;
        if (!(player instanceof ShieldInfo info)) return;
        if (!(player instanceof ClientTickInterface fullTicks)) return;

        int percentageBlocked = info.getPercentageDamage();

        if (!player.getUseItem().is(CRItemTags.SHIELD) && percentageBlocked == 0 && fullTicks.getClientTicks() >= ClientTickInterface.maxTicks) return;

        float f = 1F - percentageBlocked / 100F;

        int size = 12;

        int j = guiGraphics.guiHeight() - 49; // height
        int k = guiGraphics.guiWidth() / 2 - size / 2; // width

        if (f >= 1F) {
            guiGraphics.blitSprite(SHIELD_INDICATOR, HOTBAR_SHIELD_INDICATOR_FULL, k, j, size, size);
        } else {
            int l = (int) (f * size * 1.0625);
            guiGraphics.blitSprite(SHIELD_INDICATOR_BACKGROUND, HOTBAR_SHIELD_INDICATOR_BACKGROUND, k, j, size, size);
            guiGraphics.blitSprite(SHIELD_INDICATOR, HOTBAR_SHIELD_INDICATOR_PROGRESS, size, size, 0, 0, k, j, l, size);
        }
    }
}