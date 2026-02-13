package net.rebel459.combat_reborn;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.rebel459.combat_reborn.config.CRArmorConfig;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.config.CRWeaponConfig;
import net.rebel459.combat_reborn.entity.PlayerSpawnCallback;
import net.rebel459.combat_reborn.item.ArmorAttributeModifierCallback;
import net.rebel459.combat_reborn.item.ItemAttributeModifierCallback;
import net.rebel459.combat_reborn.item.ModifyItemComponentsCallback;
import net.rebel459.combat_reborn.item.QuiverItem;
import net.rebel459.combat_reborn.network.SelectQuiverItemPacket;
import net.rebel459.combat_reborn.network.SelectQuiverSlotPacket;
import net.rebel459.combat_reborn.network.ShieldInfo;
import net.rebel459.combat_reborn.registry.*;
import net.rebel459.combat_reborn.sound.CRSounds;
import net.rebel459.combat_reborn.util.QuiverHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.rebel459.unified.platform.UnifiedEvents;
import net.rebel459.unified.platform.UnifiedHelpers;
import net.rebel459.unified.util.PackInfo;

public class CombatReborn {

    public static boolean hasEndReborn() {
        return UnifiedHelpers.Platform.get().isModLoaded("end_reborn");
    }
    public static boolean hasEnchantsAndExpeditions() {
        return UnifiedHelpers.Platform.get().isModLoaded("enchants_and_expeditions");
    }
    public static boolean hasLegaciesAndLegends() {
        return UnifiedHelpers.Platform.get().isModLoaded("legacies_and_legends");
    }
    
    public static void initRegistries() {
        CRDataComponents.init();
        CRItems.init();
        CRSounds.init();
    }

	public static void init() {
        AutoConfig.register(CRWeaponConfig.class, GsonConfigSerializer::new);
        AutoConfig.register(CRArmorConfig.class, GsonConfigSerializer::new);

        loadResources();

        CRCreativeInventory.init();
        CREnchantments.init();
        CRLootTables.init();

        ModifyItemComponentsCallback.init();
        ArmorAttributeModifierCallback.init();
        ItemAttributeModifierCallback.init();
        PlayerSpawnCallback.init();

        registerPayloads();
	}

    public static void loadResources() {
        var packs = UnifiedHelpers.Packs.get();
        if (!CRConfig.get.general.shields.shield_overhaul) {
            packs.add(CombatReborn.id("no_shield_overhaul"), PackInfo.REQUIRED_DATA);
        }
        if (!CRConfig.get.general.misc.cleaving_enchantment) {
            packs.add(CombatReborn.id("no_cleaving"), PackInfo.REQUIRED_DATA);
        }
        if (CRConfig.get.general.quivers.craftable) {
            packs.add(CombatReborn.id("craftable_quivers"), PackInfo.REQUIRED_DATA);
        }
        if (hasLegaciesAndLegends()) {
            if (CRConfig.get.general.quivers.craftable && CRConfig.get.general.integrations.lal_quiver_variants) {
                packs.add(CombatReborn.id("weighted_quiver"), PackInfo.REQUIRED_DATA);
            }
            if (CRConfig.get.general.quivers.enable_quivers && CRConfig.get.general.integrations.lal_quiver_variants) {
                packs.add(CombatReborn.id("sapphire_quiver"), PackInfo.REQUIRED_DATA);
            }
            if (CRConfig.get.general.integrations.lal_quiver_accessories) {
                packs.add(CombatReborn.id("quiver_accessories"), PackInfo.REQUIRED_DATA);
            }
        }
    }

    public static void registerPayloads() {

        var networking = UnifiedHelpers.NetworkPayloads.get();

        networking.registerS2C(ShieldInfo.Sync.TYPE, ShieldInfo.Sync.CODEC, (payload) -> {
            Player player = Minecraft.getInstance().player;
            if (player instanceof ShieldInfo shieldInfo) {
                shieldInfo.setPercentageDamage(((ShieldInfo.Sync)payload).percentageDamage());
            }
        });
        networking.registerC2S(ShieldInfo.Request.TYPE, ShieldInfo.Request.CODEC, (payload, context) -> {
            ServerPlayer player = (ServerPlayer) context;
            if (player instanceof ShieldInfo shieldInfo) {
                int current = shieldInfo.getPercentageDamage();
                networking.send(new ShieldInfo.Sync(current), player);
            }
        });

        networking.registerC2S(SelectQuiverItemPacket.TYPE, SelectQuiverItemPacket.CODEC, (payload, context) -> {
            SelectQuiverItemPacket packet = (SelectQuiverItemPacket) payload;
            ServerPlayer player = (ServerPlayer) context;
            int slotId = packet.slotId();
            int selectedSlot = packet.selectedSlot();

            player.level().getServer().execute(() -> {
                ItemStack quiverStack = player.containerMenu.getSlot(slotId).getItem();
                if (quiverStack.getItem() instanceof QuiverItem) {
                    quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT.get(), selectedSlot);
                }
            });
        });

        networking.registerC2S(SelectQuiverSlotPacket.TYPE, SelectQuiverSlotPacket.CODEC, (payload, context) -> {
            ServerPlayer player = (ServerPlayer) context;
            int slot = ((SelectQuiverSlotPacket)payload).slot();

            player.level().getServer().execute(() -> {
                ItemStack quiverStack = QuiverHelper.getQuiver(player);
                if (quiverStack != null) {
                    quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT.get(), slot);
                }
            });
        });

        UnifiedEvents.PlayerJoin.access(player -> {
            if (player instanceof ServerPlayer serverPlayer && serverPlayer instanceof ShieldInfo shieldInfo) {
                int current = shieldInfo.getPercentageDamage();
                UnifiedHelpers.NetworkPayloads.get().send(new ShieldInfo.Sync(current), serverPlayer);
            }
        });
    }

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
	public static final String MOD_ID = "combat_reborn";

}