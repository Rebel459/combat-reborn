package net.rebel459.combat_reborn.client.util;

import net.minecraft.client.Minecraft;

public class ClientHelper {

    public static boolean hasKeyDown() {
        return Minecraft.getInstance().hasShiftDown();
    }
}
