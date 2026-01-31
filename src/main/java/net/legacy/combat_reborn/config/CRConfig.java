package net.legacy.combat_reborn.config;

import me.shedaniel.autoconfig.AutoConfig;

public class CRConfig {
    
    public static class get {
        public static CRArmorConfig armor = AutoConfig.getConfigHolder(CRArmorConfig.class).getConfig();
        public static CRGeneralConfig general = AutoConfig.getConfigHolder(CRGeneralConfig.class).getConfig();
        public static CRWeaponConfig weapons = AutoConfig.getConfigHolder(CRWeaponConfig.class).getConfig();
    }
}
