package net.legacy.combat_reborn.registry;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.legacy.combat_reborn.CombatReborn;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.config.CRWeaponConfig;
import net.legacy.combat_reborn.item.ItemAttributeModifierCallback;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.legacy.combat_reborn.util.QuiverContents;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Repairable;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class CRDataComponents {
    public static void init(){
        DefaultItemComponentEvents.MODIFY.register(context -> {
            if (!CombatReborn.isEnchantsAndExpeditionsLoaded()) {
                context.modify(Items.SHIELD, builder -> {
                    builder.set(DataComponents.ENCHANTABLE, new Enchantable(10));
                });
            }
            if (CombatReborn.isLegaciesAndLegendsLoaded() || CombatReborn.isEnchantsAndExpeditionsLoaded()) {
                Optional<CRWeaponConfig.Modifiers> optionalToolsModifier = CRConfig.get.weapons.sets.stream()
                        .filter(modifier -> modifier.ids.contains("minecraft:trident"))
                        .findFirst();
                context.modify(Items.TRIDENT, builder -> {
                    optionalToolsModifier.ifPresent(modifiers -> builder.set(
                            DataComponents.ATTRIBUTE_MODIFIERS,
                            ItemAttributeModifierCallback.createAttributeModifiers(
                                    modifiers.damage - ItemAttributeModifierCallback.DEFAULT_ATTACK_DAMAGE,
                                    modifiers.speed - ItemAttributeModifierCallback.DEFAULT_ATTACK_SPEED,
                                    modifiers.reach - ItemAttributeModifierCallback.DEFAULT_ATTACK_RANGE,
                                    modifiers.attributes
                            )
                    ));
                    HolderGetter<Item> holderGetter = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ITEM);
                    builder.set(DataComponents.REPAIRABLE, new Repairable(holderGetter.getOrThrow(CRItemTags.TRIDENT_REPAIR_MATERIALS)));
                });
            }
        });
    }



    public static final DataComponentType<QuiverContents> QUIVER_CONTENTS = register(
            "quiver_contents", builder -> builder.persistent(QuiverContents.CODEC).networkSynchronized(QuiverContents.STREAM_CODEC).cacheEncoding()
    );
    public static final DataComponentType<Integer> QUIVER_CONTENTS_SLOT = register(
            "quiver_contents_slot", builder -> builder.persistent(ExtraCodecs.intRange(-1, 99)).networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    private static <T> DataComponentType<T> register(String string, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, CombatReborn.id(string), unaryOperator.apply(DataComponentType.builder()).build());
    }
}