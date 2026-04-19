package net.rebel459.combat_reborn.client.util;

import net.minecraft.client.Minecraft;

public class ClientAttributeHelper {

    public static boolean hasKeyDown() {
        return Minecraft.getInstance().hasShiftDown();
    }

    public static String formatAttribute(double value) {
        double rounded = Math.round(value * 10.0) / 10.0;
        return (rounded % 1.0 == 0.0) ? Integer.toString((int) rounded) : String.format(java.util.Locale.ROOT, "%.1f", rounded);
    }
}
