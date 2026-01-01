package net.legacy.combat_reborn.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;


@Config(name = "general")
public class CRGeneralConfig implements ConfigData {

    public enum ShieldDisplay {
        HOTBAR,
        CROSSHAIR
    }

    @ConfigEntry.Gui.CollapsibleObject
    public CombatConfig combat = new CombatConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public FoodConfig food = new FoodConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public ConsumableConfig consumables = new ConsumableConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public IntegrationConfig integrations = new IntegrationConfig();

    public static class CombatConfig {
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean modified_values = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean shield_overhaul = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option=ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ShieldDisplay shield_display = ShieldDisplay.HOTBAR;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(max=5L)
        public int shield_delay = 0;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean cleaving = true;
    }

    public static class FoodConfig {
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean hunger_rework = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean stackable_stews = true;
    }

    public static class ConsumableConfig {
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean damage_interruptions = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean stackable_potions = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean knockback_throwables = true;
    }

    public static class IntegrationConfig {
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean end_reborn = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean enderscape = true;
    }

}