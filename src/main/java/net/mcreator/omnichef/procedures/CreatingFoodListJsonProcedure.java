package net.mcreator.omnichef.procedures;

import net.neoforged.fml.loading.FMLPaths;

import net.minecraft.world.level.LevelAccessor;

import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

public class CreatingFoodListJsonProcedure {
	public static void execute(LevelAccessor world) {
		com.google.gson.JsonObject FoodDatabaseObject = new com.google.gson.JsonObject();
		File FoodDatabase = new File("");
		if (!world.isClientSide()) {
			if (!(world instanceof net.minecraft.server.level.ServerLevel level)) {
				return;
			}
			if (!level.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
				return;
			}
			final long generationStarted = System.nanoTime();
			net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef food database v5: indexing items and recipe outputs.");
			java.util.Set<String> excludedFoods = new java.util.HashSet<>();
			excludedFoods.add("minecraft:ominous_bottle");
			excludedFoods.add("minecraft:enchanted_golden_apple");
			excludedFoods.add("minecraft:suspicious_stew");
			com.google.gson.JsonObject loadedSpecialFoodBonuses = CreateSpecialFoodBonusMapProcedure.execute();
			final com.google.gson.JsonObject specialFoodBonuses = loadedSpecialFoodBonuses != null ? loadedSpecialFoodBonuses : new com.google.gson.JsonObject();
			com.google.gson.JsonObject loadedFoodCategoryBonuses = CreateFoodCategoryBonusMapProcedure.execute();
			final com.google.gson.JsonObject foodCategoryBonuses = loadedFoodCategoryBonuses != null ? loadedFoodCategoryBonuses : new com.google.gson.JsonObject();
			java.util.List<com.google.gson.JsonObject> allFoods = new java.util.ArrayList<>();
			com.google.gson.JsonObject foodDatabase = new com.google.gson.JsonObject();
			com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
			com.google.gson.JsonArray disabledFoodsArray = new com.google.gson.JsonArray();
			final double MODDED_FOOD_BONUS = 4.0;
			java.util.Map<String, net.minecraft.world.item.Item> itemsById = new java.util.HashMap<>();
			for (net.minecraft.world.item.Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
				var itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
				if (itemId != null) {
					itemsById.put(itemId.toString(), item);
				}
			}
			net.minecraft.world.item.crafting.RecipeManager recipeManager = level.getRecipeManager();
			java.util.Map<String, java.util.List<net.minecraft.world.item.crafting.RecipeHolder<?>>> recipesByOutput = new java.util.HashMap<>();
			for (net.minecraft.world.item.crafting.RecipeHolder<?> recipeHolder : recipeManager.getRecipes()) {
				net.minecraft.world.item.crafting.Recipe<?> recipe = recipeHolder.value();
				net.minecraft.world.item.ItemStack result = recipe.getResultItem(level.registryAccess());
				if (result.isEmpty())
					continue;
				var resultId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(result.getItem());
				if (resultId == null)
					continue;
				recipesByOutput.computeIfAbsent(resultId.toString(), key -> new java.util.ArrayList<>()).add(recipeHolder);
			}
			class FoodClassificationRules {
				final java.util.Set<String> excludedItems = new java.util.HashSet<>();
				final java.util.Set<String> excludedClasses = new java.util.HashSet<>();
				final java.util.Set<String> zeroNutritionAllowed = new java.util.HashSet<>();
				final java.util.Map<String, Integer> minimumTiers = new java.util.HashMap<>();
				final java.util.Map<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>, Integer> tagMinimumTiers = new java.util.HashMap<>();
				final java.util.Map<String, Integer> categoryMinimumCache = new java.util.HashMap<>();
				final java.util.Set<String> preparedPortions = new java.util.HashSet<>();
				boolean acquisitionEnabled = true;
				int sourcePasses = 48;
				int unresolvedTier = 3;
				double breedingStep = 1.0;
				double chanceWeight = 0.5;
				double stepsPerTier = 2.0;
				final java.util.Map<String, String> overrides = new java.util.HashMap<>();
				final java.util.Set<String> culinaryIds = new java.util.HashSet<>();
				final java.util.List<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> ingredientTags = new java.util.ArrayList<>();
				final java.util.List<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> preparedTags = new java.util.ArrayList<>();
				final java.util.List<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> culinaryTags = new java.util.ArrayList<>();
				final java.util.List<String> allowedTypes = new java.util.ArrayList<>();
				final java.util.List<String> cuttingTypes = new java.util.ArrayList<>();
				final java.util.Map<String, String> hints = new java.util.HashMap<>();
				final java.util.Map<String, String> hintReasons = new java.util.HashMap<>();
				final java.util.Map<String, Boolean> edible = new java.util.HashMap<>();
				final java.util.Map<String, Boolean> culinary = new java.util.HashMap<>();

				com.google.gson.JsonArray strings(String... values) {
					com.google.gson.JsonArray result = new com.google.gson.JsonArray();
					for (String value : values)
						result.add(value);
					return result;
				}

				com.google.gson.JsonObject defaults() {
					com.google.gson.JsonObject config = new com.google.gson.JsonObject();
					config.addProperty("config_version", 3);
					config.add("excluded_items", strings("artifacts:everlasting_beef", "artifacts:eternal_steak"));
					config.add("excluded_item_classes", strings("artifacts.item.EverlastingFoodItem"));
					config.add("zero_nutrition_allow_items", strings());
					com.google.gson.JsonObject acquisition = new com.google.gson.JsonObject();
					acquisition.addProperty("enabled", true);
					acquisition.addProperty("max_passes", 48);
					acquisition.addProperty("breeding_step", 1.0);
					acquisition.addProperty("chance_weight", 0.5);
					acquisition.addProperty("steps_per_tier", 2.0);
					acquisition.addProperty("unresolved_min_tier", 3);
					acquisition.add("minimum_tiers", new com.google.gson.JsonObject());
					com.google.gson.JsonObject tagMinimums = new com.google.gson.JsonObject();
					tagMinimums.addProperty("c:foods/raw_fish", 1);
					tagMinimums.addProperty("c:foods/cooked_fish", 1);
					tagMinimums.addProperty("c:foods/berry", 1);
					acquisition.add("tag_minimum_tiers", tagMinimums);
					config.add("acquisition", acquisition);
					config.add("role_overrides", new com.google.gson.JsonObject());
					config.add("ingredient_tags", strings("c:foods/raw_meat", "c:foods/raw_fish", "c:foods/fruit", "c:foods/vegetable", "c:foods/berry", "c:foods/dough", "c:crops", "c:eggs", "c:drinks/milk", "c:buckets/milk", "c:flours", "c:flour",
							"c:dough", "c:doughs"));
					config.add("prepared_tags", strings("c:foods/bread", "c:foods/cookie", "c:foods/soup", "c:foods/cooked_meat", "c:foods/cooked_fish", "c:foods/pie", "c:foods/candy", "c:foods/golden"));
					config.add("culinary_tags", strings("c:foods", "c:crops", "c:eggs", "c:drinks/milk", "c:buckets/milk", "c:flours", "c:flour", "c:dough", "c:doughs", "c:sugar", "c:sugars", "c:salt", "c:salts", "c:spices"));
					config.add("culinary_items", strings("minecraft:wheat", "minecraft:cocoa_beans", "minecraft:sugar", "minecraft:milk_bucket", "minecraft:egg"));
					config.add("culinary_recipe_types", strings("*:crafting", "*:smelting", "*:smoking", "*:campfire_cooking", "*:blasting", "farmersdelight:cooking", "farmersdelight:cutting", "create:mixing", "create:compacting", "create:filling",
							"create:cutting", "*:cooking", "*:baking", "*:frying", "*:toaster_heating", "*:microwave_heating", "*:oven_baking", "*:frying_pan_cooking", "*:cutting_board_combining", "*:cutting_board_slicing"));
					config.add("cutting_recipe_types", strings("*:cutting", "*:slicing", "*:cutting_board_slicing"));
					return config;
				}

				java.util.List<String> stringList(com.google.gson.JsonObject config, String key) {
					com.google.gson.JsonElement value = config.get(key);
					if (value == null || !value.isJsonArray())
						throw new IllegalArgumentException(key + " must be an array of strings");
					java.util.List<String> result = new java.util.ArrayList<>();
					for (com.google.gson.JsonElement entry : value.getAsJsonArray()) {
						if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString() || entry.getAsString().isBlank())
							throw new IllegalArgumentException(key + " contains an invalid entry");
						result.add(entry.getAsString());
					}
					return result;
				}

				void validate(com.google.gson.JsonObject config) {
					for (String key : new String[]{"ingredient_tags", "prepared_tags", "culinary_tags", "culinary_items", "excluded_items", "zero_nutrition_allow_items"}) {
						for (String id : stringList(config, key))
							net.minecraft.resources.ResourceLocation.parse(id);
					}
					for (String key : new String[]{"culinary_recipe_types", "cutting_recipe_types"}) {
						for (String pattern : stringList(config, key)) {
							if (!pattern.contains(":"))
								throw new IllegalArgumentException(key + " requires namespaced recipe types");
							net.minecraft.resources.ResourceLocation.parse(pattern.replace("*", "wildcard"));
							if (pattern.indexOf('*') >= 0 && !(pattern.startsWith("*:") && pattern.indexOf('*', 1) < 0) && !(pattern.endsWith(":*") && pattern.indexOf('*') == pattern.length() - 1))
								throw new IllegalArgumentException("Unsupported recipe pattern: " + pattern);
						}
					}
					stringList(config, "excluded_item_classes");
					var acquisition = config.getAsJsonObject("acquisition");
					if (!acquisition.get("enabled").isJsonPrimitive() || !acquisition.get("enabled").getAsJsonPrimitive().isBoolean())
						throw new IllegalArgumentException("acquisition.enabled must be a boolean");
					for (String key : new String[]{"max_passes", "breeding_step", "chance_weight", "steps_per_tier", "unresolved_min_tier"}) {
						double number = acquisition.get(key).getAsDouble();
						if (!Double.isFinite(number) || number < 0 || number > 64)
							throw new IllegalArgumentException("Invalid acquisition setting: " + key);
					}
					if (acquisition.get("steps_per_tier").getAsDouble() <= 0 || acquisition.get("max_passes").getAsInt() < 1)
						throw new IllegalArgumentException("Acquisition passes and steps_per_tier must be positive");
					for (String minimumKey : new String[]{"minimum_tiers", "tag_minimum_tiers"})
						for (var entry : acquisition.getAsJsonObject(minimumKey).entrySet()) {
							net.minecraft.resources.ResourceLocation.parse(entry.getKey());
							double tier = entry.getValue().getAsDouble();
							if (!Double.isFinite(tier) || tier < 0 || tier > 9 || tier != Math.floor(tier))
								throw new IllegalArgumentException("Minimum tiers must be integers from 0 to 9");
						}
					if (!config.get("role_overrides").isJsonObject())
						throw new IllegalArgumentException("role_overrides must be an object");
					for (var entry : config.getAsJsonObject("role_overrides").entrySet()) {
						net.minecraft.resources.ResourceLocation.parse(entry.getKey());
						if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isString() || !("FOOD".equals(entry.getValue().getAsString()) || "INGREDIENT".equals(entry.getValue().getAsString())))
							throw new IllegalArgumentException("Invalid role override for " + entry.getKey());
					}
				}

				void addTags(java.util.List<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> target, com.google.gson.JsonObject config, String key) {
					for (String id : stringList(config, key))
						target.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse(id)));
				}

				FoodClassificationRules() {
					com.google.gson.JsonObject config = defaults();
					java.nio.file.Path file = net.neoforged.fml.loading.FMLPaths.GAMEDIR.get().resolve("config/omnichef/FoodClassification.json");
					try {
						if (java.nio.file.Files.exists(file)) {
							try (java.io.Reader reader = java.nio.file.Files.newBufferedReader(file, java.nio.charset.StandardCharsets.UTF_8)) {
								com.google.gson.JsonElement value = com.google.gson.JsonParser.parseReader(reader);
								if (!value.isJsonObject())
									throw new IllegalArgumentException("Expected a configuration object");
								for (var entry : value.getAsJsonObject().entrySet()) {
									if (entry.getKey().equals("acquisition") && entry.getValue().isJsonObject()) {
										for (var setting : entry.getValue().getAsJsonObject().entrySet())
											config.getAsJsonObject("acquisition").add(setting.getKey(), setting.getValue());
									} else
										config.add(entry.getKey(), entry.getValue());
								}
							}
							validate(config);
						} else {
							java.nio.file.Files.createDirectories(file.getParent());
							java.nio.file.Files.writeString(file, new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(config), java.nio.charset.StandardCharsets.UTF_8);
						}
					} catch (Exception exception) {
						config = defaults();
						net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef food classification: could not load or create {}; using built-in defaults. {}", file, exception.toString());
					}
					for (var entry : config.getAsJsonObject("role_overrides").entrySet())
						overrides.put(entry.getKey(), entry.getValue().getAsString());
					addTags(ingredientTags, config, "ingredient_tags");
					addTags(preparedTags, config, "prepared_tags");
					addTags(culinaryTags, config, "culinary_tags");
					culinaryIds.addAll(stringList(config, "culinary_items"));
					allowedTypes.addAll(stringList(config, "culinary_recipe_types"));
					cuttingTypes.addAll(stringList(config, "cutting_recipe_types"));
					excludedItems.addAll(stringList(config, "excluded_items"));
					excludedClasses.addAll(stringList(config, "excluded_item_classes"));
					zeroNutritionAllowed.addAll(stringList(config, "zero_nutrition_allow_items"));
					var acquisition = config.getAsJsonObject("acquisition");
					acquisitionEnabled = acquisition.get("enabled").getAsBoolean();
					sourcePasses = acquisition.get("max_passes").getAsInt();
					unresolvedTier = acquisition.get("unresolved_min_tier").getAsInt();
					breedingStep = acquisition.get("breeding_step").getAsDouble();
					chanceWeight = acquisition.get("chance_weight").getAsDouble();
					stepsPerTier = acquisition.get("steps_per_tier").getAsDouble();
					for (var entry : acquisition.getAsJsonObject("minimum_tiers").entrySet())
						minimumTiers.put(entry.getKey(), entry.getValue().getAsInt());
					for (var entry : acquisition.getAsJsonObject("tag_minimum_tiers").entrySet())
						tagMinimumTiers.put(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse(entry.getKey())), entry.getValue().getAsInt());
				}

				int categoryMinimum(String id) {
					return categoryMinimumCache.computeIfAbsent(id, key -> {
						var item = itemsById.get(key);
						int tier = 0;
						if (item != null) {
							var stack = new net.minecraft.world.item.ItemStack(item);
							for (var entry : tagMinimumTiers.entrySet())
								if (stack.is(entry.getKey()))
									tier = Math.max(tier, entry.getValue());
						}
						return tier;
					});
				}

				String exclusionReason(String id, net.minecraft.world.food.FoodProperties food) {
					if (excludedItems.contains(id))
						return "excluded_item";
					var item = itemsById.get(id);
					for (Class<?> type = item.getClass(); type != null; type = type.getSuperclass()) {
						if (excludedClasses.contains(type.getName()))
							return "reusable_food_class";
					}
					try {
						Object result = food.getClass().getMethod("usingConvertsTo").invoke(food);
						if (result instanceof java.util.Optional<?> optional && optional.orElse(null) instanceof net.minecraft.world.item.ItemStack remainder) {
							if (net.minecraft.world.item.ItemStack.isSameItemSameComponents(new net.minecraft.world.item.ItemStack(item), remainder))
								return "returns_same_food";
						}
					} catch (ReflectiveOperationException ignored) {
						// Optional food-conversion API; custom reusable items are covered by class/ID rules.
					}
					if (food.nutrition() <= 0 && food.saturation() <= 0 && !zeroNutritionAllowed.contains(id))
						return "no_nutritional_value";
					return "";
				}

				boolean matchesTags(String id, java.util.List<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> tags) {
					net.minecraft.world.item.Item item = itemsById.get(id);
					if (item == null)
						return false;
					var stack = new net.minecraft.world.item.ItemStack(item);
					for (var tag : tags) {
						if (stack.is(tag))
							return true;
					}
					return false;
				}

				boolean isEdible(String id) {
					return edible.computeIfAbsent(id, key -> {
						var item = itemsById.get(key);
						return item != null && item.getFoodProperties(new net.minecraft.world.item.ItemStack(item), null) != null;
					});
				}

				String hint(String id) {
					return hints.computeIfAbsent(id, key -> {
						if (overrides.containsKey(key)) {
							hintReasons.put(key, "override");
							return overrides.get(key);
						}
						if (preparedPortions.contains(key)) {
							hintReasons.put(key, "feast_portion");
							return "FOOD";
						}
						if (matchesTags(key, preparedTags)) {
							hintReasons.put(key, "prepared_tag");
							return "FOOD";
						}
						if (matchesTags(key, ingredientTags)) {
							hintReasons.put(key, "ingredient_tag");
							return "INGREDIENT";
						}
						return "";
					});
				}

				boolean isCulinary(String id) {
					return culinary.computeIfAbsent(id, key -> isEdible(key) || "FOOD".equals(hint(key)) || culinaryIds.contains(key) || matchesTags(key, culinaryTags));
				}

				boolean matchesType(String id, java.util.List<String> patterns) {
					for (String pattern : patterns) {
						if (pattern.equals(id) || (pattern.startsWith("*:") && id.endsWith(pattern.substring(1))) || (pattern.endsWith(":*") && id.startsWith(pattern.substring(0, pattern.length() - 1))))
							return true;
					}
					return false;
				}

				String typeId(net.minecraft.world.item.crafting.RecipeHolder<?> holder) {
					var id = net.minecraft.core.registries.BuiltInRegistries.RECIPE_TYPE.getKey(holder.value().getType());
					return id == null ? holder.value().getType().toString() : id.toString();
				}
			}
			FoodClassificationRules classificationRules = new FoodClassificationRules();
			class FeastPortions {
				final java.util.Map<String, java.util.List<ServingSource>> sources = new java.util.TreeMap<>();
				int count = 0;
				int warnings = 0;

				final class ServingSource {
					final String whole, portion, container;
					final int servings;

					ServingSource(String whole, String portion, String container, int servings) {
						this.whole = whole;
						this.portion = portion;
						this.container = container;
						this.servings = servings;
					}
				}

				FeastPortions() {
					for (var block : net.minecraft.core.registries.BuiltInRegistries.BLOCK) {
						boolean feast = false;
						for (Class<?> type = block.getClass(); type != null; type = type.getSuperclass())
							feast |= type.getName().equals("vectorwing.farmersdelight.common.block.FeastBlock");
						if (!feast)
							continue;
						try {
							int servings = ((Number) block.getClass().getMethod("getMaxServings").invoke(block)).intValue();
							Object value = block.getClass().getMethod("getServingItem", net.minecraft.world.level.block.state.BlockState.class).invoke(block, block.defaultBlockState());
							if (!(value instanceof net.minecraft.world.item.ItemStack serving) || serving.isEmpty() || servings < 1 || servings > 64)
								throw new IllegalArgumentException("Invalid feast serving or serving count");
							String whole = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(block.asItem()).toString();
							String portion = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(serving.getItem()).toString();
							if (whole.equals(portion) || !itemsById.containsKey(whole) || !classificationRules.isEdible(portion))
								continue;
							String container = "";
							if (serving.hasCraftingRemainingItem()) {
								var remainder = serving.getCraftingRemainingItem();
								if (!remainder.isEmpty())
									container = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(remainder.getItem()).toString();
							}
							sources.computeIfAbsent(portion, key -> new java.util.ArrayList<>()).add(new ServingSource(whole, portion, container, servings));
							classificationRules.preparedPortions.add(portion);
							count++;
						} catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
							warnings++;
							net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef feast adapter {}: {}", block.getClass().getName(), exception.toString());
						}
					}
					for (var list : sources.values())
						list.sort(java.util.Comparator.comparing(source -> source.whole));
				}
			}
			FeastPortions feastPortions = new FeastPortions();
			class AcquisitionSources {
				final java.util.Map<String, java.util.List<SourceRoute>> routes = new java.util.HashMap<>();
				final java.util.Map<String, Double> costs = new java.util.HashMap<>();
				final java.util.Map<String, SourceRoute> selected = new java.util.HashMap<>();
				final java.util.Set<String> nodes = new java.util.HashSet<>();
				final java.util.Set<String> controlled = new java.util.HashSet<>();
				final java.util.Set<String> treeSaplings = new java.util.HashSet<>();
				final java.util.Set<String> breedingTargets = new java.util.HashSet<>();
				final java.util.Map<java.util.List<Object>, String> variantNodes = new java.util.HashMap<>();
				final java.util.Map<String, String> nodeItems = new java.util.HashMap<>();
				final java.util.Map<String, String> warnings = new java.util.TreeMap<>();
				int treeCount = 0;
				int beeRoutes = 0;
				int passes = 0;
				int totalRoutes = 0;
				boolean settled = false;

				final class SourceRoute {
					final String system;
					final java.util.List<java.util.List<String>> requirements;
					final double effort;
					final double chance;
					final double yield;

					SourceRoute(String system, java.util.List<java.util.List<String>> requirements, double effort, double chance, double yield) {
						this.system = system;
						this.requirements = requirements;
						this.effort = effort;
						this.chance = chance;
						this.yield = yield;
					}
				}

				String itemNode(String id) {
					String key = "item|" + id;
					nodes.add(key);
					nodeItems.put(key, id);
					return key;
				}

				String stackNode(net.minecraft.world.item.ItemStack stack) {
					String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
					if (net.minecraft.world.item.ItemStack.isSameItemSameComponents(stack, new net.minecraft.world.item.ItemStack(stack.getItem())))
						return itemNode(id);
					java.util.List<Object> key = java.util.List.of(id, stack.copy().getComponents());
					String node = variantNodes.computeIfAbsent(key, ignored -> "variant|" + id + "|" + variantNodes.size());
					nodes.add(node);
					nodeItems.put(node, id);
					controlled.add(node);
					return node;
				}

				java.util.List<String> ingredientNodes(net.minecraft.world.item.crafting.Ingredient ingredient) {
					java.util.Set<String> ids = new java.util.TreeSet<>();
					for (var stack : ingredient.getItems()) {
						if (stack != null && !stack.isEmpty())
							ids.add(stackNode(stack));
					}
					return new java.util.ArrayList<>(ids);
				}

				Object field(Object target, String name) throws ReflectiveOperationException {
					return target.getClass().getField(name).get(target);
				}

				Object call(Object target, String name) throws ReflectiveOperationException {
					return target.getClass().getMethod(name).invoke(target);
				}

				String beeNode(Object value) throws ReflectiveOperationException {
					if (value instanceof java.util.function.Supplier<?> supplier)
						value = supplier.get();
					if (value == null)
						throw new IllegalArgumentException("Unresolved bee ingredient");
					String key = "bee|" + call(value, "getBeeType");
					nodes.add(key);
					return key;
				}

				double probabilityEffort(double chance) {
					return classificationRules.chanceWeight * Math.log10(1.0 / Math.max(0.000001, Math.min(1.0, chance)));
				}

				void add(String output, String system, java.util.List<java.util.List<String>> inputs, double effort, double chance, double yield) {
					if (!Double.isFinite(chance) || chance <= 0 || !Double.isFinite(yield) || yield <= 0 || !Double.isFinite(effort) || effort < 0)
						return;
					for (var slot : inputs) {
						if (slot.isEmpty())
							return;
						nodes.addAll(slot);
					}
					nodes.add(output);
					routes.computeIfAbsent(output, key -> new java.util.ArrayList<>()).add(new SourceRoute(system, inputs, effort, Math.min(1.0, chance), yield));
					totalRoutes++;
				}

				void loadTrees() {
					try {
						Class<?> finder = Class.forName("cy.jdkdigital.productivetrees.registry.TreeFinder", false, getClass().getClassLoader());
						Object value = finder.getField("trees").get(null);
						if (!(value instanceof java.util.Map<?, ?> trees))
							throw new IllegalArgumentException("Tree registry is not a map");
						for (var entry : trees.entrySet()) {
							String id = entry.getKey().toString();
							String saplingId = id + "_sapling";
							if (!itemsById.containsKey(saplingId))
								continue;
							String sapling = itemNode(saplingId);
							treeSaplings.add(sapling);
							String leavesId = id + "_leaves";
							if (itemsById.containsKey(leavesId)) {
								String leaves = itemNode(leavesId);
								controlled.add(leaves);
								add(leaves, "productivetrees:grown_leaves", java.util.List.of(java.util.List.of(sapling)), 0.0, 1.0, 1.0);
							}
							Object fruit = call(entry.getValue(), "getFruit");
							String fruitId = call(fruit, "fruitItem").toString();
							if (itemsById.containsKey(fruitId)) {
								String output = itemNode(fruitId);
								controlled.add(output);
								add(output, "productivetrees:fruit", java.util.List.of(java.util.List.of(sapling)), 0.0, 1.0, ((Number) call(fruit, "count")).doubleValue());
							}
							treeCount++;
						}
					} catch (ClassNotFoundException ignored) {
						// Optional integration; this mod is not installed.
					} catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
						warnings.put("productivetrees:registry", exception.toString());
					}
				}

				boolean loadSpecial(Object recipe) throws ReflectiveOperationException {
					String type = recipe.getClass().getName();
					if (type.equals("cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe")) {
						var result = (net.minecraft.world.item.ItemStack) field(recipe, "result");
						if (result.isEmpty())
							return true;
						String output = stackNode(result);
						controlled.add(output);
						breedingTargets.add(output);
						double chance = ((Number) field(recipe, "chance")).doubleValue();
						add(output, "productivetrees:tree_pollination",
								java.util.List.of(ingredientNodes((net.minecraft.world.item.crafting.Ingredient) field(recipe, "leafA")), ingredientNodes((net.minecraft.world.item.crafting.Ingredient) field(recipe, "leafB"))),
								classificationRules.breedingStep + probabilityEffort(chance), chance, result.getCount());
						return true;
					}
					if (type.equals("cy.jdkdigital.productivebees.common.recipe.BeeBreedingRecipe")) {
						String output = beeNode(field(recipe, "offspring"));
						controlled.add(output);
						add(output, "productivebees:breeding", java.util.List.of(java.util.List.of(beeNode(field(recipe, "parent1"))), java.util.List.of(beeNode(field(recipe, "parent2")))), classificationRules.breedingStep, 1.0, 1.0);
						beeRoutes++;
						return true;
					}
					if (type.equals("cy.jdkdigital.productivebees.common.recipe.BeeConversionRecipe")) {
						String output = beeNode(field(recipe, "result"));
						controlled.add(output);
						double chance = ((Number) field(recipe, "chance")).doubleValue();
						add(output, "productivebees:conversion", java.util.List.of(java.util.List.of(beeNode(field(recipe, "source"))), ingredientNodes((net.minecraft.world.item.crafting.Ingredient) field(recipe, "item"))),
								classificationRules.breedingStep + probabilityEffort(chance), chance, 1.0);
						beeRoutes++;
						return true;
					}
					if (type.equals("cy.jdkdigital.productivebees.common.recipe.AdvancedBeehiveRecipe")) {
						String bee = beeNode(field(recipe, "ingredient"));
						Object outputs = call(recipe, "getRecipeOutputs");
						if (!(outputs instanceof java.util.Map<?, ?> map))
							throw new IllegalArgumentException("Bee outputs are not a map");
						for (var entry : map.entrySet()) {
							var result = (net.minecraft.world.item.ItemStack) entry.getKey();
							if (result.isEmpty())
								continue;
							String output = stackNode(result);
							String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(result.getItem()).toString();
							if (!(id.startsWith("minecraft:") && "INGREDIENT".equals(classificationRules.hint(id))))
								controlled.add(output);
							double chance = ((Number) call(entry.getValue(), "chance")).doubleValue();
							double yield = (((Number) call(entry.getValue(), "min")).doubleValue() + ((Number) call(entry.getValue(), "max")).doubleValue()) / 2.0;
							add(output, "productivebees:production", java.util.List.of(java.util.List.of(bee)), classificationRules.breedingStep + probabilityEffort(chance), chance, yield);
							beeRoutes++;
						}
						return true;
					}
					return false;
				}

				void build() {
					if (!classificationRules.acquisitionEnabled)
						return;
					net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef food database v5: indexing acquisition sources.");
					loadTrees();
					for (var holder : recipeManager.getRecipes()) {
						var recipe = holder.value();
						try {
							if (loadSpecial(recipe))
								continue;
							var result = recipe.getResultItem(level.registryAccess());
							if (result.isEmpty())
								continue;
							var ingredients = recipe.getIngredients();
							if (ingredients == null || ingredients.isEmpty())
								continue;
							java.util.List<java.util.List<String>> inputs = new java.util.ArrayList<>();
							for (var ingredient : ingredients)
								inputs.add(ingredientNodes(ingredient));
							add(stackNode(result), classificationRules.typeId(holder), inputs, 1.0, 1.0, result.getCount());
						} catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
							warnings.putIfAbsent(recipe.getClass().getName(), exception.toString());
						}
					}
					for (var sources : feastPortions.sources.values()) {
						for (var source : sources) {
							java.util.List<java.util.List<String>> requirements = new java.util.ArrayList<>();
							requirements.add(java.util.List.of(itemNode(source.whole)));
							if (!source.container.isEmpty())
								requirements.add(java.util.List.of(itemNode(source.container)));
							add(itemNode(source.portion), "farmersdelight:portioning", requirements, 0.0, 1.0, source.servings);
						}
					}
					for (String id : itemsById.keySet()) {
						if (classificationRules.isEdible(id))
							itemNode(id);
					}
					java.util.List<String> ordered = new java.util.ArrayList<>(nodes);
					java.util.Collections.sort(ordered);
					for (String node : ordered) {
						String id = nodeItems.get(node);
						boolean assumedBase = !routes.containsKey(node) && !controlled.contains(node);
						assumedBase |= treeSaplings.contains(node) && !breedingTargets.contains(node);
						assumedBase |= node.startsWith("item|") && !controlled.contains(node) && id != null && "INGREDIENT".equals(classificationRules.hint(id));
						if (assumedBase)
							costs.put(node, 0.0);
					}
					for (int pass = 0; pass < classificationRules.sourcePasses; pass++) {
						boolean changed = false;
						java.util.Map<String, Double> next = new java.util.HashMap<>(costs);
						for (String output : ordered) {
							double current = costs.getOrDefault(output, Double.POSITIVE_INFINITY);
							for (SourceRoute route : routes.getOrDefault(output, java.util.Collections.emptyList())) {
								double dependency = 0.0;
								for (var slot : route.requirements) {
									double cheapest = Double.POSITIVE_INFINITY;
									for (String alternative : slot)
										cheapest = Math.min(cheapest, costs.getOrDefault(alternative, Double.POSITIVE_INFINITY));
									dependency = Math.max(dependency, cheapest);
								}
								double candidate = dependency + route.effort;
								if (candidate + 0.0000001 < current) {
									current = candidate;
									next.put(output, candidate);
									selected.put(output, route);
									changed = true;
								}
							}
						}
						costs.clear();
						costs.putAll(next);
						passes = pass + 1;
						if (!changed) {
							settled = true;
							break;
						}
					}
					for (var entry : warnings.entrySet())
						net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef acquisition adapter {}: {}", entry.getKey(), entry.getValue());
					if (!settled)
						net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef acquisition analysis reached its pass limit; some source estimates may be incomplete.");
					net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef food database v5: acquisition analysis: {} nodes, {} routes, {} trees, {} bee routes, {} passes, settled={}; base sources are estimates.", nodes.size(), totalRoutes,
							treeCount, beeRoutes, passes, settled);
				}

				int minimumTier(String id, int tierCount) {
					int tier = 0;
					if (classificationRules.acquisitionEnabled) {
						String key = "item|" + id;
						double cost = costs.getOrDefault(key, Double.POSITIVE_INFINITY);
						if (Double.isFinite(cost))
							tier = (int) Math.floor((cost + 0.0000001) / classificationRules.stepsPerTier);
						else if (controlled.contains(key))
							tier = classificationRules.unresolvedTier;
					}
					tier = Math.max(tier, classificationRules.minimumTiers.getOrDefault(id, 0));
					tier = Math.max(tier, classificationRules.categoryMinimum(id));
					return Math.max(0, Math.min(tierCount - 1, tier));
				}

				com.google.gson.JsonObject describe(String id, int tierCount) {
					String node = "item|" + id;
					com.google.gson.JsonObject result = new com.google.gson.JsonObject();
					Double cost = costs.get(node);
					SourceRoute route = selected.get(node);
					String status = !classificationRules.acquisitionEnabled
							? "disabled"
							: (cost == null ? (controlled.contains(node) ? "unresolved" : "unrecognized") : (route == null ? "assumed_base" : (settled ? "estimated" : "estimated_partial")));
					result.addProperty("status", status);
					result.addProperty("minimum_tier", minimumTier(id, tierCount));
					result.addProperty("manual_minimum", classificationRules.minimumTiers.getOrDefault(id, 0));
					result.addProperty("category_minimum", classificationRules.categoryMinimum(id));
					if (cost != null)
						result.addProperty("effort", cost);
					if (route != null) {
						result.addProperty("system", route.system);
						result.addProperty("chance", route.chance);
						result.addProperty("yield", route.yield);
						com.google.gson.JsonArray requirements = new com.google.gson.JsonArray();
						for (var slot : route.requirements) {
							com.google.gson.JsonArray alternatives = new com.google.gson.JsonArray();
							for (String alternative : slot)
								alternatives.add(alternative);
							requirements.add(alternatives);
						}
						result.add("requirements", requirements);
					}
					return result;
				}
			}
			AcquisitionSources acquisitionSources = new AcquisitionSources();
			class FoodScoreResolver {
				final java.util.Map<String, String> roles = new java.util.HashMap<>();
				final java.util.Map<String, String> roleReasons = new java.util.HashMap<>();
				final java.util.Map<String, RecipeAnalysis> chosenAnalyses = new java.util.HashMap<>();
				static final int MAX_HEIGHT_PASSES = 12;
				static final int UNKNOWN_HEIGHT = Integer.MAX_VALUE / 4;
				static final double INVALID = Double.POSITIVE_INFINITY;
				final java.util.Map<String, Double> scoreCache = new java.util.HashMap<>();
				final java.util.Map<String, Double> recipeScoreCache = new java.util.HashMap<>();
				final java.util.Map<String, net.minecraft.world.item.crafting.RecipeHolder<?>> chosenRecipeCache = new java.util.HashMap<>();
				final java.util.Map<String, java.util.List<String>> chosenIngredientsCache = new java.util.HashMap<>();
				final java.util.Map<String, java.util.List<RecipeAnalysis>> validRecipesCache = new java.util.HashMap<>();
				final java.util.Map<String, Double> categoryBonusCache = new java.util.HashMap<>();
				final java.util.Map<String, java.util.List<String>> categoriesCache = new java.util.HashMap<>();
				final java.util.Map<String, java.util.List<IndexedRecipe>> rawRecipes = new java.util.HashMap<>();
				final java.util.Map<String, java.util.List<IndexedRecipe>> usableRecipes = new java.util.HashMap<>();
				final java.util.Map<String, java.util.Set<String>> directInputs = new java.util.HashMap<>();
				final java.util.Map<String, java.util.Set<String>> unavoidableInputs = new java.util.HashMap<>();
				final java.util.Set<String> independentRecipeOutputs = new java.util.HashSet<>();
				final java.util.Map<net.minecraft.world.item.crafting.Ingredient, java.util.List<String>> ingredientIds = new java.util.IdentityHashMap<>();
				final java.util.Map<String, Double> qualityScores = new java.util.HashMap<>();
				final java.util.Map<String, Double> acquisitionBonuses = new java.util.HashMap<>();
				final java.util.Map<String, Double> rawScores = new java.util.HashMap<>();
				long lastProgressLog = System.nanoTime();
				long evaluatedRecipes = 0;
				int indexedOutputs = 0;
				int graphNodes = 0;
				int scoredItems = 0;
				int foodsProcessed = 0;
				int cyclicComponents = 0;
				int cycleAnchors = 0;
				String currentFood = "";
				String phase = "indexing";

				final class IndexedRecipe {
					final net.minecraft.world.item.crafting.RecipeHolder<?> holder;
					final String method;
					final int outputCount;
					final java.util.List<java.util.List<String>> slots;
					String portionSource = "";
					int servings = 1;

					IndexedRecipe(net.minecraft.world.item.crafting.RecipeHolder<?> holder, String method, int outputCount, java.util.List<java.util.List<String>> slots) {
						this.holder = holder;
						this.method = method;
						this.outputCount = outputCount;
						this.slots = slots;
					}
				}

				void logProgress(boolean force) {
					long now = System.nanoTime();
					if (force || now - lastProgressLog >= 2000000000L) {
						net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef food database v5: phase={}, indexed={}, scored={}/{}, foods={}, current={}, evaluated={}, elapsed={}s", phase, indexedOutputs, scoredItems, graphNodes, foodsProcessed,
								currentFood, evaluatedRecipes, (now - generationStarted) / 1000000000L);
						lastProgressLog = now;
					}
				}

				final class RecipeAnalysis {
					final net.minecraft.world.item.crafting.RecipeHolder<?> recipeHolder;
					final String method;
					final int outputCount;
					final double preparePoints;
					final double ingredientScore;
					final double recipeScore;
					final java.util.List<java.util.List<String>> ingredientSlots;
					final java.util.List<String> selectedIngredients;
					String portionSource = "";
					int servings = 1;

					RecipeAnalysis(net.minecraft.world.item.crafting.RecipeHolder<?> recipeHolder, String method, int outputCount, double preparePoints, double ingredientScore, double recipeScore,
							java.util.List<java.util.List<String>> ingredientSlots, java.util.List<String> selectedIngredients) {
						this.recipeHolder = recipeHolder;
						this.method = method;
						this.outputCount = outputCount;
						this.preparePoints = preparePoints;
						this.ingredientScore = ingredientScore;
						this.recipeScore = recipeScore;
						this.ingredientSlots = ingredientSlots;
						this.selectedIngredients = selectedIngredients;
					}

					com.google.gson.JsonObject toJson() {
						com.google.gson.JsonObject recipeObject = new com.google.gson.JsonObject();
						recipeObject.addProperty("method", method);
						recipeObject.addProperty("acquisition", recipeHolder == null);
						recipeObject.addProperty("outputCount", outputCount);
						if (!portionSource.isEmpty()) {
							recipeObject.addProperty("portion_source", portionSource);
							recipeObject.addProperty("servings", servings);
						}
						recipeObject.addProperty("preparePoints", preparePoints);
						recipeObject.addProperty("ingredientScore", ingredientScore);
						recipeObject.addProperty("recipeScore", recipeScore);
						com.google.gson.JsonArray slotsArray = new com.google.gson.JsonArray();
						for (java.util.List<String> slot : ingredientSlots) {
							com.google.gson.JsonArray alternativesArray = new com.google.gson.JsonArray();
							for (String alternativeName : slot) {
								alternativesArray.add(alternativeName);
							}
							slotsArray.add(alternativesArray);
						}
						recipeObject.add("ingredientSlots", slotsArray);
						com.google.gson.JsonArray selectedArray = new com.google.gson.JsonArray();
						for (String selectedIngredient : selectedIngredients) {
							selectedArray.add(selectedIngredient);
						}
						recipeObject.add("selectedIngredients", selectedArray);
						return recipeObject;
					}
				}

				java.util.List<String> idsOf(net.minecraft.world.item.crafting.Ingredient ingredient) {
					return ingredientIds.computeIfAbsent(ingredient, key -> {
						java.util.Set<String> ids = new java.util.TreeSet<>();
						net.minecraft.world.item.ItemStack[] alternatives = key.getItems();
						if (alternatives != null) {
							for (net.minecraft.world.item.ItemStack alternative : alternatives) {
								if (alternative == null || alternative.isEmpty())
									continue;
								var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(alternative.getItem());
								if (id != null && itemsById.containsKey(id.toString()))
									ids.add(id.toString());
							}
						}
						return new java.util.ArrayList<>(ids);
					});
				}

				java.util.List<IndexedRecipe> rawRecipesFor(String output) {
					java.util.List<IndexedRecipe> cached = rawRecipes.get(output);
					if (cached != null)
						return cached;
					java.util.List<IndexedRecipe> indexed = new java.util.ArrayList<>();
					java.util.Set<String> inputs = new java.util.HashSet<>();
					java.util.Set<String> unavoidable = null;
					for (net.minecraft.world.item.crafting.RecipeHolder<?> holder : recipesByOutput.getOrDefault(output, java.util.Collections.emptyList())) {
						var recipe = holder.value();
						var ingredients = recipe.getIngredients();
						if (ingredients == null || ingredients.isEmpty())
							continue;
						java.util.List<java.util.List<String>> slots = new java.util.ArrayList<>();
						java.util.Set<String> mandatory = new java.util.HashSet<>();
						boolean valid = true;
						for (var ingredient : ingredients) {
							java.util.List<String> ids = idsOf(ingredient);
							inputs.addAll(ids);
							slots.add(ids);
							if (ids.isEmpty())
								valid = false;
							if (ids.size() == 1)
								mandatory.add(ids.get(0));
						}
						if (!valid)
							continue;
						independentRecipeOutputs.add(output);
						if (unavoidable == null)
							unavoidable = new java.util.HashSet<>(mandatory);
						else
							unavoidable.retainAll(mandatory);
						var result = recipe.getResultItem(level.registryAccess());
						if (!result.isEmpty())
							indexed.add(new IndexedRecipe(holder, classificationRules.typeId(holder), Math.max(1, result.getCount()), slots));
					}
					for (var source : feastPortions.sources.getOrDefault(output, java.util.Collections.emptyList())) {
						for (var holder : recipesByOutput.getOrDefault(source.whole, java.util.Collections.emptyList())) {
							var recipe = holder.value();
							var ingredients = recipe.getIngredients();
							var result = recipe.getResultItem(level.registryAccess());
							long batch = (long) result.getCount() * source.servings;
							if (result.isEmpty() || batch < 1 || batch > 4096 || ingredients == null || ingredients.isEmpty())
								continue;
							java.util.List<java.util.List<String>> slots = new java.util.ArrayList<>();
							java.util.Set<String> mandatory = new java.util.HashSet<>();
							boolean valid = true;
							for (var ingredient : ingredients) {
								var ids = idsOf(ingredient);
								slots.add(ids);
								if (ids.isEmpty())
									valid = false;
							}
							if (!source.container.isEmpty())
								for (int i = 0; i < batch; i++)
									slots.add(java.util.List.of(source.container));
							if (!valid)
								continue;
							for (var slot : slots) {
								inputs.addAll(slot);
								if (slot.size() == 1)
									mandatory.add(slot.get(0));
							}
							independentRecipeOutputs.add(output);
							if (unavoidable == null)
								unavoidable = new java.util.HashSet<>(mandatory);
							else
								unavoidable.retainAll(mandatory);
							var normalized = new IndexedRecipe(holder, classificationRules.typeId(holder), (int) batch, slots);
							normalized.portionSource = source.whole;
							normalized.servings = source.servings;
							indexed.add(normalized);
						}
					}
					indexed.sort(java.util.Comparator.comparing((IndexedRecipe recipe) -> recipe.method).thenComparingInt(recipe -> recipe.outputCount).thenComparing(recipe -> recipe.slots.toString()));
					directInputs.put(output, inputs);
					unavoidableInputs.put(output, unavoidable == null ? java.util.Collections.emptySet() : unavoidable);
					rawRecipes.put(output, indexed);
					indexedOutputs++;
					if ((indexedOutputs & 255) == 0)
						logProgress(false);
					return indexed;
				}

				boolean hasRecipeUsingItem(String producedItemName, String requiredItemName) {
					rawRecipesFor(producedItemName);
					return directInputs.get(producedItemName).contains(requiredItemName);
				}

				boolean hasIndependentRecipe(String producedItemName, String excludedIngredientName) {
					rawRecipesFor(producedItemName);
					return independentRecipeOutputs.contains(producedItemName) && !unavoidableInputs.get(producedItemName).contains(excludedIngredientName);
				}

				boolean culinaryRecipe(IndexedRecipe recipe, String output) {
					if (!classificationRules.matchesType(recipe.method, classificationRules.allowedTypes))
						return false;
					boolean culinaryInput = false;
					for (java.util.List<String> slot : recipe.slots) {
						for (String ingredient : slot)
							culinaryInput |= classificationRules.isCulinary(ingredient);
					}
					if (!culinaryInput)
						return false;
					boolean conversion = classificationRules.matchesType(recipe.method, classificationRules.cuttingTypes);
					if (recipe.slots.size() == 1 && recipe.outputCount > 1) {
						for (String ingredient : recipe.slots.get(0))
							conversion |= hasRecipeUsingItem(ingredient, output);
					}
					if (recipe.slots.size() == 1 && conversion) {
						// A single raw ingredient being cut or unpacked does not become a prepared dish.
						for (String ingredient : recipe.slots.get(0)) {
							if ("FOOD".equals(classificationRules.hint(ingredient)))
								return true;
						}
						return false;
					}
					return true;
				}

				java.util.List<IndexedRecipe> usableRecipesFor(String output) {
					java.util.List<IndexedRecipe> cached = usableRecipes.get(output);
					if (cached != null)
						return cached;
					java.util.List<IndexedRecipe> result = new java.util.ArrayList<>();
					if ("INGREDIENT".equals(classificationRules.hint(output))) {
						usableRecipes.put(output, result);
						return result;
					}
					for (IndexedRecipe recipe : rawRecipesFor(output)) {
						if (classificationRules.isEdible(output) && !culinaryRecipe(recipe, output))
							continue;
						java.util.List<java.util.List<String>> slots = new java.util.ArrayList<>();
						boolean valid = true;
						for (java.util.List<String> slot : recipe.slots) {
							java.util.List<String> alternatives = new java.util.ArrayList<>();
							for (String ingredient : slot) {
								if (recipe.outputCount > 1 && hasRecipeUsingItem(ingredient, output) && !hasIndependentRecipe(ingredient, output))
									continue;
								alternatives.add(ingredient);
							}
							if (alternatives.isEmpty()) {
								valid = false;
								break;
							}
							slots.add(alternatives);
						}
						if (valid) {
							var filtered = new IndexedRecipe(recipe.holder, recipe.method, recipe.outputCount, slots);
							filtered.portionSource = recipe.portionSource;
							filtered.servings = recipe.servings;
							result.add(filtered);
						}
					}
					usableRecipes.put(output, result);
					return result;
				}

				double rawScore(String itemName) {
					return rawScores.computeIfAbsent(itemName, key -> roundScore(Math.max(1.0, 1.0 + qualityScores.get(key) + acquisitionBonuses.get(key))));
				}

				void buildScores() {
					java.util.Set<String> relevant = new java.util.HashSet<>();
					java.util.ArrayDeque<String> pending = new java.util.ArrayDeque<>();
					java.util.List<String> registered = new java.util.ArrayList<>(itemsById.keySet());
					java.util.Collections.sort(registered);
					for (String id : registered) {
						var item = itemsById.get(id);
						if (!excludedFoods.contains(id) && item.getFoodProperties(new net.minecraft.world.item.ItemStack(item), null) != null && relevant.add(id))
							pending.addLast(id);
					}
					java.util.Map<String, java.util.Set<String>> edges = new java.util.HashMap<>();
					java.util.Map<String, java.util.Set<String>> reverse = new java.util.HashMap<>();
					while (!pending.isEmpty()) {
						String output = pending.removeFirst();
						currentFood = output;
						java.util.Set<String> dependencies = new java.util.TreeSet<>();
						for (IndexedRecipe recipe : usableRecipesFor(output)) {
							for (java.util.List<String> slot : recipe.slots)
								dependencies.addAll(slot);
						}
						edges.put(output, dependencies);
						for (String ingredient : dependencies) {
							reverse.computeIfAbsent(ingredient, key -> new java.util.TreeSet<>()).add(output);
							if (relevant.add(ingredient))
								pending.addLast(ingredient);
						}
						logProgress(false);
					}
					java.util.List<String> nodes = new java.util.ArrayList<>(relevant);
					java.util.Collections.sort(nodes);
					graphNodes = nodes.size();
					phase = "finding cycles";
					logProgress(true);
					java.util.Set<String> visited = new java.util.HashSet<>();
					java.util.List<String> finished = new java.util.ArrayList<>();
					java.util.ArrayDeque<String> path = new java.util.ArrayDeque<>();
					java.util.ArrayDeque<java.util.Iterator<String>> iterators = new java.util.ArrayDeque<>();
					for (String start : nodes) {
						if (!visited.add(start))
							continue;
						path.push(start);
						iterators.push(edges.get(start).iterator());
						while (!path.isEmpty()) {
							var iterator = iterators.peek();
							if (iterator.hasNext()) {
								String next = iterator.next();
								if (visited.add(next)) {
									path.push(next);
									iterators.push(edges.get(next).iterator());
								}
							} else {
								finished.add(path.pop());
								iterators.pop();
							}
						}
					}
					visited.clear();
					java.util.List<java.util.List<String>> components = new java.util.ArrayList<>();
					for (int index = finished.size() - 1; index >= 0; index--) {
						String start = finished.get(index);
						if (!visited.add(start))
							continue;
						java.util.List<String> component = new java.util.ArrayList<>();
						pending.addLast(start);
						while (!pending.isEmpty()) {
							String current = pending.removeFirst();
							component.add(current);
							for (String next : reverse.getOrDefault(current, java.util.Collections.emptySet())) {
								if (visited.add(next))
									pending.addLast(next);
							}
						}
						components.add(component);
					}
					java.util.Map<String, Integer> heights = new java.util.HashMap<>();
					for (String id : nodes) {
						heights.put(id, usableRecipesFor(id).isEmpty() ? 0 : UNKNOWN_HEIGHT);
						qualityScores.put(id, getQualityScore(id));
						acquisitionBonuses.put(id, getAppliedAcquisitionBonus(id));
					}
					phase = "ordering dependencies";
					for (int pass = 0; pass < MAX_HEIGHT_PASSES; pass++) {
						java.util.Map<String, Integer> nextHeights = new java.util.HashMap<>(heights);
						boolean changed = false;
						for (String output : nodes) {
							int bestHeight = heights.get(output);
							for (IndexedRecipe recipe : usableRecipesFor(output)) {
								int recipeHeight = 0;
								for (java.util.List<String> slot : recipe.slots) {
									int slotHeight = UNKNOWN_HEIGHT;
									for (String ingredient : slot)
										slotHeight = Math.min(slotHeight, heights.get(ingredient));
									recipeHeight = Math.max(recipeHeight, slotHeight);
								}
								if (recipeHeight < UNKNOWN_HEIGHT)
									bestHeight = Math.min(bestHeight, recipeHeight + 1);
							}
							if (bestHeight != heights.get(output)) {
								nextHeights.put(output, bestHeight);
								changed = true;
							}
						}
						heights = nextHeights;
						logProgress(false);
						if (!changed)
							break;
					}
					final java.util.Map<String, Integer> acquisitionHeights = heights;
					java.util.Comparator<String> cycleOrder = java.util.Comparator.comparingInt((String id) -> acquisitionHeights.get(id)).thenComparingDouble(id -> rawScore(id)).thenComparing(java.util.Comparator.naturalOrder());
					phase = "scoring";
					logProgress(true);
					for (int index = components.size() - 1; index >= 0; index--) {
						java.util.List<String> component = components.get(index);
						boolean cyclic = component.size() > 1 || edges.get(component.get(0)).contains(component.get(0));
						if (cyclic) {
							cyclicComponents++;
							component.sort(cycleOrder);
						}
						for (String output : component) {
							currentFood = output;
							java.util.List<RecipeAnalysis> validRecipes = new java.util.ArrayList<>();
							for (IndexedRecipe recipe : usableRecipesFor(output)) {
								RecipeAnalysis analysis = analyzeIndexedRecipe(recipe, output);
								if (analysis != null)
									validRecipes.add(analysis);
							}
							validRecipes.sort((first, second) -> compareRecipes(first, second));
							RecipeAnalysis best = validRecipes.isEmpty() ? null : validRecipes.get(0);
							String hint = classificationRules.hint(output);
							String role = hint.isEmpty() ? (best == null ? "INGREDIENT" : "FOOD") : hint;
							roles.put(output, role);
							roleReasons.put(output, hint.isEmpty() ? (best == null ? "no_culinary_recipe" : "culinary_recipe") : classificationRules.hintReasons.get(output));
							if (best == null && "FOOD".equals(role)) {
								// Acquisition has no recipe prerequisites, so tagged prepared foods remain unlockable.
								best = new RecipeAnalysis(null, "omnichef:acquisition", 1, 0.0, 1.0, 1.0, java.util.Collections.emptyList(), java.util.Collections.emptyList());
								validRecipes.add(best);
							}
							if (best != null)
								chosenAnalyses.put(output, best);
							double score;
							if (best == null) {
								score = rawScore(output);
								recipeScoreCache.put(output, 0.0);
								chosenIngredientsCache.put(output, java.util.Collections.emptyList());
								if (cyclic)
									cycleAnchors++;
							} else {
								score = roundScore(Math.max(0.1, best.recipeScore + qualityScores.get(output) + acquisitionBonuses.get(output)));
								recipeScoreCache.put(output, best.recipeScore);
								chosenRecipeCache.put(output, best.recipeHolder);
								chosenIngredientsCache.put(output, best.selectedIngredients);
							}
							scoreCache.put(output, score);
							validRecipesCache.put(output, validRecipes);
							scoredItems++;
							logProgress(false);
						}
					}
					phase = "exporting foods";
					currentFood = "";
					logProgress(true);
					net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef food database v5: dependency analysis completed; cyclic groups={}, acquisition anchors={}, evaluated recipes={}.", cyclicComponents, cycleAnchors, evaluatedRecipes);
				}

				RecipeAnalysis analyzeIndexedRecipe(IndexedRecipe recipe, String output) {
					evaluatedRecipes++;
					double ingredientTotal = 0.0;
					java.util.List<java.util.List<String>> slots = new java.util.ArrayList<>();
					java.util.List<String> selected = new java.util.ArrayList<>();
					boolean unpacking = recipe.slots.size() == 1 && recipe.outputCount > 1;
					for (java.util.List<String> slot : recipe.slots) {
						java.util.List<String> available = new java.util.ArrayList<>();
						double cheapest = INVALID;
						String chosen = null;
						for (String ingredient : slot) {
							Double score = scoreCache.get(ingredient);
							// Only earlier dependencies are available; every retained path is acyclic.
							if (score == null || !Double.isFinite(score))
								continue;
							available.add(ingredient);
							if (score < cheapest || (Double.compare(score, cheapest) == 0 && (chosen == null || ingredient.compareTo(chosen) < 0))) {
								cheapest = score;
								chosen = ingredient;
							}
						}
						if (chosen == null)
							return null;
						slots.add(available);
						selected.add(chosen);
						ingredientTotal += cheapest;
						unpacking = unpacking && hasRecipeUsingItem(chosen, output) && hasIndependentRecipe(chosen, output);
					}
					ingredientTotal = roundScore(ingredientTotal);
					double preparePoints = unpacking ? 0.0 : getPreparePoints(recipe.method);
					double recipeScore = roundScore((ingredientTotal + preparePoints) / recipe.outputCount);
					var analysis = new RecipeAnalysis(recipe.holder, recipe.method, recipe.outputCount, preparePoints, ingredientTotal, recipeScore, slots, selected);
					analysis.portionSource = recipe.portionSource;
					analysis.servings = recipe.servings;
					return analysis;
				}

				double resolve(String itemName) {
					return scoreCache.getOrDefault(itemName, INVALID);
				}

				double roundScore(double value) {
					if (!Double.isFinite(value))
						return value;
					return Math.round(value * 10.0) / 10.0;
				}

				int compareRecipes(RecipeAnalysis first, RecipeAnalysis second) {
					int scoreComparison = Double.compare(first.recipeScore, second.recipeScore);
					if (scoreComparison != 0)
						return scoreComparison;
					int methodComparison = first.method.compareTo(second.method);
					if (methodComparison != 0)
						return methodComparison;
					int outputComparison = Integer.compare(first.outputCount, second.outputCount);
					if (outputComparison != 0)
						return outputComparison;
					return String.join("|", first.selectedIngredients).compareTo(String.join("|", second.selectedIngredients));
				}

				java.util.List<RecipeAnalysis> getValidRecipes(String itemName) {
					resolve(itemName);
					return validRecipesCache.getOrDefault(itemName, java.util.Collections.emptyList());
				}

				double getSpecialFoodBonus(String itemName) {
					if (itemName == null || !specialFoodBonuses.has(itemName))
						return 0.0;
					com.google.gson.JsonElement bonusElement = specialFoodBonuses.get(itemName);
					if (bonusElement == null || !bonusElement.isJsonPrimitive() || !bonusElement.getAsJsonPrimitive().isNumber())
						return 0.0;
					try {
						return roundScore(bonusElement.getAsDouble());
					} catch (Exception ignored) {
						return 0.0;
					}
				}

				boolean hasSpecialFoodBonus(String itemName) {
					if (itemName == null || !specialFoodBonuses.has(itemName))
						return false;
					com.google.gson.JsonElement bonusElement = specialFoodBonuses.get(itemName);
					return bonusElement != null && bonusElement.isJsonPrimitive() && bonusElement.getAsJsonPrimitive().isNumber();
				}

				double getAppliedAcquisitionBonus(String itemName) {
					return roundScore((hasSpecialFoodBonus(itemName) ? getSpecialFoodBonus(itemName) : getCategoryBonus(itemName)) + getModdedFoodBonus(itemName));
				}

				double getModdedFoodBonus(String itemName) {
					if (itemName == null)
						return 0.0;
					net.minecraft.world.item.Item item = itemsById.get(itemName);
					if (item == null)
						return 0.0;
					net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
					if (item.getFoodProperties(stack, null) == null)
						return 0.0;
					var itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
					if (itemId == null)
						return 0.0;
					String namespace = itemId.getNamespace();
					if ("minecraft".equals(namespace) || "omnichef".equals(namespace))
						return 0.0;
					return MODDED_FOOD_BONUS;
				}

				double getCategoryBonus(String itemName) {
					resolveCategoryData(itemName);
					return categoryBonusCache.getOrDefault(itemName, 0.0);
				}

				java.util.List<String> getCategories(String itemName) {
					resolveCategoryData(itemName);
					return categoriesCache.getOrDefault(itemName, java.util.Collections.singletonList("uncategorized"));
				}

				void resolveCategoryData(String itemName) {
					if (itemName == null || categoryBonusCache.containsKey(itemName))
						return;
					net.minecraft.world.item.Item item = itemsById.get(itemName);
					if (item == null) {
						categoryBonusCache.put(itemName, 0.0);
						categoriesCache.put(itemName, java.util.Collections.singletonList("uncategorized"));
						return;
					}
					net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
					java.util.Set<String> foundCategories = new java.util.LinkedHashSet<>();
					double totalBonus = 0.0;
					for (java.util.Map.Entry<String, com.google.gson.JsonElement> entry : foodCategoryBonuses.entrySet()) {
						String tagName = entry.getKey();
						com.google.gson.JsonElement bonusElement = entry.getValue();
						if (tagName == null || bonusElement == null || !bonusElement.isJsonPrimitive() || !bonusElement.getAsJsonPrimitive().isNumber())
							continue;
						try {
							net.minecraft.resources.ResourceLocation tagLocation = net.minecraft.resources.ResourceLocation.parse(tagName);
							net.minecraft.tags.TagKey<net.minecraft.world.item.Item> tagKey = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, tagLocation);
							if (!stack.is(tagKey))
								continue;
							String categoryName = tagName;
							int foodPathStart = tagName.indexOf(":foods/");
							if (foodPathStart >= 0) {
								categoryName = tagName.substring(foodPathStart + 7);
							} else {
								int namespaceEnd = tagName.indexOf(':');
								if (namespaceEnd >= 0 && namespaceEnd + 1 < tagName.length())
									categoryName = tagName.substring(namespaceEnd + 1);
							}
							foundCategories.add(categoryName.replace('/', '_'));
							totalBonus += bonusElement.getAsDouble();
						} catch (Exception ignored) {
						}
					}
					if (foundCategories.isEmpty())
						foundCategories.add("uncategorized");
					categoryBonusCache.put(itemName, roundScore(totalBonus));
					categoriesCache.put(itemName, new java.util.ArrayList<>(foundCategories));
				}

				double getPreparePoints(String recipeType) {
					if (recipeType.contains("smoking") || recipeType.contains("campfire_cooking") || recipeType.contains("smelting"))
						return 3.0;
					if (recipeType.contains("crafting"))
						return 4.0;
					if (recipeType.contains("cutting"))
						return 2.5;
					return 5.0;
				}

				double getQualityScore(String itemName) {
					net.minecraft.world.item.Item item = itemsById.get(itemName);
					if (item == null)
						return 0.0;
					net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
					net.minecraft.world.food.FoodProperties food = item.getFoodProperties(stack, null);
					if (food == null)
						return 0.0;
					int positiveEffects = 0;
					int negativeEffects = 0;
					for (net.minecraft.world.food.FoodProperties.PossibleEffect possibleEffect : food.effects()) {
						var mobEffectInstance = possibleEffect.effect();
						if (mobEffectInstance == null || mobEffectInstance.getEffect() == null)
							continue;
						if (mobEffectInstance.getEffect().value().isBeneficial()) {
							positiveEffects++;
						} else {
							negativeEffects++;
						}
					}
					return roundScore(food.nutrition() + food.saturation() * 1.5 + positiveEffects * 4.0 - negativeEffects * 8.0);
				}
			}
			FoodScoreResolver scoreResolver = new FoodScoreResolver();
			scoreResolver.buildScores();
			acquisitionSources.build();
			com.google.gson.JsonArray excludedFoodsArray = new com.google.gson.JsonArray();
			for (net.minecraft.world.item.Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
				net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
				net.minecraft.world.food.FoodProperties food = item.getFoodProperties(stack, null);
				if (food == null)
					continue;
				var itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
				if (itemId == null)
					continue;
				String itemName = itemId.toString();
				String exclusionReason = excludedFoods.contains(itemName) ? "built_in_exclusion" : classificationRules.exclusionReason(itemName, food);
				if (!exclusionReason.isEmpty()) {
					com.google.gson.JsonObject excluded = new com.google.gson.JsonObject();
					excluded.addProperty("id", itemName);
					excluded.addProperty("reason", exclusionReason);
					excludedFoodsArray.add(excluded);
					continue;
				}
				scoreResolver.currentFood = itemName;
				scoreResolver.foodsProcessed++;
				scoreResolver.logProgress(false);
				int hunger = food.nutrition();
				float saturation = food.saturation();
				com.google.gson.JsonArray positiveEffectsArray = new com.google.gson.JsonArray();
				com.google.gson.JsonArray negativeEffectsArray = new com.google.gson.JsonArray();
				for (net.minecraft.world.food.FoodProperties.PossibleEffect possibleEffect : food.effects()) {
					var mobEffectInstance = possibleEffect.effect();
					if (mobEffectInstance == null || mobEffectInstance.getEffect() == null)
						continue;
					var effect = mobEffectInstance.getEffect().value();
					var effectId = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getKey(effect);
					if (effectId == null)
						continue;
					if (effect.isBeneficial()) {
						positiveEffectsArray.add(effectId.toString());
					} else {
						negativeEffectsArray.add(effectId.toString());
					}
				}
				double qualityScore = scoreResolver.getQualityScore(itemName);
				double specialBonus = scoreResolver.getSpecialFoodBonus(itemName);
				double categoryBonus = scoreResolver.getCategoryBonus(itemName);
				double moddedFoodBonus = scoreResolver.getModdedFoodBonus(itemName);
				double appliedAcquisitionBonus = scoreResolver.getAppliedAcquisitionBonus(itemName);
				double score = scoreResolver.resolve(itemName);
				if (!Double.isFinite(score))
					score = Math.max(1.0, 1.0 + qualityScore + appliedAcquisitionBonus);
				double recipeScore = scoreResolver.recipeScoreCache.getOrDefault(itemName, 0.0);
				FoodScoreResolver.RecipeAnalysis chosenAnalysis = scoreResolver.chosenAnalyses.get(itemName);
				java.util.List<String> selectedIngredients = scoreResolver.chosenIngredientsCache.getOrDefault(itemName, java.util.Collections.emptyList());
				java.util.List<FoodScoreResolver.RecipeAnalysis> validRecipes = scoreResolver.getValidRecipes(itemName);
				com.google.gson.JsonArray ingredientsArray = new com.google.gson.JsonArray();
				for (String ingredientName : selectedIngredients) {
					ingredientsArray.add(ingredientName);
				}
				com.google.gson.JsonArray recipesArray = new com.google.gson.JsonArray();
				int selectedRecipeIndex = -1;
				for (int recipeIndex = 0; recipeIndex < validRecipes.size(); recipeIndex++) {
					FoodScoreResolver.RecipeAnalysis recipeAnalysis = validRecipes.get(recipeIndex);
					recipesArray.add(recipeAnalysis.toJson());
					if (recipeAnalysis == chosenAnalysis)
						selectedRecipeIndex = recipeIndex;
				}
				com.google.gson.JsonArray categoriesArray = new com.google.gson.JsonArray();
				for (String categoryName : scoreResolver.getCategories(itemName)) {
					categoriesArray.add(categoryName);
				}
				String recipeType = chosenAnalysis == null ? "none" : chosenAnalysis.method;
				int recipeOutputCount = chosenAnalysis == null ? 0 : chosenAnalysis.outputCount;
				int recipeCount = recipesByOutput.getOrDefault(itemName, java.util.Collections.emptyList()).size();
				String role = scoreResolver.roles.getOrDefault(itemName, "INGREDIENT");
				com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
				foodObject.addProperty("id", itemName);
				foodObject.addProperty("role", role);
				foodObject.addProperty("role_reason", scoreResolver.roleReasons.getOrDefault(itemName, "no_culinary_recipe"));
				foodObject.addProperty("nutrition", hunger);
				foodObject.addProperty("saturation", saturation);
				foodObject.add("ingredients", ingredientsArray);
				foodObject.addProperty("method", recipeType);
				foodObject.addProperty("recipeCount", recipeCount);
				foodObject.addProperty("validRecipeCount", validRecipes.size());
				foodObject.addProperty("selectedRecipe", selectedRecipeIndex);
				foodObject.addProperty("recipeOutputCount", recipeOutputCount);
				foodObject.add("recipes", recipesArray);
				foodObject.addProperty("positiveEffects", positiveEffectsArray.size());
				foodObject.addProperty("negativeEffects", negativeEffectsArray.size());
				foodObject.add("positiveEffectsList", positiveEffectsArray);
				foodObject.add("negativeEffectsList", negativeEffectsArray);
				foodObject.addProperty("rawBaseScore", 1.0);
				foodObject.addProperty("qualityScore", qualityScore);
				foodObject.addProperty("recipeScore", recipeScore);
				foodObject.add("categories", categoriesArray);
				foodObject.addProperty("categoryBonus", categoryBonus);
				foodObject.addProperty("specialBonus", specialBonus);
				foodObject.addProperty("moddedFoodBonus", moddedFoodBonus);
				foodObject.addProperty("appliedAcquisitionBonus", appliedAcquisitionBonus);
				foodObject.addProperty("score", score);
				if (negativeEffectsArray.size() > 0) {
					disabledFoodsArray.add(foodObject);
				} else {
					allFoods.add(foodObject);
				}
			}
			allFoods.sort(java.util.Comparator.<com.google.gson.JsonObject>comparingDouble(food -> food.get("score").getAsDouble()).thenComparing(food -> food.get("id").getAsString()));
			com.google.gson.JsonArray tier0Array = new com.google.gson.JsonArray();
			com.google.gson.JsonArray tier1Array = new com.google.gson.JsonArray();
			com.google.gson.JsonArray tier2Array = new com.google.gson.JsonArray();
			com.google.gson.JsonArray tier3Array = new com.google.gson.JsonArray();
			com.google.gson.JsonArray tier4Array = new com.google.gson.JsonArray();
			com.google.gson.JsonArray tier5Array = new com.google.gson.JsonArray();
			com.google.gson.JsonArray tier6Array = new com.google.gson.JsonArray();
			com.google.gson.JsonArray tier7Array = new com.google.gson.JsonArray();
			com.google.gson.JsonArray tier8Array = new com.google.gson.JsonArray();
			com.google.gson.JsonArray tier9Array = new com.google.gson.JsonArray();
			int totalFoods = allFoods.size();
			int tierCount = totalFoods / 40;
			tierCount = Math.max(6, tierCount);
			tierCount = Math.min(10, tierCount);
			for (int i = 0; i < totalFoods; i++) {
				com.google.gson.JsonObject food = allFoods.get(i);
				double percentile = (double) i / totalFoods;
				int tier = (int) (percentile * tierCount);
				if (tier >= tierCount)
					tier = tierCount - 1;
				String foodId = food.get("id").getAsString();
				food.add("acquisition", acquisitionSources.describe(foodId, tierCount));
				food.addProperty("tier", Math.max(tier, acquisitionSources.minimumTier(foodId, tierCount)));
			}
			java.util.Map<String, com.google.gson.JsonObject> foodObjectsById = new java.util.HashMap<>();
			for (com.google.gson.JsonObject food : allFoods) {
				foodObjectsById.put(food.get("id").getAsString(), food);
			}
			boolean tierChanged;
			do {
				tierChanged = false;
				for (com.google.gson.JsonObject food : allFoods) {
					if (!food.has("ingredients") || !food.get("ingredients").isJsonArray())
						continue;
					int currentTier = food.get("tier").getAsInt();
					int requiredTier = currentTier;
					for (com.google.gson.JsonElement ingredientElement : food.getAsJsonArray("ingredients")) {
						if (ingredientElement == null || !ingredientElement.isJsonPrimitive())
							continue;
						String ingredientId = ingredientElement.getAsString();
						com.google.gson.JsonObject ingredientFood = foodObjectsById.get(ingredientId);
						if (ingredientFood == null || !ingredientFood.has("tier"))
							continue;
						requiredTier = Math.max(requiredTier, ingredientFood.get("tier").getAsInt());
					}
					if (requiredTier > currentTier) {
						food.addProperty("tier", requiredTier);
						tierChanged = true;
					}
				}
			} while (tierChanged);
			java.util.List<com.google.gson.JsonArray> finalTierArrays = java.util.Arrays.asList(tier0Array, tier1Array, tier2Array, tier3Array, tier4Array, tier5Array, tier6Array, tier7Array, tier8Array, tier9Array);
			double tierStep = tierCount > 1 ? 90.0 / (tierCount - 1) : 90.0;
			for (int finalTierIndex = 0; finalTierIndex < tierCount; finalTierIndex++) {
				java.util.List<com.google.gson.JsonObject> foodsInTier = new java.util.ArrayList<>();
				for (com.google.gson.JsonObject food : allFoods) {
					if (food.get("tier").getAsInt() == finalTierIndex) {
						foodsInTier.add(food);
					}
				}
				foodsInTier.sort((com.google.gson.JsonObject foodA, com.google.gson.JsonObject foodB) -> {
					int scoreComparison = Double.compare(foodA.get("score").getAsDouble(), foodB.get("score").getAsDouble());
					if (scoreComparison != 0) {
						return scoreComparison;
					}
					return foodA.get("id").getAsString().compareTo(foodB.get("id").getAsString());
				});
				int finalTierSize = foodsInTier.size();
				double minimumTierScore = finalTierSize > 0 ? foodsInTier.get(0).get("score").getAsDouble() : 0.0;
				double maximumTierScore = finalTierSize > 0 ? foodsInTier.get(finalTierSize - 1).get("score").getAsDouble() : 0.0;
				double tierScoreRange = maximumTierScore - minimumTierScore;
				for (int foodIndexInTier = 0; foodIndexInTier < finalTierSize; foodIndexInTier++) {
					com.google.gson.JsonObject food = foodsInTier.get(foodIndexInTier);
					double currentFoodScore = food.get("score").getAsDouble();
					double scorePosition;
					if (finalTierSize <= 1 || tierScoreRange <= 0.0000001) {
						scorePosition = 0.5;
					} else {
						scorePosition = (currentFoodScore - minimumTierScore) / tierScoreRange;
					}
					scorePosition = Math.max(0.0, Math.min(1.0, scorePosition));
					double tierMinimumExp = 10.0 + finalTierIndex * tierStep;
					double scoreDifference = Math.max(0.0, currentFoodScore - minimumTierScore);
					double maximumScoreBonus = finalTierIndex == tierCount - 1 ? tierStep * 3.0 : Math.max(0.0, tierStep - 1.0);
					double scoreBonus = Math.min(scoreDifference, maximumScoreBonus);
					int baseExp = (int) Math.round(tierMinimumExp + scoreBonus);
					food.addProperty("score_position", scorePosition);
					food.addProperty("base_exp", baseExp);
					finalTierArrays.get(finalTierIndex).add(food);
				}
			}
			tiersObject.add("0", tier0Array);
			tiersObject.add("1", tier1Array);
			tiersObject.add("2", tier2Array);
			tiersObject.add("3", tier3Array);
			tiersObject.add("4", tier4Array);
			tiersObject.add("5", tier5Array);
			tiersObject.add("6", tier6Array);
			tiersObject.add("7", tier7Array);
			tiersObject.add("8", tier8Array);
			tiersObject.add("9", tier9Array);
			foodDatabase.add("tiers", tiersObject);
			foodDatabase.addProperty("tier_count", tierCount);
			foodDatabase.addProperty("food_count", totalFoods);
			double discoveryExpRequired = Math.ceil(10.0 * Math.sqrt(100.0 / Math.max(1, totalFoods)));
			discoveryExpRequired = Math.max(3.0, Math.min(10.0, discoveryExpRequired));
			net.mcreator.omnichef.network.OmnichefModVariables.MapVariables.get(world).RecipeDiscoveryExpRequired = discoveryExpRequired;
			net.mcreator.omnichef.network.OmnichefModVariables.MapVariables.get(world).markSyncDirty();
			foodDatabase.addProperty("recipe_discovery_exp_required", discoveryExpRequired);
			foodDatabase.addProperty("schema_version", 6);
			foodDatabase.addProperty("scoring_version", 11);
			foodDatabase.addProperty("eligibility_version", 1);
			foodDatabase.addProperty("acquisition_version", 1);
			foodDatabase.addProperty("classification_version", 2);
			foodDatabase.add("disabledFoods", disabledFoodsArray);
			foodDatabase.add("excludedFoods", excludedFoodsArray);
			com.google.gson.JsonObject sourceSummary = new com.google.gson.JsonObject();
			sourceSummary.addProperty("trees", acquisitionSources.treeCount);
			sourceSummary.addProperty("feast_portions", feastPortions.count);
			sourceSummary.addProperty("feast_warnings", feastPortions.warnings);
			sourceSummary.addProperty("bee_routes", acquisitionSources.beeRoutes);
			sourceSummary.addProperty("passes", acquisitionSources.passes);
			sourceSummary.addProperty("settled", acquisitionSources.settled);
			sourceSummary.addProperty("adapter_warnings", acquisitionSources.warnings.size());
			sourceSummary.addProperty("coverage",
					"Loaded item recipes, Productive Trees registry/pollination, Productive Bees breeding/conversion/production. Base sources are assumed; worldgen, loot, trades, machinery and environment gates are not fully assessed.");
			foodDatabase.add("acquisition_analysis", sourceSummary);
			net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef food database v5: analysis completed; enabled foods={}, disabled={}, elapsed={}ms.", totalFoods, disabledFoodsArray.size(), (System.nanoTime() - generationStarted) / 1000000L);
			FoodDatabaseObject = foodDatabase;
			FoodDatabase = new File((FMLPaths.GAMEDIR.get().toString() + "/config/omnichef"), File.separator + "FoodDatabase.json");
			try {
				FoodDatabase.getParentFile().mkdirs();
				FoodDatabase.createNewFile();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			{
				com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
				try {
					FileWriter fileWriter = new FileWriter(FoodDatabase);
					fileWriter.write(mainGSONBuilderVariable.toJson(FoodDatabaseObject));
					fileWriter.close();
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		}
	}
}