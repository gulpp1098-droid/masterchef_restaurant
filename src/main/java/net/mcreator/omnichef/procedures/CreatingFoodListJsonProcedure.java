package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.crafting.Recipe;

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
			net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef food database v7: indexing items and recipe outputs.");
			java.util.Set<String> excludedFoods = new java.util.HashSet<>();
			excludedFoods.add("minecraft:ominous_bottle");
			excludedFoods.add("minecraft:enchanted_golden_apple");
			excludedFoods.add("minecraft:suspicious_stew");
			com.google.gson.JsonObject loadedSpecialFoodBonuses = CreateSpecialFoodBonusMapProcedure.execute();
			final com.google.gson.JsonObject specialFoodBonuses = loadedSpecialFoodBonuses != null ? loadedSpecialFoodBonuses : new com.google.gson.JsonObject();
			com.google.gson.JsonObject loadedFoodCategoryBonuses = CreateFoodCategoryBonusMapProcedure.execute();
			final com.google.gson.JsonObject foodCategoryBonuses = loadedFoodCategoryBonuses != null ? loadedFoodCategoryBonuses : new com.google.gson.JsonObject();
			java.util.List<com.google.gson.JsonObject> allFoods = new java.util.ArrayList<>();
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
				final java.util.Set<String> pantryItems = new java.util.HashSet<>();
				final java.util.Set<String> preparedItems = new java.util.HashSet<>();
				final java.util.Set<String> harmlessEffects = new java.util.HashSet<>();
				final java.util.Map<String, Integer> sourceTiers = new java.util.HashMap<>();
				final java.util.Map<String, Integer> progressionMinimumTiers = new java.util.HashMap<>();
				final java.util.Map<String, Integer> recipeAccessTiers = new java.util.HashMap<>();
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
					config.addProperty("config_version", 5);
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
					config.add("pantry_items", strings("herbsandharvest:cream_cheese", "farmersdelight:tomato_sauce", "merrymaking:cookie_dough", "merrymaking:gingerbread_dough", "aquaculture:fish_fillet_raw", "pamhc2foodcore:flouritem",
							"pamhc2foodcore:doughitem", "pamhc2foodcore:butteritem", "pamhc2foodcore:mayonaiseitem", "pamhc2foodcore:vinegaritem", "pamhc2foodcore:creamitem", "pamhc2foodcore:cheeseitem"));
					config.add("prepared_items", strings("farmersdelight:melon_popsicle", "farmersdelight:cake_slice", "pneumaticcraft:salmon_tempura", "pamhc2foodextended:honeyglazedhamitem", "pamhc2foodextended:meringuebrownieitem",
							"actuallyadditions:coffee_cup", "iceandfire:cannoli", "extended_industrialization:canned_food"));
					config.add("compatibility_exclusions", strings("modern_industrialization:mixed_ingot_iridium", "potionsmaster:activated_charcoal", "potionsmaster:bezoar", "potionsmaster:gallbladder", "extended_industrialization:mulch"));
					config.add("harmless_effects", strings("minecraft:glowing"));
					com.google.gson.JsonObject origins = new com.google.gson.JsonObject();
					origins.addProperty("aether:black_moa_egg", 3);
					origins.addProperty("aether:blue_berry", 3);
					origins.addProperty("aether:blue_gummy_swet", 3);
					origins.addProperty("aether:blue_moa_egg", 3);
					origins.addProperty("aether:candy_cane", 3);
					origins.addProperty("aether:enchanted_berry", 3);
					origins.addProperty("aether:gingerbread_man", 3);
					origins.addProperty("aether:golden_gummy_swet", 3);
					origins.addProperty("aether:white_moa_egg", 3);
					origins.addProperty("deeperdarker:angler_fish", 4);
					origins.addProperty("eternal_starlight:aurora_deer_steak", 3);
					origins.addProperty("eternal_starlight:blossom_of_stars", 3);
					origins.addProperty("eternal_starlight:luminaris", 3);
					origins.addProperty("eternal_starlight:luminofish", 3);
					origins.addProperty("eternal_starlight:lunar_berries", 3);
					origins.addProperty("eternal_starlight:lunaris_cactus_gel", 3);
					origins.addProperty("eternal_starlight:ratlin_meat", 3);
					origins.addProperty("eternal_starlight:rookfish", 3);
					origins.addProperty("eternal_starlight:rotten_flesh_jerky", 3);
					origins.addProperty("eternal_starlight:seeker_tentacle", 3);
					origins.addProperty("eternal_starlight:shadow_snail_pie", 3);
					origins.addProperty("eternal_starlight:velvetumoss_ball", 3);
					origins.addProperty("minecraft:apple", 0);
					origins.addProperty("minecraft:chorus_flower", 4);
					origins.addProperty("minecraft:chorus_fruit", 4);
					origins.addProperty("minecraft:end_stone", 4);
					origins.addProperty("twilightforest:blackberry", 2);
					origins.addProperty("twilightforest:blightberry", 2);
					origins.addProperty("twilightforest:blueberry", 2);
					origins.addProperty("twilightforest:duskberry", 2);
					origins.addProperty("twilightforest:experiment_115", 2);
					origins.addProperty("twilightforest:gelatinous_maze_slime_drop", 2);
					origins.addProperty("twilightforest:hydra_chop", 2);
					origins.addProperty("twilightforest:maloberry", 2);
					origins.addProperty("twilightforest:maze_wafer", 2);
					origins.addProperty("twilightforest:meef_stroganoff", 2);
					origins.addProperty("twilightforest:raspberry", 2);
					origins.addProperty("twilightforest:raw_meef", 2);
					origins.addProperty("twilightforest:raw_venison", 2);
					origins.addProperty("twilightforest:shika_senbei", 2);
					origins.addProperty("twilightforest:skyberry", 2);
					origins.addProperty("twilightforest:stingberry", 2);
					origins.addProperty("undergarden:blisterberry", 3);
					origins.addProperty("undergarden:raw_dweller_meat", 3);
					origins.addProperty("undergarden:raw_gloomper_leg", 3);
					origins.addProperty("undergarden:raw_gwibling", 3);
					origins.addProperty("undergarden:roasted_underbeans", 3);
					origins.addProperty("undergarden:underbeans", 3);
					config.add("base_source_tiers", origins);
					com.google.gson.JsonObject minimums = new com.google.gson.JsonObject();
					minimums.addProperty("minecraft:chorus_fruit", 4);
					minimums.addProperty("iceandfire:fire_dragon_flesh", 6);
					minimums.addProperty("iceandfire:ice_dragon_flesh", 6);
					minimums.addProperty("iceandfire:lightning_dragon_flesh", 6);
					config.add("progression_minimum_tiers", minimums);
					com.google.gson.JsonObject access = new com.google.gson.JsonObject();
					access.addProperty("create_dragons_plus:ending", 4);
					config.add("recipe_access_tiers", access);
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
					for (String key : new String[]{"ingredient_tags", "prepared_tags", "culinary_tags", "culinary_items", "excluded_items", "zero_nutrition_allow_items", "pantry_items", "prepared_items", "compatibility_exclusions",
							"harmless_effects"}) {
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
					for (String key : new String[]{"base_source_tiers", "progression_minimum_tiers", "recipe_access_tiers"})
						for (var entry : config.getAsJsonObject(key).entrySet()) {
							net.minecraft.resources.ResourceLocation.parse(entry.getKey());
							double tier = entry.getValue().getAsDouble();
							if (!Double.isFinite(tier) || tier < 0 || tier > 9 || tier != Math.floor(tier))
								throw new IllegalArgumentException("Invalid base source tier");
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
					excludedItems.addAll(stringList(config, "compatibility_exclusions"));
					pantryItems.addAll(stringList(config, "pantry_items"));
					preparedItems.addAll(stringList(config, "prepared_items"));
					harmlessEffects.addAll(stringList(config, "harmless_effects"));
					for (var entry : config.getAsJsonObject("base_source_tiers").entrySet())
						sourceTiers.put(entry.getKey(), entry.getValue().getAsInt());
					for (var entry : config.getAsJsonObject("progression_minimum_tiers").entrySet())
						progressionMinimumTiers.put(entry.getKey(), entry.getValue().getAsInt());
					for (var entry : config.getAsJsonObject("recipe_access_tiers").entrySet())
						recipeAccessTiers.put(entry.getKey(), entry.getValue().getAsInt());
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
						if ("aquaculture:fish_fillet_raw".equals(key))
							tier = Math.max(tier, 1);
						return tier;
					});
				}

				boolean hasClass(String id, String name) {
					var item = itemsById.get(id);
					for (Class<?> type = item == null ? null : item.getClass(); type != null; type = type.getSuperclass())
						if (name.equals(type.getName()))
							return true;
					return false;
				}

				boolean equipment(String id) {
					return java.util.Set.of("pamhc2foodcore:potitem", "pamhc2foodcore:skilletitem", "pamhc2foodcore:cuttingboarditem", "pamhc2foodcore:grinderitem", "pamhc2foodcore:bakewareitem", "pamhc2foodcore:saucepanitem",
							"pamhc2foodcore:mixingbowlitem", "pamhc2foodcore:juiceritem", "pamhc2foodcore:rolleritem").contains(id);
				}

				boolean pantry(String id) {
					String path = id.substring(id.indexOf(':') + 1);
					return pantryItems.contains(id) || (id.startsWith("pamhc2crops:") && path.endsWith("seeditem"))
							|| matchesTags(id, java.util.List.of(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("c:seeds"))));
				}

				boolean natural(String id) {
					if (sourceTiers.containsKey(id))
						return true;
					if ("FOOD".equals(hint(id)) || pantryItems.contains(id))
						return false;
					return "INGREDIENT".equals(hint(id));
				}

				boolean harmless(String effectId) {
					return harmlessEffects.contains(effectId);
				}

				int sourceTier(String id) {
					return sourceTiers.getOrDefault(id, -1);
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
						if (pantry(key)) {
							hintReasons.put(key, "pantry_or_seed");
							return "INGREDIENT";
						}
						if (preparedItems.contains(key) || hasClass(key, "com.buuz135.sushigocrafting.item.FoodItem")) {
							hintReasons.put(key, "prepared_compatibility");
							return "FOOD";
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
			class CulinaryAdapters {
				final class Dish {
					final String method, whole;
					final int count;
					final java.util.List<java.util.List<String>> slots;

					Dish(String method, String whole, int count, java.util.List<java.util.List<String>> slots) {
						this.method = method;
						this.whole = whole;
						this.count = count;
						this.slots = slots;
					}
				}

				final java.util.Map<String, java.util.List<Dish>> dishes = new java.util.HashMap<>();
				int sushi = 0, portions = 0, warnings = 0;

				void add(String output, Dish dish) {
					dishes.computeIfAbsent(output, key -> new java.util.ArrayList<>()).add(dish);
				}

				CulinaryAdapters() {
					for (var entry : itemsById.entrySet()) {
						String id = entry.getKey();
						if (!classificationRules.hasClass(id, "com.buuz135.sushigocrafting.item.FoodItem"))
							continue;
						try {
							Object value = entry.getValue().getClass().getMethod("getIngredientList").invoke(entry.getValue());
							java.util.List<java.util.List<String>> slots = new java.util.ArrayList<>();
							if (value instanceof Iterable<?> list)
								for (Object ingredient : list) {
									if (Boolean.TRUE.equals(ingredient.getClass().getMethod("isEmpty").invoke(ingredient)))
										continue;
									Object item = ingredient.getClass().getMethod("getItem").invoke(ingredient);
									if (item instanceof net.minecraft.world.item.Item input)
										slots.add(java.util.List.of(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(input).toString()));
								}
							if (!slots.isEmpty()) {
								add(id, new Dish("omnichef:sushi_rolling", "", 1, slots));
								sushi++;
							}
						} catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
							warnings++;
							net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef sushi adapter {}: {}", id, exception.toString());
						}
					}
					// Preserve the real one-input portion route. Whole dishes remain nodes even when not edible.
					for (var entry : recipesByOutput.entrySet()) {
						String output = entry.getKey();
						if (!classificationRules.isEdible(output))
							continue;
						for (var holder : entry.getValue()) {
							if (!classificationRules.matchesType(classificationRules.typeId(holder), classificationRules.cuttingTypes))
								continue;
							var ingredients = holder.value().getIngredients();
							java.util.List<java.util.List<String>> slots = new java.util.ArrayList<>();
							for (var ingredient : ingredients) {
								if (ingredient.isEmpty())
									continue;
								java.util.List<String> ids = new java.util.ArrayList<>();
								for (var stack : ingredient.getItems())
									if (!stack.isEmpty())
										ids.add(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
								slots.add(ids);
							}
							if (slots.size() != 1 || slots.get(0).isEmpty())
								continue;
							boolean prepared = false;
							for (String whole : slots.get(0)) {
								prepared |= "FOOD".equals(classificationRules.hint(whole));
								if (!classificationRules.isEdible(whole)) {
									for (var parent : recipesByOutput.getOrDefault(whole, java.util.Collections.emptyList())) {
										if (!classificationRules.matchesType(classificationRules.typeId(parent), classificationRules.allowedTypes))
											continue;
										int culinary = 0;
										for (var input : parent.value().getIngredients()) {
											for (var stack : input.getItems()) {
												String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
												if (classificationRules.isCulinary(id)) {
													culinary++;
													break;
												}
											}
										}
										if (culinary >= 2)
											prepared = true;
									}
								}
							}
							if (prepared) {
								classificationRules.preparedPortions.add(output);
								portions++;
							}
						}
					}
					classificationRules.hints.clear();
					classificationRules.hintReasons.clear();
					classificationRules.culinary.clear();
				}
			}
			CulinaryAdapters culinaryAdapters = new CulinaryAdapters();
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
					net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef food database v7: indexing acquisition sources.");
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
							for (var ingredient : ingredients) {
								if (ingredient.isEmpty())
									continue;
								var alternatives = ingredientNodes(ingredient);
								if (!alternatives.isEmpty() && alternatives.stream().allMatch(node -> classificationRules.equipment(nodeItems.getOrDefault(node, ""))))
									continue;
								inputs.add(alternatives);
							}
							String method = classificationRules.typeId(holder);
							double access = classificationRules.matchesType(method, classificationRules.allowedTypes) ? 0.0 : 1.0;
							if (method.startsWith("pneumaticcraft:"))
								access = 4.0;
							access = Math.max(access, classificationRules.recipeAccessTiers.getOrDefault(method, 0) * classificationRules.stepsPerTier);
							add(stackNode(result), method, inputs, access, 1.0, result.getCount());
						} catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
							warnings.putIfAbsent(recipe.getClass().getName(), exception.toString());
						}
					}
					for (var entry : culinaryAdapters.dishes.entrySet())
						for (var dish : entry.getValue()) {
							java.util.List<java.util.List<String>> requirements = new java.util.ArrayList<>();
							for (var slot : dish.slots) {
								java.util.List<String> alternatives = new java.util.ArrayList<>();
								for (String id : slot)
									alternatives.add(itemNode(id));
								requirements.add(alternatives);
							}
							add(itemNode(entry.getKey()), dish.method, requirements, 0.0, 1.0, dish.count);
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
					for (var list : routes.values())
						list.sort(java.util.Comparator.comparing((SourceRoute route) -> route.system).thenComparing(route -> route.requirements.toString()).thenComparingDouble(route -> route.chance).thenComparingDouble(route -> route.yield));
					java.util.List<String> ordered = new java.util.ArrayList<>(nodes);
					java.util.Collections.sort(ordered);
					for (String node : ordered) {
						String id = nodeItems.get(node);
						boolean assumedBase = !routes.containsKey(node) && !controlled.contains(node);
						assumedBase |= treeSaplings.contains(node) && !breedingTargets.contains(node);
						assumedBase |= node.startsWith("item|") && !controlled.contains(node) && id != null && classificationRules.natural(id);
						// Known natural alternatives remain available even when a mod also offers a machine recipe.
						if (id != null && classificationRules.sourceTier(id) >= 0 && node.startsWith("item|"))
							assumedBase = true;
						if (assumedBase)
							costs.put(node, id == null ? 0.0 : Math.max(Math.max(0, classificationRules.sourceTier(id)), classificationRules.progressionMinimumTiers.getOrDefault(id, 0)) * classificationRules.stepsPerTier);
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
								String itemId = nodeItems.get(output);
								if (itemId != null)
									candidate = Math.max(candidate, classificationRules.progressionMinimumTiers.getOrDefault(itemId, 0) * classificationRules.stepsPerTier);
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
					net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef food database v7: acquisition analysis: {} nodes, {} routes, {} trees, {} bee routes, {} passes, settled={}; base sources are estimates.", nodes.size(), totalRoutes,
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
					tier = Math.max(tier, classificationRules.progressionMinimumTiers.getOrDefault(id, 0));
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
					result.addProperty("progression_minimum", classificationRules.progressionMinimumTiers.getOrDefault(id, 0));
					result.addProperty("category_minimum", classificationRules.categoryMinimum(id));
					result.addProperty("base_source_profile", classificationRules.sourceTier(id));
					result.addProperty("confidence", route == null && classificationRules.sourceTier(id) < 0 ? "assumed" : "estimated");
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
				final java.util.List<String> scoreOrder = new java.util.ArrayList<>();
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
						net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef food database v7: phase={}, indexed={}, scored={}/{}, foods={}, current={}, evaluated={}, elapsed={}s", phase, indexedOutputs, scoredItems, graphNodes, foodsProcessed,
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
						recipeObject.addProperty("acquisition", "omnichef:acquisition".equals(method));
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

				java.util.List<IndexedRecipe> expandCutPortions(String output, java.util.List<IndexedRecipe> indexed) {
					if (!classificationRules.preparedPortions.contains(output))
						return indexed;
					java.util.List<IndexedRecipe> result = new java.util.ArrayList<>();
					for (var cut : indexed) {
						if (cut.slots.size() != 1 || !classificationRules.matchesType(cut.method, classificationRules.cuttingTypes)) {
							result.add(cut);
							continue;
						}
						boolean retained = false;
						for (String whole : cut.slots.get(0)) {
							if (classificationRules.isEdible(whole)) {
								result.add(new IndexedRecipe(cut.holder, cut.method, cut.outputCount, java.util.List.of(java.util.List.of(whole))));
								retained = true;
								continue;
							}
							for (var holder : recipesByOutput.getOrDefault(whole, java.util.Collections.emptyList())) {
								String method = classificationRules.typeId(holder);
								if (!classificationRules.matchesType(method, classificationRules.allowedTypes))
									continue;
								java.util.List<java.util.List<String>> slots = new java.util.ArrayList<>();
								boolean valid = true;
								for (var input : holder.value().getIngredients()) {
									if (input.isEmpty())
										continue;
									var ids = idsOf(input);
									if (!ids.isEmpty() && ids.stream().allMatch(classificationRules::equipment))
										continue;
									if (ids.isEmpty())
										valid = false;
									slots.add(ids);
								}
								long count = (long) holder.value().getResultItem(level.registryAccess()).getCount() * cut.outputCount;
								if (!valid || slots.isEmpty() || count < 1 || count > 4096)
									continue;
								var expanded = new IndexedRecipe(holder, method, (int) count, slots);
								expanded.portionSource = whole;
								expanded.servings = cut.outputCount;
								result.add(expanded);
								retained = true;
							}
						}
						// Unsupported non-edible whole dishes are reported through the incomplete-recipe fallback.
					}
					return result;
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
							if (ingredient.isEmpty())
								continue;
							java.util.List<String> ids = idsOf(ingredient);
							if (!ids.isEmpty() && ids.stream().allMatch(classificationRules::equipment))
								continue;
							inputs.addAll(ids);
							slots.add(ids);
							if (ids.isEmpty())
								valid = false;
							if (ids.size() == 1)
								mandatory.add(ids.get(0));
						}
						if (!valid || slots.isEmpty())
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
								if (ingredient.isEmpty())
									continue;
								var ids = idsOf(ingredient);
								if (!ids.isEmpty() && ids.stream().allMatch(classificationRules::equipment))
									continue;
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
					for (var dish : culinaryAdapters.dishes.getOrDefault(output, java.util.Collections.emptyList())) {
						var normalized = new IndexedRecipe(null, dish.method, dish.count, dish.slots);
						indexed.add(normalized);
						for (var slot : dish.slots)
							inputs.addAll(slot);
					}
					indexed = expandCutPortions(output, indexed);
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
					if (!"omnichef:sushi_rolling".equals(recipe.method) && !classificationRules.matchesType(recipe.method, classificationRules.allowedTypes))
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
						if (classificationRules.preparedPortions.contains(output))
							return true;
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
							scoreOrder.add(output);
							validRecipesCache.put(output, validRecipes);
							scoredItems++;
							logProgress(false);
						}
					}
					phase = "exporting foods";
					currentFood = "";
					logProgress(true);
					net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef food database v7: dependency analysis completed; cyclic groups={}, acquisition anchors={}, evaluated recipes={}.", cyclicComponents, cycleAnchors, evaluatedRecipes);
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

				void alignRecipesWithTiers(java.util.Map<String, Integer> tiers) {
					for (String output : scoreOrder) {
						java.util.List<RecipeAnalysis> updated = new java.util.ArrayList<>();
						for (var previous : getValidRecipes(output)) {
							double total = 0;
							java.util.List<String> selected = new java.util.ArrayList<>();
							for (var slot : previous.ingredientSlots) {
								String best = null;
								for (String id : slot) {
									if (best == null || tiers.getOrDefault(id, 0) < tiers.getOrDefault(best, 0)
											|| (tiers.getOrDefault(id, 0).equals(tiers.getOrDefault(best, 0)) && (resolve(id) < resolve(best) || (resolve(id) == resolve(best) && id.compareTo(best) < 0))))
										best = id;
								}
								if (best != null) {
									selected.add(best);
									total += resolve(best);
								}
							}
							if ("omnichef:acquisition".equals(previous.method))
								total = 1.0;
							total = roundScore(total);
							var analysis = new RecipeAnalysis(previous.recipeHolder, previous.method, previous.outputCount, previous.preparePoints, total, roundScore((total + previous.preparePoints) / previous.outputCount), previous.ingredientSlots,
									selected);
							analysis.portionSource = previous.portionSource;
							analysis.servings = previous.servings;
							updated.add(analysis);
						}
						updated.sort((first, second) -> {
							int firstTier = 0, secondTier = 0;
							for (String id : first.selectedIngredients)
								firstTier = Math.max(firstTier, tiers.getOrDefault(id, 0));
							for (String id : second.selectedIngredients)
								secondTier = Math.max(secondTier, tiers.getOrDefault(id, 0));
							int compare = Integer.compare(firstTier, secondTier);
							return compare == 0 ? compareRecipes(first, second) : compare;
						});
						validRecipesCache.put(output, updated);
						if (!updated.isEmpty()) {
							var best = updated.get(0);
							chosenAnalyses.put(output, best);
							chosenIngredientsCache.put(output, best.selectedIngredients);
							recipeScoreCache.put(output, best.recipeScore);
							scoreCache.put(output, roundScore(Math.max(0.1, best.recipeScore + qualityScores.get(output) + acquisitionBonuses.get(output))));
						}
					}
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
					return roundScore(Math.max(-3.0, Math.min(3.0, getSpecialFoodBonus(itemName))));
				}

				double getModdedFoodBonus(String itemName) {
					return 0.0;
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
							var effectId = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getKey(mobEffectInstance.getEffect().value());
							if (effectId == null || !classificationRules.harmless(effectId.toString()))
								negativeEffects++;
						}
					}
					return roundScore(food.nutrition() + food.saturation() * 1.5 + Math.min(2.0, positiveEffects) - negativeEffects * 8.0);
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
					} else if (!classificationRules.harmless(effectId.toString())) {
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
			int totalFoods = allFoods.size();
			class FoodProgression {
				final java.util.Map<String, Double> effort = new java.util.HashMap<>();
				final java.util.Map<String, java.util.List<String>> selectedInputs = new java.util.HashMap<>();
				final java.util.Map<String, Integer> tiers = new java.util.HashMap<>();
				final java.util.Map<String, java.util.List<FoodScoreResolver.IndexedRecipe>> recipes = new java.util.HashMap<>();
				final java.util.Set<String> uncertain = new java.util.HashSet<>();
				int passes = 0;
				boolean settled = false;

				int tierForWork(double value) {
					return value <= 0 ? 0 : Math.min(9, 1 + (int) Math.floor((value + 0.000001) / 2.0));
				}

				double work(FoodScoreResolver.IndexedRecipe recipe) {
					if (classificationRules.matchesType(recipe.method, classificationRules.cuttingTypes))
						return 0.0;
					java.util.Set<String> distinct = new java.util.HashSet<>();
					for (var slot : recipe.slots) {
						java.util.List<String> group = new java.util.ArrayList<>();
						for (String id : slot)
							if (!classificationRules.equipment(id) && !"minecraft:bowl".equals(id) && !"minecraft:glass_bottle".equals(id))
								group.add(id);
						if (!group.isEmpty())
							distinct.add(group.toString());
					}
					return 1.0 + Math.min(2.0, Math.max(0, distinct.size() - 1) * 0.4);
				}

				void build() {
					java.util.ArrayDeque<String> pending = new java.util.ArrayDeque<>();
					java.util.Set<String> nodes = new java.util.TreeSet<>();
					for (var food : allFoods) {
						String id = food.get("id").getAsString();
						nodes.add(id);
						pending.add(id);
					}
					while (!pending.isEmpty()) {
						String id = pending.removeFirst();
						java.util.List<FoodScoreResolver.IndexedRecipe> list = new java.util.ArrayList<>();
						if (!classificationRules.natural(id))
							for (var recipe : scoreResolver.rawRecipesFor(id)) {
								if (!classificationRules.matchesType(recipe.method, classificationRules.allowedTypes) && !"omnichef:sushi_rolling".equals(recipe.method))
									continue;
								list.add(recipe);
								for (var slot : recipe.slots)
									for (String input : slot)
										if (nodes.add(input))
											pending.addLast(input);
							}
						recipes.put(id, list);
					}
					for (String id : nodes) {
						int minimum = acquisitionSources.minimumTier(id, 10);
						if (recipes.get(id).isEmpty()) {
							boolean unknownDish = "FOOD".equals(classificationRules.hint(id)) && !classificationRules.natural(id);
							effort.put(id, 0.0);
							tiers.put(id, Math.max(minimum, unknownDish ? 3 : 0));
							if (unknownDish)
								uncertain.add(id);
						} else {
							effort.put(id, Double.POSITIVE_INFINITY);
							tiers.put(id, 99);
						}
					}
					for (int pass = 0; pass < 64; pass++) {
						boolean changed = false;
						var nextEffort = new java.util.HashMap<>(effort);
						var nextTiers = new java.util.HashMap<>(tiers);
						for (String id : nodes) {
							int bestTier = tiers.get(id);
							double bestWork = effort.get(id);
							java.util.List<String> bestInputs = selectedInputs.getOrDefault(id, java.util.Collections.emptyList());
							for (var recipe : recipes.get(id)) {
								int dependencyTier = 0;
								double dependencyWork = 0;
								boolean valid = true;
								java.util.List<String> selected = new java.util.ArrayList<>();
								for (var slot : recipe.slots) {
									int slotTier = 99;
									double slotWork = Double.POSITIVE_INFINITY;
									String slotInput = null;
									for (String input : slot) {
										int t = tiers.getOrDefault(input, 99);
										double w = effort.getOrDefault(input, Double.POSITIVE_INFINITY);
										if (t < slotTier || (t == slotTier && (w < slotWork || (w == slotWork && (slotInput == null || input.compareTo(slotInput) < 0))))) {
											slotTier = t;
											slotWork = w;
											slotInput = input;
										}
									}
									if (slotTier == 99) {
										valid = false;
										break;
									}
									selected.add(slotInput);
									dependencyTier = Math.max(dependencyTier, slotTier);
									dependencyWork = Math.max(dependencyWork, slotWork);
								}
								if (!valid)
									continue;
								double candidateWork = dependencyWork + work(recipe);
								int candidate = Math.max(acquisitionSources.minimumTier(id, 10), Math.max(dependencyTier, tierForWork(candidateWork)));
								if (candidate < bestTier || (candidate == bestTier && candidateWork + 0.000001 < bestWork)) {
									bestTier = candidate;
									bestWork = candidateWork;
									bestInputs = selected;
									changed = true;
								}
							}
							nextTiers.put(id, bestTier);
							nextEffort.put(id, bestWork);
							selectedInputs.put(id, bestInputs);
						}
						tiers.clear();
						tiers.putAll(nextTiers);
						effort.clear();
						effort.putAll(nextEffort);
						passes = pass + 1;
						if (!changed) {
							settled = true;
							break;
						}
					}
					for (String id : nodes)
						if (tiers.get(id) == 99) {
							tiers.put(id, Math.max(3, acquisitionSources.minimumTier(id, 10)));
							uncertain.add(id);
						}
					// Retained culinary alternatives are acyclic; keep their actual unlock prerequisites within the assigned tier.
					for (int pass = 0; pass < 64; pass++) {
						boolean changed = false;
						for (String id : nodes) {
							int minimum = 99;
							for (var recipe : scoreResolver.getValidRecipes(id)) {
								if ("omnichef:acquisition".equals(recipe.method))
									continue;
								int requirement = 0;
								for (var slot : recipe.ingredientSlots) {
									int easiest = 99;
									for (String input : slot)
										easiest = Math.min(easiest, tiers.getOrDefault(input, 0));
									requirement = Math.max(requirement, easiest);
								}
								minimum = Math.min(minimum, requirement);
							}
							if (minimum < 99 && minimum > tiers.get(id)) {
								tiers.put(id, minimum);
								changed = true;
							}
						}
						if (!changed)
							break;
					}
				}

				int tier(String id, int tierCount) {
					return Math.min(tierCount - 1, tiers.getOrDefault(id, acquisitionSources.minimumTier(id, tierCount)));
				}

				com.google.gson.JsonObject describe(String id) {
					com.google.gson.JsonObject result = new com.google.gson.JsonObject();
					result.addProperty("preparation_effort", Double.isFinite(effort.getOrDefault(id, 0.0)) ? effort.getOrDefault(id, 0.0) : 0);
					result.addProperty("availability_minimum", acquisitionSources.minimumTier(id, 10));
					result.addProperty("status", uncertain.contains(id) ? "incomplete_recipe" : (settled ? "estimated" : "estimated_partial"));
					return result;
				}
			}
			FoodProgression foodProgression = new FoodProgression();
			foodProgression.build();
			scoreResolver.alignRecipesWithTiers(foodProgression.tiers);
			for (var food : allFoods) {
				String id = food.get("id").getAsString();
				var chosen = scoreResolver.chosenAnalyses.get(id);
				com.google.gson.JsonArray ingredients = new com.google.gson.JsonArray();
				for (String input : scoreResolver.chosenIngredientsCache.getOrDefault(id, java.util.Collections.emptyList()))
					ingredients.add(input);
				com.google.gson.JsonArray recipes = new com.google.gson.JsonArray();
				int selected = -1, index = 0;
				for (var recipe : scoreResolver.getValidRecipes(id)) {
					recipes.add(recipe.toJson());
					if (recipe == chosen)
						selected = index;
					index++;
				}
				food.add("ingredients", ingredients);
				food.add("recipes", recipes);
				food.addProperty("selectedRecipe", selected);
				food.addProperty("validRecipeCount", recipes.size());
				food.addProperty("method", chosen == null ? "none" : chosen.method);
				food.addProperty("recipeOutputCount", chosen == null ? 0 : chosen.outputCount);
				food.addProperty("recipeScore", scoreResolver.recipeScoreCache.getOrDefault(id, 0.0));
				food.addProperty("score", scoreResolver.resolve(id));
			}
			class SplitTierPlanner {
				static final int UNREACHABLE = 1000000;

				final class Node {
					final String id;
					final String kind;
					final int minimum;
					final double rank;
					final boolean canServe;
					final java.util.List<java.util.List<java.util.List<String>>> routes = new java.util.ArrayList<>();
					int tier = -1;
					int priority = 0;
					boolean starterEligible;
					String aliasOf = "";
					int preparationTier = UNREACHABLE;

					Node(String id, String kind, int minimum, double rank, boolean canServe) {
						this.id = id;
						this.kind = kind;
						this.minimum = Math.max(0, Math.min(("INGREDIENT".equals(kind) ? ingredientTierCount : tierCount) - 1, minimum));
						this.rank = Double.isFinite(rank) ? rank : 0.0;
						this.canServe = canServe;
						this.starterEligible = canServe;
					}
				}

				final int tierCount;
				final int ingredientTierCount;
				final java.util.Map<String, Node> nodes = new java.util.TreeMap<>();
				final java.util.Map<String, int[]> groupSizes = new java.util.TreeMap<>();

				SplitTierPlanner(int tierCount) {
					this(tierCount, tierCount);
				}

				SplitTierPlanner(int ingredientTierCount, int foodTierCount) {
					if (ingredientTierCount < 1 || foodTierCount < ingredientTierCount || foodTierCount > 10)
						throw new IllegalArgumentException("Invalid ingredient/food tier counts");
					this.ingredientTierCount = ingredientTierCount;
					this.tierCount = foodTierCount;
				}

				Node add(String id, String kind, int minimum, double rank, boolean canServe) {
					if (nodes.containsKey(id))
						throw new IllegalArgumentException("Duplicate catalog ID: " + id);
					Node node = new Node(id, kind, minimum, rank, canServe);
					nodes.put(id, node);
					return node;
				}

				boolean isCatalog(Node node) {
					return "INGREDIENT".equals(node.kind) || "FOOD".equals(node.kind);
				}

				void rankGroup(String kind) {
					int count = "INGREDIENT".equals(kind) ? ingredientTierCount : tierCount;
					java.util.Comparator<Node> byRank = java.util.Comparator.comparingInt((Node n) -> n.priority).thenComparingDouble(n -> n.rank).thenComparing(n -> n.id);
					java.util.List<Node> waiting = new java.util.ArrayList<>();
					for (Node node : nodes.values())
						if (kind.equals(node.kind))
							waiting.add(node);
					waiting.sort(java.util.Comparator.comparingInt(this::rankingMinimum).thenComparing(byRank));
					java.util.PriorityQueue<Node> ready = new java.util.PriorityQueue<>(byRank);
					int[] sizes = new int[count];
					int next = 0, assigned = 0;
					for (int tier = 0; tier < count; tier++) {
						while (next < waiting.size() && rankingMinimum(waiting.get(next)) <= tier)
							ready.add(waiting.get(next++));
						int remaining = waiting.size() - assigned;
						int target = (remaining + count - tier - 1) / (count - tier);
						if (tier == 0 && "INGREDIENT".equals(kind)) {
							java.util.List<Node> starters = new java.util.ArrayList<>();
							for (Node node : ready)
								if (node.canServe && node.starterEligible)
									starters.add(node);
							starters.sort(byRank);
							for (int index = 0; index < Math.min(3, starters.size()); index++) {
								Node node = starters.get(index);
								ready.remove(node);
								node.tier = 0;
								sizes[0]++;
								assigned++;
							}
						}
						while (!ready.isEmpty() && (sizes[tier] < target || tier == count - 1)) {
							Node node = ready.remove();
							node.tier = tier;
							sizes[tier]++;
							assigned++;
						}
					}
					if (assigned != waiting.size())
						throw new IllegalStateException("Unassigned " + kind + " entries");
					groupSizes.put(kind, sizes);
				}

				int rankingMinimum(Node node) {
					if (!"FOOD".equals(node.kind))
						return node.minimum;
					return node.preparationTier == UNREACHABLE ? tierCount - 1 : Math.max(node.minimum, node.preparationTier);
				}

				int routeTier(java.util.List<java.util.List<String>> route) {
					int result = 0;
					for (java.util.List<String> slot : route) {
						int easiest = UNREACHABLE;
						for (String id : slot) {
							Node input = nodes.get(id);
							if (input != null)
								easiest = Math.min(easiest, input.preparationTier);
						}
						if (easiest == UNREACHABLE)
							return UNREACHABLE;
						result = Math.max(result, easiest);
					}
					return result;
				}

				void build() {
					rankGroup("INGREDIENT");
					for (Node node : nodes.values()) {
						if ("INGREDIENT".equals(node.kind))
							node.preparationTier = node.tier;
						else if ("AUXILIARY".equals(node.kind))
							node.preparationTier = 0;
						else if ("EXTERNAL".equals(node.kind))
							node.preparationTier = node.minimum;
					}
					// Values can only decrease. An unanchored recipe cycle remains unavailable.
					for (int pass = 0; pass <= nodes.size(); pass++) {
						boolean changed = false;
						for (Node node : nodes.values()) {
							if ("ALIAS".equals(node.kind)) {
								Node target = nodes.get(node.aliasOf);
								if (target != null && target.preparationTier < node.preparationTier) {
									node.preparationTier = target.preparationTier;
									changed = true;
								}
								continue;
							}
							if (!("FOOD".equals(node.kind) || "INTERMEDIATE".equals(node.kind)))
								continue;
							int best = node.preparationTier;
							for (var route : node.routes) {
								int requirement = routeTier(route);
								if (requirement < UNREACHABLE)
									best = Math.min(best, Math.max(node.minimum, requirement));
							}
							if (best < node.preparationTier) {
								node.preparationTier = best;
								changed = true;
							}
						}
						if (!changed) {
							rankGroup("FOOD");
							return;
						}
					}
					throw new IllegalStateException("Split recipe requirements did not converge");
				}

				int earliestUnlock(Node node) {
					return node.preparationTier == UNREACHABLE ? -1 : Math.max(node.tier, node.preparationTier);
				}

				boolean canPrepare(String id, java.util.Set<String> unlockedIngredients) {
					return preparableNodes(unlockedIngredients).contains(id);
				}

				java.util.Set<String> preparableNodes(java.util.Set<String> unlockedIngredients) {
					java.util.Set<String> usable = new java.util.HashSet<>();
					for (Node node : nodes.values()) {
						if ("AUXILIARY".equals(node.kind) || "EXTERNAL".equals(node.kind) || ("INGREDIENT".equals(node.kind) && unlockedIngredients.contains(node.id)))
							usable.add(node.id);
					}
					boolean changed;
					do {
						changed = false;
						for (Node node : nodes.values()) {
							if ("ALIAS".equals(node.kind)) {
								if (usable.contains(node.aliasOf) && usable.add(node.id))
									changed = true;
								continue;
							}
							if (usable.contains(node.id) || !("FOOD".equals(node.kind) || "INTERMEDIATE".equals(node.kind)))
								continue;
							for (var route : node.routes) {
								boolean possible = true;
								for (var slot : route) {
									boolean slotPossible = false;
									for (String input : slot)
										if (usable.contains(input)) {
											slotPossible = true;
											break;
										}
									if (!slotPossible) {
										possible = false;
										break;
									}
								}
								if (possible) {
									usable.add(node.id);
									changed = true;
									break;
								}
							}
						}
					} while (changed);
					return usable;
				}
			}
			class SplitSourceCatalog {
				final class Recipe {
					final String output, method;
					final int count;
					final java.util.List<java.util.List<String>> slots;
					final com.google.gson.JsonObject evidence;

					Recipe(String output, String method, int count, java.util.List<java.util.List<String>> slots, com.google.gson.JsonObject evidence) {
						this.output = output;
						this.method = method;
						this.count = count;
						this.slots = slots;
						this.evidence = evidence.deepCopy();
					}
				}

				final java.util.Map<String, java.util.List<Recipe>> recipes = new java.util.TreeMap<>();
				final java.util.Set<String> wholeSources = new java.util.TreeSet<>(), containers = new java.util.TreeSet<>();
				final java.util.Set<String> inputs = new java.util.TreeSet<>();
				final java.util.Map<String, Integer> skipped = new java.util.TreeMap<>();
				final com.google.gson.JsonArray rejectedResources = new com.google.gson.JsonArray();
				final java.util.function.Predicate<String> registered;
				final java.util.function.Function<String, java.util.List<String>> tagItems;

				SplitSourceCatalog(java.util.function.Predicate<String> registered, java.util.function.Function<String, java.util.List<String>> tagItems) {
					this.registered = registered;
					this.tagItems = tagItems;
				}

				void skip(String reason) {
					skipped.merge(reason, 1, Integer::sum);
				}

				String shape(com.google.gson.JsonElement value) {
					if (value == null || value.isJsonNull())
						return "missing_or_null";
					if (value.isJsonObject())
						return "object";
					if (value.isJsonArray())
						return "array";
					if (value.getAsJsonPrimitive().isString())
						return "string";
					if (value.getAsJsonPrimitive().isNumber())
						return "number";
					return "boolean";
				}

				boolean reject(String reason, String resource, String field, String expected, com.google.gson.JsonElement actual) {
					skip(reason);
					if (rejectedResources.size() < 100) {
						var entry = new com.google.gson.JsonObject();
						entry.addProperty("resource", resource);
						entry.addProperty("reason", reason);
						entry.addProperty("field", field);
						entry.addProperty("expected", expected);
						entry.addProperty("actual_type", shape(actual));
						rejectedResources.add(entry);
					}
					return false;
				}

				int quantity(com.google.gson.JsonObject object, String key, int fallback, int maximum) {
					if (!object.has(key))
						return fallback;
					var value = object.get(key);
					if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber())
						throw new IllegalArgumentException("Expected integer count");
					int count = value.getAsBigDecimal().intValueExact();
					if (count < 1 || count > maximum)
						throw new IllegalArgumentException("Count outside supported range");
					return count;
				}

				boolean add(String output, String method, int count, java.util.List<java.util.List<String>> slots, com.google.gson.JsonObject evidence) {
					if (!registered.test(output) || count < 1 || count > 4096 || slots.isEmpty() || slots.size() > 256)
						return false;
					for (var slot : slots)
						if (slot.isEmpty() || slot.stream().anyMatch(id -> !registered.test(id)))
							return false;
					var list = recipes.computeIfAbsent(output, key -> new java.util.ArrayList<>());
					for (var existing : list)
						if (existing.method.equals(method) && existing.count == count && existing.slots.equals(slots) && existing.evidence.equals(evidence))
							return true;
					list.add(new Recipe(output, method, count, slots, evidence));
					for (var slot : slots)
						inputs.addAll(slot);
					return true;
				}

				boolean minecolonies(com.google.gson.JsonObject json, String resourceId) {
					String field = "type";
					com.google.gson.JsonElement actual = json.get(field);
					try {
						if (!json.has("type") || !"recipe".equals(json.get("type").getAsString()) || json.has("conditions") || json.has("neoforge:conditions") || json.has("must-exist") && json.get("must-exist").getAsBoolean())
							return reject("minecolonies_conditional_or_template", resourceId, "type/conditions/must-exist", "supported unconditional recipe definition", json.get("type"));
						field = "crafter";
						actual = json.get(field);
						String crafter = json.get("crafter").getAsString();
						if (!java.util.Set.of("chef_crafting", "baker_crafting", "baker_smelting", "chef_smelting").contains(crafter))
							return false;
						field = "result";
						actual = json.get(field);
						String output;
						int count;
						if (actual != null && actual.isJsonObject()) {
							var result = actual.getAsJsonObject();
							if (result.has("components") || result.has("nbt"))
								return reject("minecolonies_special_result", resourceId, "result.components/nbt", "result representable by item ID without components", actual);
							field = "result.id";
							actual = result.get("id");
							output = actual.getAsString();
							field = "result.count";
							actual = result.get("count");
							count = quantity(result, "count", 1, 4096);
							if (json.has("count")) {
								field = "count";
								actual = json.get("count");
								int legacyCount = quantity(json, "count", 1, 4096);
								if (result.has("count") && count != legacyCount)
									return reject("minecolonies_conflicting_counts", resourceId, "count/result.count", "equal counts when both forms exist", actual);
								if (!result.has("count"))
									count = legacyCount;
							}
						} else {
							output = actual.getAsString();
							field = "count";
							actual = json.get("count");
							count = quantity(json, "count", 1, 4096);
						}
						java.util.List<java.util.List<String>> slots = new java.util.ArrayList<>();
						field = "inputs";
						actual = json.get(field);
						int inputIndex = 0;
						for (var value : json.getAsJsonArray("inputs")) {
							String inputField = "inputs[" + inputIndex++ + "]";
							field = inputField;
							actual = value;
							var input = value.getAsJsonObject();
							if (input.has("nbt") || input.has("components") || input.has("type"))
								return reject("minecolonies_special_ingredient", resourceId, inputField, "item ID or tag without component restrictions", actual);
							field = inputField + ".id/item/tag";
							if (input.has("id") && input.has("item") && !input.get("id").equals(input.get("item")))
								return reject("minecolonies_conflicting_ids", resourceId, field, "matching IDs when both forms exist", value);
							java.util.List<String> alternatives = input.has("id")
									? java.util.List.of(input.get("id").getAsString())
									: input.has("item") ? java.util.List.of(input.get("item").getAsString()) : input.has("tag") ? tagItems.apply(input.get("tag").getAsString()) : java.util.Collections.emptyList();
							field = inputField + ".count";
							actual = input.get("count");
							int amount = quantity(input, "count", 1, 64);
							if (alternatives.isEmpty() || alternatives.stream().anyMatch(id -> !registered.test(id)))
								return reject("minecolonies_unresolved_input", resourceId, inputField + ".id/item/tag", "registered item alternatives", value);
							for (int i = 0; i < amount; i++)
								slots.add(alternatives);
						}
						var evidence = new com.google.gson.JsonObject();
						evidence.addProperty("adapter", "minecolonies_crafterrecipes");
						evidence.addProperty("resource", resourceId);
						var conditions = new com.google.gson.JsonObject();
						conditions.addProperty("crafter", crafter);
						for (String key : java.util.List.of("min-building-level", "max-building-level", "research-id", "excluded-research-id", "intermediate", "tool"))
							if (json.has(key))
								conditions.add(key, json.get(key).deepCopy());
						evidence.add("source_conditions", conditions);
						if (json.has("loot-table"))
							evidence.add("byproduct_loot_table", json.get("loot-table").deepCopy());
						if (!add(output, "minecolonies:" + crafter, count, slots, evidence))
							return reject("minecolonies_invalid_recipe", resourceId, "result/inputs", "registered output and 1..256 input slots", json.get("result"));
						return true;
					} catch (RuntimeException invalid) {
						return reject("minecolonies_invalid_json", resourceId, field, "value matching the supported MineColonies recipe schema", actual);
					}
				}

				boolean portion(String whole, String output, int servings, String container, String mapping, boolean heated) {
					if (servings < 1 || servings > 64 || whole.equals(output)) {
						skip("herbs_invalid_portion_size");
						return false;
					}
					java.util.List<java.util.List<String>> slots = new java.util.ArrayList<>();
					slots.add(java.util.List.of(whole));
					if (!container.isEmpty())
						for (int i = 0; i < servings; i++)
							slots.add(java.util.List.of(container));
					var evidence = new com.google.gson.JsonObject();
					evidence.addProperty("adapter", "herbsandharvest_portions");
					evidence.addProperty("runtime_mapping", mapping);
					evidence.addProperty("portion_source", whole);
					evidence.addProperty("servings", servings);
					if (heated) {
						var conditions = new com.google.gson.JsonObject();
						conditions.addProperty("heat_source", true);
						evidence.add("source_conditions", conditions);
					}
					if (!add(output, "omnichef:herbs_portion", servings, slots, evidence)) {
						skip("herbs_unresolved_portion_mapping");
						return false;
					}
					wholeSources.add(whole);
					if (!container.isEmpty())
						containers.add(container);
					return true;
				}

				com.google.gson.JsonObject diagnostics() {
					var json = new com.google.gson.JsonObject();
					var counts = new com.google.gson.JsonObject();
					for (var list : recipes.values())
						for (var recipe : list) {
							String key = recipe.evidence.get("adapter").getAsString();
							counts.addProperty(key, counts.has(key) ? counts.get(key).getAsInt() + 1 : 1);
						}
					json.add("imported_routes", counts);
					var missing = new com.google.gson.JsonObject();
					for (var e : skipped.entrySet())
						missing.addProperty(e.getKey(), e.getValue());
					json.add("skipped", missing);
					json.add("rejected_resources", rejectedResources.deepCopy());
					json.addProperty("rejected_resource_limit", 100);
					json.addProperty("conditions_policy", "Imported source conditions are retained verbatim; this catalog does not verify player colony, research or heat state.");
					return json;
				}
			}
			SplitSourceCatalog splitSources = new SplitSourceCatalog(itemsById::containsKey, tag -> {
				var location = net.minecraft.resources.ResourceLocation.tryParse(tag);
				if (location == null)
					return java.util.Collections.emptyList();
				var key = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, location);
				java.util.List<String> ids = new java.util.ArrayList<>();
				net.minecraft.core.registries.BuiltInRegistries.ITEM.getTag(key).ifPresent(values -> {
					for (var value : values)
						ids.add(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(value.value()).toString());
				});
				java.util.Collections.sort(ids);
				return ids;
			});
			try {
				var resources = level.getServer().getResourceManager().listResources("crafterrecipes", id -> id.getNamespace().equals("minecolonies") && id.getPath().endsWith(".json"));
				for (var resource : new java.util.TreeMap<>(resources).entrySet()) {
					try (var reader = resource.getValue().openAsReader()) {
						splitSources.minecolonies(com.google.gson.JsonParser.parseReader(reader).getAsJsonObject(), resource.getKey().toString());
					} catch (Exception invalid) {
						splitSources.reject("minecolonies_resource_read_failed", resource.getKey().toString(), "resource", "readable JSON object", null);
					}
				}
			} catch (Exception unavailable) {
				splitSources.skip("minecolonies_resource_scan_failed");
			}
			class SplitHerbsRuntime {
				Object field(Class<?> type, String name) throws ReflectiveOperationException {
					var field = type.getDeclaredField(name);
					if (!field.trySetAccessible())
						throw new IllegalAccessException(name);
					return field.get(null);
				}

				String itemId(Object value) {
					if (value instanceof java.util.function.Supplier<?> supplier)
						value = supplier.get();
					return value instanceof net.minecraft.world.item.Item item ? net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).toString() : "";
				}

				int servings(net.minecraft.world.level.block.Block block, String property) {
					for (var key : block.defaultBlockState().getProperties())
						if (key.getName().equals(property) && key instanceof net.minecraft.world.level.block.state.properties.IntegerProperty count)
							return count.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(0);
					return 0;
				}

				void scan() {
					java.util.Map<Object, String> plates = new java.util.LinkedHashMap<>();
					for (var entry : itemsById.entrySet())
						if (entry.getValue().getClass().getName().equals("com.mamailes.herbsandharvest.item.DinnerPlateBlockItem")) {
							try {
								plates.put(entry.getValue().getClass().getMethod("getType").invoke(entry.getValue()), entry.getKey());
							} catch (ReflectiveOperationException invalid) {
								splitSources.skip("herbs_plate_mapping_unavailable");
							}
						}
					for (var entry : itemsById.entrySet()) {
						if (!entry.getKey().startsWith("herbsandharvest:") || !(entry.getValue() instanceof net.minecraft.world.item.BlockItem item))
							continue;
						var block = item.getBlock();
						String name = item.getClass().getName(), blockName = block.getClass().getName();
						try {
							if (name.equals("com.mamailes.herbsandharvest.item.PotFoodBlockItem")) {
								Object type = item.getClass().getMethod("getType").invoke(item);
								var map = (java.util.Map<?, ?>) field(item.getClass(), "FOOD_TO_BOWL");
								splitSources.portion(entry.getKey(), itemId(map.get(type)), servings(block, "bites"), "minecraft:bowl", "PotFoodBlockItem.FOOD_TO_BOWL", true);
							} else if (name.equals("com.mamailes.herbsandharvest.item.PieBlockItem")) {
								Object type = item.getClass().getMethod("getType").invoke(item);
								var map = (java.util.Map<?, ?>) field(block.getClass(), "TYPE_TO_SLICE");
								splitSources.portion(entry.getKey(), itemId(map.get(type)), servings(block, "slices"), "", "EdiblePieBlock.TYPE_TO_SLICE", false);
							} else if (block.getClass().getSuperclass() != null && block.getClass().getSuperclass().getName().equals("com.mamailes.herbsandharvest.block.food.AbstractPanBlock")) {
								for (var plate : plates.entrySet()) {
									var method = block.getClass().getDeclaredMethod("getPlateItem", plate.getKey().getClass());
									if (!method.trySetAccessible())
										throw new IllegalAccessException("getPlateItem");
									var output = method.invoke(block, plate.getKey());
									if (output instanceof net.minecraft.world.item.ItemStack stack && !stack.isEmpty())
										splitSources.portion(entry.getKey(), itemId(stack.getItem()), servings(block, "servings"), plate.getValue(), "AbstractPanBlock.getPlateItem", false);
								}
							} else if (blockName.equals("com.mamailes.herbsandharvest.block.food.CharcuterieBoardBlock")) {
								var map = (java.util.Map<?, ?>) field(block.getClass(), "PLATE_TO_ITEM");
								for (var meal : map.entrySet())
									for (var plate : plates.entrySet())
										if (meal.getKey() instanceof Enum<?> mealType && plate.getKey() instanceof Enum<?> plateType && mealType.name().equals(plateType.name()))
											splitSources.portion(entry.getKey(), itemId(meal.getValue()), servings(block, "servings"), plate.getValue(), "CharcuterieBoardBlock.PLATE_TO_ITEM", false);
							}
						} catch (Exception incompatible) {
							splitSources.skip("herbs_runtime_mapping_unavailable");
						}
					}
				}
			}
			new SplitHerbsRuntime().scan();
			// Live Ingredients/Food catalog; one authoritative FoodDatabase.json.
			class FoodCatalogBuilder {
				final java.util.Set<String> vanillaIngredients = java.util.Set.of("minecraft:wheat", "minecraft:cocoa_beans", "minecraft:sugar", "minecraft:milk_bucket", "minecraft:egg", "minecraft:pumpkin", "minecraft:melon",
						"minecraft:brown_mushroom", "minecraft:red_mushroom", "minecraft:sugar_cane", "minecraft:kelp");
				final java.util.Set<String> auxiliaryItems = java.util.Set.of("minecraft:bowl", "minecraft:glass_bottle", "minecraft:bucket", "minecraft:stick");
				final java.util.Map<String, com.google.gson.JsonObject> catalog = new java.util.TreeMap<>();
				final java.util.Map<String, com.google.gson.JsonArray> recipeData = new java.util.TreeMap<>();
				final java.util.Map<String, java.util.List<FoodScoreResolver.IndexedRecipe>> allowedRecipeCache = new java.util.HashMap<>();
				final java.util.Map<FoodScoreResolver.IndexedRecipe, com.google.gson.JsonObject> importedEvidence = new java.util.IdentityHashMap<>();
				final java.util.Set<String> preparedSources = new java.util.HashSet<>();
				final java.util.Set<String> basicSupplies = java.util.Set.of("minecraft:apple", "minecraft:carrot", "minecraft:potato", "minecraft:wheat", "minecraft:cocoa_beans", "minecraft:brown_mushroom", "minecraft:red_mushroom",
						"minecraft:chicken", "minecraft:mutton", "minecraft:egg", "minecraft:milk_bucket", "minecraft:sugar_cane");
				final java.util.Set<String> basicFoods = java.util.Set.of("minecraft:bread", "minecraft:baked_potato", "minecraft:cooked_chicken", "minecraft:cookie");
				final java.util.Map<String, String> correctedRoles = new java.util.TreeMap<>();
				final java.util.Set<String> blocked = new java.util.HashSet<>();
				final java.util.Map<String, Double> firstFoodUse = new java.util.HashMap<>();
				final java.util.Map<String, Integer> foodUses = new java.util.HashMap<>();
				final java.util.Map<String, String> aliases = new java.util.TreeMap<>();
				final java.util.Set<String> culinarySeeds = new java.util.HashSet<>();
				final java.util.Set<String> plantingSeeds = new java.util.TreeSet<>();
				final java.util.Map<String, Double> kitchenCosts = new java.util.HashMap<>();
				final java.util.Map<String, Integer> kitchenDepths = new java.util.HashMap<>();
				final java.util.Set<String> unverifiedAcquisition = new java.util.TreeSet<>();
				final java.util.Map<String, Integer> sourceFloors = new java.util.HashMap<>();
				final java.util.Map<String, Double> ingredientRanks = new java.util.HashMap<>();
				final java.util.Map<String, Double> ingredientBaseRanks = new java.util.HashMap<>();
				final java.util.Set<String> foundationalPromotions = new java.util.HashSet<>();
				final java.util.Set<String> pantryCompat = java.util.Set.of("herbsandharvest:cinnamon", "herbsandharvest:corn_kernels", "herbsandharvest:pinto_beans", "minecolonies:cornmeal", "minecolonies:creamcheese", "minecolonies:soysauce",
						"minecolonies:large_milk_bottle", "minecolonies:large_soy_milk_bottle", "productivebees:milk_bottle", "pneumaticcraft:sourdough");
				double foodRankReference = 1.0;
				SplitTierPlanner planner;

				boolean auxiliary(String id) {
					String path = id.substring(id.indexOf(':') + 1);
					return auxiliaryItems.contains(id) || splitSources.containers.contains(id) || classificationRules.equipment(id) || path.contains("empty_bottle") || path.contains("empty_bucket") || path.contains("empty_mug")
							|| path.contains("empty_cup") || path.contains("empty_plate") || path.contains("empty_bowl");
				}

				String path(String id) {
					return id.substring(id.indexOf(':') + 1);
				}

				boolean plantingSeedName(String id) {
					return id.startsWith("pamhc2crops:") && path(id).endsWith("seeditem");
				}

				String cosmeticBase(String id) {
					if (classificationRules.overrides.containsKey(id))
						return id;
					String p = path(id), base = id;
					if (id.startsWith("chromacarvings:") && p.endsWith("_pumpkin")
							&& java.util.Set.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black").contains(p.substring(0, p.length() - 8)))
						base = "minecraft:pumpkin";
					if (id.startsWith("herbsandharvest:") && (p.endsWith("_plate_holiday") || p.endsWith("_plate_delftware")))
						base = id.substring(0, id.lastIndexOf('_') + 1) + "plain";
					return catalog.containsKey(base) && !blocked.contains(base) && catalog.get(base).get("role").getAsString().equals(catalog.get(id).get("role").getAsString()) ? base : id;
				}

				boolean pantryName(String id) {
					String p = path(id);
					if (p.endsWith("item"))
						p = p.substring(0, p.length() - 4);
					if (pantryCompat.contains(id) || id.startsWith("herbsandharvest:") && (p.startsWith("chopped_") || p.startsWith("ground_") || p.startsWith("minced_")))
						return true;
					String compact = p.replace("_", "");
					if (id.startsWith("pamhc2food") && java.util.Set.of("stock", "groundbeef", "groundchicken", "groundduck", "groundfish", "groundmutton", "groundpork", "groundrabbit", "groundturkey", "groundvenison").contains(compact))
						return true;
					if (java.util.Set.of("cookingoil", "sesameoil", "soysauce", "hotsauce", "mustard", "relish", "creamcheese", "freshmilk", "cornmeal", "sourdough").contains(compact))
						return true;
					if (id.startsWith("pamhc2food") && java.util.Set.of("almondbutter", "cashewbutter", "chestnutbutter", "hazelnutbutter", "peanutbutter", "pecanbutter", "pistachiobutter", "sunflowerseedsbutter", "walnutbutter", "applesauce",
							"bbqsauce", "cranberrysauce", "hoisinsauce", "mintsauce", "oystersauce", "sweetandsoursauce", "tomatosauce", "caramelsauce", "mushroomoil", "oliveoil", "sunfloweroil", "coconutoil").contains(compact))
						return true;
					if (p.startsWith("raw_") && (p.endsWith("_pie") || p.endsWith("_pizza") || p.endsWith("_cake")))
						return true;
					return java.util.Set.of("icing", "frosting", "dough", "batter", "flour", "broth", "sugar", "salt", "butter", "cream", "cheese", "vinegar", "oil", "mayonnaise", "mayonaise", "ketchup", "raw_pasta").contains(p)
							|| p.endsWith("_icing") || p.endsWith("_frosting") || p.endsWith("_dough") || p.endsWith("_batter") || p.endsWith("_flour") || p.endsWith("_broth") || p.endsWith("_sauce") || p.endsWith("_extract") || p.endsWith("_oil");
				}

				boolean preparedName(String id) {
					String p = path(id);
					return p.contains("cake_slice") || p.contains("pie_slice") || p.contains("bread_slice") || p.contains("pizza_slice") || p.endsWith("cheesecake") || p.contains("charcuterie_plate") || p.contains("_plate_plain")
							|| p.contains("_plate_holiday") || p.contains("_plate_delftware") || p.endsWith("_cake") || p.endsWith("_pie") || p.endsWith("_loaf") || p.endsWith("_sandwich") || p.endsWith("_pizza") || p.endsWith("_stew")
							|| p.endsWith("_soup") || p.endsWith("_curry") || p.endsWith("_dinner") || p.endsWith("_meal") && classificationRules.isEdible(id);
				}

				boolean rawTagged(String id) {
					return classificationRules.matchesTags(id, classificationRules.ingredientTags);
				}

				boolean starterCandidate(String id, String role, boolean canServe) {
					return "INGREDIENT".equals(role) && canServe && !pantryName(id) && !plantingSeedName(id) && !preparedName(id) && recipes(id).isEmpty() && basicSupplies.contains(id);
				}

				boolean foundationalPantry(String id) {
					String p = path(id);
					if (p.endsWith("item"))
						p = p.substring(0, p.length() - 4);
					return java.util.Set.of("flour", "wheatflour", "dough", "wheatdough", "breaddough", "salt", "sugar", "rawpasta").contains(p.replace("_", ""));
				}

				String correctedRole(String id, String oldRole) {
					if (classificationRules.overrides.containsKey(id))
						return classificationRules.overrides.get(id);
					if (classificationRules.pantryItems.contains(id) || pantryName(id))
						return "INGREDIENT";
					if (classificationRules.preparedItems.contains(id))
						return "FOOD";
					if ("minecraft:melon_slice".equals(id) || "farmersdelight:pumpkin_slice".equals(id))
						return "INGREDIENT";
					if (preparedName(id))
						return "FOOD";
					if (classificationRules.matchesTags(id, classificationRules.preparedTags))
						return "FOOD";
					if (rawTagged(id))
						return "INGREDIENT";
					return oldRole;
				}

				boolean portionIntermediate(String id) {
					return !classificationRules.overrides.containsKey(id) && !classificationRules.isEdible(id) && !pantryName(id) && (preparedSources.contains(id) || preparedName(id));
				}

				int declaredSourceMinimum(String id, int tiers) {
					int minimum = acquisitionSources.minimumTier(id, tiers);
					// Exact compatibility profile: these routes originate from Ad Astra's Moon cheese ore.
					if (java.util.Set.of("ad_astra:cheese", "ad_astra:cheese_block", "ad_astra:moon_cheese_ore").contains(id))
						minimum = Math.max(minimum, (int) Math.ceil((tiers - 1) * 0.75));
					return minimum;
				}

				int sourceMinimum(String id, int tiers) {
					return Math.min(tiers - 1, Math.max(declaredSourceMinimum(id, tiers), sourceFloors.getOrDefault(id, 0)));
				}

				void measureSourceFloors() {
					for (var node : planner.nodes.values()) {
						if ("BLOCKED".equals(node.kind) || "ALIAS".equals(node.kind))
							continue;
						if ("AUXILIARY".equals(node.kind))
							sourceFloors.put(node.id, 0);
						else if ("EXTERNAL".equals(node.kind) || "INGREDIENT".equals(node.kind) && node.routes.isEmpty())
							sourceFloors.put(node.id, declaredSourceMinimum(node.id, planner.tierCount));
					}
					for (int pass = 0; pass <= planner.nodes.size(); pass++) {
						boolean changed = false;
						for (var node : planner.nodes.values()) {
							if ("BLOCKED".equals(node.kind) || "EXTERNAL".equals(node.kind) || "AUXILIARY".equals(node.kind))
								continue;
							int best = sourceFloors.getOrDefault(node.id, SplitTierPlanner.UNREACHABLE);
							if ("ALIAS".equals(node.kind))
								best = Math.min(best, sourceFloors.getOrDefault(node.aliasOf, SplitTierPlanner.UNREACHABLE));
							else
								for (var value : recipeData.get(node.id)) {
									var recipe = value.getAsJsonObject();
									if (packingOnly(node.id, recipe))
										continue;
									int required = declaredSourceMinimum(node.id, planner.tierCount);
									for (var slot : recipe.getAsJsonArray("ingredientSlots")) {
										int easiest = SplitTierPlanner.UNREACHABLE;
										for (var input : slot.getAsJsonArray())
											easiest = Math.min(easiest, sourceFloors.getOrDefault(input.getAsString(), SplitTierPlanner.UNREACHABLE));
										required = Math.max(required, easiest);
									}
									best = Math.min(best, required);
								}
							if (best < sourceFloors.getOrDefault(node.id, SplitTierPlanner.UNREACHABLE)) {
								sourceFloors.put(node.id, best);
								changed = true;
							}
						}
						if (!changed)
							break;
					}
					for (var node : planner.nodes.values())
						if ("INGREDIENT".equals(node.kind) && !sourceFloors.containsKey(node.id))
							sourceFloors.put(node.id, planner.ingredientTierCount - 1);
				}

				boolean evidencedAcquisition(String id) {
					var source = acquisitionSources.describe(id, planner.tierCount);
					if (!source.has("status") || !java.util.Set.of("estimated", "estimated_partial").contains(source.get("status").getAsString()) || !source.has("system") || source.get("system").getAsString().isEmpty())
						return false;
					if (source.has("confidence") && "assumed".equals(source.get("confidence").getAsString()))
						return false;
					return source.has("requirements");
				}

				java.util.List<java.util.List<String>> acquisitionInputs(String id) {
					java.util.List<java.util.List<String>> slots = new java.util.ArrayList<>();
					for (var value : acquisitionSources.describe(id, planner.tierCount).getAsJsonArray("requirements")) {
						java.util.List<String> alternatives = new java.util.ArrayList<>();
						for (var input : value.getAsJsonArray()) {
							String key = input.getAsString();
							if (key.startsWith("item|") && itemsById.containsKey(key.substring(5)))
								alternatives.add(key.substring(5));
						}
						// Unsupported fluids/entities must not silently turn into an empty, free slot.
						if (alternatives.isEmpty())
							return null;
						slots.add(alternatives);
					}
					return slots;
				}

				int ingredientAccessMinimum(String id, int sourceMinimum) {
					// Vanilla startup profiles: rabbit meat and honey are not initial supply choices.
					return "minecraft:rabbit".equals(id) || "minecraft:honey_bottle".equals(id) ? Math.max(1, sourceMinimum) : sourceMinimum;
				}

				boolean culinaryIngredient(String id) {
					if (auxiliary(id) || excludedFoods.contains(id) || classificationRules.excludedItems.contains(id))
						return false;
					if ("FOOD".equals(classificationRules.hint(id)) && !pantryName(id) && !rawTagged(id))
						return false;
					return vanillaIngredients.contains(id) || classificationRules.culinaryIds.contains(id) || classificationRules.pantryItems.contains(id) || splitSources.inputs.contains(id) && rawTagged(id)
							|| (foodProgression.recipes.containsKey(id) && classificationRules.isCulinary(id));
				}

				boolean culinaryInput(String id) {
					return !auxiliary(id) && (catalog.containsKey(id) || vanillaIngredients.contains(id) || classificationRules.isCulinary(id) || classificationRules.pantryItems.contains(id) || pantryName(id) || preparedSources.contains(id)
							|| preparedName(id) || splitSources.inputs.contains(id) && rawTagged(id));
				}

				com.google.gson.JsonObject ingredient(String id) {
					com.google.gson.JsonObject entry = new com.google.gson.JsonObject();
					entry.addProperty("id", id);
					entry.addProperty("role", "INGREDIENT");
					entry.addProperty("role_reason", "non_edible_culinary_ingredient");
					entry.addProperty("edible", false);
					entry.addProperty("can_serve", false);
					entry.addProperty("starter_eligible", false);
					entry.add("acquisition", acquisitionSources.describe(id, planner.tierCount));
					double score = scoreResolver.resolve(id);
					entry.addProperty("score", Double.isFinite(score) ? score : 1.0);
					entry.addProperty("nutrition", 0);
					entry.addProperty("saturation", 0);
					return entry;
				}

				java.util.List<FoodScoreResolver.IndexedRecipe> allowedRecipes(String id) {
					if (allowedRecipeCache.containsKey(id))
						return allowedRecipeCache.get(id);
					java.util.List<FoodScoreResolver.IndexedRecipe> result = new java.util.ArrayList<>();
					for (var recipe : scoreResolver.rawRecipesFor(id)) {
						if (!classificationRules.matchesType(recipe.method, classificationRules.allowedTypes) && !"omnichef:sushi_rolling".equals(recipe.method))
							continue;
						if (!recipe.slots.isEmpty())
							result.add(recipe);
					}
					for (var imported : splitSources.recipes.getOrDefault(id, java.util.Collections.emptyList())) {
						var recipe = scoreResolver.new IndexedRecipe(null, imported.method, imported.count, imported.slots);
						if (imported.evidence.has("portion_source")) {
							recipe.portionSource = imported.evidence.get("portion_source").getAsString();
							recipe.servings = imported.count;
						}
						result.add(recipe);
						importedEvidence.put(recipe, imported.evidence);
					}
					allowedRecipeCache.put(id, result);
					return result;
				}

				java.util.List<FoodScoreResolver.IndexedRecipe> recipes(String id) {
					if (auxiliary(id))
						return java.util.Collections.emptyList();
					boolean dish = catalog.containsKey(id) && "FOOD".equals(catalog.get(id).get("role").getAsString());
					if (dish || preparedSources.contains(id) || preparedName(id) || classificationRules.pantryItems.contains(id) || pantryName(id))
						return allowedRecipes(id);
					// Raw ingredients are acquisition leaves. Farming, essence recipes and packaging do not define culinary discovery prerequisites.
					if (catalog.containsKey(id)) {
						java.util.List<FoodScoreResolver.IndexedRecipe> thermal = new java.util.ArrayList<>();
						for (var recipe : allowedRecipes(id))
							if (recipe.method.equals("minecraft:smelting") || recipe.method.equals("minecraft:smoking") || recipe.method.equals("minecraft:campfire_cooking"))
								thermal.add(recipe);
						return thermal;
					}
					if (classificationRules.isCulinary(id))
						return allowedRecipes(id);
					return java.util.Collections.emptyList();
				}

				double rank(String id, String role, com.google.gson.JsonObject entry) {
					double work = kitchenCosts.getOrDefault(id, "FOOD".equals(role) ? 6.0 : 1.0);
					int depth = kitchenDepths.getOrDefault(id, 0);
					if ("FOOD".equals(role)) {
						double structural = scoreResolver.recipeScoreCache.getOrDefault(id, 0.0);
						if (!Double.isFinite(structural))
							structural = 0;
						return Math.round((work + depth * 1.5 + Math.log1p(Math.max(0, structural)) * 0.5) * 100.0) / 100.0;
					}
					double source = acquisitionSources.costs.getOrDefault("item|" + id, 0.0);
					if (!Double.isFinite(source))
						source = sourceMinimum(id, planner.tierCount) * classificationRules.stepsPerTier;
					source = Math.max(0, source);
					// Availability and production effort come first; usefulness distinguishes cheap base ingredients.
					double usefulness = firstFoodUse.containsKey(id) ? 20.0 * Math.min(1.0, firstFoodUse.get(id) / foodRankReference) : 25.0;
					double result = source * 10.0 + work * 2.0 + usefulness + (3 - Math.min(3, foodUses.getOrDefault(id, 0))) * 0.5;
					return Math.round(result * 100.0) / 100.0;
				}

				void measureIngredientRanks() {
					// A processing step cannot rank before the easiest complete route's raw supplies.
					// This orders discovery choices; it does not require unlocking each raw input to buy/use a prepared ingredient.
					java.util.Map<String, Double> bounds = new java.util.HashMap<>();
					for (var node : planner.nodes.values()) {
						if ("INGREDIENT".equals(node.kind) && catalog.containsKey(node.id)) {
							double base = rank(node.id, "INGREDIENT", catalog.get(node.id));
							ingredientBaseRanks.put(node.id, base);
							if (recipeData.get(node.id).isEmpty())
								bounds.put(node.id, base);
						} else if ("AUXILIARY".equals(node.kind) || "EXTERNAL".equals(node.kind))
							bounds.put(node.id, 0.0);
					}
					for (int pass = 0; pass <= planner.nodes.size(); pass++) {
						boolean changed = false;
						for (var node : planner.nodes.values()) {
							if ("BLOCKED".equals(node.kind) || "AUXILIARY".equals(node.kind) || "EXTERNAL".equals(node.kind))
								continue;
							double best = bounds.getOrDefault(node.id, Double.POSITIVE_INFINITY);
							if ("ALIAS".equals(node.kind))
								best = Math.min(best, bounds.getOrDefault(node.aliasOf, Double.POSITIVE_INFINITY));
							else
								for (var value : recipeData.get(node.id)) {
									var recipe = value.getAsJsonObject();
									if (packingOnly(node.id, recipe))
										continue;
									double inputFloor = 0.0;
									for (var slot : recipe.getAsJsonArray("ingredientSlots")) {
										double easiest = Double.POSITIVE_INFINITY;
										for (var input : slot.getAsJsonArray())
											easiest = Math.min(easiest, bounds.getOrDefault(input.getAsString(), Double.POSITIVE_INFINITY));
										inputFloor = Math.max(inputFloor, easiest);
									}
									if (!Double.isFinite(inputFloor))
										continue;
									double candidate = Math.max(ingredientBaseRanks.getOrDefault(node.id, 0.0), inputFloor + 0.25);
									best = Math.min(best, candidate);
								}
							if (best + 0.00001 < bounds.getOrDefault(node.id, Double.POSITIVE_INFINITY)) {
								bounds.put(node.id, best);
								changed = true;
							}
						}
						if (!changed)
							break;
					}
					for (var entry : ingredientBaseRanks.entrySet())
						ingredientRanks.put(entry.getKey(), Math.round(Math.max(entry.getValue(), bounds.getOrDefault(entry.getKey(), entry.getValue())) * 100.0) / 100.0);
				}

				void measureFoundationalPromotions() {
					java.util.Set<String> ready = new java.util.HashSet<>();
					for (var node : planner.nodes.values())
						if (sourceMinimum(node.id, planner.tierCount) == 0 && (basicSupplies.contains(node.id) || "AUXILIARY".equals(node.kind) || "EXTERNAL".equals(node.kind)))
							ready.add(node.id);
					boolean changed;
					do {
						changed = false;
						for (var node : planner.nodes.values()) {
							if (ready.contains(node.id) || sourceMinimum(node.id, planner.tierCount) > 0)
								continue;
							if ("ALIAS".equals(node.kind)) {
								if (ready.contains(node.aliasOf) && ready.add(node.id))
									changed = true;
								continue;
							}
							boolean foundation = "INGREDIENT".equals(node.kind) && catalog.containsKey(node.id) && foundationalPantry(node.id) && kitchenCosts.getOrDefault(node.id, Double.POSITIVE_INFINITY) <= 6.0
									&& kitchenDepths.getOrDefault(node.id, 0) <= 3;
							if (!foundation && !"FOOD".equals(node.kind) && !"INTERMEDIATE".equals(node.kind))
								continue;
							for (var value : recipeData.get(node.id)) {
								var recipe = value.getAsJsonObject();
								if (packingOnly(node.id, recipe) || recipe.has("source_evidence") && recipe.getAsJsonObject("source_evidence").has("source_conditions"))
									continue;
								boolean possible = true;
								for (var slot : recipe.getAsJsonArray("ingredientSlots")) {
									boolean any = false;
									for (var input : slot.getAsJsonArray())
										if (ready.contains(input.getAsString()))
											any = true;
									if (!any) {
										possible = false;
										break;
									}
								}
								if (possible) {
									if (ready.add(node.id))
										changed = true;
									if (foundation)
										foundationalPromotions.add(node.id);
									break;
								}
							}
						}
					} while (changed);
				}

				void measureKitchenWork() {
					// Recompute culinary production cost independently of nutrition and legacy farming/essence chains.
					for (var node : planner.nodes.values()) {
						if ("BLOCKED".equals(node.kind) || "ALIAS".equals(node.kind))
							continue;
						if (("FOOD".equals(node.kind) || "INTERMEDIATE".equals(node.kind)) && recipeData.get(node.id).isEmpty())
							continue;
						boolean processed = !recipeData.get(node.id).isEmpty();
						if (processed && !"EXTERNAL".equals(node.kind) && !"AUXILIARY".equals(node.kind))
							continue;
						double source = acquisitionSources.costs.getOrDefault("item|" + node.id, 0.0);
						if (!Double.isFinite(source))
							source = node.minimum * classificationRules.stepsPerTier;
						double cost = "AUXILIARY".equals(node.kind) ? 0.0 : 1.0 + Math.min(12, Math.max(0, source));
						if ("EXTERNAL".equals(node.kind)) {
							double legacy = scoreResolver.resolve(node.id);
							if (Double.isFinite(legacy))
								cost += Math.min(12, Math.log1p(Math.max(0, legacy)));
						}
						kitchenCosts.put(node.id, cost);
						kitchenDepths.put(node.id, 0);
					}
					for (int pass = 0; pass <= planner.nodes.size(); pass++) {
						boolean changed = false;
						for (var node : planner.nodes.values()) {
							if ("ALIAS".equals(node.kind)) {
								if (kitchenCosts.containsKey(node.aliasOf)
										&& (Double.compare(kitchenCosts.get(node.aliasOf), kitchenCosts.getOrDefault(node.id, Double.POSITIVE_INFINITY)) != 0 || !kitchenDepths.get(node.aliasOf).equals(kitchenDepths.get(node.id)))) {
									kitchenCosts.put(node.id, kitchenCosts.get(node.aliasOf));
									kitchenDepths.put(node.id, kitchenDepths.get(node.aliasOf));
									changed = true;
								}
								continue;
							}
							if ("BLOCKED".equals(node.kind) || "EXTERNAL".equals(node.kind) || "AUXILIARY".equals(node.kind))
								continue;
							for (var value : recipeData.get(node.id)) {
								var recipe = value.getAsJsonObject();
								if (packingOnly(node.id, recipe))
									continue;
								double total = 0;
								int depth = 0;
								boolean possible = true;
								for (var slot : recipe.getAsJsonArray("ingredientSlots")) {
									double best = Double.POSITIVE_INFINITY;
									int bestDepth = 0;
									for (var input : slot.getAsJsonArray()) {
										String id = input.getAsString();
										double candidate = kitchenCosts.getOrDefault(id, Double.POSITIVE_INFINITY);
										if (candidate < best) {
											best = candidate;
											bestDepth = kitchenDepths.getOrDefault(id, 0);
										}
									}
									if (!Double.isFinite(best)) {
										possible = false;
										break;
									}
									total += best;
									depth = Math.max(depth, bestDepth);
								}
								if (!possible)
									continue;
								String method = recipe.get("method").getAsString();
								double step = method.contains("cutting") || method.contains("slicing") ? 0.5 : 1.5;
								double cost = Math.max(0.25, (total + step) / Math.max(1, recipe.get("outputCount").getAsInt()));
								depth++;
								if (cost + depth * 1.5 + 0.0001 < kitchenCosts.getOrDefault(node.id, Double.POSITIVE_INFINITY) + kitchenDepths.getOrDefault(node.id, 0) * 1.5) {
									kitchenCosts.put(node.id, cost);
									kitchenDepths.put(node.id, depth);
									changed = true;
								}
							}
						}
						if (!changed)
							break;
					}
				}

				void measureFoodUses() {
					firstFoodUse.clear();
					foodUses.clear();
					java.util.List<Double> foodRanks = new java.util.ArrayList<>();
					for (var entry : catalog.entrySet())
						if ("FOOD".equals(entry.getValue().get("role").getAsString()))
							foodRanks.add(Math.max(0.1, rank(entry.getKey(), "FOOD", entry.getValue())));
					java.util.Collections.sort(foodRanks);
					if (!foodRanks.isEmpty())
						foodRankReference = Math.max(1.0, foodRanks.get((int) Math.floor((foodRanks.size() - 1) * 0.75)));
					for (var entry : catalog.entrySet()) {
						if (!"FOOD".equals(entry.getValue().get("role").getAsString()))
							continue;
						double foodRank = Math.max(0.1, rank(entry.getKey(), "FOOD", entry.getValue()));
						java.util.ArrayDeque<String> pending = new java.util.ArrayDeque<>();
						java.util.Set<String> visited = new java.util.HashSet<>();
						pending.add(entry.getKey());
						while (!pending.isEmpty()) {
							String id = pending.removeFirst();
							if (!visited.add(id))
								continue;
							if (aliases.containsKey(id)) {
								pending.addLast(aliases.get(id));
								continue;
							}
							var ingredient = catalog.get(id);
							if (ingredient != null && "INGREDIENT".equals(ingredient.get("role").getAsString())) {
								firstFoodUse.merge(id, foodRank, Math::min);
								foodUses.merge(id, 1, Integer::sum);
							}
							for (var recipe : recipes(id))
								for (var slot : recipe.slots)
									for (String input : slot)
										if (!auxiliary(input) && !blocked.contains(input))
											pending.addLast(input);
						}
					}
				}

				com.google.gson.JsonArray routeJson(java.util.List<java.util.List<String>> route) {
					com.google.gson.JsonArray slots = new com.google.gson.JsonArray();
					for (var slot : route) {
						com.google.gson.JsonArray alternatives = new com.google.gson.JsonArray();
						for (String id : slot)
							alternatives.add(id);
						slots.add(alternatives);
					}
					return slots;
				}

				String singleMaterial(com.google.gson.JsonArray slots) {
					String material = null;
					for (var value : slots) {
						var alternatives = value.getAsJsonArray();
						if (alternatives.size() != 1)
							return null;
						String input = alternatives.get(0).getAsString();
						if (material != null && !material.equals(input))
							return null;
						material = input;
					}
					return material;
				}

				boolean packingOnly(String output, com.google.gson.JsonObject recipe) {
					var slots = recipe.getAsJsonArray("ingredientSlots");
					String input = singleMaterial(slots);
					if (input == null || output.equals(input) || !recipeData.containsKey(input))
						return false;
					for (var value : recipeData.get(input)) {
						var reverse = value.getAsJsonObject();
						var reverseSlots = reverse.getAsJsonArray("ingredientSlots");
						if (!output.equals(singleMaterial(reverseSlots)))
							continue;
						long consumed = (long) slots.size() * reverseSlots.size();
						long produced = (long) recipe.get("outputCount").getAsInt() * reverse.get("outputCount").getAsInt();
						if (consumed == produced && (slots.size() != recipe.get("outputCount").getAsInt() || reverseSlots.size() != reverse.get("outputCount").getAsInt()))
							return true;
					}
					return false;
				}

				java.util.Set<String> neededIngredients() {
					java.util.Set<String> allIngredients = new java.util.HashSet<>();
					for (var node : planner.nodes.values())
						if ("INGREDIENT".equals(node.kind))
							allIngredients.add(node.id);
					var usable = planner.preparableNodes(allIngredients);
					java.util.Set<String> visited = new java.util.HashSet<>(), needed = new java.util.HashSet<>();
					java.util.ArrayDeque<String> pending = new java.util.ArrayDeque<>();
					for (var node : planner.nodes.values())
						if (node.canServe && usable.contains(node.id))
							pending.addLast(node.id);
					while (!pending.isEmpty()) {
						String id = pending.removeFirst();
						if (!visited.add(id))
							continue;
						var node = planner.nodes.get(id);
						if (node == null)
							continue;
						if ("ALIAS".equals(node.kind)) {
							pending.addLast(node.aliasOf);
							continue;
						}
						if ("INGREDIENT".equals(node.kind))
							needed.add(id);
						for (var value : recipeData.get(id)) {
							var recipe = value.getAsJsonObject();
							if (packingOnly(id, recipe))
								continue;
							var slots = recipe.getAsJsonArray("ingredientSlots");
							boolean feasible = true;
							for (var slot : slots) {
								boolean available = false;
								for (var input : slot.getAsJsonArray())
									if (usable.contains(input.getAsString())) {
										available = true;
										break;
									}
								if (!available) {
									feasible = false;
									break;
								}
							}
							if (feasible)
								for (var slot : slots)
									for (var input : slot.getAsJsonArray())
										if (usable.contains(input.getAsString()))
											pending.addLast(input.getAsString());
						}
					}
					return needed;
				}

				boolean restorableInput(String id, java.util.Set<String> referenced) {
					if (!itemsById.containsKey(id) || !referenced.contains(id) || excludedFoods.contains(id) || classificationRules.excludedItems.contains(id) || auxiliary(id) || !culinaryInput(id))
						return false;
					for (var value : excludedFoodsArray) {
						var excluded = value.getAsJsonObject();
						if (id.equals(excluded.get("id").getAsString()) && (!excluded.has("reason") || !"no_nutritional_value".equals(excluded.get("reason").getAsString())))
							return false;
					}
					return true;
				}

				java.util.Set<String> conditionFreeNodes() {
					java.util.Set<String> usable = new java.util.HashSet<>();
					for (var node : planner.nodes.values())
						if (java.util.Set.of("INGREDIENT", "AUXILIARY", "EXTERNAL").contains(node.kind))
							usable.add(node.id);
					boolean changed;
					do {
						changed = false;
						for (var node : planner.nodes.values()) {
							if (usable.contains(node.id))
								continue;
							if ("ALIAS".equals(node.kind)) {
								if (usable.contains(node.aliasOf) && usable.add(node.id))
									changed = true;
								continue;
							}
							if (!"FOOD".equals(node.kind) && !"INTERMEDIATE".equals(node.kind))
								continue;
							for (var value : recipeData.get(node.id)) {
								var recipe = value.getAsJsonObject();
								if (recipe.has("source_evidence") && recipe.getAsJsonObject("source_evidence").has("source_conditions"))
									continue;
								boolean possible = true;
								for (var slot : recipe.getAsJsonArray("ingredientSlots")) {
									boolean any = false;
									for (var input : slot.getAsJsonArray())
										if (usable.contains(input.getAsString()))
											any = true;
									if (!any) {
										possible = false;
										break;
									}
								}
								if (possible) {
									if (usable.add(node.id))
										changed = true;
									break;
								}
							}
						}
					} while (changed);
					return usable;
				}

				com.google.gson.JsonObject build() {
					preparedSources.addAll(splitSources.wholeSources);
					for (var entry : disabledFoodsArray)
						blocked.add(entry.getAsJsonObject().get("id").getAsString());
					for (var entry : excludedFoodsArray)
						blocked.add(entry.getAsJsonObject().get("id").getAsString());
					blocked.addAll(excludedFoods);
					blocked.addAll(classificationRules.excludedItems);
					for (var old : allFoods) {
						String id = old.get("id").getAsString();
						com.google.gson.JsonObject entry = old.deepCopy();
						String previousRole = old.get("role").getAsString(), role = correctedRole(id, previousRole);
						entry.addProperty("role", role);
						if (!role.equals(previousRole)) {
							entry.addProperty("role_reason", "split_role_corrected_from_" + previousRole);
							correctedRoles.put(id, previousRole + " -> " + role);
						}
						for (String key : java.util.List.of("tier", "ranking_score", "base_exp", "score_position"))
							entry.remove(key);
						entry.addProperty("edible", true);
						entry.addProperty("can_serve", true);
						entry.remove("progression");
						catalog.put(id, entry);
					}
					for (var entry : catalog.entrySet())
						if ("FOOD".equals(entry.getValue().get("role").getAsString()))
							for (var recipe : scoreResolver.rawRecipesFor(entry.getKey()))
								if (!recipe.portionSource.isEmpty())
									preparedSources.add(recipe.portionSource);
					java.util.Set<String> additions = new java.util.TreeSet<>();
					for (String id : itemsById.keySet())
						if (!catalog.containsKey(id) && !blocked.contains(id) && !classificationRules.isEdible(id) && !auxiliary(id) && (culinaryIngredient(id) || pantryName(id))) {
							if (!portionIntermediate(id))
								additions.add(id);
						}
					java.util.Set<String> unsafeIngredients = new java.util.TreeSet<>();
					java.util.Set<String> referencedInputs = new java.util.HashSet<>(foodProgression.recipes.keySet());
					referencedInputs.addAll(splitSources.inputs);
					for (var food : allFoods)
						for (var recipe : scoreResolver.rawRecipesFor(food.get("id").getAsString()))
							for (var slot : recipe.slots)
								referencedInputs.addAll(slot);
					for (var value : disabledFoodsArray) {
						var entry = value.getAsJsonObject();
						String id = entry.get("id").getAsString();
						if (restorableInput(id, referencedInputs)) {
							unsafeIngredients.add(id);
							blocked.remove(id);
						}
					}
					for (var value : excludedFoodsArray) {
						String id = value.getAsJsonObject().get("id").getAsString();
						if (restorableInput(id, referencedInputs)) {
							unsafeIngredients.add(id);
							blocked.remove(id);
						}
					}
					for (String id : new java.util.ArrayList<>(unsafeIngredients))
						if (portionIntermediate(id))
							unsafeIngredients.remove(id);
					int ingredientCount = additions.size() + unsafeIngredients.size(), foodCount = 0;
					for (var entry : catalog.values())
						if ("INGREDIENT".equals(entry.get("role").getAsString()))
							ingredientCount++;
						else
							foodCount++;
					int ingredientTierCount = Math.max(3, Math.min(10, (ingredientCount + 79) / 80));
					int splitTierCount = Math.max(ingredientTierCount, Math.max(5, Math.min(10, (foodCount + 39) / 40)));
					planner = new SplitTierPlanner(ingredientTierCount, splitTierCount);
					for (String id : additions)
						catalog.put(id, ingredient(id));
					for (String id : unsafeIngredients) {
						var entry = ingredient(id);
						entry.addProperty("edible", classificationRules.isEdible(id));
						entry.addProperty("role_reason", "culinary_input_not_safe_to_serve");
						catalog.put(id, entry);
					}
					// A planting seed is a culinary discovery only when an actual kitchen recipe explicitly names it.
					// Broad seed-tag alternatives do not turn hundreds of propagation items into restaurant products.
					for (var entry : catalog.entrySet())
						if ("FOOD".equals(entry.getValue().get("role").getAsString()) || pantryName(entry.getKey()))
							for (var recipe : allowedRecipes(entry.getKey()))
								for (var slot : recipe.slots)
									if (slot.size() == 1 && plantingSeedName(slot.get(0)))
										culinarySeeds.add(slot.get(0));
					for (String id : new java.util.ArrayList<>(catalog.keySet())) {
						if (plantingSeedName(id) && !culinarySeeds.contains(id) && !classificationRules.overrides.containsKey(id)) {
							catalog.remove(id);
							plantingSeeds.add(id);
							blocked.add(id);
							continue;
						}
						String base = cosmeticBase(id);
						if (!base.equals(id))
							aliases.put(id, base);
					}
					com.google.gson.JsonObject discoveryVariants = new com.google.gson.JsonObject();
					for (var alias : aliases.entrySet()) {
						var original = catalog.remove(alias.getKey());
						if (!discoveryVariants.has(alias.getValue()))
							discoveryVariants.add(alias.getValue(), new com.google.gson.JsonArray());
						var variant = new com.google.gson.JsonObject();
						variant.addProperty("id", alias.getKey());
						variant.addProperty("can_serve", original.get("can_serve").getAsBoolean());
						variant.addProperty("role", original.get("role").getAsString());
						discoveryVariants.getAsJsonArray(alias.getValue()).add(variant);
					}
					ingredientCount = 0;
					foodCount = 0;
					for (var entry : catalog.values())
						if ("INGREDIENT".equals(entry.get("role").getAsString()))
							ingredientCount++;
						else
							foodCount++;
					measureFoodUses();
					for (var entry : catalog.entrySet()) {
						String id = entry.getKey(), role = entry.getValue().get("role").getAsString();
						int minimum = sourceMinimum(id, "INGREDIENT".equals(role) ? ingredientTierCount : splitTierCount);
						if ("INGREDIENT".equals(role))
							minimum = ingredientAccessMinimum(id, minimum);
						double ranking = rank(id, role, entry.getValue());
						entry.getValue().addProperty("ranking_score", ranking);
						var node = planner.add(id, role, minimum, ranking, entry.getValue().get("can_serve").getAsBoolean());
						node.starterEligible = starterCandidate(id, role, node.canServe);
						if ("INGREDIENT".equals(role) && basicSupplies.contains(id))
							node.priority = -2;
					}
					for (var alias : aliases.entrySet())
						planner.add(alias.getKey(), "ALIAS", 0, 0.0, false).aliasOf = alias.getValue();
					java.util.ArrayDeque<String> pending = new java.util.ArrayDeque<>(catalog.keySet());
					pending.addAll(aliases.keySet());
					java.util.Set<String> processed = new java.util.HashSet<>();
					while (!pending.isEmpty()) {
						String id = pending.removeFirst();
						if (!processed.add(id))
							continue;
						var node = planner.nodes.get(id);
						com.google.gson.JsonArray variants = new com.google.gson.JsonArray();
						if ("FOOD".equals(node.kind) || "INTERMEDIATE".equals(node.kind) || "INGREDIENT".equals(node.kind) || "ALIAS".equals(node.kind)) {
							for (var recipe : "ALIAS".equals(node.kind) ? allowedRecipes(id) : recipes(id)) {
								node.routes.add(recipe.slots);
								com.google.gson.JsonObject variant = new com.google.gson.JsonObject();
								variant.addProperty("method", recipe.method);
								variant.addProperty("outputCount", recipe.outputCount);
								variant.add("ingredientSlots", routeJson(recipe.slots));
								if (!recipe.portionSource.isEmpty()) {
									variant.addProperty("portion_source", recipe.portionSource);
									variant.addProperty("servings", recipe.servings);
								}
								if (importedEvidence.containsKey(recipe))
									variant.add("source_evidence", importedEvidence.get(recipe));
								variants.add(variant);
								for (var slot : recipe.slots)
									for (String input : slot) {
										if (planner.nodes.containsKey(input))
											continue;
										String kind = blocked.contains(input) || plantingSeedName(input) && !culinarySeeds.contains(input) && !classificationRules.overrides.containsKey(input) || !itemsById.containsKey(input)
												? "BLOCKED"
												: (auxiliary(input) ? "AUXILIARY" : (!recipes(input).isEmpty() ? "INTERMEDIATE" : (culinaryInput(input) ? "BLOCKED" : "EXTERNAL")));
										planner.add(input, kind, sourceMinimum(input, splitTierCount), 0.0, false);
										pending.addLast(input);
									}
							}
							// Only an exported source route is evidence. An assumed base is not a discovered acquisition method.
							if ("FOOD".equals(node.kind) && variants.isEmpty() && scoreResolver.rawRecipesFor(id).isEmpty()) {
								var slots = evidencedAcquisition(id) ? acquisitionInputs(id) : null;
								if (slots != null) {
									node.routes.add(slots);
									var variant = new com.google.gson.JsonObject();
									variant.addProperty("method", "omnichef:acquisition");
									variant.addProperty("outputCount", 1);
									variant.add("ingredientSlots", routeJson(slots));
									variant.add("source_evidence", acquisitionSources.describe(id, splitTierCount));
									variants.add(variant);
									for (var slot : slots)
										for (String input : slot)
											if (!planner.nodes.containsKey(input)) {
												String kind = blocked.contains(input) ? "BLOCKED" : auxiliary(input) ? "AUXILIARY" : !recipes(input).isEmpty() ? "INTERMEDIATE" : culinaryInput(input) ? "BLOCKED" : "EXTERNAL";
												planner.add(input, kind, sourceMinimum(input, splitTierCount), 0.0, false);
												pending.addLast(input);
											}
								} else
									unverifiedAcquisition.add(id);
							}
						}
						recipeData.put(id, variants);
					}
					var needed = neededIngredients();
					needed.addAll(aliases.values());
					com.google.gson.JsonArray filteredIngredients = new com.google.gson.JsonArray();
					java.util.List<String> unnecessary = new java.util.ArrayList<>();
					for (var entry : catalog.entrySet())
						if ("INGREDIENT".equals(entry.getValue().get("role").getAsString()) && !entry.getValue().get("can_serve").getAsBoolean() && !needed.contains(entry.getKey()))
							unnecessary.add(entry.getKey());
					for (String id : unnecessary) {
						catalog.remove(id);
						com.google.gson.JsonObject removed = new com.google.gson.JsonObject();
						removed.addProperty("id", id);
						removed.addProperty("reason", "no_non_packing_path_to_servable_product");
						filteredIngredients.add(removed);
					}
					for (String id : plantingSeeds) {
						var removed = new com.google.gson.JsonObject();
						removed.addProperty("id", id);
						removed.addProperty("reason", "planting_seed_without_explicit_culinary_use");
						filteredIngredients.add(removed);
					}
					additions.retainAll(catalog.keySet());
					unsafeIngredients.retainAll(catalog.keySet());
					ingredientCount -= unnecessary.size();
					ingredientTierCount = Math.max(3, Math.min(10, (ingredientCount + 79) / 80));
					splitTierCount = Math.max(ingredientTierCount, Math.max(5, Math.min(10, (foodCount + 39) / 40)));
					SplitTierPlanner candidatePlanner = planner;
					java.util.Set<String> retainedNodes = new java.util.HashSet<>();
					java.util.ArrayDeque<String> retainedPending = new java.util.ArrayDeque<>(catalog.keySet());
					retainedPending.addAll(aliases.keySet());
					while (!retainedPending.isEmpty()) {
						String id = retainedPending.removeFirst();
						if (!retainedNodes.add(id))
							continue;
						if (aliases.containsKey(id))
							retainedPending.addLast(aliases.get(id));
						for (var value : recipeData.get(id))
							for (var slot : value.getAsJsonObject().getAsJsonArray("ingredientSlots"))
								for (var input : slot.getAsJsonArray())
									retainedPending.addLast(input.getAsString());
					}
					measureSourceFloors();
					measureKitchenWork();
					measureFoodUses();
					measureIngredientRanks();
					measureFoundationalPromotions();
					planner = new SplitTierPlanner(ingredientTierCount, splitTierCount);
					for (var old : candidatePlanner.nodes.values()) {
						if (!retainedNodes.contains(old.id))
							continue;
						boolean removed = "INGREDIENT".equals(old.kind) && !catalog.containsKey(old.id);
						String kind = removed ? (old.routes.isEmpty() ? "EXTERNAL" : "INTERMEDIATE") : old.kind;
						int minimum = sourceMinimum(old.id, "INGREDIENT".equals(kind) ? ingredientTierCount : splitTierCount);
						if ("INGREDIENT".equals(kind))
							minimum = ingredientAccessMinimum(old.id, minimum);
						double finalRank = catalog.containsKey(old.id) ? ("INGREDIENT".equals(kind) ? ingredientRanks.get(old.id) : rank(old.id, kind, catalog.get(old.id))) : 0.0;
						var node = planner.add(old.id, kind, minimum, finalRank, old.canServe);
						node.priority = old.priority;
						node.aliasOf = old.aliasOf;
						node.starterEligible = old.starterEligible;
						if ("INGREDIENT".equals(kind) && node.priority == 0 && foundationalPromotions.contains(node.id))
							node.priority = -1;
						if ("FOOD".equals(kind) && basicFoods.contains(node.id))
							node.priority = -1;
						node.routes.addAll(old.routes);
					}
					planner.build();
					var conditionFree = conditionFreeNodes();
					var conditionalFoods = new com.google.gson.JsonArray();
					com.google.gson.JsonObject graph = new com.google.gson.JsonObject();
					com.google.gson.JsonObject ingredientTiers = new com.google.gson.JsonObject(), foodTiers = new com.google.gson.JsonObject();
					for (int tier = 0; tier < ingredientTierCount; tier++)
						ingredientTiers.add("" + tier, new com.google.gson.JsonArray());
					for (int tier = 0; tier < splitTierCount; tier++)
						foodTiers.add("" + tier, new com.google.gson.JsonArray());
					com.google.gson.JsonArray starters = new com.google.gson.JsonArray(), unresolved = new com.google.gson.JsonArray();
					com.google.gson.JsonArray external = new com.google.gson.JsonArray(), auxiliaries = new com.google.gson.JsonArray();
					com.google.gson.JsonObject blockedInputs = new com.google.gson.JsonObject();
					com.google.gson.JsonArray roleReview = new com.google.gson.JsonArray();
					java.util.List<SplitTierPlanner.Node> ordered = new java.util.ArrayList<>(planner.nodes.values());
					ordered.sort(java.util.Comparator.comparingInt((SplitTierPlanner.Node n) -> n.tier).thenComparingDouble(n -> n.rank).thenComparing(n -> n.id));
					for (var node : ordered) {
						if (planner.isCatalog(node)) {
							var original = catalog.get(node.id);
							var entry = new com.google.gson.JsonObject();
							entry.addProperty("id", node.id);
							entry.addProperty("role", node.kind);
							entry.addProperty("tier", node.tier);
							entry.addProperty("can_serve", node.canServe);
							entry.addProperty("edible", original.get("edible").getAsBoolean());
							entry.addProperty("score", original.has("score") ? original.get("score").getAsDouble() : node.rank);
							entry.addProperty("qualityScore", original.has("qualityScore") ? original.get("qualityScore").getAsDouble() : 0);
							boolean starter = "INGREDIENT".equals(node.kind) && node.canServe && node.starterEligible && node.tier == 0;
							entry.addProperty("starter_eligible", starter);
							boolean conditional = "FOOD".equals(node.kind) && node.preparationTier != SplitTierPlanner.UNREACHABLE && !conditionFree.contains(node.id);
							entry.addProperty("unlock_status", node.preparationTier == SplitTierPlanner.UNREACHABLE ? "unresolved_recipe" : conditional ? "external_conditions_required" : "available_after_requirements");
							if (conditional)
								conditionalFoods.add(node.id);
							entry.add("recipes", recipeData.get(node.id));
							var analysis = new com.google.gson.JsonObject();
							analysis.addProperty("ranking_score", node.rank);
							if ("INGREDIENT".equals(node.kind))
								analysis.addProperty("ranking_base_score", ingredientBaseRanks.get(node.id));
							analysis.addProperty("startup_priority", node.priority);
							analysis.addProperty("requires_external_source_validation", conditional);
							if (kitchenCosts.containsKey(node.id))
								analysis.addProperty("kitchen_cost", kitchenCosts.get(node.id));
							analysis.addProperty("kitchen_depth", kitchenDepths.getOrDefault(node.id, 0));
							analysis.addProperty("kitchen_work_status", kitchenCosts.containsKey(node.id) ? "estimated_from_supported_routes_or_raw_source" : "unsupported_or_blocked_route");
							analysis.addProperty("structural_recipe_score", scoreResolver.recipeScoreCache.getOrDefault(node.id, 0.0));
							analysis.addProperty("source_minimum_tier", sourceMinimum(node.id, "INGREDIENT".equals(node.kind) ? ingredientTierCount : splitTierCount));
							analysis.addProperty("declared_source_minimum_tier", acquisitionSources.minimumTier(node.id, "INGREDIENT".equals(node.kind) ? ingredientTierCount : splitTierCount));
							analysis.addProperty("inferred_source_minimum_tier", sourceFloors.getOrDefault(node.id, 0));
							analysis.addProperty("access_minimum_tier", node.minimum);
							if ("INGREDIENT".equals(node.kind)) {
								if ("minecraft:apple".equals(node.id) || "minecraft:carrot".equals(node.id))
									analysis.addProperty("startup_profile", "basic_supply_preference");
								else if ("minecraft:rabbit".equals(node.id) || "minecraft:honey_bottle".equals(node.id))
									analysis.addProperty("startup_profile", "after_startup");
							}
							analysis.addProperty("recipe_requirement_tier", node.preparationTier == SplitTierPlanner.UNREACHABLE ? -1 : node.preparationTier);
							analysis.addProperty("food_uses", foodUses.getOrDefault(node.id, 0));
							if (original.has("role_reason"))
								analysis.add("classification", original.get("role_reason"));
							if ("FOOD".equals(node.kind) && node.preparationTier == SplitTierPlanner.UNREACHABLE)
								analysis.addProperty("requirement_issue",
										unverifiedAcquisition.contains(node.id) ? "no_supported_recipe_or_verified_acquisition" : recipeData.get(node.id).size() == 0 ? "no_supported_recipe_exported" : "blocked_or_cyclic_input");
							if (node.id.equals("ad_astra:cheese"))
								analysis.addProperty("compatibility_source_profile", "ad_astra_moon_cheese");
							if (original.has("role_reason") && "no_culinary_recipe".equals(original.get("role_reason").getAsString()))
								roleReview.add(node.id);
							if (original.has("acquisition"))
								analysis.add("acquisition", original.get("acquisition"));
							entry.add("analysis", analysis);
							("INGREDIENT".equals(node.kind) ? ingredientTiers : foodTiers).getAsJsonArray("" + node.tier).add(entry);
							if (starter)
								starters.add(node.id);
							if (node.preparationTier == SplitTierPlanner.UNREACHABLE)
								unresolved.add(node.id);
						}
						com.google.gson.JsonObject graphNode = new com.google.gson.JsonObject();
						graphNode.addProperty("kind", node.kind);
						if ("ALIAS".equals(node.kind))
							graphNode.addProperty("discovery_id", node.aliasOf);
						graphNode.addProperty("minimum_tier", node.minimum);
						if (planner.isCatalog(node))
							graphNode.addProperty("tier", node.tier);
						graphNode.addProperty("ingredient_unlock_required", "INGREDIENT".equals(node.kind));
						graphNode.add("recipes", recipeData.get(node.id));
						graph.add(node.id, graphNode);
						if ("EXTERNAL".equals(node.kind))
							external.add(node.id);
						if ("AUXILIARY".equals(node.kind))
							auxiliaries.add(node.id);
						if ("BLOCKED".equals(node.kind)) {
							String reason = itemsById.containsKey(node.id) ? "unresolved_culinary_input" : "unregistered_item";
							if (excludedFoods.contains(node.id) || classificationRules.excludedItems.contains(node.id))
								reason = "explicit_exclusion";
							for (var value : excludedFoodsArray) {
								var excluded = value.getAsJsonObject();
								if (node.id.equals(excluded.get("id").getAsString()) && excluded.has("reason"))
									reason = excluded.get("reason").getAsString();
							}
							blockedInputs.addProperty(node.id, reason);
						}
					}
					double tierStep = splitTierCount > 1 ? 90.0 / (splitTierCount - 1) : 90.0;
					for (int tier = 0; tier < splitTierCount; tier++) {
						java.util.List<com.google.gson.JsonObject> tierItems = new java.util.ArrayList<>();
						if (ingredientTiers.has("" + tier))
							for (var item : ingredientTiers.getAsJsonArray("" + tier))
								tierItems.add(item.getAsJsonObject());
						for (var item : foodTiers.getAsJsonArray("" + tier))
							tierItems.add(item.getAsJsonObject());
						double minimum = tierItems.stream().filter(item -> item.get("can_serve").getAsBoolean()).mapToDouble(item -> item.get("score").getAsDouble()).min().orElse(0);
						double maximum = tierItems.stream().filter(item -> item.get("can_serve").getAsBoolean()).mapToDouble(item -> item.get("score").getAsDouble()).max().orElse(minimum);
						for (var item : tierItems) {
							double score = item.get("score").getAsDouble();
							double cap = tier == splitTierCount - 1 ? tierStep * 3.0 : Math.max(0, tierStep - 1.0);
							int baseExp = item.get("can_serve").getAsBoolean() ? (int) Math.round(10 + tier * tierStep + Math.min(Math.max(0, score - minimum), cap)) : 0;
							item.addProperty("base_exp", baseExp);
							item.addProperty("score_position", maximum > minimum ? Math.max(0, Math.min(1, (score - minimum) / (maximum - minimum))) : 0.5);
						}
					}
					com.google.gson.JsonObject result = new com.google.gson.JsonObject();
					result.addProperty("schema_version", 7);
					result.addProperty("catalog_integration_version", 1);
					result.addProperty("split_generator_version", 11);
					result.addProperty("mode", "split_catalog");
					result.addProperty("generated_at_epoch_ms", System.currentTimeMillis());
					result.addProperty("source_commit", "414d8821558f7de0c22ef0763f16944c5657c59a");
					result.addProperty("tier_count", splitTierCount);
					result.addProperty("ingredient_tier_count", ingredientTierCount);
					result.addProperty("food_tier_count", splitTierCount);
					result.addProperty("ingredient_count", ingredientCount);
					result.addProperty("food_count", foodCount);
					result.addProperty("item_count", catalog.size());
					double discoveryExpRequired = Math.max(3.0, Math.min(10.0, Math.ceil(10.0 * Math.sqrt(100.0 / Math.max(1, foodCount)))));
					result.addProperty("recipe_discovery_exp_required", discoveryExpRequired);
					result.addProperty("variant_count", aliases.size());
					result.addProperty("registry_item_count", catalog.size() + aliases.size());
					result.add("discovery_variants", discoveryVariants);
					result.addProperty("non_edible_ingredient_count", additions.size());
					result.addProperty("unsafe_ingredient_count", unsafeIngredients.size());
					result.addProperty("tier_numbering", "zero_based; displayed tier = tier + 1; both groups share the FOOD stage scale");
					com.google.gson.JsonObject ingredientsGroup = new com.google.gson.JsonObject(), foodsGroup = new com.google.gson.JsonObject();
					ingredientsGroup.addProperty("count", ingredientCount);
					ingredientsGroup.addProperty("tier_count", ingredientTierCount);
					ingredientsGroup.add("tiers", ingredientTiers);
					foodsGroup.addProperty("count", foodCount);
					foodsGroup.addProperty("tier_count", splitTierCount);
					foodsGroup.add("tiers", foodTiers);
					result.add("ingredients", ingredientsGroup);
					result.add("foods", foodsGroup);
					result.add("starter_options", starters);
					result.addProperty("starter_choices_required", Math.min(3, starters.size()));
					result.addProperty("starter_status", starters.size() >= 3 ? "ready" : (starters.isEmpty() ? "blocked_no_servable_starters" : "reduced_pool"));
					com.google.gson.JsonObject diagnostics = new com.google.gson.JsonObject();
					for (String role : java.util.List.of("INGREDIENT", "FOOD")) {
						com.google.gson.JsonArray sizes = new com.google.gson.JsonArray();
						for (int size : planner.groupSizes.get(role))
							sizes.add(size);
						diagnostics.add(role.toLowerCase(java.util.Locale.ROOT) + "_tier_sizes", sizes);
					}
					diagnostics.add("unresolved_foods", unresolved);
					diagnostics.add("external_requirements", external);
					diagnostics.add("automatic_auxiliaries", auxiliaries);
					diagnostics.add("filtered_ingredients", filteredIngredients);
					com.google.gson.JsonObject roleChanges = new com.google.gson.JsonObject();
					for (var correction : correctedRoles.entrySet())
						roleChanges.addProperty(correction.getKey(), correction.getValue());
					diagnostics.add("role_corrections", roleChanges);
					diagnostics.addProperty("food_rank_reference_p75", foodRankReference);
					diagnostics.add("blocked_inputs", blockedInputs);
					diagnostics.add("role_review_no_recipe", roleReview);
					diagnostics.add("source_adapters", splitSources.diagnostics());
					diagnostics.add("conditional_source_foods", conditionalFoods);
					diagnostics.addProperty("starter_policy",
							"Only serveable unprocessed basic-supply profiles qualify, preserving the four verified native starter choices as other raw ingredients move earlier. Serving eligibility remains independent.");
					var unverified = new com.google.gson.JsonArray();
					for (String id : unverifiedAcquisition)
						if (catalog.containsKey(id))
							unverified.add(id);
					diagnostics.add("unverified_acquisition_foods", unverified);
					var intermediateIds = new com.google.gson.JsonArray();
					for (var node : planner.nodes.values())
						if ("INTERMEDIATE".equals(node.kind))
							intermediateIds.add(node.id);
					diagnostics.add("preparation_intermediates", intermediateIds);
					diagnostics.addProperty("acquisition_evidence_policy",
							"Food without a supported recipe requires an estimated source with explicit system and representable requirements. Assumed base, unrecognized, disabled and unsupported source routes never create free preparation paths.");
					diagnostics.addProperty("source_floor_policy",
							"Culinary processing propagates the easiest complete route's source minimum through every mandatory slot; alternatives do not all need to be late. Ad Astra cheese, cheese block and Moon cheese ore share an explicit late source profile. Original acquisition estimates remain separately exported.");
					diagnostics.addProperty("graph_boundary_policy", "Only known culinary products, prepared portion sources and pantry processing are expanded. Raw acquisition, empty containers, tools and technical materials stop traversal.");
					diagnostics.addProperty("ranking_policy",
							"Ingredients: source effort, culinary work/usefulness and easiest complete input-route rank floor. Pantry priority is limited to basic flour/dough/salt/sugar/pasta with supported low-cost routes and zero source floor. Basic vanilla supplies include eggs, milk and sugar cane. Ingredient input floors affect ordering, not a new requirement to discover upstream ingredients. Food ranking formula is unchanged.");
					diagnostics.addProperty("variant_policy",
							"Known colored Chromacarvings pumpkins and Herbs & Harvest plate decorations share a discovery. Physical recipe IDs are preserved through ALIAS nodes. Explicit role overrides are not grouped.");
					diagnostics.addProperty("seed_policy",
							"Pam propagation seeditem discoveries require explicit singleton culinary use; broad tag use is insufficient. Ordinary edible seed crops are retained. Unapproved propagation inputs remain BLOCKED, never free EXTERNAL requirements.");
					diagnostics.addProperty("requirements_policy", "Every recipe slot needs one usable alternative. FOOD intermediates are expanded; ingredient nodes require discovery. Unanchored cycles remain unresolved.");
					diagnostics.addProperty("live_catalog", true);
					com.google.gson.JsonObject technical = new com.google.gson.JsonObject();
					technical.add("diagnostics", diagnostics);
					technical.add("recipe_nodes", graph);
					result.add("technical", technical);
					return result;
				}

				String summary(com.google.gson.JsonObject database) {
					StringBuilder text = new StringBuilder("OMNICHEF — KATALOG INGREDIENTS / FOOD v11\nTiery ponizej sa numerowane od 1. W JSON indeksy zaczynaja sie od 0.\n\n");
					text.append("Ingredients: ").append(database.get("ingredient_count")).append(" / tiery: ").append(database.get("ingredient_tier_count")).append('\n');
					text.append("Food: ").append(database.get("food_count")).append(" / tiery: ").append(database.get("food_tier_count")).append("\n\n");
					text.append("Warianty wspolnych odkryc: ").append(database.get("variant_count")).append("\n\n");
					for (String key : java.util.List.of("ingredients", "foods")) {
						text.append(key.toUpperCase(java.util.Locale.ROOT)).append('\n');
						for (var tier : database.getAsJsonObject(key).getAsJsonObject("tiers").entrySet()) {
							text.append("Tier ").append(Integer.parseInt(tier.getKey()) + 1).append(" (pozycji: ").append(tier.getValue().getAsJsonArray().size()).append(")\n");
							for (var value : tier.getValue().getAsJsonArray()) {
								var item = value.getAsJsonObject();
								text.append("  ").append(item.get("id").getAsString());
								text.append(item.get("can_serve").getAsBoolean() ? " | mozna podawac" : " | tylko do receptur");
								if (item.get("starter_eligible").getAsBoolean())
									text.append(" | wybor startowy");
								if (database.getAsJsonObject("discovery_variants").has(item.get("id").getAsString()))
									text.append(" | warianty: ").append(database.getAsJsonObject("discovery_variants").get(item.get("id").getAsString()));
								if ("unresolved_recipe".equals(item.get("unlock_status").getAsString()))
									text.append(" | UWAGA: nierozwiazana receptura");
								if ("external_conditions_required".equals(item.get("unlock_status").getAsString()))
									text.append(" | warunki zrodla do sprawdzenia");
								text.append('\n');
							}
							text.append('\n');
						}
					}
					text.append("Nierozwiazane potrawy: ").append(database.getAsJsonObject("technical").getAsJsonObject("diagnostics").get("unresolved_foods")).append('\n');
					text.append("Food bez receptury lub potwierdzonej sciezki pozyskania: ").append(database.getAsJsonObject("technical").getAsJsonObject("diagnostics").get("unverified_acquisition_foods")).append('\n');
					text.append("Pominiete odkrycia bez zastosowania: ").append(database.getAsJsonObject("technical").getAsJsonObject("diagnostics").get("filtered_ingredients")).append('\n');
					text.append("Gra korzysta z tego katalogu w FoodDatabase.json. Oddzielny postep Ingredients/Food jest kolejnym etapem integracji.\n");
					return text.toString();
				}
			}
			java.nio.file.Path splitPreviewFile = net.neoforged.fml.loading.FMLPaths.GAMEDIR.get().resolve("config/omnichef/" + net.mcreator.omnichef.network.OmnichefModVariables.MapVariables.get(world).FoodDatabase_File_Name);
			java.nio.file.Path splitPreviewTemp = null;
			try {
				FoodCatalogBuilder splitBuilder = new FoodCatalogBuilder();
				com.google.gson.JsonObject splitPreview = splitBuilder.build();
				java.nio.file.Files.createDirectories(splitPreviewFile.getParent());
				splitPreviewTemp = java.nio.file.Files.createTempFile(splitPreviewFile.getParent(), "FoodDatabase-", ".tmp");
				java.nio.file.Files.writeString(splitPreviewTemp, new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(splitPreview), java.nio.charset.StandardCharsets.UTF_8);
				try {
					java.nio.file.Files.move(splitPreviewTemp, splitPreviewFile, java.nio.file.StandardCopyOption.ATOMIC_MOVE, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
				} catch (java.nio.file.AtomicMoveNotSupportedException unsupported) {
					java.nio.file.Files.move(splitPreviewTemp, splitPreviewFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
				}
				net.mcreator.omnichef.network.OmnichefModVariables.MapVariables.get(world).RecipeDiscoveryExpRequired = splitPreview.get("recipe_discovery_exp_required").getAsDouble();
				net.mcreator.omnichef.network.OmnichefModVariables.MapVariables.get(world).markSyncDirty();
				java.nio.file.Path summaryFile = splitPreviewFile.resolveSibling("FoodDatabase_summary.txt");
				java.nio.file.Files.writeString(summaryFile, splitBuilder.summary(splitPreview), java.nio.charset.StandardCharsets.UTF_8);
				for (String obsolete : java.util.List.of("FoodDatabase_split_test.json", "FoodDatabase_split_test_summary.txt")) {
					try {
						java.nio.file.Files.deleteIfExists(splitPreviewFile.resolveSibling(obsolete));
					} catch (java.io.IOException cleanup) {
						net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Could not remove obsolete catalog preview {}.", obsolete, cleanup);
					}
				}
				net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef split catalog: {} ingredients ({} non-edible), {} foods, {} starter options; saved to {}.", splitPreview.get("ingredient_count"), splitPreview.get("non_edible_ingredient_count"),
						splitPreview.get("food_count"), splitPreview.getAsJsonArray("starter_options").size(), splitPreviewFile);
				if (!"ready".equals(splitPreview.get("starter_status").getAsString()))
					net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef split catalog: starter pool status is {}.", splitPreview.get("starter_status"));
			} catch (Exception splitPreviewError) {
				throw new IllegalStateException("Omnichef catalog generation failed; startup stopped before using stale catalog data.", splitPreviewError);
			} finally {
				if (splitPreviewTemp != null)
					try {
						java.nio.file.Files.deleteIfExists(splitPreviewTemp);
					} catch (java.io.IOException cleanupError) {
						net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Could not remove split catalog temporary file {}.", splitPreviewTemp, cleanupError);
					}
			}
		}
	}
}