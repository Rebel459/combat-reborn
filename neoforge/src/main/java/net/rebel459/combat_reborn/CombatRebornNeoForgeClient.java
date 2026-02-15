package net.rebel459.combat_reborn;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = CombatReborn.MOD_ID, dist = Dist.CLIENT)
public class CombatRebornNeoForgeClient {

    public CombatRebornNeoForgeClient(IEventBus modEventBus) {
        CombatRebornClient.initClient();
    }
}