package net.legacy.combat_reborn;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.legacy.combat_reborn.network.ShieldInfo;
import net.minecraft.world.entity.player.Player;

public class CombatRebornClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

        // Client-side: receive Sync packet (server → client)
        ClientPlayNetworking.registerGlobalReceiver(ShieldInfo.Sync.TYPE, (payload, context) -> {
            Player player = context.player();
            if (player instanceof ShieldInfo shieldInfo) {
                shieldInfo.setPercentageDamage(payload.percentageDamage());
            }
        });
	}
}