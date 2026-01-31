package net.legacy.combat_reborn.item;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.legacy.combat_reborn.config.CRConfig;
import net.legacy.combat_reborn.config.CRWeaponConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Optional;

public class ItemAttributeModifierCallback {
    public static final ResourceLocation BASE_ATTACK_RANGE_MODIFIER_ID = ResourceLocation.withDefaultNamespace("base_attack_range");

    private static final double DEFAULT_ATTACK_DAMAGE = 1.0; // GENERIC_ATTACK_DAMAGE base value is changed for players!
    private static final double DEFAULT_ATTACK_SPEED = Attributes.ATTACK_SPEED.value().getDefaultValue();
    private static final double DEFAULT_ATTACK_RANGE = Attributes.ENTITY_INTERACTION_RANGE.value().getDefaultValue();

    private ItemAttributeModifierCallback() {
    }

    public static void init() {
        if (!CRConfig.get.general.modifiers.weapons) return;

        DefaultItemComponentEvents.MODIFY.register((context -> context.modify(
                item -> {
                    Optional<ResourceKey<Item>> optionalItem = BuiltInRegistries.ITEM.getResourceKey(item);
                    return optionalItem.filter(itemRegistryKey -> CRConfig.get.weapons.sets.stream()
                                    .anyMatch(modifier -> modifier.ids.contains(itemRegistryKey.location().toString())))
                            .isPresent();
                },
                (builder, item) -> {
                    Optional<ResourceKey<Item>> optionalItem = BuiltInRegistries.ITEM.getResourceKey(item);
                    if (optionalItem.isEmpty()) return;

                    Optional<CRWeaponConfig.Modifiers> optionalToolsModifier = CRConfig.get.weapons.sets.stream()
                            .filter(modifier -> modifier.ids.contains(optionalItem.get().location().toString()))
                            .findFirst();
                    if (optionalToolsModifier.isEmpty()) return;

                    builder.set(
                            DataComponents.ATTRIBUTE_MODIFIERS,
                            createAttributeModifiers(
                                    optionalToolsModifier.get().damage - DEFAULT_ATTACK_DAMAGE,
                                    optionalToolsModifier.get().speed - DEFAULT_ATTACK_SPEED,
                                    optionalToolsModifier.get().reach - DEFAULT_ATTACK_RANGE,
                                    optionalToolsModifier.get().attributes
                            ));
                })));
    }

    public static ItemAttributeModifiers createAttributeModifiers(double attackDamage, double attackSpeed, double attackRange, List<Triple<String, Double, AttributeModifier.Operation>> attributes) {
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
        for (Triple<String, Double, AttributeModifier.Operation> entry : attributes) {
            String attribute = entry.getLeft();
            double value = entry.getMiddle();
            AttributeModifier.Operation operation = entry.getRight();
            if (BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse(attribute)).isEmpty()) {
                LogUtils.getLogger().warn("Ignoring invalid attribute: " + attribute);
            }
            else {
                itemAttributes = itemAttributes.withModifierAdded(
                        BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse(attribute)).get(),
                        new AttributeModifier(
                                ResourceLocation.parse(attribute),
                                value,
                                operation),
                        EquipmentSlotGroup.MAINHAND
                );
            }
        }
        return itemAttributes;
    }
}