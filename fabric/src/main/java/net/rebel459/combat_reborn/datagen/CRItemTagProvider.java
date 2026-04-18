package net.rebel459.combat_reborn.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.rebel459.combat_reborn.registry.CRItems;
import net.rebel459.combat_reborn.tag.CRItemTags;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public final class CRItemTagProvider extends FabricTagsProvider.ItemTagsProvider {

	public CRItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
        CRItems.QUIVERS.forEach( quiver -> {
            this.valueLookupBuilder(CRItemTags.QUIVER)
                    .add(quiver.get());
        });
	}
}