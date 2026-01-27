package net.legacy.combat_reborn;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.legacy.combat_reborn.client.ClientQuiverTooltip;
import net.legacy.combat_reborn.item.QuiverItem;
import net.legacy.combat_reborn.network.SelectQuiverSlotPacket;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CRDataComponents;
import net.legacy.combat_reborn.util.QuiverContents;
import net.legacy.combat_reborn.util.QuiverHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class CombatRebornClient implements ClientModInitializer {

    public static final KeyMapping QUIVER_KEY = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                    "key.combat_reborn.quiver",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_G,
                    KeyMapping.Category.GAMEPLAY
            )
    );

    @Override
    public void onInitializeClient() {
        TooltipComponentCallback.EVENT.register((component) -> component instanceof QuiverItem.Tooltip(ItemStack quiver) ? new ClientQuiverTooltip(quiver) : null);

        ClientPlayNetworking.registerGlobalReceiver(ShieldInfo.Sync.TYPE, (payload, context) -> {
            Player player = context.player();
            if (player instanceof ShieldInfo shieldInfo) {
                shieldInfo.setPercentageDamage(payload.percentageDamage());
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (QUIVER_KEY.consumeClick()) {
                if (client.player == null) return;

                ItemStack quiverStack = QuiverHelper.getStack(client.player);

                if (quiverStack == null) return;

                QuiverContents contents = quiverStack.getOrDefault(CRDataComponents.QUIVER_CONTENTS, QuiverContents.empty(QuiverHelper.getType(quiverStack)));
                if (contents.isEmpty()) return;

                Integer currentRaw = quiverStack.get(CRDataComponents.QUIVER_CONTENTS_SLOT);
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

                quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT, nextSlot);

                SelectQuiverSlotPacket packet = new SelectQuiverSlotPacket(nextSlot);
                ClientPlayNetworking.send(packet);

                client.player.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 0.5F, 1.0F);
            }
        });
    }
}