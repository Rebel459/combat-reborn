package net.legacy.combat_reborn.item;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.legacy.combat_reborn.CombatReborn;
import net.legacy.combat_reborn.config.CRConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.Optional;

public class AttributeModifierCallback {
    public static final Identifier BASE_ATTACK_RANGE_MODIFIER_ID = Identifier.withDefaultNamespace("base_attack_range");

    private static final double DEFAULT_ATTACK_DAMAGE = 1.0; // GENERIC_ATTACK_DAMAGE base value is changed for players!
    private static final double DEFAULT_ATTACK_SPEED = Attributes.ATTACK_SPEED.value().getDefaultValue();
    private static final double DEFAULT_ATTACK_RANGE = Attributes.ENTITY_INTERACTION_RANGE.value().getDefaultValue();

    private AttributeModifierCallback() {
    }

    public static void init() {
        if (!CRConfig.get.combat.modified_values) return;

        DefaultItemComponentEvents.MODIFY.register((context -> context.modify(
                item -> {
                    Optional<ResourceKey<Item>> optionalItem = BuiltInRegistries.ITEM.getResourceKey(item);
                    return optionalItem.filter(itemRegistryKey -> CRConfig.get.modifiers.modifiers.stream()
                                    .anyMatch(modifier -> modifier.ids.contains(itemRegistryKey.identifier().toString())))
                            .isPresent();
                },
                (builder, item) -> {
                    Optional<ResourceKey<Item>> optionalItem = BuiltInRegistries.ITEM.getResourceKey(item);
                    if (optionalItem.isEmpty()) return;

                    Optional<CRConfig.ModifierConfig.Modifiers> optionalToolsModifier = CRConfig.get.modifiers.modifiers.stream()
                            .filter(modifier -> modifier.ids.contains(optionalItem.get().identifier().toString()))
                            .findFirst();
                    if (optionalToolsModifier.isEmpty()) return;

                    int bonus = 0;
                    if (CombatReborn.isEndRebornLoaded && CRConfig.get.integrations.end_reborn && optionalItem.get().identifier().getPath().contains("netherite")) {
                        bonus = 1;
                    }

                    builder.set(
                            DataComponents.ATTRIBUTE_MODIFIERS,
                            createAttributeModifiers(
                                    optionalToolsModifier.get().damage - DEFAULT_ATTACK_DAMAGE + bonus,
                                    optionalToolsModifier.get().speed - DEFAULT_ATTACK_SPEED,
                                    optionalToolsModifier.get().reach - DEFAULT_ATTACK_RANGE
                            ));
                })));
    }

    public static ItemAttributeModifiers createAttributeModifiers(double attackDamage, double attackSpeed,
                                                                  double attackRange) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                Item.BASE_ATTACK_DAMAGE_ID,
                                attackDamage,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                Item.BASE_ATTACK_SPEED_ID,
                                attackSpeed,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(
                                BASE_ATTACK_RANGE_MODIFIER_ID,
                                attackRange,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }
}