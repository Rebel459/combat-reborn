package net.legacy.combat_reborn.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.legacy.combat_reborn.CombatReborn;


@Config(name = CombatReborn.MOD_ID)
public class CRConfig extends PartitioningSerializer.GlobalData {

    public static CRConfig get() {
        return AutoConfig.getConfigHolder(CRConfig.class).getConfig();
    }

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    public CRGeneralConfig general = new CRGeneralConfig();

    @ConfigEntry.Category("modifiers")
    @ConfigEntry.Gui.TransitiveObject
    public CRModifierConfig modifiers = new CRModifierConfig();

}