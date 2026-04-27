package net.rebel459.combat_reborn;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.rebel459.unified.platform.NeoForgeUnifiedRegistries;

@Mod(CombatReborn.MOD_ID)
public class CombatRebornNeoForge {

    public CombatRebornNeoForge(IEventBus modEventBus) {
        NeoForgeUnifiedRegistries.registerBus(CombatReborn.MOD_ID, modEventBus);
        CombatReborn.initRegistries();
        modEventBus.addListener(CombatRebornNeoForge::commonSetup);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        CombatReborn.init();
    }
}