package net.rebel459.combat_reborn.mixin.menu;

import net.rebel459.combat_reborn.CombatReborn;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.tag.CREnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @Inject(method = "createResult", at = @At(value = "TAIL"))
    private void CR$getAvailableEnchantmentResults(CallbackInfo ci) {
        AnvilMenu menu = AnvilMenu.class.cast(this);
        ItemStack input = menu.inputSlots.getItem(0);
        ItemStack output = menu.resultSlots.getItem(0);
        if (CombatReborn.hasEnchantsAndExpeditions() || !output.is(ItemTags.AXES) || !CRConfig.get.general.misc.cleaving_enchantment) return;
        if (output.isEnchanted()) {
            ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(output));
            if (!menu.player.hasInfiniteMaterials()) mutable.removeIf(holder -> (holder.is(CREnchantmentTags.NOT_ON_AXES)));
            EnchantmentHelper.setEnchantments(output, mutable.toImmutable());
            menu.resultSlots.setItem(0, output);
            if (input.getDamageValue() == output.getDamageValue() && input.getEnchantments() == output.getEnchantments()) {
                menu.resultSlots.setItem(0, ItemStack.EMPTY);
            }
        }
    }
}