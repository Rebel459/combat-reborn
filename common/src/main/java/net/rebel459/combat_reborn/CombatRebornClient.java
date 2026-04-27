package net.rebel459.combat_reborn;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.GameType;
import net.rebel459.combat_reborn.client.ClientQuiverTooltip;
import net.rebel459.combat_reborn.client.QuiverMouseActions;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.config.CRGeneralConfig;
import net.rebel459.combat_reborn.item.QuiverItem;
import net.rebel459.combat_reborn.network.SelectQuiverSlotPacket;
import net.rebel459.combat_reborn.network.ShieldInfo;
import net.rebel459.combat_reborn.registry.CRAttributes;
import net.rebel459.combat_reborn.registry.CRDataComponents;
import net.rebel459.combat_reborn.tag.CRItemTags;
import net.rebel459.combat_reborn.util.ClientTickInterface;
import net.rebel459.combat_reborn.util.QuiverContents;
import net.rebel459.combat_reborn.util.QuiverHelper;
import net.rebel459.unified.platform.client.UnifiedClientEvents;
import net.rebel459.unified.platform.client.UnifiedClientHelpers;
import net.rebel459.unified.platform.client.UnifiedClientRegistries;
import net.rebel459.unified.util.EventType;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class CombatRebornClient {

    private static final Identifier HOTBAR_SHIELD_INDICATOR_FULL = CombatReborn.id("hud/hotbar_shield_indicator_full");
    private static final Identifier HOTBAR_SHIELD_INDICATOR_BACKGROUND = CombatReborn.id("hud/hotbar_shield_indicator_background");
    private static final Identifier HOTBAR_SHIELD_INDICATOR_PROGRESS = CombatReborn.id("hud/hotbar_shield_indicator_progress");

    private static final RenderPipeline SHIELD_INDICATOR = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET).withLocation("pipeline/shield_indicator").withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)).build()
    );
    private static final RenderPipeline SHIELD_INDICATOR_BACKGROUND = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET).withLocation("pipeline/shield_indicator_background").withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY)).build()
    );

    public static UnifiedClientRegistries.KeyMappings KEY_MAPPINGS = UnifiedClientRegistries.KeyMappings.create(CombatReborn.MOD_ID);

    public static final Supplier<KeyMapping> QUIVER_KEY = KEY_MAPPINGS.registerKeybind(
                    "quiver",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_G,
                    KeyMapping.Category.GAMEPLAY
    );

    public static void initClient() {
        UnifiedClientHelpers.TOOLTIPS.bind(QuiverItem.Tooltip.class, tooltip -> new ClientQuiverTooltip(tooltip.quiver()));

        UnifiedClientEvents.Screens.initAbstractContainerScreen(screen -> {
            screen.addItemSlotMouseAction(new QuiverMouseActions(screen.minecraft));
        });

        UnifiedClientEvents.Guis.renderHotbar(((gui, guiGraphics, deltaTracker) -> {
            if (!CRConfig.getGeneral().shields.shield_overhaul || CRConfig.getGeneral().shields.display_style != CRGeneralConfig.ShieldDisplay.HOTBAR || gui.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) return;
            guiGraphics.nextStratum();
            Player player = gui.minecraft.player;
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
        }));

        UnifiedClientEvents.Instance.onTick(EventType.POST, client -> {
            while (QUIVER_KEY.get().consumeClick()) {
                if (client.player == null) return;

                ItemStack quiverStack = QuiverHelper.getStack(client.player);

                if (quiverStack == null) return;

                QuiverContents contents = quiverStack.getOrDefault(CRDataComponents.QUIVER_CONTENTS.get(), QuiverContents.empty(QuiverHelper.getType(quiverStack)));
                if (contents.isEmpty()) return;

                Integer currentRaw = quiverStack.get(CRDataComponents.QUIVER_CONTENTS_SLOT.get());
                int current = (currentRaw != null && currentRaw >= 0) ? currentRaw : -1;

                int nextSlot = (current + 1) % contents.size();
                if (current == -1) {
                    nextSlot = 0;
                }

                double stacks = contents.items.size();
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putDouble("quiver_stacks", stacks);
                quiverStack.applyComponents(DataComponentPatch.builder()
                        .set(DataComponents.CUSTOM_DATA, CustomData.of(compoundTag))
                        .build()
                );
                LogUtils.getLogger().info("client: " + compoundTag);

                quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT.get(), nextSlot);

                SelectQuiverSlotPacket packet = new SelectQuiverSlotPacket(nextSlot);
                UnifiedClientHelpers.NETWORKING.send(packet);

                client.player.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 0.5F, 1.0F);
            }
        });

        UnifiedClientEvents.ItemTooltips.afterBaseAttributeAdded((builder, stack, itemModifiers, player, attribute, displayValue) -> {
            if (CRConfig.getGeneral().tooltips.critical_tooltip == CRGeneralConfig.CriticalTooltip.NONE) return;
            if (player == null || itemModifiers == null || !attribute.is(Attributes.ATTACK_DAMAGE)) return;
            if (!hasKeyDown() && CRConfig.getGeneral().tooltips.critical_tooltip == CRGeneralConfig.CriticalTooltip.SHIFT) return;
            if (stack != null && (!(stack.has(DataComponents.WEAPON) || stack.has(DataComponents.TOOL)) || stack.has(DataComponents.KINETIC_WEAPON))) return;

            List<ItemAttributeModifiers.Entry> modifiers = new ArrayList<>(itemModifiers.modifiers());
            modifiers.removeIf(entry -> !entry.attribute().is(CRAttributes.CRITICAL_DAMAGE_BOOST));

            double critMultiplier = player.getAttributeBaseValue(CRAttributes.CRITICAL_DAMAGE_BOOST);
            for (ItemAttributeModifiers.Entry entry : modifiers) {
                var critModifier = entry.modifier();
                if (critModifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                    critMultiplier += critModifier.amount();
                }
                else if (critModifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE) {
                    critMultiplier += player.getAttributeBaseValue(CRAttributes.CRITICAL_DAMAGE_BOOST) * critModifier.amount();
                }
                else if (critModifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    critMultiplier += critMultiplier * critModifier.amount();
                }
            }

            ChatFormatting formatting = ChatFormatting.GOLD;
            if (CRConfig.getGeneral().tooltips.critical_tooltip == CRGeneralConfig.CriticalTooltip.ALWAYS) formatting = ChatFormatting.DARK_GREEN;

            builder.accept(
                    Component.literal(" ")
                            .append(Component.literal(ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(displayValue * critMultiplier) + " "))
                            .append(Component.translatable("tooltip.combat_reborn.attribute.critical_damage"))
                            .withStyle(formatting)
            );
        });
    }

    public static boolean hasKeyDown() {
        return Minecraft.getInstance().hasShiftDown();
    }
}