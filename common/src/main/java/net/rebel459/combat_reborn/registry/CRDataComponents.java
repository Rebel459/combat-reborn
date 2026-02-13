package net.rebel459.combat_reborn.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.rebel459.combat_reborn.CombatReborn;
import net.rebel459.combat_reborn.util.QuiverContents;
import net.rebel459.unified.platform.UnifiedRegistries;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class CRDataComponents {
    
    public static UnifiedRegistries.DataComponentTypes COMPONENTS = UnifiedRegistries.DataComponentTypes.create(CombatReborn.MOD_ID);

    public static void init() {

    }

    public static final Supplier<DataComponentType<QuiverContents>> QUIVER_CONTENTS = COMPONENTS.register(
            "quiver_contents", builder -> builder.persistent(QuiverContents.CODEC).networkSynchronized(QuiverContents.STREAM_CODEC).cacheEncoding()
    );
    public static final Supplier<DataComponentType<Integer>> QUIVER_CONTENTS_SLOT = COMPONENTS.register(
            "quiver_contents_slot", builder -> builder.persistent(ExtraCodecs.intRange(-1, 99)).networkSynchronized(ByteBufCodecs.VAR_INT)
    );
}