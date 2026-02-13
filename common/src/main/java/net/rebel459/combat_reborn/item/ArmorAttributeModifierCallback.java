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
import net.rebel459.unified.platform.UnifiedEvents;

import java.util.List;
import java.util.Optional;

public class ArmorAttributeModifierCallback {
    
    private ArmorAttributeModifierCallback() {}

    public static void init() {
        if (!CRConfig.get.general.modifiers.armor) return;

        UnifiedEvents.ModifyItemComponents.access(
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
        String burningTime = "minecraft:burning_time";
        boolean applyEndRebornBurningTime = CombatReborn.hasEndReborn() && CRConfig.get.general.integrations.end_reborn_netherite && itemPath.contains("netherite");
        for (CRConfig.AttributeEntry entry : attributes) {
            String attribute = entry.attribute;
            double value = entry.value;
            AttributeModifier.Operation operation = entry.operation;
            if (BuiltInRegistries.ATTRIBUTE.get(Identifier.parse(attribute)).isEmpty()) {
                LogUtils.getLogger().warn("Ignoring invalid attribute: " + attribute);
            }
            else {
                if (attribute.equals(burningTime)) applyEndRebornBurningTime = false;
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
        if (applyEndRebornBurningTime) {
            itemAttributes = itemAttributes.withModifierAdded(
                    BuiltInRegistries.ATTRIBUTE.get(Identifier.parse(burningTime)).get(),
                    new AttributeModifier(
                            attributeId(burningTime, slot),
                            -0.2,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    slot
            );
        }
        return itemAttributes;
    }

    private static Identifier attributeId(String attribute, EquipmentSlotGroup slot) {
        return Identifier.parse(attribute + "_" + slot.name().toLowerCase());
    }
}