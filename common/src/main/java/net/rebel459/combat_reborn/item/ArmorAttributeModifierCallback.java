package net.rebel459.combat_reborn.item;

import com.mojang.logging.LogUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.rebel459.combat_reborn.CombatReborn;
import net.rebel459.combat_reborn.config.CRArmorConfig;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.config.CRWeaponConfig;
import net.rebel459.unified.platform.UnifiedEvents;

import java.util.List;
import java.util.Optional;

public class ArmorAttributeModifierCallback {
    
    private ArmorAttributeModifierCallback() {}

    public static void init() {
        if (!CRConfig.getGeneral().modifiers.armor) return;

        UnifiedEvents.DefaultDataComponents.modifyWithFilter(
                item -> {
                    Optional<ResourceKey<Item>> optionalItem = BuiltInRegistries.ITEM.getResourceKey(item);
                    return optionalItem.filter(itemRegistryKey -> CRConfig.getArmor().sets.stream()
                                    .anyMatch(modifier -> modifier.ids.contains(itemRegistryKey.identifier().toString())))
                            .isPresent();
                },
                (item, builder, provider) -> {
                    Optional<ResourceKey<Item>> optionalItem = BuiltInRegistries.ITEM.getResourceKey(item);
                    if (optionalItem.isEmpty()) return;

                    Optional<CRArmorConfig.Modifiers> optionalArmorModifier = CRConfig.getArmor().sets.stream()
                            .filter(modifier -> modifier.ids.contains(optionalItem.get().identifier().toString()))
                            .findFirst();
                    if (optionalArmorModifier.isEmpty()) return;

                    if (CombatReborn.hasEndReborn() && CRConfig.getGeneral().integrations.end_reborn_netherite && optionalItem.get().identifier().getPath().contains("netherite")) {
                        Optional<CRArmorConfig.Modifiers> optionalEndRebornModifier = CRConfig.getArmor().sets.stream()
                                .filter(modifier -> modifier.ids.contains(Identifier.fromNamespaceAndPath("end_reborn", optionalItem.get().identifier().getPath()).toString()))
                                .findFirst();
                        if (optionalEndRebornModifier.isPresent()) optionalArmorModifier = optionalEndRebornModifier;
                    }

                    builder.set(
                            DataComponents.ATTRIBUTE_MODIFIERS,
                            createAttributeModifiers(
                                    optionalArmorModifier.get().defense,
                                    optionalArmorModifier.get().toughness,
                                    optionalArmorModifier.get().knockback_resistance,
                                    optionalArmorModifier.get().slot,
                                    optionalArmorModifier.get().attributes,
                                    optionalItem.get().identifier().getPath()
                            ));
                });
    }

    public static ItemAttributeModifiers createAttributeModifiers(double defense, double toughness, double knockbackResistance, EquipmentSlotGroup slot, List<CRConfig.AttributeEntry> attributes, String itemPath) {
        var itemAttributes = ItemAttributeModifiers.builder()
                .add(
                        Attributes.ARMOR,
                        new AttributeModifier(
                                attributeId("minecraft:armor", slot),
                                defense,
                                AttributeModifier.Operation.ADD_VALUE),
                        slot
                )
                .add(
                        Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(
                                attributeId("minecraft:armor_toughness", slot),
                                toughness,
                                AttributeModifier.Operation.ADD_VALUE),
                        slot
                )
                .add(
                        Attributes.KNOCKBACK_RESISTANCE,
                        new AttributeModifier(
                                attributeId("minecraft:knockback_resistance", slot),
                                knockbackResistance / 10D,
                                AttributeModifier.Operation.ADD_VALUE),
                        slot
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
                                attributeId(attribute, slot),
                                value,
                                operation),
                        slot
                );
            }
        }
        return itemAttributes;
    }

    private static Identifier attributeId(String attribute, EquipmentSlotGroup slot) {
        return Identifier.parse(attribute + "_" + slot.name().toLowerCase());
    }
}