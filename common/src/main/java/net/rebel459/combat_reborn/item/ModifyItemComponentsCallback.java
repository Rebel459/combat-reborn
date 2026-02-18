package net.rebel459.combat_reborn.item;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Repairable;
import net.rebel459.combat_reborn.CombatReborn;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.config.CRWeaponConfig;
import net.rebel459.combat_reborn.tag.CRItemTags;
import net.rebel459.unified.platform.UnifiedEvents;

import java.util.Optional;

public class ModifyItemComponentsCallback {

    public static void init(){
        UnifiedEvents.ItemComponents.modify((
                        item -> true),
                (builder, item) -> {
                    if (!CombatReborn.hasEnchantsAndExpeditions()) {
                        if (item.getDefaultInstance().is(Items.SHIELD)) {
                            builder.set(DataComponents.ENCHANTABLE, new Enchantable(10));
                        }
                    }
                    if (CombatReborn.hasLegaciesAndLegends() || CombatReborn.hasEnchantsAndExpeditions()) {
                        Optional<CRWeaponConfig.Modifiers> optionalToolsModifier = CRConfig.get.weapons.sets.stream()
                                .filter(modifier -> modifier.ids.contains("minecraft:trident"))
                                .findFirst();
                        if (item.getDefaultInstance().is(Items.TRIDENT)) {
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
                        }
                    }
                });
    }
}