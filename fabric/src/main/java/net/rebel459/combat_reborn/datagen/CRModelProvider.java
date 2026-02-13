package net.rebel459.combat_reborn.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.rebel459.combat_reborn.CombatReborn;
import net.rebel459.combat_reborn.registry.CRItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

@Environment(EnvType.CLIENT)
public final class CRModelProvider extends FabricModelProvider {

    public CRModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {}

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        CRItems.QUIVERS.forEach(quiver -> {
            this.addQuiver(quiver.get(), generator);
        });
    }

    private void addQuiver(Item quiver, ItemModelGenerators generator) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(quiver);
        String name = itemId.getPath();

        Identifier texEmpty = CombatReborn.id("item/" + name);
        Identifier tex1 = CombatReborn.id("item/" + name + "_stack0");
        Identifier tex2 = CombatReborn.id("item/" + name + "_stack1");
        Identifier tex3 = CombatReborn.id("item/" + name + "_stack2");
        Identifier texFull = CombatReborn.id("item/" + name + "_stack3");

        ModelTemplates.FLAT_ITEM.create(CombatReborn.id("item/" + name), TextureMapping.layer0(texEmpty), generator.modelOutput);
        ModelTemplates.FLAT_ITEM.create(CombatReborn.id("item/" + name + "_stack0"), TextureMapping.layer0(tex1), generator.modelOutput);
        ModelTemplates.FLAT_ITEM.create(CombatReborn.id("item/" + name + "_stack1"), TextureMapping.layer0(tex2), generator.modelOutput);
        ModelTemplates.FLAT_ITEM.create(CombatReborn.id("item/" + name + "_stack2"), TextureMapping.layer0(tex3), generator.modelOutput);
        ModelTemplates.FLAT_ITEM.create(CombatReborn.id("item/" + name + "_stack3"), TextureMapping.layer0(texFull), generator.modelOutput);

        JsonObject root = new JsonObject();

        JsonObject select = new JsonObject();
        select.addProperty("type", "minecraft:select");
        select.addProperty("property", "minecraft:custom_model_data");

        JsonArray cases = new JsonArray();

        cases.add(createCase("hidden", createEmptyModel()));
        cases.add(createCase("empty", createModelRef("item/" + name)));
        cases.add(createCase("one", createModelRef("item/" + name + "_stack0")));
        cases.add(createCase("two", createModelRef("item/" + name + "_stack1")));
        cases.add(createCase("three", createModelRef("item/" + name + "_stack2")));
        cases.add(createCase("full", createModelRef("item/" + name + "_stack3")));

        select.add("cases", cases);
        select.add("fallback", createModelRef("item/" + name));

        root.add("model", select);

        // items (must be uncommented and manually moved if changed)
        //generator.modelOutput.accept(CombatReborn.id(name), root::deepCopy);
    }

    private JsonObject createCase(String whenValue, JsonObject model) {
        JsonObject caseObj = new JsonObject();
        caseObj.addProperty("when", whenValue);
        caseObj.add("model", model);
        return caseObj;
    }

    private JsonObject createEmptyModel() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:empty");
        return obj;
    }

    private JsonObject createModelRef(String path) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:model");
        obj.addProperty("model", CombatReborn.MOD_ID + ":" + path);
        return obj;
    }
}