package net.legacy.combat_reborn;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.entity.PlayerSpawnCallback;
import net.legacy.combat_reborn.item.AttributeModifierCallback;
import net.legacy.combat_reborn.item.QuiverItem;
import net.legacy.combat_reborn.network.SelectQuiverItemPacket;
import net.legacy.combat_reborn.network.SelectQuiverSlotPacket;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CRDataComponents;
import net.legacy.combat_reborn.registry.CREnchantments;
import net.legacy.combat_reborn.registry.CRItems;
import net.legacy.combat_reborn.sound.CRSounds;
import net.legacy.combat_reborn.util.QuiverHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class CombatReborn implements ModInitializer {

    public static boolean isEndRebornLoaded = false;
    public static boolean isEnchantsAndExpeditionsLoaded = false;
    public static boolean isLegaciesAndLegendsLoaded = false;

	@Override
	public void onInitialize() {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(MOD_ID);
        AutoConfig.register(CRConfig.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));

        CRDataComponents.init();
        CRItems.init();
        CREnchantments.init();
        CRSounds.init();

        AttributeModifierCallback.init();
        PlayerSpawnCallback.init();

        registerPayloads();

        if (!CRConfig.get().general.shields.shield_overhaul) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    CombatReborn.id("no_shield_overhaul"), modContainer.get(),
                    Component.translatable("pack.combat_reborn.no_shield_overhaul"),
                    ResourcePackActivationType.ALWAYS_ENABLED
            );
        }
        if (!CRConfig.get().general.combat.cleaving) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    CombatReborn.id("no_cleaving"), modContainer.get(),
                    Component.translatable("pack.combat_reborn.no_cleaving"),
                    ResourcePackActivationType.ALWAYS_ENABLED
            );
        }
        if (CRConfig.get().general.quivers.craftable) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    CombatReborn.id("craftable_quivers"), modContainer.get(),
                    Component.translatable("pack.combat_reborn.craftable_quivers"),
                    ResourcePackActivationType.ALWAYS_ENABLED
            );
        }
        if (CRConfig.get().general.quivers.craftable && CRConfig.get().general.integrations.lal_quivers) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    CombatReborn.id("weighted_quiver"), modContainer.get(),
                    Component.translatable("pack.combat_reborn.weighted_quiver"),
                    ResourcePackActivationType.ALWAYS_ENABLED
            );
        }
        if (CRConfig.get().general.quivers.enable_quivers && CRConfig.get().general.integrations.lal_quivers) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    CombatReborn.id("sapphire_quiver"), modContainer.get(),
                    Component.translatable("pack.combat_reborn.sapphire_quiver"),
                    ResourcePackActivationType.ALWAYS_ENABLED
            );
        }

        if (FabricLoader.getInstance().isModLoaded("end_reborn_netherite")) {
            isEndRebornLoaded = true;
        }
        if (FabricLoader.getInstance().isModLoaded("enchants_and_expeditions")) {
            isEnchantsAndExpeditionsLoaded = true;
        }
        if (FabricLoader.getInstance().isModLoaded("legacies_and_legends")) {
            isLegaciesAndLegendsLoaded = true;
        }
	}

    public static void registerPayloads() {

        PayloadTypeRegistry.playS2C().register(ShieldInfo.Sync.TYPE, ShieldInfo.Sync.CODEC);
        PayloadTypeRegistry.playC2S().register(ShieldInfo.Request.TYPE, ShieldInfo.Request.CODEC);

        PayloadTypeRegistry.playC2S().register(SelectQuiverItemPacket.TYPE, SelectQuiverItemPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SelectQuiverSlotPacket.TYPE, SelectQuiverSlotPacket.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(ShieldInfo.Sync.TYPE, (payload, context) -> {
            Player player = context.player();
            if (player instanceof ShieldInfo shieldInfo) {
                shieldInfo.setPercentageDamage(payload.percentageDamage());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(ShieldInfo.Request.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            if (player instanceof ShieldInfo shieldInfo) {
                int current = shieldInfo.getPercentageDamage();
                ServerPlayNetworking.send(player, new ShieldInfo.Sync(current));
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (player instanceof ShieldInfo shieldInfo) {
                int current = shieldInfo.getPercentageDamage();
                ServerPlayNetworking.send(player, new ShieldInfo.Sync(current));
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(SelectQuiverItemPacket.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            int slotId = payload.slotId();
            int selectedSlot = payload.selectedSlot();

            context.player().level().getServer().execute(() -> {
                ItemStack quiverStack = player.containerMenu.getSlot(slotId).getItem();
                if (quiverStack.getItem() instanceof QuiverItem) {
                    quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT, selectedSlot);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SelectQuiverSlotPacket.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            int slot = payload.slot();

            context.player().level().getServer().execute(() -> {
                ItemStack quiverStack = QuiverHelper.getQuiver(player);
                if (quiverStack != null) {
                    quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT, slot);
                }
            });
        });
    }

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
	public static final String MOD_ID = "combat_reborn";

}