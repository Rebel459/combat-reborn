package net.legacy.combat_reborn.mixin.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.legacy.combat_reborn.CombatReborn;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.config.CRGeneralConfig;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.legacy.combat_reborn.util.ClientTickInterface;
import net.legacy.combat_reborn.util.DamageHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract boolean canRenderCrosshairForSpectator(HitResult hitResult);

    @Unique
    private static final ResourceLocation HOTBAR_SHIELD_INDICATOR_FULL = CombatReborn.id("hud/hotbar_shield_indicator_full");
    @Unique
    private static final ResourceLocation HOTBAR_SHIELD_INDICATOR_BACKGROUND = CombatReborn.id("hud/hotbar_shield_indicator_background");
    @Unique
    private static final ResourceLocation HOTBAR_SHIELD_INDICATOR_PROGRESS = CombatReborn.id("hud/hotbar_shield_indicator_progress");

    @Unique
    private static final ResourceLocation CROSSHAIR_SHIELD_INDICATOR_FULL = CombatReborn.id("hud/crosshair_shield_indicator_full");
    @Unique
    private static final ResourceLocation CROSSHAIR_SHIELD_INDICATOR_BACKGROUND = CombatReborn.id("hud/crosshair_shield_indicator_background");
    @Unique
    private static final ResourceLocation CROSSHAIR_SHIELD_INDICATOR_PROGRESS = CombatReborn.id("hud/crosshair_shield_indicator_progress");

    @Unique
    private static final ResourceLocation TOUGHNESS_HALF_SPRITE = CombatReborn.id("hud/toughness_half");
    @Unique
    private static final ResourceLocation TOUGHNESS_FULL_SPRITE = CombatReborn.id("hud/toughness_full");

    @Inject(method = "renderHotbarAndDecorations", at = @At(value = "HEAD"))
    private void renderShieldHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!CRConfig.get.general.shields.shield_overhaul || CRConfig.get.general.shields.display_style != CRGeneralConfig.ShieldDisplay.HOTBAR) return;
        Options options = this.minecraft.options;
        if (options.getCameraType().isFirstPerson()) {
            if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
                Player player = this.minecraft.player;
                if (!(player instanceof ShieldInfo info)) return;
                if (!(player instanceof ClientTickInterface fullTicks)) return;

                int percentageBlocked = info.getPercentageDamage();

                if (!player.getUseItem().is(CRItemTags.SHIELD) && percentageBlocked == 0 && fullTicks.getClientTicks() >= ClientTickInterface.maxTicks) return;

                float f = 1F - percentageBlocked / 100F;

                int size = 12;

                int j = guiGraphics.guiHeight() - 49; // height
                int k = guiGraphics.guiWidth() / 2 - size / 2; // width

                RenderSystem.enableBlend();

                if (f >= 1F) {
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    guiGraphics.blitSprite(HOTBAR_SHIELD_INDICATOR_FULL, k, j, size, size);
                } else {
                    int l = (int) (f * size * 1.0625);
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    guiGraphics.blitSprite(HOTBAR_SHIELD_INDICATOR_BACKGROUND, k, j, size, size);
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    guiGraphics.blitSprite(HOTBAR_SHIELD_INDICATOR_PROGRESS, size, size, 0, 0, k, j, l, size);
                }

                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();
            }
        }
    }

    @Inject(method = "renderCrosshair", at = @At(value = "HEAD", target = "Lnet/minecraft/client/Options;attackIndicator()Lnet/minecraft/client/OptionInstance;"))
    private void renderShieldCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!CRConfig.get.general.shields.shield_overhaul || CRConfig.get.general.shields.display_style != CRGeneralConfig.ShieldDisplay.CROSSHAIR) return;
        Options options = this.minecraft.options;
        if (options.getCameraType().isFirstPerson()) {
            if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
                Player player = this.minecraft.player;
                if (!(player instanceof ShieldInfo info)) return;

                int percentageBlocked = info.getPercentageDamage();

                if (!player.getUseItem().is(CRItemTags.SHIELD) && percentageBlocked == 0) return;

                float f = 1F - percentageBlocked / 100F;

                int size = 16;

                int j = guiGraphics.guiHeight() / 2 - 7 + 16 + 8;
                int k = guiGraphics.guiWidth() / 2 - 8;

                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                if (f >= 1F) {
                    guiGraphics.blitSprite(CROSSHAIR_SHIELD_INDICATOR_FULL, k, j, size, size);
                } else {
                    int l = (int) (f * size * 1.0625);
                    guiGraphics.blitSprite(CROSSHAIR_SHIELD_INDICATOR_BACKGROUND, k, j, size, size);
                    guiGraphics.blitSprite(CROSSHAIR_SHIELD_INDICATOR_PROGRESS, size, size, 0, 0, k, j, l, size);
                }

                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();
            }
        }
    }

    @Inject(method = "renderArmor", at = @At("TAIL"))
    private static void renderArmorToughness(GuiGraphics guiGraphics, Player player, int i, int j, int k, int l, CallbackInfo ci) {
        if (!CRConfig.get.general.armor.toughness.toughness_overlay) return;

        int m;
        if (CRConfig.get.general.armor.toughness.toughness_type == CRGeneralConfig.ToughnessMechanics.DURABILITY) m = (int) Math.ceil(DamageHelper.getDurabilityToughness(player, (float) player.getAttributeValue(Attributes.ARMOR_TOUGHNESS)));
        else if (CRConfig.get.general.armor.toughness.toughness_type == CRGeneralConfig.ToughnessMechanics.NONE) return;
        else m = (int) Math.ceil(player.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        m = Math.min(m, player.getArmorValue());

        if (m > 0) {
            int n = i - (j - 1) * k - 10;
            RenderSystem.enableBlend();
            for(int o = 0; o < 10; ++o) {
                int p = l + o * 8;
                if (o * 2 + 1 < m) {
                    guiGraphics.blitSprite(TOUGHNESS_FULL_SPRITE, p, n, 9, 9);
                }

                if (o * 2 + 1 == m) {
                    guiGraphics.blitSprite(TOUGHNESS_HALF_SPRITE, p, n, 9, 9);
                }
            }
            RenderSystem.disableBlend();
        }
    }
}