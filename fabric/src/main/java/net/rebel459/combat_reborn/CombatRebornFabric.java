package net.rebel459.combat_reborn;

import net.fabricmc.api.ModInitializer;
import net.rebel459.combat_reborn.registry.CRVillagerTrades;

public class CombatRebornFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CombatReborn.initRegistries();
        CombatReborn.init();
        CRVillagerTrades.init();
    }
}
