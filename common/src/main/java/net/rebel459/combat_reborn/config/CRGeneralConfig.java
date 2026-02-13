package net.rebel459.combat_reborn.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.rebel459.combat_reborn.CombatReborn;

@Config(name = CombatReborn.MOD_ID + "/" + "general")
public class CRGeneralConfig implements ConfigData {

    public enum ShieldDisplay {
        HOTBAR,
        CROSSHAIR
    }

    public enum ToughnessMechanics {
        DURABILITY,
        DAMAGE,
        NONE
    }

    @ConfigEntry.Gui.CollapsibleObject
    public ModifiersConfig modifiers = new ModifiersConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public ShieldConfig shields = new ShieldConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public QuiverConfig quivers = new QuiverConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public HungerConfig hunger = new HungerConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public ArmorConfig armor = new ArmorConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public MiscConfig misc = new MiscConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public IntegrationConfig integrations = new IntegrationConfig();

    public static class ModifiersConfig {
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean weapons = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean armor = true;
    }

    public static class ShieldConfig {
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean shield_overhaul = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler(option=ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ShieldDisplay display_style = ShieldDisplay.HOTBAR;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean show_tooltips = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(max=5L)
        public int block_delay = 0;
    }

    public static class QuiverConfig {
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean enable_quivers = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean show_tooltips = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean lootable = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean tradable = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean craftable = false;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean ranged_tweaks = true;
    }

    public static class HungerConfig {
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean hunger_rework = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(max=20L)
        public int hunger_barrier = 6;
    }

    public static class ArmorConfig {
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean armor_rebalance = true;

        @ConfigEntry.Gui.CollapsibleObject
        public ToughnessConfig toughness = new ToughnessConfig();

        public static class ToughnessConfig {
            @ConfigEntry.Category("config")
            @ConfigEntry.Gui.Tooltip
            @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
            public ToughnessMechanics toughness_type = ToughnessMechanics.DURABILITY;
            @ConfigEntry.Category("config")
            @ConfigEntry.Gui.Tooltip
            public boolean toughness_overlay = true;
            @ConfigEntry.Category("config")
            @ConfigEntry.Gui.Tooltip
            public float multiplier = 0.5F;
        }

        @ConfigEntry.Gui.CollapsibleObject
        public FormulaConfig formula = new FormulaConfig();

        public static class FormulaConfig {
            @ConfigEntry.Category("config")
            @ConfigEntry.Gui.Tooltip
            public float middle_points = 20F;
            @ConfigEntry.Category("config")
            @ConfigEntry.Gui.Tooltip
            public float middle_percentage = 60F;
            @ConfigEntry.Category("config")
            @ConfigEntry.Gui.Tooltip
            public float max_points = 100;
            @ConfigEntry.Category("config")
            @ConfigEntry.Gui.Tooltip
            public float max_percentage = 80F;
            @ConfigEntry.Category("config")
            @ConfigEntry.Gui.Tooltip
            public float gradient = 1F;
            @ConfigEntry.Category("config")
            @ConfigEntry.Gui.Tooltip
            public float multiplier = 1F;
        }

        @ConfigEntry.Gui.CollapsibleObject
        public ProtectionConfig protection = new ProtectionConfig();

        public static class ProtectionConfig {
            @ConfigEntry.Category("config")
            @ConfigEntry.Gui.Tooltip
            public float multiplier = 1F;
            @ConfigEntry.Category("config")
            @ConfigEntry.Gui.Tooltip
            public float max_percentage = 80F;
        }
    }

    public static class MiscConfig {
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean cleaving_enchantment = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean stackable_stews = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean stackable_potions = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean damage_interruptions = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean knockback_throwables = true;
    }

    public static class IntegrationConfig {
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean lal_quiver_variants = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean lal_quiver_accessories = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean enderscape_rubble_shields = true;
        @ConfigEntry.Category("config")
        @ConfigEntry.Gui.Tooltip
        public boolean end_reborn_netherite = true;
    }

}