package net.rebel459.combat_reborn.registry;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.rebel459.combat_reborn.config.CRConfig;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;

public class CRVillagerTrades {

    public static void init() {

        if (CRConfig.get.general.quivers.enable_quivers && CRConfig.get.general.quivers.tradable) {
            TradeOfferHelper.registerVillagerOffers(
                    VillagerProfession.FLETCHER,
                    5,
                    (trades, rebalanced) -> trades.add(
                            new VillagerTrades.ItemsForEmeralds(
                                    CRItems.QUIVER.get(),
                                    20,
                                    1,
                                    15
                            )
                    )
            );
        }
    }
}
