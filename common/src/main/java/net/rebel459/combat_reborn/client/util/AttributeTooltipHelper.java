package net.rebel459.combat_reborn.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jspecify.annotations.Nullable;

public final class AttributeTooltipHelper {

    private static final ThreadLocal<ItemAttributeModifiers> CURRENT_MODIFIERS = new ThreadLocal<>();
    private static final ThreadLocal<ItemStack> CURRENT_STACK = new ThreadLocal<>();

    private AttributeTooltipHelper() {}

    public static void set(ItemAttributeModifiers modifiers) {
        CURRENT_MODIFIERS.set(modifiers);
    }

    @Nullable
    public static ItemAttributeModifiers get() {
        return CURRENT_MODIFIERS.get();
    }

    public static void setStack(ItemStack stack) {
        CURRENT_STACK.set(stack);
    }

    @Nullable
    public static ItemStack getStack() {
        return CURRENT_STACK.get();
    }

    public static void clear() {
        CURRENT_MODIFIERS.remove();
        CURRENT_STACK.remove();
    }

    public static boolean hasKeyDown() {
        return Minecraft.getInstance().hasShiftDown();
    }
}
