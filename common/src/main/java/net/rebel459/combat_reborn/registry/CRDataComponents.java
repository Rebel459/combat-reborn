package net.rebel459.combat_reborn.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.rebel459.combat_reborn.CombatReborn;
import net.rebel459.combat_reborn.config.CRConfig;
import net.rebel459.combat_reborn.tag.CRItemTags;
import net.rebel459.combat_reborn.util.QuiverContents;
import net.rebel459.unified.platform.UnifiedEvents;
import net.rebel459.unified.platform.UnifiedRegistries;

import java.util.function.Supplier;

public class CRDataComponents {
    
    public static UnifiedRegistries.DataComponentTypes COMPONENTS = UnifiedRegistries.DataComponentTypes.create(CombatReborn.MOD_ID);

    public static void init() {
        UnifiedEvents.DefaultDataComponents.modify((item, builder, provider) -> {
            ItemStack stack = item.getDefaultInstance();
            if (((CRConfig.getGeneral().misc.stackable_stews && (stack.is(CRItemTags.SOUP)) || (CRConfig.getGeneral().misc.stackable_potions && (stack.is(CRItemTags.POTIONS)))) && item.getDefaultMaxStackSize() == 1 && stack.has(DataComponents.MAX_STACK_SIZE) && stack.get(DataComponents.MAX_STACK_SIZE) == 1)) {
                builder.set(DataComponents.MAX_STACK_SIZE, 16);
            }
        });
    }

    public static final Supplier<DataComponentType<QuiverContents>> QUIVER_CONTENTS = COMPONENTS.register(
            "quiver_contents", builder -> builder.persistent(QuiverContents.CODEC).networkSynchronized(QuiverContents.STREAM_CODEC).cacheEncoding()
    );
    public static final Supplier<DataComponentType<Integer>> QUIVER_CONTENTS_SLOT = COMPONENTS.register(
            "quiver_contents_slot", builder -> builder.persistent(ExtraCodecs.intRange(-1, 99)).networkSynchronized(ByteBufCodecs.VAR_INT)
    );
}