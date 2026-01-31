package net.legacy.combat_reborn;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.entity.PlayerSpawnCallback;
import net.legacy.combat_reborn.item.ArmorAttributeModifierCallback;
import net.legacy.combat_reborn.item.ItemAttributeModifierCallback;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.legacy.combat_reborn.registry.CREnchantments;
import net.legacy.combat_reborn.sound.CRSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class CombatReborn implements ModInitializer {

    public static boolean isEnchantsAndExpeditionsLoaded() {
            return FabricLoader.getInstance().isModLoaded("enchants_and_expeditions");
    }

	@Override
	public void onInitialize() {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(MOD_ID);

        CREnchantments.init();
        CRSounds.init();

        ArmorAttributeModifierCallback.init();
        ItemAttributeModifierCallback.init();
        PlayerSpawnCallback.init();

        registerPayloads();

        if (!CRConfig.get.general.shields.shield_overhaul) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    CombatReborn.id("no_shield_overhaul"), modContainer.get(),
                    Component.translatable("pack.combat_reborn.no_shield_overhaul"),
                    ResourcePackActivationType.ALWAYS_ENABLED
            );
        }
        if (!CRConfig.get.general.misc.cleaving_enchantment) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    CombatReborn.id("no_cleaving"), modContainer.get(),
                    Component.translatable("pack.combat_reborn.no_cleaving"),
                    ResourcePackActivationType.ALWAYS_ENABLED
            );
        }
	}

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(ShieldInfo.Sync.TYPE, ShieldInfo.Sync.CODEC);     // Server → Client
        PayloadTypeRegistry.playC2S().register(ShieldInfo.Request.TYPE, ShieldInfo.Request.CODEC); // Client → Server

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