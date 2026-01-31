package net.legacy.combat_reborn.item;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.legacy.combat_reborn.CombatReborn;
import net.legacy.combat_reborn.config.CRArmorConfig;
import net.legacy.combat_reborn.config.CRConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Optional;

public class ArmorAttributeModifierCallback {
    
    private ArmorAttributeModifierCallback() {}

    public static void init() {
        if (!CRConfig.get.general.modifiers.armor) return;

        DefaultItemComponentEvents.MODIFY.register((context -> context.modify(
                item -> {
                    Optional<ResourceKey<Item>> optionalItem = BuiltInRegistries.ITEM.getResourceKey(item);
                    return optionalItem.filter(itemRegistryKey -> CRConfig.get.armor.sets.stream()
                                    .anyMatch(modifier -> modifier.ids.contains(itemRegistryKey.identifier().toString())))
                            .isPresent();
                },
                (builder, item) -> {
                    Optional<ResourceKey<Item>> optionalItem = BuiltInRegistries.ITEM.getResourceKey(item);
                    if (optionalItem.isEmpty()) return;

                    Optional<CRArmorConfig.Modifiers> optionalArmorModifier = CRConfig.get.armor.sets.stream()
                            .filter(modifier -> modifier.ids.contains(optionalItem.get().identifier().toString()))
                            .findFirst();
                    if (optionalArmorModifier.isEmpty()) return;

                    var attributes = optionalArmorModifier.get().attributes;

                    if (CombatReborn.isEndRebornLoaded && CRConfig.get.general.integrations.end_reborn_netherite && optionalItem.get().identifier().getPath().contains("netherite")) {
                        attributes.add(Triple.of("minecraft:burning_time", 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                    }

                    builder.set(
                            DataComponents.ATTRIBUTE_MODIFIERS,
                            createAttributeModifiers(
                                    optionalArmorModifier.get().defense,
                                    optionalArmorModifier.get().toughness,
                                    optionalArmorModifier.get().knockback_resistance,
                                    optionalArmorModifier.get().slot,
                                    attributes
                            ));
                })));
    }

    public static ItemAttributeModifiers createAttributeModifiers(double defense, double toughness, double knockbackResistance, EquipmentSlotGroup slot, List<Triple<String, Double, AttributeModifier.Operation>> attributes) {
        var itemAttributes = ItemAttributeModifiers.builder()
                .add(
                        Attributes.ARMOR,
                        new AttributeModifier(
                                attributeIdentifier("minecraft:armor", slot),
                                defense,
                                AttributeModifier.Operation.ADD_VALUE),
                        slot
                )
                .add(
                        Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(
                                attributeIdentifier("minecraft:armor_toughness", slot),
                                toughness,
                                AttributeModifier.Operation.ADD_VALUE),
                        slot
                )
                .add(
                        Attributes.KNOCKBACK_RESISTANCE,
                        new AttributeModifier(
                                attributeIdentifier("minecraft:knockback_resistance", slot),
                                knockbackResistance / 10D,
                                AttributeModifier.Operation.ADD_VALUE),
                        slot
                )
                .build();
        for (Triple<String, Double, AttributeModifier.Operation> entry : attributes) {
            String attribute = entry.getLeft();
            double value = entry.getMiddle();
            AttributeModifier.Operation operation = entry.getRight();
            if (BuiltInRegistries.ATTRIBUTE.get(Identifier.parse(attribute)).isEmpty()) {
                LogUtils.getLogger().warn("Ignoring invalid attribute: " + attribute);
            }
            else {
                itemAttributes = itemAttributes.withModifierAdded(
                        BuiltInRegistries.ATTRIBUTE.get(Identifier.parse(attribute)).get(),
                        new AttributeModifier(
                                attributeIdentifier(attribute, slot),
                                value,
                                operation),
                        slot
                );
            }
        }
        return itemAttributes;
    }

    private static Identifier attributeIdentifier(String attribute, EquipmentSlotGroup slot) {
        return Identifier.parse(attribute + "_" + slot.name().toLowerCase());
    }
}