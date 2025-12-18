package net.legacy.combat_reborn.util;

import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.Nullable;

public interface BlockedSourceInterface {

    @Nullable
    DamageSource getLastBlockedSource();

    void setLastBlockedSource(DamageSource source);
}