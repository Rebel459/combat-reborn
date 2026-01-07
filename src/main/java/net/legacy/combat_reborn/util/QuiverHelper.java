package net.legacy.combat_reborn.util;

import net.legacy.combat_reborn.CombatReborn;
import net.legacy.combat_reborn.item.QuiverItem;
import net.legacy.combat_reborn.registry.CRItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class QuiverHelper {
    public static final String QUIVER = CombatReborn.id("quiver").toString();

    public static ItemStack getStack(Player player) {
        return getQuiver(player);
    }

    public static ItemStack getQuiver(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack offhand = player.getOffhandItem();
        if (main.getItem() instanceof QuiverItem) {
            return main;
        } else if (offhand.getItem() instanceof QuiverItem) {
            return offhand;
        }
        return null;
    }

    public static String getType(ItemStack stack) {
        return QUIVER;
    }

    public static int getStorage(ItemStack stack) {
        return getStorage(getType(stack));
    }
    public static int getStorage(String type) {
        return 4;
    }

    public static float getAccuracy(ItemStack stack) {
        if (stack.is(CRItems.QUIVER)) return 1.4F;
        return 1;
    }

    public static float getBowSpeed(ItemStack stack) {
        if (stack.is(CRItems.QUIVER)) return 1.2F;
        return 1;
    }

    public static float getPower(ItemStack stack) {
        return 1;
    }
}
