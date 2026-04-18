package net.rebel459.combat_reborn.config;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class CRConfig {

    public static CRArmorConfig getArmor() {
        return AutoConfig.getConfigHolder(CRArmorConfig.class).getConfig();
    }
    public static CRGeneralConfig getGeneral() {
        return AutoConfig.getConfigHolder(CRGeneralConfig.class).getConfig();
    }
    public static CRWeaponConfig getWeapons() {
        return AutoConfig.getConfigHolder(CRWeaponConfig.class).getConfig();
    }

    public static class AttributeEntry {
        public String attribute;
        public double value;
        public AttributeModifier.Operation operation;

        public AttributeEntry() {}

        public AttributeEntry(String attribute, double value, AttributeModifier.Operation operation) {
            this.attribute = attribute;
            this.value = value;
            this.operation = operation;
        }
    }
}
