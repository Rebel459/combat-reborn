package net.legacy.combat_reborn.entity;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.legacy.combat_reborn.config.CRConfig;
import net.minecraft.stats.Stats;

public class PlayerSpawnCallback {
    public static void init() {
        ServerPlayerEvents.JOIN.register((player) -> {
            if (!CRConfig.get.food.hunger_rework) return;
            int playTime = player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
            if (playTime < 1) {
                player.getFoodData().setSaturation(20F);
            }
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, player, alive) -> {
            if (!CRConfig.get.food.hunger_rework) return;
            player.getFoodData().setSaturation(20F);
        });
    }
}
