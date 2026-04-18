package net.rebel459.combat_reborn;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.rebel459.combat_reborn.config.CRGeneralConfig;

@Mod(value = CombatReborn.MOD_ID, dist = Dist.CLIENT)
public class CombatRebornNeoForgeClient {

    public CombatRebornNeoForgeClient(IEventBus modEventBus) {
        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (modContainer, parent) ->
                        AutoConfigClient.getConfigScreen(CRGeneralConfig.class, parent).get()
        );
        CombatRebornClient.initClient();
    }
}