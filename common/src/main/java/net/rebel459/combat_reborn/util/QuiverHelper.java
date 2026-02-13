package net.rebel459.combat_reborn.util;

import net.rebel459.combat_reborn.CombatReborn;
import net.rebel459.combat_reborn.item.QuiverItem;
import net.rebel459.combat_reborn.registry.CRItems;
import net.rebel459.combat_reborn.util.QuiverContents;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuiverHelper {
    public static final String QUIVER = CombatReborn.id("quiver").toString();
    public static final String WEIGHTED_QUIVER = CombatReborn.id("heavy_quiver").toString();
    public static final String SAPPHIRE_QUIVER = CombatReborn.id("sapphire_quiver").toString();

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

    public static int getStorage(ItemStack stack) {
        return getStorage(getType(stack));
    }
    public static int getStorage(String type) {
        if (type.equals(WEIGHTED_QUIVER)) return 8;
        if (type.equals(SAPPHIRE_QUIVER)) return 1;
        return 4;
    }

    public static String getType(ItemStack stack) {
        if (stack.is(CRItems.WEIGHTED_QUIVER.get())) return WEIGHTED_QUIVER;
        if (stack.is(CRItems.SAPPHIRE_QUIVER.get())) return SAPPHIRE_QUIVER;
        return QUIVER;
    }

    public static float getBowSpeed(ItemStack stack) {
        if (getType(stack).equals(QUIVER)) return 1.2F;
        if (getType(stack).equals(SAPPHIRE_QUIVER)) return 1.7F;
        return 1F;
    }

    public static float getAccuracy(ItemStack stack) {
        return getAccuracy(stack, null);
    }
    public static float getAccuracy(ItemStack stack, @Nullable Player player) {
        if (getType(stack).equals(QUIVER)) return 1.4F;
        if (getType(stack).equals(WEIGHTED_QUIVER)) return 1.1F;
        return 1F;
    }

    public static float getPower(ItemStack stack) {
        return getPower(stack, null);
    }
    public static float getPower(ItemStack stack, @Nullable Player player) {
        if (getType(stack).equals(SAPPHIRE_QUIVER)) return 1.1F;
        if (getType(stack).equals(WEIGHTED_QUIVER)) return 1.2F;
        return 1F;
    }

    public static void postProjectileEvent(Player player) {}

    public static boolean shouldRender(Player player) {
        return true;
    }

    public static void updateFullness(ItemStack stack, QuiverContents.Mutable mutable) {
        double percentage = mutable.weight().doubleValue();
        String model = "empty";
        if (percentage > 0.75) model = "full";
        else if (percentage > 0.5) model = "three";
        else if (percentage > 0.25) model = "two";
        else if (percentage > 0) model = "one";
        stack.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(model), List.of()))
                .build()
        );
    }

    public class Projectile {

    }
}
