package net.rebel459.combat_reborn.registry;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.rebel459.combat_reborn.CombatReborn;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;

public class CREnchantments {

	public static final ResourceKey<Enchantment> CLEAVING = key("cleaving");
    public static final ResourceKey<Enchantment> PARRY = key("parry");
    public static final ResourceKey<Enchantment> STAGGER = key("stagger");
    public static final ResourceKey<Enchantment> ENDURANCE = key("endurance");
    public static final ResourceKey<Enchantment> DUELING = key("dueling");

	public static void init() {
	}

	private static @NotNull ResourceKey<Enchantment> key(String path) {
		return ResourceKey.create(Registries.ENCHANTMENT, CombatReborn.id(path));
	}

    public static int getLevel(ItemStack stack, ResourceKey<Enchantment> enchantment) {
        ItemEnchantments itemEnchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            if (holder.is(enchantment)) {
                return EnchantmentHelper.getItemEnchantmentLevel(holder, stack);
            }
        }
        return 0;
    }
}