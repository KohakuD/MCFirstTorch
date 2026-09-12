package ch.minenox.firsttorch.client;

import com.google.gson.*;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Compare teaching diagrams with the installed target's data, including nested item tags. */
final class Backport1211VanillaCompatibilityTest {
    @Test void craftingDiagramsMatchVanillaIngredientsPlacementAndOutput() throws Exception {
        var recipes = new ArrayList<LiveRecipeCatalog.Recipe>();
        LiveRecipeCatalog.resources().forEach(r -> recipes.add(LiveRecipeCatalog.find(r)));
        LiveRecipePanels.resources().forEach(r -> recipes.addAll(LiveRecipePanels.find(r)));
        recipes.add(LiveCartographyLayout.PANELS.getFirst());
        recipes.add(new LiveRecipeCatalog.Recipe(List.of(LiveHandCraftingLayout.INPUT, "", "", "", "", "", "", "", ""),
                LiveHandCraftingLayout.OUTPUT, LiveHandCraftingLayout.OUTPUT_COUNT, true));
        for (var recipe : recipes) {
            if (recipe.result().equals("minecraft:firework_rocket")) continue; // Special Java recipe, checked separately.
            var vanilla = json("/data/minecraft/recipe/" + recipe.result().substring(10) + ".json");
            String type = vanilla.get("type").getAsString();
            var output = vanilla.getAsJsonObject("result");
            assertEquals(recipe.result(), output.get("id").getAsString());
            assertEquals(recipe.count(), output.has("count") ? output.get("count").getAsInt() : 1, recipe.result());
            if (type.equals("minecraft:crafting_shaped")) {
                assertFalse(recipe.shapeless(), recipe.result());
                var pattern = vanilla.getAsJsonArray("pattern");
                int rows = pattern.size(), columns = pattern.get(0).getAsString().length();
                boolean found = false;
                for (int y = 0; y <= 3 - rows; y++) for (int x = 0; x <= 3 - columns; x++) {
                    for (boolean mirror : new boolean[]{false, true}) {
                        boolean matches = true;
                        for (int sy = 0; sy < 3; sy++) for (int sx = 0; sx < 3; sx++) {
                            char key = ' ';
                            if (sx >= x && sx < x + columns && sy >= y && sy < y + rows)
                                key = pattern.get(sy - y).getAsString().charAt(mirror ? columns - 1 - (sx - x) : sx - x);
                            String item = recipe.ingredients().get(sy * 3 + sx);
                            matches &= key == ' ' ? item.isEmpty() : accepts(vanilla.getAsJsonObject("key").get(String.valueOf(key)), item);
                        }
                        found |= matches;
                    }
                }
                assertTrue(found, "No matching shaped placement: " + recipe.result());
            } else {
                assertEquals("minecraft:crafting_shapeless", type, recipe.result());
                assertTrue(recipe.shapeless(), recipe.result());
                var remaining = new ArrayList<>(recipe.ingredients().stream().filter(i -> !i.isEmpty()).toList());
                for (var ingredient : vanilla.getAsJsonArray("ingredients")) {
                    int matched = -1;
                    for (int i = 0; i < remaining.size(); i++) if (accepts(ingredient, remaining.get(i))) { matched = i; break; }
                    assertTrue(matched >= 0, "Missing ingredient for " + recipe.result());
                    remaining.remove(matched);
                }
                assertTrue(remaining.isEmpty(), "Extra ingredients for " + recipe.result());
            }
        }
    }

