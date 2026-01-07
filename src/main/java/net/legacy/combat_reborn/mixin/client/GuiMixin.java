package net.legacy.combat_reborn.mixin.client;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.legacy.combat_reborn.CombatReborn;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.config.CRGeneralConfig;
import net.legacy.combat_reborn.item.QuiverItem;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CRDataComponents;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.legacy.combat_reborn.util.ClientTickInterface;
import net.legacy.combat_reborn.util.QuiverContents;
import net.legacy.combat_reborn.util.QuiverHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract boolean canRenderCrosshairForSpectator(@Nullable HitResult hitResult);

    @Shadow
    protected abstract void renderSlot(GuiGraphics guiGraphics, int i, int j, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int k);

    @Unique
    private static final Identifier HOTBAR_SHIELD_INDICATOR_FULL = CombatReborn.id("hud/hotbar_shield_indicator_full");
    @Unique
    private static final Identifier HOTBAR_SHIELD_INDICATOR_BACKGROUND = CombatReborn.id("hud/hotbar_shield_indicator_background");
    @Unique
    private static final Identifier HOTBAR_SHIELD_INDICATOR_PROGRESS = CombatReborn.id("hud/hotbar_shield_indicator_progress");

    @Unique
    private static final Identifier CROSSHAIR_SHIELD_INDICATOR_FULL = CombatReborn.id("hud/crosshair_shield_indicator_full");
    @Unique
    private static final Identifier CROSSHAIR_SHIELD_INDICATOR_BACKGROUND = CombatReborn.id("hud/crosshair_shield_indicator_background");
    @Unique
    private static final Identifier CROSSHAIR_SHIELD_INDICATOR_PROGRESS = CombatReborn.id("hud/crosshair_shield_indicator_progress");

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
        if (!CRConfig.get().general.shields.shield_overhaul || CRConfig.get().general.shields.display_style != CRGeneralConfig.ShieldDisplay.HOTBAR) return;
        Options options = this.minecraft.options;
        if (options.getCameraType().isFirstPerson()) {
            if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
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
    }

    @Inject(method = "renderCrosshair", at = @At(value = "HEAD", target = "Lnet/minecraft/client/Options;attackIndicator()Lnet/minecraft/client/OptionInstance;"))
    private void renderShieldCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!CRConfig.get().general.shields.shield_overhaul || CRConfig.get().general.shields.display_style != CRGeneralConfig.ShieldDisplay.CROSSHAIR) return;
        Options options = this.minecraft.options;
        if (options.getCameraType().isFirstPerson()) {
            if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
                if (!this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR)) {
                    guiGraphics.nextStratum();
                    Player player = this.minecraft.player;
                    if (!(player instanceof ShieldInfo info)) return;

                    int percentageBlocked = info.getPercentageDamage();

                    if (!player.getUseItem().is(CRItemTags.SHIELD) && percentageBlocked == 0) return;

                    float f = 1F - percentageBlocked / 100F;

                    int size = 16;

                    int j = guiGraphics.guiHeight() / 2 - 7 + 16 + 8;
                    int k = guiGraphics.guiWidth() / 2 - 8;

                    if (f >= 1F) {
                        guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_SHIELD_INDICATOR_FULL, k, j, size, size);
                    } else {
                        int l = (int) (f * size * 1.0625);
                        guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_SHIELD_INDICATOR_BACKGROUND, k, j, size, size);
                        guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_SHIELD_INDICATOR_PROGRESS, size, size, 0, 0, k, j, l, size);
                    }
                }
            }
        }
    }

    @Inject(method = "renderItemHotbar", at = @At("TAIL"))
    private void renderQuiverSelectedArrow(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack quiverStack = QuiverHelper.getQuiver(player);

        if (quiverStack == null) return;

        int selectedSlot = Math.max(quiverStack.get(CRDataComponents.QUIVER_CONTENTS_SLOT), 0);

        QuiverContents contents = quiverStack.getOrDefault(CRDataComponents.QUIVER_CONTENTS, QuiverContents.getEmpty(QuiverHelper.getType(quiverStack)));
        if (contents.items.isEmpty()) return;

        ItemStack selectedArrow = contents.getItemUnsafe(selectedSlot).copy();
        if (selectedArrow.isEmpty()) return;

        int centerX = guiGraphics.guiWidth() / 2;

        HumanoidArm mainArm = player.getMainArm();
        boolean isLeftHanded = mainArm == HumanoidArm.LEFT;

        Identifier backgroundSprite = isLeftHanded ? Gui.HOTBAR_OFFHAND_LEFT_SPRITE : Gui.HOTBAR_OFFHAND_RIGHT_SPRITE;

        int bgX = isLeftHanded ? centerX - 91 - 29 : centerX + 91;
        int bgY = guiGraphics.guiHeight() - 23;
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, backgroundSprite, bgX, bgY, 29, 24);

        int pos1 = centerX - 91 - 26;
        int pos2 = centerX + 91 + 10;

        int arrowX = isLeftHanded ? pos1 : pos2;
        int quiverX = !isLeftHanded ? pos1 : pos2;
        int arrowY = guiGraphics.guiHeight() - 16 - 3;

        ItemStack renderedQuiver = quiverStack.copy();
        renderedQuiver.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(false), List.of(), List.of()))
                .build()
        );

        renderSlot(guiGraphics, arrowX, arrowY, deltaTracker, player, selectedArrow, 0);
        renderSlot(guiGraphics, quiverX, arrowY, deltaTracker, player, renderedQuiver, 0);
        guiGraphics.renderItemDecorations(this.minecraft.font, quiverStack, quiverX, arrowY);
    }
}