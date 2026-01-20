package net.legacy.combat_reborn.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.legacy.combat_reborn.registry.CRItems;
import net.legacy.combat_reborn.tag.CRItemTags;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public final class CRItemTagProvider extends FabricTagProvider.ItemTagProvider {

	public CRItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
        CRItems.QUIVERS.forEach( quiver -> {
            this.valueLookupBuilder(CRItemTags.QUIVER)
                    .add(quiver);
        });
	}
}