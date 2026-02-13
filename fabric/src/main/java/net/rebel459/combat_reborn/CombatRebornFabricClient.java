package net.rebel459.combat_reborn;

import net.fabricmc.api.ClientModInitializer;

public class CombatRebornFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CombatRebornClient.initClient();
    }
}
