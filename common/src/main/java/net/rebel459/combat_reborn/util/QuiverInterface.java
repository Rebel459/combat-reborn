package net.rebel459.combat_reborn.util;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface QuiverInterface {

    @Nullable
    ItemStack getQuiver();

    void setQuiver(ItemStack quiver);
}