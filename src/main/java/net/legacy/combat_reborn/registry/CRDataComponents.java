package net.legacy.combat_reborn.registry;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.legacy.combat_reborn.CombatReborn;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantable;

public class CRDataComponents {
    public static void init(){
        DefaultItemComponentEvents.MODIFY.register(context -> {
            if (!CombatReborn.isEnchantsAndExpeditionsLoaded) {
                context.modify(Items.SHIELD, builder -> {
                    builder.set(DataComponents.ENCHANTABLE, new Enchantable(10));
                });
            }
        });
    }
}