package net.rebel459.combat_reborn.datagen;

import com.ibm.icu.impl.Pair;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.rebel459.combat_reborn.registry.CRItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public final class CRRecipeProvider extends FabricRecipeProvider {

    public CRRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Contract("_, _ -> new")
    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput exporter) {
        return new RecipeProvider(registries, exporter) {
            @Override
            public void buildRecipes() {

                dyedQuivers(
					List.of(
						Pair.of(CRItems.BLACK_QUIVER.get(), Items.BLACK_DYE),
						Pair.of(CRItems.BLUE_QUIVER.get(), Items.BLUE_DYE),
						Pair.of(CRItems.BROWN_QUIVER.get(), Items.BROWN_DYE),
						Pair.of(CRItems.CYAN_QUIVER.get(), Items.CYAN_DYE),
						Pair.of(CRItems.GRAY_QUIVER.get(), Items.GRAY_DYE),
						Pair.of(CRItems.GREEN_QUIVER.get(), Items.GREEN_DYE),
						Pair.of(CRItems.LIGHT_BLUE_QUIVER.get(), Items.LIGHT_BLUE_DYE),
						Pair.of(CRItems.LIGHT_GRAY_QUIVER.get(), Items.LIGHT_GRAY_DYE),
						Pair.of(CRItems.LIME_QUIVER.get(), Items.LIME_DYE),
						Pair.of(CRItems.MAGENTA_QUIVER.get(), Items.MAGENTA_DYE),
						Pair.of(CRItems.ORANGE_QUIVER.get(), Items.ORANGE_DYE),
						Pair.of(CRItems.PINK_QUIVER.get(), Items.PINK_DYE),
						Pair.of(CRItems.PURPLE_QUIVER.get(), Items.PURPLE_DYE),
						Pair.of(CRItems.RED_QUIVER.get(), Items.RED_DYE),
						Pair.of(CRItems.YELLOW_QUIVER.get(), Items.YELLOW_DYE),
						Pair.of(CRItems.WHITE_QUIVER.get(), Items.WHITE_DYE)
					)
				);
            }

			public void dyedQuivers(List<Pair<Item, Item>> list) {
				list.forEach(pair -> {
				Item quiver = pair.first;
				Item dye = pair.second;
				Stream<Pair<Item, Item>> stream = list.stream().filter(rugPair -> !rugPair.first.equals(quiver));
				List<Pair<Item, Item>> streamList = stream.toList();
				List<Item> rugList = new ArrayList<>();
				for (Pair<Item, Item> rugPair : streamList) {
					rugList.add(rugPair.first);
				}
				this.shapeless(RecipeCategory.COMBAT, quiver)
					.requires(Ingredient.of(rugList.stream()))
					.unlockedBy("has_dye", this.has(dye))
					.group("quiver_dye")
					.save(this.output, "dye_" + RecipeProvider.getItemName(quiver));
				});
			}
        };
    }

    @Override
    public String getName() {
        return "Combat Reborn Recipes";
    }
}