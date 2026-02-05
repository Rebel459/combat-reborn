package net.legacy.combat_reborn.item;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.legacy.combat_reborn.CombatReborn;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.config.CRWeaponConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;
import java.util.Optional;

public class ItemAttributeModifierCallback {
    public static final Identifier BASE_ATTACK_RANGE_MODIFIER_ID = Identifier.withDefaultNamespace("base_attack_range");

    public static final double DEFAULT_ATTACK_DAMAGE = 1.0;
    public static final double DEFAULT_ATTACK_SPEED = Attributes.ATTACK_SPEED.value().getDefaultValue();
    public static final double DEFAULT_ATTACK_RANGE = Attributes.ENTITY_INTERACTION_RANGE.value().getDefaultValue();

    private ItemAttributeModifierCallback() {
    }

    public static void init() {
        if (!CRConfig.get.general.modifiers.weapons) return;

        DefaultItemComponentEvents.MODIFY.register((context -> context.modify(
                item -> {
                    Optional<ResourceKey<Item>> optionalItem = BuiltInRegistries.ITEM.getResourceKey(item);
                    return optionalItem.filter(itemRegistryKey -> CRConfig.get.weapons.sets.stream()
                                    .anyMatch(modifier -> modifier.ids.contains(itemRegistryKey.identifier().toString())))
                            .isPresent();
                },
                (builder, item) -> {
                    Optional<ResourceKey<Item>> optionalItem = BuiltInRegistries.ITEM.getResourceKey(item);
                    if (optionalItem.isEmpty()) return;

                    Optional<CRWeaponConfig.Modifiers> optionalToolsModifier = CRConfig.get.weapons.sets.stream()
                            .filter(modifier -> modifier.ids.contains(optionalItem.get().identifier().toString()))
                            .findFirst();
                    if (optionalToolsModifier.isEmpty()) return;

                    int bonus = 0;
                    if (CombatReborn.hasEndReborn() && CRConfig.get.general.integrations.end_reborn_netherite && optionalItem.get().identifier().getPath().contains("netherite")) {
                        bonus = 1;
                    }

                    builder.set(
                            DataComponents.ATTRIBUTE_MODIFIERS,
                            createAttributeModifiers(
                                    optionalToolsModifier.get().damage - DEFAULT_ATTACK_DAMAGE + bonus,
                                    optionalToolsModifier.get().speed - DEFAULT_ATTACK_SPEED,
                                    optionalToolsModifier.get().reach - DEFAULT_ATTACK_RANGE,
                                    optionalToolsModifier.get().attributes
                            ));
                })));
    }

    public static ItemAttributeModifiers createAttributeModifiers(double attackDamage, double attackSpeed, double attackRange, List<CRConfig.AttributeEntry> attributes) {
        var itemAttributes = ItemAttributeModifiers.builder()
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
        for (CRConfig.AttributeEntry entry : attributes) {
            String attribute = entry.attribute;
            double value = entry.value;
            AttributeModifier.Operation operation = entry.operation;
            if (BuiltInRegistries.ATTRIBUTE.get(Identifier.parse(attribute)).isEmpty()) {
                LogUtils.getLogger().warn("Ignoring invalid attribute: " + attribute);
            }
            else {
                itemAttributes = itemAttributes.withModifierAdded(
                        BuiltInRegistries.ATTRIBUTE.get(Identifier.parse(attribute)).get(),
                        new AttributeModifier(
                                Identifier.parse(attribute),
                                value,
                                operation),
                        EquipmentSlotGroup.MAINHAND
                );
            }
        }
        return itemAttributes;
    }
}