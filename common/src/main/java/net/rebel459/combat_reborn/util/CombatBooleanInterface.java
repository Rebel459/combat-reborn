package net.rebel459.combat_reborn.util;

public interface CombatBooleanInterface {

    boolean shouldDisableShield();
    void setShouldDisableShield(boolean shouldDisableShield);

    boolean getHiddenQuiver();
    void setHiddenQuiver(boolean hiddenQuiver);

    boolean getKnockbackOnly();
    void setKnockbackOnly(boolean knockbackOnly);
}
