package net.rebel459.combat_reborn;

import net.fabricmc.api.ModInitializer;

public class CombatRebornFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CombatReborn.initRegistries();
        CombatReborn.init();
    }
}
