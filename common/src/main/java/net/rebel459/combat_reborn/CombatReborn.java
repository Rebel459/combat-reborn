package net.rebel459.combat_reborn;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
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
import net.rebel459.unified.platform.UnifiedEvents;
import net.rebel459.unified.platform.UnifiedHelpers;
import net.rebel459.unified.platform.UnifiedPlatform;
import net.rebel459.unified.util.PackType;

public class CombatReborn {

    public static boolean hasEndReborn() {
        return UnifiedPlatform.get().isModLoaded("end_reborn");
    }
    public static boolean hasEnchantsAndExpeditions() {
        return UnifiedPlatform.get().isModLoaded("enchants_and_expeditions");
    }
    public static boolean hasLegaciesAndLegends() {
        return UnifiedPlatform.get().isModLoaded("legacies_and_legends");
    }
    
    public static void initRegistries() {
        CRAttributes.init();
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
        if (!CRConfig.getGeneral().shields.shield_overhaul) {
            UnifiedHelpers.PACKS.add(CombatReborn.id("no_shield_overhaul"), PackType.REQUIRED_DATA);
        }
        if (!CRConfig.getGeneral().misc.cleaving_enchantment) {
            UnifiedHelpers.PACKS.add(CombatReborn.id("no_cleaving"), PackType.REQUIRED_DATA);
        }
        if (CRConfig.getGeneral().quivers.craftable) {
            UnifiedHelpers.PACKS.add(CombatReborn.id("craftable_quivers"), PackType.REQUIRED_DATA);
        }
        if (CRConfig.getGeneral().quivers.tradable) {
            UnifiedHelpers.PACKS.add(CombatReborn.id("tradable_quivers"), PackType.REQUIRED_DATA);
        }
        if (hasLegaciesAndLegends()) {
            if (CRConfig.getGeneral().quivers.craftable && CRConfig.getGeneral().integrations.lal_quiver_variants) {
                UnifiedHelpers.PACKS.add(CombatReborn.id("weighted_quiver"), PackType.REQUIRED_DATA);
            }
            if (CRConfig.getGeneral().quivers.enable_quivers && CRConfig.getGeneral().integrations.lal_quiver_variants) {
                UnifiedHelpers.PACKS.add(CombatReborn.id("sapphire_quiver"), PackType.REQUIRED_DATA);
            }
            if (CRConfig.getGeneral().integrations.lal_quiver_accessories) {
                UnifiedHelpers.PACKS.add(CombatReborn.id("quiver_accessories"), PackType.REQUIRED_DATA);
            }
        }
    }

    public static void registerPayloads() {

        UnifiedHelpers.NETWORKING.registerPlayToClient(ShieldInfo.Sync.TYPE, ShieldInfo.Sync.CODEC, (payload, context) -> {
            if (context instanceof ShieldInfo shieldInfo) {
                shieldInfo.setPercentageDamage(payload.percentageDamage());
            }
        });
        UnifiedHelpers.NETWORKING.registerPlayToServer(ShieldInfo.Request.TYPE, ShieldInfo.Request.CODEC, (payload, context) -> {
            if (context instanceof ShieldInfo shieldInfo) {
                int current = shieldInfo.getPercentageDamage();
                UnifiedHelpers.NETWORKING.send(new ShieldInfo.Sync(current), context);
            }
        });

        UnifiedHelpers.NETWORKING.registerPlayToServer(SelectQuiverItemPacket.TYPE, SelectQuiverItemPacket.CODEC, (payload, context) -> {
            int slotId = payload.slotId();
            int selectedSlot = payload.selectedSlot();

            context.level().getServer().execute(() -> {
                ItemStack quiverStack = context.containerMenu.getSlot(slotId).getItem();
                if (quiverStack.getItem() instanceof QuiverItem) {
                    quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT.get(), selectedSlot);
                }
            });
        });

        UnifiedHelpers.NETWORKING.registerPlayToServer(SelectQuiverSlotPacket.TYPE, SelectQuiverSlotPacket.CODEC, (payload, context) -> {
            int slot = payload.slot();

            context.level().getServer().execute(() -> {
                ItemStack quiverStack = QuiverHelper.getQuiver(context);
                if (quiverStack != null) {
                    quiverStack.set(CRDataComponents.QUIVER_CONTENTS_SLOT.get(), slot);
                }
            });
        });

        UnifiedEvents.Players.onJoin(player -> {
            if (player instanceof ServerPlayer serverPlayer && serverPlayer instanceof ShieldInfo shieldInfo) {
                int current = shieldInfo.getPercentageDamage();
                UnifiedHelpers.NETWORKING.send(new ShieldInfo.Sync(current), serverPlayer);
            }
        });
    }

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
	public static final String MOD_ID = "combat_reborn";

}