package net.rebel459.combat_reborn.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.network.ShieldInfo;
import net.rebel459.unified.platform.UnifiedEvents;

public class PlayerSpawnCallback {
    public static void init() {
        UnifiedEvents.Players.onJoin((player) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            if (player instanceof ShieldInfo shieldInfo && CRConfig.get.general.shields.shield_overhaul) {
                shieldInfo.setPercentageDamageAndSync(0, serverPlayer);
            }
            if (CRConfig.get.general.hunger.hunger_rework) {
                int playTime = serverPlayer.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
                if (playTime < 1) {
                    player.getFoodData().setSaturation(20F);
                }
            }
        });
        UnifiedEvents.Players.onRespawn((player) -> {
            if (player instanceof ShieldInfo shieldInfo && CRConfig.get.general.shields.shield_overhaul && player instanceof ServerPlayer serverPlayer) {
                shieldInfo.setPercentageDamageAndSync(0, serverPlayer);
            }
            if (CRConfig.get.general.hunger.hunger_rework) {
                player.getFoodData().setSaturation(20F);
            }
        });
    }
}
