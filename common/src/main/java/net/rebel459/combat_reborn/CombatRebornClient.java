package net.rebel459.combat_reborn;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.rebel459.combat_reborn.client.ClientQuiverTooltip;
import net.rebel459.combat_reborn.item.QuiverItem;
import net.rebel459.combat_reborn.network.SelectQuiverSlotPacket;
import net.rebel459.combat_reborn.registry.CRDataComponents;
import net.rebel459.combat_reborn.util.QuiverContents;
import net.rebel459.combat_reborn.util.QuiverHelper;
import net.rebel459.unified.platform.client.UnifiedClientEvents;
import net.rebel459.unified.platform.client.UnifiedClientHelpers;
import net.rebel459.unified.platform.client.UnifiedClientRegistries;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public final class CombatRebornClient {

    public static UnifiedClientRegistries.KeyMappings KEY_MAPPINGS = UnifiedClientRegistries.KeyMappings.create(CombatReborn.MOD_ID);

    public static final Supplier<KeyMapping> QUIVER_KEY = KEY_MAPPINGS.registerKeybind(
                    "quiver",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_G,
                    KeyMapping.Category.GAMEPLAY
    );

    public static void initClient() {
        UnifiedClientHelpers.Tooltips.get().bind(QuiverItem.Tooltip.class, tooltip -> new ClientQuiverTooltip(tooltip.quiver()));

        UnifiedClientEvents.EndTick.access(client -> {
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
                UnifiedClientHelpers.NetworkPayloads.get().send(packet);

                client.player.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 0.5F, 1.0F);
            }
        });
    }
}