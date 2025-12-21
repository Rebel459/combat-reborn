package net.legacy.combat_reborn;

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
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CREnchantments;
import net.legacy.combat_reborn.sound.CRSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class CombatReborn implements ModInitializer {

    public static boolean isEndRebornLoaded = false;
    public static boolean isEnchantsAndExpeditionsLoaded = false;

	@Override
	public void onInitialize() {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(MOD_ID);

		CRConfig.init();
        CREnchantments.init();
        CRSounds.init();

        AttributeModifierCallback.init();
        PlayerSpawnCallback.init();

        registerPayloads();

        if (!CRConfig.get.combat.shield_overhaul) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    CombatReborn.id("no_shield_overhaul"), modContainer.get(),
                    Component.translatable("pack.combat_reborn.no_shield_overhaul"),
                    ResourcePackActivationType.ALWAYS_ENABLED
            );
        }
        if (!CRConfig.get.combat.cleaving) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    CombatReborn.id("no_cleaving"), modContainer.get(),
                    Component.translatable("pack.combat_reborn.no_cleaving"),
                    ResourcePackActivationType.ALWAYS_ENABLED
            );
        }

        if (FabricLoader.getInstance().isModLoaded("end_reborn")) {
            isEndRebornLoaded = true;
        }
        if (FabricLoader.getInstance().isModLoaded("enchants_and_expeditions")) {
            isEnchantsAndExpeditionsLoaded = true;
        }
	}

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(ShieldInfo.Sync.TYPE, ShieldInfo.Sync.CODEC);     // Server → Client
        PayloadTypeRegistry.playC2S().register(ShieldInfo.Request.TYPE, ShieldInfo.Request.CODEC); // Client → Server

        // Client-side: receive Sync packet (server → client)
        ClientPlayNetworking.registerGlobalReceiver(ShieldInfo.Sync.TYPE, (payload, context) -> {
            Player player = context.player();
            if (player instanceof ShieldInfo shieldInfo) {
                shieldInfo.setPercentageDamage(payload.percentageDamage());
            }
        });

        // Server-side: receive Request packet (client → server)
        ServerPlayNetworking.registerGlobalReceiver(ShieldInfo.Request.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            if (player instanceof ShieldInfo shieldInfo) {
                int current = shieldInfo.getPercentageDamage();
                ServerPlayNetworking.send(player, new ShieldInfo.Sync(current));
            }
        });

        // 3. Initial sync on player join (server-side)
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (player instanceof ShieldInfo shieldInfo) {
                int current = shieldInfo.getPercentageDamage();
                ServerPlayNetworking.send(player, new ShieldInfo.Sync(current));
            }
        });
    }

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
	public static final String MOD_ID = "combat_reborn";

}