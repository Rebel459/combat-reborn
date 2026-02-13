package net.rebel459.combat_reborn.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.rebel459.combat_reborn.CombatReborn;

public final class CRDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		final FabricDataGenerator.Pack pack = dataGenerator.createPack();

		pack.addProvider(CRModelProvider::new);
		pack.addProvider(CRItemTagProvider::new);
		pack.addProvider(CRRecipeProvider::new);

	}

	@Override
	public String getEffectiveModId() {
		return CombatReborn.MOD_ID;
	}
}