    @Test void flightOneRocketMatchesNativeSpecialRecipe() {
        var diagram = LiveRecipeCatalog.find("firsttorch:textures/questpics/firework_rocket_recipe.png");
        var slots = diagram.ingredients().stream().map(id -> id.isEmpty() ? net.minecraft.world.item.ItemStack.EMPTY
                : new net.minecraft.world.item.ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                        net.minecraft.resources.ResourceLocation.parse(id)))).toList();
        var input = net.minecraft.world.item.crafting.CraftingInput.of(3, 3, slots);
        var recipe = new net.minecraft.world.item.crafting.FireworkRocketRecipe(net.minecraft.world.item.crafting.CraftingBookCategory.MISC);
        assertTrue(recipe.matches(input, null));
        var result = recipe.assemble(input, null);
        assertEquals(diagram.count(), result.getCount());
        assertEquals(diagram.result(), net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(result.getItem()).toString());
        var fireworks = result.get(net.minecraft.core.component.DataComponents.FIREWORKS);
        assertNotNull(fireworks);
        assertEquals(1, fireworks.flightDuration());
        assertTrue(fireworks.explosions().isEmpty());
    }

    @Test void curriculumItemsTagsAndAdvancementCriteriaExistInTheTarget() throws Exception {
        try (var input = getClass().getResourceAsStream("/data/firsttorch/guides/course.json")) {
            var guide = ch.minenox.firsttorch.guide.data.GuideJson.read(input);
            for (var chapter : guide.chapters()) {
                if (chapter.iconItemId() != null) requireItem(chapter.iconItemId());
                for (var quest : chapter.quests()) {
                    if (quest.iconItemId() != null) requireItem(quest.iconItemId());
                    for (var reward : quest.rewards()) if (reward.type() == ch.minenox.firsttorch.guide.model.RewardDefinition.Type.ITEM)
                        requireItem(reward.itemId());
                    for (var task : quest.tasks()) {
                        switch (task.type()) {
                            case INVENTORY -> requireItem(task.itemId());
                            case INVENTORY_TAG -> requireTag(task.itemId(), new HashSet<>());
                            case ADVANCEMENT -> {
                                String[] id = task.advancementId().split(":", 2);
                                var advancement = json("/data/" + id[0] + "/advancement/" + id[1] + ".json");
                                if (task.criterion() != null)
                                    assertTrue(advancement.getAsJsonObject("criteria").has(task.criterion()), task.id());
                            }
                            case MANUAL -> { }
                        }
                    }
                }
            }
        }
    }

    @Test void furnaceDiagramsUseMatchingRecipesAndEnoughFuel() throws Exception {
        var fuels = new HashMap<String, Integer>();
        net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.buildFuels((entry, ticks) ->
                entry.map(item -> fuels.put(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).toString(), ticks),
                        tag -> fuels.put("#" + tag.location(), ticks)));
        for (var resource : LiveSmeltingCatalog.resources()) {
            var diagram = LiveSmeltingCatalog.find(resource);
            var recipe = json("/data/minecraft/recipe/" + diagram.result().substring(10) + ".json");
            assertEquals("minecraft:smelting", recipe.get("type").getAsString());
            assertTrue(accepts(recipe.get("ingredient"), diagram.input()), resource);
            assertEquals(diagram.result(), recipe.getAsJsonObject("result").get("id").getAsString());
            int burnTime = 0;
            for (var fuel : fuels.entrySet()) {
                if (fuel.getKey().equals(diagram.fuel()) || fuel.getKey().startsWith("#")
                        && tagContains(fuel.getKey().substring(1), diagram.fuel(), new HashSet<>()))
                    burnTime = Math.max(burnTime, fuel.getValue());
            }
            assertTrue(burnTime >= recipe.get("cookingtime").getAsInt(), "Insufficient fuel: " + resource);
        }
    }

    @Test void brewingDiagramsProduceTheIntendedNativePotions() {
        var expected = Map.of("awkward_potion_brewing", "awkward", "strength_potion_brewing", "strength",
                "fire_resistance_brewing", "fire_resistance", "long_fire_resistance_brewing", "long_fire_resistance");
        var brewing = net.minecraft.world.item.alchemy.PotionBrewing.bootstrap(net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS);
        for (var resource : LiveBrewingCatalog.resources()) {
            var step = LiveBrewingCatalog.find(resource);
            var inputPotion = net.minecraft.core.registries.BuiltInRegistries.POTION.getHolder(
                    net.minecraft.resources.ResourceLocation.withDefaultNamespace(step.inputPotion())).orElseThrow();
            var bottle = net.minecraft.world.item.alchemy.PotionContents.createItemStack(net.minecraft.world.item.Items.POTION, inputPotion);
            var ingredient = new net.minecraft.world.item.ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                    net.minecraft.resources.ResourceLocation.parse(step.ingredient())));
            assertTrue(brewing.hasMix(bottle, ingredient), resource);
            var output = brewing.mix(ingredient, bottle);
            assertTrue(output.is(net.minecraft.world.item.Items.POTION), resource);
            var result = output.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS).potion().orElseThrow();
            String name = resource.substring(resource.lastIndexOf('/') + 1, resource.length() - 4);
            assertNotNull(expected.get(name), resource);
            assertEquals("minecraft:" + expected.get(name), result.unwrapKey().orElseThrow().location().toString(), resource);
        }
    }

    private void requireItem(String id) {
        var key = net.minecraft.resources.ResourceLocation.parse(id);
        var registry = net.minecraft.core.registries.BuiltInRegistries.ITEM;
        assertTrue(registry.containsKey(key), "Unknown item: " + id);
        var item = registry.get(key);
        assertNotEquals(net.minecraft.world.item.Items.AIR, item, id);
        assertTrue(item.isEnabled(net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS), "Experimental item: " + id);
    }

    private void requireTag(String tag, Set<String> visited) throws Exception {
        if (!visited.add(tag)) return;
        String[] id = tag.split(":", 2);
        var values = json("/data/" + id[0] + "/tags/item/" + id[1] + ".json").getAsJsonArray("values");
        assertFalse(values.isEmpty(), "Empty teaching tag: " + tag);
        for (var entry : values) {
            String value = entry.isJsonObject() ? entry.getAsJsonObject().get("id").getAsString() : entry.getAsString();
            if (value.startsWith("#")) requireTag(value.substring(1), visited);
            else requireItem(value);
        }
    }

    private boolean accepts(JsonElement ingredient, String item) throws Exception {
        if (ingredient.isJsonArray()) {
            for (var option : ingredient.getAsJsonArray()) if (accepts(option, item)) return true;
            return false;
        }
        var value = ingredient.getAsJsonObject();
        if (value.has("item")) return item.equals(value.get("item").getAsString());
        return tagContains(value.get("tag").getAsString(), item, new HashSet<>());
    }

    private boolean tagContains(String tag, String item, Set<String> visited) throws Exception {
        if (!visited.add(tag)) return false;
        String[] id = tag.split(":", 2);
        for (var entry : json("/data/" + id[0] + "/tags/item/" + id[1] + ".json").getAsJsonArray("values")) {
            String value = entry.isJsonObject() ? entry.getAsJsonObject().get("id").getAsString() : entry.getAsString();
            if (value.equals(item) || value.startsWith("#") && tagContains(value.substring(1), item, visited)) return true;
        }
        return false;
    }

    private JsonObject json(String path) throws Exception {
        if (path.startsWith("/data/firsttorch/")) {
            try (var input = getClass().getResourceAsStream(path)) {
                assertNotNull(input, path);
                return JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
            }
        }
        try (var zip = new java.util.zip.ZipFile(System.getProperty("firsttorch.vanillaDataJar"))) {
            var entry = zip.getEntry(path.substring(1));
            assertNotNull(entry, path);
            try (var input = zip.getInputStream(entry)) {
            assertNotNull(input, "Missing target data: " + path);
            return JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
            }
        }
    }
}