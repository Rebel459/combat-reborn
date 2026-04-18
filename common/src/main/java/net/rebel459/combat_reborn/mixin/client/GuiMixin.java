package net.rebel459.combat_reborn.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.rebel459.combat_reborn.CombatReborn;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.config.CRGeneralConfig;
import net.rebel459.combat_reborn.network.ShieldInfo;
import net.rebel459.combat_reborn.registry.CRDataComponents;
import net.rebel459.combat_reborn.tag.CRItemTags;
import net.rebel459.combat_reborn.util.DamageHelper;
import net.rebel459.combat_reborn.util.QuiverContents;
import net.rebel459.combat_reborn.util.QuiverHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract boolean canRenderCrosshairForSpectator(@Nullable HitResult hitResult);

    @Shadow
    protected abstract void extractSlot(GuiGraphicsExtractor graphics, int i, int j, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int k);

    @Unique
    private static final Identifier CROSSHAIR_SHIELD_INDICATOR_FULL = CombatReborn.id("hud/crosshair_shield_indicator_full");
    @Unique
    private static final Identifier CROSSHAIR_SHIELD_INDICATOR_BACKGROUND = CombatReborn.id("hud/crosshair_shield_indicator_background");
    @Unique
    private static final Identifier CROSSHAIR_SHIELD_INDICATOR_PROGRESS = CombatReborn.id("hud/crosshair_shield_indicator_progress");

    @Unique
    private static final Identifier TOUGHNESS_HALF_SPRITE = CombatReborn.id("hud/toughness_half");
    @Unique
    private static final Identifier TOUGHNESS_FULL_SPRITE = CombatReborn.id("hud/toughness_full");

    @Inject(method = "extractCrosshair", at = @At(value = "HEAD"))
    private void renderShieldCrosshair(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!CRConfig.getGeneral().shields.shield_overhaul || CRConfig.getGeneral().shields.display_style != CRGeneralConfig.ShieldDisplay.CROSSHAIR) return;
        Options options = this.minecraft.options;
        if (options.getCameraType().isFirstPerson()) {
            if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
                if (!this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR)) {
                    graphics.nextStratum();
                    Player player = this.minecraft.player;
                    if (!(player instanceof ShieldInfo info)) return;

                    int percentageBlocked = info.getPercentageDamage();

                    if (!player.getUseItem().is(CRItemTags.SHIELD) && percentageBlocked == 0) return;

                    float f = 1F - percentageBlocked / 100F;

                    int size = 16;

                    int j = graphics.guiHeight() / 2 - 7 + 16 + 8;
                    int k = graphics.guiWidth() / 2 - 8;

                    if (f >= 1F) {
                        graphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_SHIELD_INDICATOR_FULL, k, j, size, size);
                    } else {
                        int l = (int) (f * size * 1.0625);
                        graphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_SHIELD_INDICATOR_BACKGROUND, k, j, size, size);
                        graphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_SHIELD_INDICATOR_PROGRESS, size, size, 0, 0, k, j, l, size);
                    }
                }
            }
        }
    }

    @Inject(method = "extractItemHotbar", at = @At("TAIL"))
    private void renderQuiverSelectedArrow(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack quiverStack = QuiverHelper.getQuiver(player);

        if (quiverStack == null) return;

        int selectedSlot = Math.max(quiverStack.get(CRDataComponents.QUIVER_CONTENTS_SLOT.get()), 0);

        QuiverContents contents = quiverStack.getOrDefault(CRDataComponents.QUIVER_CONTENTS.get(), QuiverContents.empty(QuiverHelper.getType(quiverStack)));
        if (contents.items.isEmpty()) return;

        ItemStack selectedArrow = contents.getItemUnsafe(selectedSlot).copy();
        if (selectedArrow.isEmpty()) return;

        int centerX = graphics.guiWidth() / 2;

        HumanoidArm mainArm = player.getMainArm();
        boolean isLeftHanded = mainArm == HumanoidArm.LEFT;

        Identifier backgroundSprite = isLeftHanded ? Gui.HOTBAR_OFFHAND_LEFT_SPRITE : Gui.HOTBAR_OFFHAND_RIGHT_SPRITE;

        int bgX = isLeftHanded ? centerX - 91 - 29 : centerX + 91;
        int bgY = graphics.guiHeight() - 23;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, backgroundSprite, bgX, bgY, 29, 24);

        int arrowX = isLeftHanded ? centerX - 91 - 26 : centerX + 91 + 10;
        int arrowY = graphics.guiHeight() - 16 - 3;

        extractSlot(graphics, arrowX, arrowY, deltaTracker, player, selectedArrow, 0);
    }

    @WrapOperation(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IILnet/minecraft/client/DeltaTracker;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V"))
    private void renderQuivers(Gui instance, GuiGraphicsExtractor graphics, int i, int j, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int k, Operation<Void> original) {
        if (!itemStack.is(CRItemTags.QUIVER)) {
            original.call(instance, graphics, i, j, deltaTracker, player, itemStack, k);
            return;
        }
        ItemStack quiver = itemStack.copy();
        QuiverContents.Mutable mutable = new QuiverContents.Mutable(quiver.get(CRDataComponents.QUIVER_CONTENTS.get()));
        QuiverHelper.updateFullness(quiver, mutable);
        original.call(instance, graphics, i, j, deltaTracker, player, quiver, k);
    }

    @Inject(method = "extractArmor", at = @At("TAIL"))
    private static void renderArmorToughness(GuiGraphicsExtractor graphics, Player player, int i, int j, int k, int l, CallbackInfo ci) {
        if (!CRConfig.getGeneral().armor.toughness.toughness_overlay) return;

        int m;
        if (CRConfig.getGeneral().armor.toughness.toughness_type == CRGeneralConfig.ToughnessMechanics.DURABILITY) m = (int) Math.ceil(DamageHelper.getDurabilityToughness(player, (float) player.getAttributeValue(Attributes.ARMOR_TOUGHNESS)));
        else if (CRConfig.getGeneral().armor.toughness.toughness_type == CRGeneralConfig.ToughnessMechanics.NONE) return;
        else m = (int) Math.ceil(player.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        m = Math.min(m, player.getArmorValue());

        if (m > 0) {
            int n = i - (j - 1) * k - 10;

            for(int o = 0; o < 10; ++o) {
                int p = l + o * 8;
                if (o * 2 + 1 < m) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TOUGHNESS_FULL_SPRITE, p, n, 9, 9);
                }

                if (o * 2 + 1 == m) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TOUGHNESS_HALF_SPRITE, p, n, 9, 9);
                }
            }

        }
    }
}