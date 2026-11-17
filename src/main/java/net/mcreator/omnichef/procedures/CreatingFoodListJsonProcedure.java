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
			final Thread catalogGenerationThread = Thread.currentThread();
			class CatalogGenerationDiagnostics implements java.lang.AutoCloseable {
				final long started = System.nanoTime();
				volatile long phaseStarted = started;
				volatile String stage = "initialization";
				volatile String current = "";
				volatile int pass = -1;
				volatile boolean closed = false;
				final java.util.concurrent.ScheduledExecutorService timer = java.util.concurrent.Executors.newSingleThreadScheduledExecutor(task -> {
					Thread thread = new Thread(task, "Omnichef-catalog-diagnostics");
					thread.setDaemon(true);
					return thread;
				});

				void start() {
					timer.scheduleAtFixedRate(this::heartbeat, 15, 15, java.util.concurrent.TimeUnit.SECONDS);
				}

				void phase(String name) {
					net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef catalog v34: finished stage={} in {} ms; next={}", stage, (System.nanoTime() - phaseStarted) / 1000000L, name);
					stage = name;
					phaseStarted = System.nanoTime();
					current = "";
					pass = -1;
				}

				void heartbeat() {
					if (closed)
						return;
					try {
						long now = System.nanoTime();
						net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef catalog heartbeat: stage={}, stage_seconds={}, total_seconds={}, pass={}, current={}, server_thread={}", stage, (now - phaseStarted) / 1000000000L,
								(now - started) / 1000000000L, pass, current, catalogGenerationThread.getState());
						// A separate daemon can report the server stack even when the current operation stops making progress.
						if (now - phaseStarted >= 30000000000L) {
							StackTraceElement[] stack = catalogGenerationThread.getStackTrace();
							StringBuilder trace = new StringBuilder();
							for (int i = 0; i < Math.min(14, stack.length); i++)
								trace.append("\n    at ").append(stack[i]);
							net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef catalog slow-stage sample: stage={}, pass={}, current={}{}", stage, pass, current, trace);
						}
					} catch (RuntimeException failure) {
						// Diagnostics must never interrupt generation or inspect mutable game data.
						net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef catalog diagnostic sample failed: {}", failure.toString());
					}
				}

				public void close() {
					closed = true;
					timer.shutdownNow();
					net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef catalog diagnostics stopped: stage={}, total_ms={}", stage, (System.nanoTime() - started) / 1000000L);
				}
			}
			final CatalogGenerationDiagnostics catalogDiagnostics = new CatalogGenerationDiagnostics();
			catalogDiagnostics.start();
			try {
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
					final java.util.Map<String, Boolean> technicalFlags = new java.util.HashMap<>();
					final java.util.Set<String> technicalProcessingItems = new java.util.TreeSet<>();
					final java.util.List<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> supplementalIngredientTags = new java.util.ArrayList<>();
					final java.util.Map<String, java.util.Set<String>> itemTagNames = new java.util.HashMap<>();
					final java.util.Map<String, java.util.Set<String>> tagFallbackMatches = new java.util.TreeMap<>();

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
						config.add("ingredient_tags", strings("c:foods/raw_meat", "c:foods/raw_fish", "c:foods/fruit", "c:foods/vegetable", "c:foods/berry", "c:foods/dough", "c:crops", "c:eggs", "c:drinks/milk", "c:buckets/milk", "c:flours",
								"c:flour", "c:dough", "c:doughs"));
						config.add("prepared_tags", strings("c:foods/bread", "c:foods/cookie", "c:foods/soup", "c:foods/cooked_meat", "c:foods/cooked_fish", "c:foods/pie", "c:foods/candy", "c:foods/golden"));
						config.add("culinary_tags", strings("c:foods", "c:crops", "c:eggs", "c:drinks/milk", "c:buckets/milk", "c:flours", "c:flour", "c:dough", "c:doughs", "c:sugar", "c:sugars", "c:salt", "c:salts", "c:spices"));
						config.add("culinary_items", strings("minecraft:wheat", "minecraft:cocoa_beans", "minecraft:sugar", "minecraft:milk_bucket", "minecraft:egg"));
						config.add("culinary_recipe_types", strings("*:crafting", "*:smelting", "*:smoking", "*:campfire_cooking", "*:blasting", "farmersdelight:cooking", "farmersdelight:cutting", "create:mixing", "create:compacting",
								"create:filling", "create:cutting", "*:cooking", "*:baking", "*:frying", "*:toaster_heating", "*:microwave_heating", "*:oven_baking", "*:frying_pan_cooking", "*:cutting_board_combining", "*:cutting_board_slicing"));
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
						// Common legacy culinary families supplement config without rewriting the user's file.
						for (String name : java.util.List.of("c:milks", "c:milk", "c:milk_bottles", "c:spices", "c:pepper", "c:peppers", "c:vanilla", "c:cinnamon", "c:paprika", "c:ginger", "c:mint", "c:hops", "c:tea_leaves", "c:grains", "c:rice",
								"c:rices", "c:seeds")) {
							var tag = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse(name));
							if (!supplementalIngredientTags.contains(tag))
								supplementalIngredientTags.add(tag);
							if (!culinaryTags.contains(tag))
								culinaryTags.add(tag);
						}
						for (String name : java.util.List.of("c:creams", "c:cheeses", "c:butters", "c:caramels", "c:noodles", "c:water_bottles")) {
							var tag = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse(name));
							if (!culinaryTags.contains(tag))
								culinaryTags.add(tag);
						}
						culinaryIds.addAll(stringList(config, "culinary_items"));
						for (String id : java.util.List.of("farm_and_charm:wild_nettle", "farm_and_charm:wild_ribwort"))
							if (itemsById.containsKey(id))
								culinaryIds.add(id);
						allowedTypes.addAll(stringList(config, "culinary_recipe_types"));
						cuttingTypes.addAll(stringList(config, "cutting_recipe_types"));
						for (String method : java.util.List.of("farm_and_charm:pot_cooking", "farm_and_charm:crafting_bowl", "farm_and_charm:stove", "farm_and_charm:roaster", "farm_and_charm:mincer", "farm_and_charm:drying",
								"farm_and_charm:cutting_board", "farm_and_charm:cutting_board_assembly", "bakery:baking_station", "bakery:jam_cooking", "omnichef:bakery_assembly", "culturaldelights:aging", "create:milling"))
							if (!allowedTypes.contains(method))
								allowedTypes.add(method);
						for (String method : java.util.List.of("farm_and_charm:cutting_board", "farm_and_charm:mincer"))
							if (!cuttingTypes.contains(method))
								cuttingTypes.add(method);
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

					boolean technicalIntermediate(String id) {
						boolean technical = technicalFlags.computeIfAbsent(id, key -> key.startsWith("delightfulcreators:incomplete_") || hasClass(key, "com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem"));
						if (technical)
							technicalProcessingItems.add(id);
						return technical;
					}

					final java.util.Map<String, Boolean> externalMaterialFlags = new java.util.HashMap<>();
					final java.util.Set<String> servingContainers = new java.util.TreeSet<>();
					boolean servingContainersIndexed = false;
					final java.util.List<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> materialTags = new java.util.ArrayList<>();
					final java.util.List<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> toolTags = java.util.List.of(
							net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("c:tools")),
							net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("minecraft:tools")));
					final java.util.List<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> botanicalTags = java.util.List
							.of(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("minecraft:flowers")));
					final java.util.List<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> rawMaterialTags = java.util.List
							.of(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("c:raw_materials")));
					final java.util.List<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> dyeRawMaterialTags = java.util.List
							.of(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("c:raw_materials/dyes")));

					boolean botanical(String id) {
						return matchesTags(id, botanicalTags);
					}

					void indexMaterialTags() {
						if (!materialTags.isEmpty())
							return;
						for (String name : java.util.List.of("c:ingots", "c:nuggets", "c:gems", "c:ores", "c:glass_blocks", "c:glass_panes", "c:stones", "c:cobblestones", "c:strings", "c:rods/wooden", "minecraft:leaves", "c:bones", "c:bone_meals",
								"c:dusts/bone"))
							materialTags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse(name)));
					}

					boolean externalMaterial(String id) {
						return externalMaterialFlags.computeIfAbsent(id, key -> {
							var item = itemsById.get(key);
							if (item == null || overrides.containsKey(key) || pantryItems.contains(key) || culinaryIds.contains(key) || preparedItems.contains(key) || preparedPortions.contains(key))
								return false;
							var food = item.getFoodProperties(new net.minecraft.world.item.ItemStack(item), null);
							// FoodProperties alone does not imply useful nutrition (some technical items have zeroes).
							if (food != null && (food.nutrition() > 0 || food.saturation() > 0 || zeroNutritionAllowed.contains(key)))
								return false;
							if (matchesTags(key, ingredientTags) || matchesTags(key, supplementalIngredientTags) || botanical(key))
								return false;
							indexMaterialTags();
							return java.util.Set.of("minecraft:gold_ingot", "minecraft:gold_nugget", "minecraft:bone", "minecraft:bone_meal", "minecraft:ice", "minecraft:packed_ice", "minecraft:blue_ice", "minecraft:snowball").contains(key)
									|| matchesTags(key, materialTags) || matchesTags(key, rawMaterialTags) && !matchesTags(key, dyeRawMaterialTags) && !matchesTags(key, culinaryTags);
						});
					}

					void indexServingContainers() {
						if (servingContainersIndexed)
							return;
						servingContainersIndexed = true;
						for (var item : itemsById.values()) {
							var stack = new net.minecraft.world.item.ItemStack(item);
							var food = item.getFoodProperties(stack, null);
							String source = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).toString();
							if (food == null && !isCulinary(source))
								continue;
							try {
								if (stack.hasCraftingRemainingItem())
									registerServingContainer(stack.getCraftingRemainingItem());
							} catch (RuntimeException unsupported) {
								// Custom crafting-remainder implementations may need a populated stack.
							}
							if (food == null)
								continue;
							try {
								Object value = food.getClass().getMethod("usingConvertsTo").invoke(food);
								if (!(value instanceof java.util.Optional<?> result) || !(result.orElse(null) instanceof net.minecraft.world.item.ItemStack remainder) || remainder.isEmpty())
									continue;
								registerServingContainer(remainder);
							} catch (ReflectiveOperationException | RuntimeException unsupported) {
								// Optional conversion APIs cannot turn an unknown mandatory slot into a free supply.
							}
						}
					}

					void registerServingContainer(net.minecraft.world.item.ItemStack remainder) {
						if (remainder == null || remainder.isEmpty())
							return;
						String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(remainder.getItem()).toString();
						if (!itemsById.containsKey(id) || isEdible(id) || overrides.containsKey(id) || pantryItems.contains(id) || culinaryIds.contains(id) || matchesTags(id, ingredientTags) || matchesTags(id, supplementalIngredientTags))
							return;
						servingContainers.add(id);
					}

					boolean servingContainer(String id) {
						indexServingContainers();
						return servingContainers.contains(id);
					}

					boolean equipment(String id) {
						return java.util.Set.of("pamhc2foodcore:potitem", "pamhc2foodcore:skilletitem", "pamhc2foodcore:cuttingboarditem", "pamhc2foodcore:grinderitem", "pamhc2foodcore:bakewareitem", "pamhc2foodcore:saucepanitem",
								"pamhc2foodcore:mixingbowlitem", "pamhc2foodcore:juiceritem", "pamhc2foodcore:rolleritem", "croptopia:cooking_pot", "croptopia:frying_pan", "croptopia:food_press", "croptopia:knife", "croptopia:mortar_and_pestle")
								.contains(id) || !isEdible(id) && !isCulinary(id) && matchesTags(id, toolTags);
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
						var item = itemsById.get(id);
						if (item == null)
							return false;
						var stack = new net.minecraft.world.item.ItemStack(item);
						var actual = itemTagNames.computeIfAbsent(id, key -> {
							java.util.Set<String> names = new java.util.TreeSet<>();
							stack.getTags().forEach(tag -> names.add(tag.location().toString()));
							return names;
						});
						for (var tag : tags) {
							if (stack.is(tag))
								return true;
							String expected = tag.location().toString();
							for (String child : actual)
								if (child.startsWith(expected + "/")) {
									tagFallbackMatches.computeIfAbsent(id, key -> new java.util.TreeSet<>()).add(expected + " <- " + child);
									return true;
								}
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
							if (matchesTags(key, ingredientTags) || !isEdible(key) && matchesTags(key, supplementalIngredientTags)) {
								hintReasons.put(key, "ingredient_tag");
								return "INGREDIENT";
							}
							return "";
						});
					}

					boolean isCulinary(String id) {
						return culinary.computeIfAbsent(id, key -> isEdible(key) || "FOOD".equals(hint(key)) || "INGREDIENT".equals(hint(key)) || culinaryIds.contains(key) || matchesTags(key, culinaryTags));
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
							boolean feast = false, bakeryPie = false;
							for (Class<?> type = block.getClass(); type != null; type = type.getSuperclass()) {
								feast |= type.getName().equals("vectorwing.farmersdelight.common.block.FeastBlock");
								bakeryPie |= type.getName().equals("net.satisfy.bakery.core.block.cake.PieBlock") || type.getName().equals("net.satisfy.bakery.core.block.PieBlock");
							}
							if (!feast && !bakeryPie)
								continue;
							try {
								int servings = ((Number) block.getClass().getMethod(bakeryPie ? "getMaxCuts" : "getMaxServings").invoke(block)).intValue();
								Object value = bakeryPie
										? block.getClass().getMethod("getPieSliceItem").invoke(block)
										: block.getClass().getMethod("getServingItem", net.minecraft.world.level.block.state.BlockState.class).invoke(block, block.defaultBlockState());
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
					int sushi = 0, portions = 0, warnings = 0, bakeryInteractions = 0;
					final com.google.gson.JsonArray bakeryIssues = new com.google.gson.JsonArray();

					void add(String output, Dish dish) {
						dishes.computeIfAbsent(output, key -> new java.util.ArrayList<>()).add(dish);
					}

					void bakeryLegacyInteractions() {
						boolean legacyStation = false;
						for (var block : net.minecraft.core.registries.BuiltInRegistries.BLOCK)
							if (block.getClass().getName().equals("net.satisfy.bakery.core.block.cake.BlankCakeBlock"))
								legacyStation = true;
						for (var holder : recipeManager.getRecipes()) {
							if (!"bakery:blank_cake_interaction".equals(classificationRules.typeId(holder)))
								continue;
							try {
								if (!legacyStation)
									throw new IllegalArgumentException("Unsupported Bakery station state model");
								var recipe = holder.value();
								String stage = recipe.getClass().getMethod("stage").invoke(recipe).toString();
								Object result = recipe.getClass().getMethod("result").invoke(recipe);
								Object target = result.getClass().getMethod("setBlock").invoke(result);
								// Pure state/tool transitions carry no new culinary input and do not make a finished dish.
								if (target == null)
									continue;
								if (!java.util.Set.of("CAKE", "CUPCAKE", "COOKIE").contains(stage) || !Boolean.TRUE.equals(result.getClass().getMethod("consumeOne").invoke(result)))
									throw new IllegalArgumentException("Unsupported Bakery completion interaction");
								if (!(target instanceof net.minecraft.resources.ResourceLocation blockId))
									throw new IllegalArgumentException("Invalid Bakery completion block");
								Object rawIngredient = recipe.getClass().getMethod("ingredient").invoke(recipe);
								if (!(rawIngredient instanceof net.minecraft.world.item.crafting.Ingredient ingredient))
									throw new IllegalArgumentException("Invalid Bakery ingredient");
								java.util.Set<String> alternatives = new java.util.TreeSet<>();
								for (var stack : ingredient.getItems())
									if (!stack.isEmpty()) {
										String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
										if (!id.equals("minecraft:barrier") && itemsById.containsKey(id))
											alternatives.add(id);
									}
								if (alternatives.isEmpty())
									alternatives.add("omnichef:unresolved_item_requirement");
								if (!itemsById.containsKey("bakery:cake_dough"))
									throw new IllegalArgumentException("Missing Bakery base dough");
								var slots = java.util.List.of(java.util.List.of("bakery:cake_dough"), new java.util.ArrayList<>(alternatives));
								String output;
								int count;
								if ("CAKE".equals(stage)) {
									var targetBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getOptional(blockId).orElseThrow();
									output = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(targetBlock.asItem()).toString();
									count = 1;
								} else {
									// These physical block -> four-items mappings are implemented by Bakery 2.1.6 itself.
									var physical = java.util.Map.of("bakery:apple_cupcake_block", "bakery:apple_cupcake", "bakery:strawberry_cupcake_block", "bakery:strawberry_cupcake", "bakery:sweetberry_cupcake_block", "bakery:sweetberry_cupcake",
											"bakery:chocolate_cookie_block", "bakery:chocolate_glazed_cookie", "bakery:strawberry_cookie_block", "bakery:strawberry_glazed_cookie", "bakery:sweetberry_cookie_block", "bakery:sweetberry_glazed_cookie");
									output = physical.get(blockId.toString());
									count = 4;
								}
								if (output == null || !itemsById.containsKey(output) || output.equals("minecraft:air"))
									throw new IllegalArgumentException("Unregistered Bakery completion output");
								add(output, new Dish("omnichef:bakery_assembly", "", count, slots));
								classificationRules.preparedItems.add(output);
								bakeryInteractions++;
							} catch (ReflectiveOperationException | RuntimeException | LinkageError error) {
								var issue = new com.google.gson.JsonObject();
								issue.addProperty("recipe", holder.id().toString());
								issue.addProperty("reason", error.toString());
								bakeryIssues.add(issue);
							}
						}
						// A finished whole cake made by interaction also supplies its real slice route.
						for (var entry : feastPortions.sources.entrySet())
							for (var source : entry.getValue())
								for (var dish : new java.util.ArrayList<>(dishes.getOrDefault(source.whole, java.util.List.of()))) {
									long count = (long) dish.count * source.servings;
									if (count < 1 || count > 4096)
										continue;
									var slots = new java.util.ArrayList<java.util.List<String>>(dish.slots);
									if (!source.container.isEmpty())
										for (int i = 0; i < count; i++)
											slots.add(java.util.List.of(source.container));
									add(entry.getKey(), new Dish(dish.method, source.whole, (int) count, slots));
									classificationRules.preparedPortions.add(entry.getKey());
								}
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
						bakeryLegacyInteractions();
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
					boolean costsFinalized = false;
					final java.util.Map<String, Double> primaryFloorCache = new java.util.HashMap<>();
					final java.util.Set<SourceRoute> primaryRoutes = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
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

					// Data-driven acquisition estimates. Unsupported loot logic never grants a free recipe.
					final java.util.Map<String, com.google.gson.JsonObject> lootResources = new java.util.HashMap<>();
					final java.util.Set<String> missingLootResources = new java.util.HashSet<>();
					final java.util.Map<SourceRoute, com.google.gson.JsonObject> mobEvidence = new java.util.IdentityHashMap<>();
					int mobRoutes = 0;
					int skippedMobBranches = 0;

					com.google.gson.JsonObject lootResource(String id, String directory) {
						String key = directory + "|" + id;
						if (lootResources.containsKey(key))
							return lootResources.get(key);
						if (missingLootResources.contains(key))
							return null;
						try {
							var location = net.minecraft.resources.ResourceLocation.parse(id);
							var resource = level.getServer().getResourceManager().getResource(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(location.getNamespace(), directory + "/" + location.getPath() + ".json"));
							if (resource.isPresent())
								try (var reader = resource.get().openAsReader()) {
									var value = com.google.gson.JsonParser.parseReader(reader);
									if (value.isJsonObject()) {
										var object = value.getAsJsonObject();
										lootResources.put(key, object);
										return object;
									}
								}
						} catch (Exception failure) {
							warnings.putIfAbsent("mob_loot:" + key, failure.getClass().getSimpleName());
						}
						missingLootResources.add(key);
						return null;
					}

					boolean loaderConditional(com.google.gson.JsonObject value) {
						return value.has("neoforge:conditions") && !value.getAsJsonArray("neoforge:conditions").isEmpty();
					}

					double lootChance(com.google.gson.JsonObject value) {
						if (loaderConditional(value))
							return -1;
						double chance = 1;
						if (value.has("conditions"))
							for (var element : value.getAsJsonArray("conditions")) {
								var condition = element.getAsJsonObject();
								String type = condition.has("condition") ? condition.get("condition").getAsString() : "";
								if ("minecraft:killed_by_player".equals(type))
									continue;
								if ("minecraft:random_chance".equals(type) && condition.has("chance") && condition.get("chance").isJsonPrimitive()) {
									double p = condition.get("chance").getAsDouble();
									if (!Double.isFinite(p) || p < 0 || p > 1)
										return -1;
									chance *= p;
								} else
									return -1;
							}
						return chance;
					}

					double minimumLootNumber(com.google.gson.JsonElement value) {
						if (value == null)
							return -1;
						if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber())
							return value.getAsDouble();
						if (!value.isJsonObject())
							return -1;
						var number = value.getAsJsonObject();
						String type = number.has("type") ? number.get("type").getAsString() : "";
						if ("minecraft:constant".equals(type))
							return minimumLootNumber(number.get("value"));
						if ("minecraft:uniform".equals(type))
							return minimumLootNumber(number.get("min"));
						return -1;
					}

					// Quantity and probability are separate: 0..2 items is a possible drop, not a missing source.
					final class LootCountEstimate {
						final double mean;
						final double positiveChance;

						LootCountEstimate(double mean, double positiveChance) {
							this.mean = mean;
							this.positiveChance = positiveChance;
						}
					}

					LootCountEstimate lootCount(com.google.gson.JsonElement value) {
						if (value == null)
							return null;
						if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
							double number = value.getAsDouble();
							if (!Double.isFinite(number) || number < 0 || number > Integer.MAX_VALUE)
								return null;
							double count = Math.floor(number);
							return new LootCountEstimate(count, count > 0 ? 1 : 0);
						}
						if (!value.isJsonObject())
							return null;
						var number = value.getAsJsonObject();
						String type = number.has("type") ? number.get("type").getAsString() : "";
						if ("minecraft:constant".equals(type))
							return lootCount(number.get("value"));
						if (!"minecraft:uniform".equals(type))
							return null;
						// Random range bounds require a separate model; only deterministic bounds are supported here.
						var low = fixedLootCount(number.get("min"));
						var high = fixedLootCount(number.get("max"));
						if (low == null || high == null)
							return null;
						if (high.mean < low.mean)
							return null;
						double mean = (low.mean + high.mean) / 2.0;
						double positive = low.mean > 0 ? 1 : (high.mean == 0 ? 0 : high.mean / (high.mean + 1.0));
						return new LootCountEstimate(mean, positive);
					}

					LootCountEstimate fixedLootCount(com.google.gson.JsonElement value) {
						if (value == null)
							return null;
						if (value.isJsonObject()) {
							var number = value.getAsJsonObject();
							if (!number.has("type") || !"minecraft:constant".equals(number.get("type").getAsString()))
								return null;
							return fixedLootCount(number.get("value"));
						}
						return lootCount(value);
					}

					boolean naturalPlant(String id) {
						var tags = java.util.List.of("c:crops", "c:foods/fruit", "c:foods/vegetable", "c:foods/berry", "minecraft:flowers", "minecraft:saplings");
						for (String tag : tags)
							if (classificationRules.matchesTags(id, java.util.List.of(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse(tag)))))
								return true;
						return false;
					}

					final java.util.Set<String> biomeSpawnEntities = new java.util.HashSet<>();
					boolean biomeSpawnIndexReady = false;

					void indexBiomeSpawns() {
						biomeSpawnEntities.clear();
						biomeSpawnIndexReady = false;
						try {
							for (var biome : level.getServer().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME))
								for (var category : net.minecraft.world.entity.MobCategory.values())
									for (var spawn : biome.getMobSettings().getMobs(category).unwrap())
										biomeSpawnEntities.add(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(spawn.type).toString());
							biomeSpawnIndexReady = true;
						} catch (RuntimeException failure) {
							warnings.putIfAbsent("biome_spawn_index", failure.getClass().getSimpleName());
						}
					}

					boolean biomeSpawnEvidence(net.minecraft.world.entity.EntityType<?> entity) {
						return biomeSpawnIndexReady && biomeSpawnEntities.contains(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity).toString());
					}

					double huntingEffort(net.minecraft.world.entity.EntityType<?> entity) {
						String category = entity.getCategory().getName();
						double units = "creature".equals(category) ? 0 : 1;
						if (net.minecraft.world.entity.ai.attributes.DefaultAttributes.hasSupplier(entity)) {
							@SuppressWarnings("unchecked")
							var living = (net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>) (net.minecraft.world.entity.EntityType<?>) entity;
							var attributes = net.minecraft.world.entity.ai.attributes.DefaultAttributes.getSupplier(living);
							if (attributes.hasAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH) && attributes.getValue(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH) >= 40)
								units++;
							if (attributes.hasAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) && attributes.getValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) >= 8)
								units++;
						}
						// Registry membership alone cannot establish survival availability (events and structures remain uncertain).
						if (!biomeSpawnEvidence(entity))
							units += 2;
						return units * classificationRules.stepsPerTier;
					}

					void mobItem(com.google.gson.JsonObject entry, net.minecraft.world.entity.EntityType<?> entity, String table, double chance) {
						if (!entry.has("name"))
							return;
						String id = entry.get("name").getAsString();
						if (!itemsById.containsKey(id) || !classificationRules.isCulinary(id) || "FOOD".equals(classificationRules.hint(id)))
							return;
						// Plant drops and explicit base profiles retain their independent natural source.
						if (naturalPlant(id) || classificationRules.sourceTier(id) >= 0)
							return;
						double meanCount = 1;
						double countChance = 1;
						if (entry.has("functions"))
							for (var element : entry.getAsJsonArray("functions")) {
								var function = element.getAsJsonObject();
								String type = function.has("function") ? function.get("function").getAsString() : "";
								// Additive looting bonuses do not change the guaranteed base drop.
								if ("minecraft:enchanted_count_increase".equals(type))
									continue;
								if (!"minecraft:set_count".equals(type) || function.has("conditions") || loaderConditional(function)) {
									skippedMobBranches++;
									return;
								}
								var quantity = lootCount(function.get("count"));
								if (quantity == null) {
									skippedMobBranches++;
									return;
								}
								if (function.has("add") && function.get("add").getAsBoolean()) {
									meanCount += quantity.mean;
									countChance = 1.0 - (1.0 - countChance) * (1.0 - quantity.positiveChance);
								} else {
									meanCount = quantity.mean;
									countChance = quantity.positiveChance;
								}
							}
						if (!Double.isFinite(meanCount) || meanCount <= 0 || countChance <= 0)
							return;
						chance *= countChance;
						double count = meanCount / countChance;
						String output = itemNode(id);
						controlled.add(output);
						add(output, "omnichef:mob_loot", java.util.List.of(), huntingEffort(entity) + probabilityEffort(chance), chance, count);
						var list = routes.get(output);
						var evidence = new com.google.gson.JsonObject();
						evidence.addProperty("entity", net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity).toString());
						evidence.addProperty("loot_table", table);
						evidence.addProperty("activity", "hunting");
						evidence.addProperty("availability", biomeSpawnEvidence(entity) ? "listed_in_registered_biome_spawns" : "unverified_event_structure_or_other_source");
						evidence.addProperty("availability_penalty_tiers", biomeSpawnEvidence(entity) ? 0 : 2);
						evidence.addProperty("estimate_basis", "mob_category_default_health_and_attack");
						evidence.addProperty("quantity_model", "expected_count_when_drop_is_positive");
						evidence.addProperty("positive_count_chance", countChance);
						SourceRoute added = list.get(list.size() - 1);
						boolean primary = net.minecraft.resources.ResourceLocation.parse(table).getNamespace().equals(net.minecraft.resources.ResourceLocation.parse(id).getNamespace());
						evidence.addProperty("primary_mod_source", primary);
						if (primary)
							primaryRoutes.add(added);
						mobEvidence.put(added, evidence);
						mobRoutes++;
					}

					void mobEntry(com.google.gson.JsonObject entry, net.minecraft.world.entity.EntityType<?> entity, String table, double chance, java.util.Set<String> visiting) {
						double own = lootChance(entry);
						if (own < 0) {
							skippedMobBranches++;
							return;
						}
						chance *= own;
						if (chance <= 0)
							return;
						String type = entry.has("type") ? entry.get("type").getAsString() : "";
						if ("minecraft:item".equals(type))
							mobItem(entry, entity, table, chance);
						else if ("minecraft:loot_table".equals(type) && entry.has("value") && entry.get("value").isJsonPrimitive() && !entry.has("functions"))
							mobTable(entry.get("value").getAsString(), entity, chance, visiting);
						else
							skippedMobBranches++;
					}

					void mobTable(String table, net.minecraft.world.entity.EntityType<?> entity, double chance, java.util.Set<String> visiting) {
						if (visiting.size() >= 32 || !visiting.add(table)) {
							skippedMobBranches++;
							return;
						}
						try {
							var object = lootResource(table, "loot_table");
							if (object == null)
								return;
							if (loaderConditional(object) || object.has("functions")) {
								skippedMobBranches++;
								return;
							}
							if (!object.has("pools"))
								return;
							for (var value : object.getAsJsonArray("pools")) {
								var pool = value.getAsJsonObject();
								double own = lootChance(pool);
								double rolls = minimumLootNumber(pool.get("rolls"));
								if (own < 0 || !Double.isFinite(rolls) || rolls < 1 || pool.has("functions")) {
									skippedMobBranches++;
									continue;
								}
								if (!pool.has("entries"))
									continue;
								double totalWeight = 0;
								boolean supported = true;
								for (var element : pool.getAsJsonArray("entries")) {
									var entry = element.getAsJsonObject();
									String type = entry.has("type") ? entry.get("type").getAsString() : "";
									if (!java.util.Set.of("minecraft:item", "minecraft:loot_table", "minecraft:empty").contains(type) || entry.has("quality") && entry.get("quality").getAsDouble() != 0) {
										supported = false;
										break;
									}
									double weight = entry.has("weight") ? entry.get("weight").getAsDouble() : 1;
									if (!Double.isFinite(weight) || weight < 0) {
										supported = false;
										break;
									}
									totalWeight += weight;
								}
								if (!supported || !Double.isFinite(totalWeight) || totalWeight <= 0) {
									skippedMobBranches++;
									continue;
								}
								for (var element : pool.getAsJsonArray("entries")) {
									var entry = element.getAsJsonObject();
									double weight = entry.has("weight") ? entry.get("weight").getAsDouble() : 1;
									mobEntry(entry, entity, table, chance * own * weight / totalWeight, visiting);
								}
							}
						} finally {
							visiting.remove(table);
						}
					}

					void loadMobLoot() {
						indexBiomeSpawns();
						try {
							var entitiesByTable = new java.util.TreeMap<String, java.util.List<net.minecraft.world.entity.EntityType<?>>>();
							for (var entity : net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE) {
								String table = entity.getDefaultLootTable().location().toString();
								entitiesByTable.computeIfAbsent(table, key -> new java.util.ArrayList<>()).add(entity);
								try {
									mobTable(table, entity, 1, new java.util.HashSet<>());
								} catch (RuntimeException failure) {
									warnings.putIfAbsent("entity_loot:" + table, failure.getClass().getSimpleName());
								}
							}
							var active = new java.util.LinkedHashSet<String>();
							var location = net.minecraft.resources.ResourceLocation.parse("neoforge:loot_modifiers/global_loot_modifiers.json");
							for (var resource : level.getServer().getResourceManager().getResourceStack(location))
								try (var reader = resource.openAsReader()) {
									var list = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
									if (list.has("replace") && list.get("replace").getAsBoolean())
										active.clear();
									if (list.has("entries"))
										for (var id : list.getAsJsonArray("entries"))
											active.add(id.getAsString());
								}
							for (String id : active) {
								try {
									var modifier = lootResource(id, "loot_modifiers");
									if (modifier == null || loaderConditional(modifier) || !modifier.has("type") || !"neoforge:add_table".equals(modifier.get("type").getAsString()))
										continue;
									if (!modifier.has("conditions") || modifier.getAsJsonArray("conditions").size() != 1 || !modifier.has("table")) {
										skippedMobBranches++;
										continue;
									}
									var condition = modifier.getAsJsonArray("conditions").get(0).getAsJsonObject();
									if (!condition.has("condition") || !"neoforge:loot_table_id".equals(condition.get("condition").getAsString()) || !condition.has("loot_table_id")) {
										skippedMobBranches++;
										continue;
									}
									String target = condition.get("loot_table_id").getAsString();
									for (var entity : entitiesByTable.getOrDefault(target, java.util.List.of()))
										mobTable(modifier.get("table").getAsString(), entity, 1, new java.util.HashSet<>());
								} catch (RuntimeException failure) {
									warnings.putIfAbsent("loot_modifier:" + id, failure.getClass().getSimpleName());
								}
							}
						} catch (Exception failure) {
							warnings.putIfAbsent("mob_loot_scan", failure.getClass().getSimpleName());
						}
					}

					double routeEffort(SourceRoute route) {
						double dependency = 0;
						for (var slot : route.requirements) {
							double best = Double.POSITIVE_INFINITY;
							for (String input : slot)
								best = Math.min(best, costs.getOrDefault(input, Double.POSITIVE_INFINITY));
							dependency = Math.max(dependency, best);
						}
						return dependency + route.effort;
					}

					double primaryFloor(String output) {
						if (costsFinalized && primaryFloorCache.containsKey(output))
							return primaryFloorCache.get(output);
						double best = Double.POSITIVE_INFINITY;
						for (var route : routes.getOrDefault(output, java.util.List.of()))
							if (primaryRoutes.contains(route))
								best = Math.min(best, routeEffort(route));
						double result = Double.isFinite(best) ? best : 0;
						if (costsFinalized)
							primaryFloorCache.put(output, result);
						return result;
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
						loadMobLoot();
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
								String output = stackNode(result);
								if (holder.id().getNamespace().equals(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(result.getItem()).getNamespace())) {
									var added = routes.get(output);
									if (added != null && !added.isEmpty())
										primaryRoutes.add(added.get(added.size() - 1));
								}
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
							catalogDiagnostics.pass = pass;
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
						costsFinalized = true;
						for (var entry : warnings.entrySet())
							net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef acquisition adapter {}: {}", entry.getKey(), entry.getValue());
						if (!settled)
							net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef acquisition analysis reached its pass limit; some source estimates may be incomplete.");
						net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef food database v7: acquisition analysis: {} nodes, {} routes, {} trees, {} bee routes, {} passes, settled={}; base sources are estimates.", nodes.size(), totalRoutes,
								treeCount, beeRoutes, passes, settled);
						net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef mob acquisition: {} supported routes; {} unsupported loot branches skipped. Estimates do not model biome or dimension availability.", mobRoutes, skippedMobBranches);
					}

					int minimumTier(String id, int tierCount) {
						int tier = 0;
						if (classificationRules.acquisitionEnabled) {
							String key = "item|" + id;
							double cost = Math.max(costs.getOrDefault(key, Double.POSITIVE_INFINITY), primaryFloor(key));
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
						result.addProperty("primary_source_effort_floor", primaryFloor(node));
						result.addProperty("source_policy", "confirmed_primary_mod_routes_set_access_floor; other_mod_routes_are_production_alternatives");
						// v35b diagnostics only. They never feed back into tiers.
						// effort_tier is derived from the settled cost, which already inherits declared minimums from inputs,
						// so "inherited_or_computed" cannot separate a late input from a genuinely laborious route.
						int declaredGate = Math.max(Math.max(classificationRules.minimumTiers.getOrDefault(id, 0), classificationRules.categoryMinimum(id)),
								Math.max(classificationRules.progressionMinimumTiers.getOrDefault(id, 0), Math.max(0, classificationRules.sourceTier(id))));
						double fullCost = Math.max(costs.getOrDefault(node, Double.POSITIVE_INFINITY), primaryFloor(node));
						int effortTier = Double.isFinite(fullCost) ? (int) Math.floor((fullCost + 0.0000001) / classificationRules.stepsPerTier) : -1;
						int finalMinimum = minimumTier(id, tierCount);
						String driver = finalMinimum <= 0 ? "none" : effortTier > declaredGate ? "inherited_or_computed" : declaredGate >= finalMinimum ? "declared_gate" : "unresolved_fallback";
						result.addProperty("gate_tier", declaredGate);
						result.addProperty("effort_tier", effortTier);
						result.addProperty("tier_driver", driver);
						if (cost != null)
							result.addProperty("effort", cost);
						if (route != null) {
							result.addProperty("system", route.system);
							if (mobEvidence.containsKey(route))
								result.add("source_conditions", mobEvidence.get(route).deepCopy());
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
				class CompleteRecipeInputs {
					final java.util.Map<Class<?>, java.util.Optional<java.lang.reflect.Method>> fluidReaders = new java.util.HashMap<>();
					final java.util.Map<String, java.util.List<java.util.List<String>>> cache = new java.util.HashMap<>();
					final java.util.Map<String, java.util.List<String>> carriers = new java.util.HashMap<>();
					final com.google.gson.JsonObject issues = new com.google.gson.JsonObject();
					final com.google.gson.JsonObject fluidMappings = new com.google.gson.JsonObject();
					final com.google.gson.JsonObject kitchenWaterRequirements = new com.google.gson.JsonObject();
					final java.util.Map<String, java.util.List<net.minecraft.world.item.crafting.RecipeHolder<?>>> fluidProduction = new java.util.TreeMap<>();
					final java.util.Map<String, String> fluidNodeIds = new java.util.TreeMap<>();
					boolean fluidProductionIndexed = false;

					void indexFluidProduction() {
						if (fluidProductionIndexed)
							return;
						fluidProductionIndexed = true;
						for (var holder : recipeManager.getRecipes()) {
							String method = classificationRules.typeId(holder);
							if (!method.startsWith("create:") || !classificationRules.matchesType(method, classificationRules.allowedTypes))
								continue;
							try {
								Object value = holder.value().getClass().getMethod("getFluidResults").invoke(holder.value());
								if (!(value instanceof java.lang.Iterable<?> outputs))
									continue;
								for (var output : outputs) {
									if (!(output instanceof net.neoforged.neoforge.fluids.FluidStack stack) || stack.isEmpty())
										continue;
									String fluid = net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString();
									String node = "omnichef:fluid/" + fluid.replace(':', '/');
									var routes = fluidProduction.computeIfAbsent(node, key -> new java.util.ArrayList<>());
									if (!routes.contains(holder))
										routes.add(holder);
									fluidNodeIds.put(node, fluid);
								}
							} catch (NoSuchMethodException absent) {
								// Recipes without fluid outputs are not fluid production routes.
							} catch (ReflectiveOperationException | RuntimeException failure) {
								issue(holder.id().toString(), "fluid_output_reader_failed", failure.getClass().getSimpleName());
							}
						}
					}

					boolean fluidNode(String id) {
						return fluidProduction.containsKey(id);
					}

					boolean carriersIndexed = false;
					final java.util.Set<String> automaticContainers = new java.util.TreeSet<>();
					static final String INVALID_ITEM = "omnichef:unresolved_item_requirement";
					static final String INVALID_FLUID = "omnichef:unresolved_fluid_requirement";

					void issue(String recipeId, String reason, String detail) {
						if (!issues.has(recipeId))
							issues.add(recipeId, new com.google.gson.JsonArray());
						var entry = new com.google.gson.JsonObject();
						entry.addProperty("reason", reason);
						entry.addProperty("detail", detail);
						issues.getAsJsonArray(recipeId).add(entry);
					}

					java.util.List<String> itemIds(net.minecraft.world.item.crafting.Ingredient ingredient) {
						java.util.Set<String> ids = new java.util.TreeSet<>();
						for (var stack : ingredient.getItems()) {
							if (stack == null || stack.isEmpty())
								continue;
							var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
							if (id != null && !"minecraft:barrier".equals(id.toString()) && itemsById.containsKey(id.toString()))
								ids.add(id.toString());
						}
						return new java.util.ArrayList<>(ids);
					}

					java.util.List<String> kitchenWaterAlternative(net.minecraft.world.item.crafting.RecipeHolder<?> holder, net.minecraft.world.item.crafting.Ingredient ingredient, java.util.List<String> ids) {
						// Only this verified kitchen family may use water as a discovery proxy for a water bottle.
						// Physical game recipes are never changed. Other potion requirements remain intact.
						if (!ids.contains("minecraft:potion") || !itemsById.containsKey("minecraft:water_bucket"))
							return ids;
						String method = classificationRules.typeId(holder);
						if (!java.util.Set.of("farm_and_charm:pot_cooking", "farm_and_charm:stove", "farm_and_charm:roaster", "farm_and_charm:crafting_bowl").contains(method))
							return ids;
						try {
							var output = holder.value().getResultItem(level.registryAccess());
							if (output.isEmpty())
								return ids;
							String outputId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(output.getItem()).toString();
							if (!outputId.startsWith("candlelight:"))
								return ids;
							var water = net.minecraft.world.item.alchemy.PotionContents.createItemStack(itemsById.get("minecraft:potion"), net.minecraft.world.item.alchemy.Potions.WATER);
							// Testing a real stack also respects component-sensitive custom ingredients.
							if (!ingredient.test(water))
								return ids;
							java.util.Set<String> alternatives = new java.util.TreeSet<>(ids);
							alternatives.remove("minecraft:potion");
							alternatives.add("minecraft:water_bucket");
							String recipeId = holder.id().toString();
							if (!kitchenWaterRequirements.has(recipeId))
								kitchenWaterRequirements.add(recipeId, new com.google.gson.JsonArray());
							var evidence = new com.google.gson.JsonObject();
							evidence.addProperty("physical_input", "minecraft:potion");
							evidence.addProperty("tested_contents", "minecraft:water");
							evidence.addProperty("discovery_input", "minecraft:water_bucket");
							evidence.addProperty("reason", "live_ingredient_accepts_plain_water_bottle");
							kitchenWaterRequirements.getAsJsonArray(recipeId).add(evidence);
							return new java.util.ArrayList<>(alternatives);
						} catch (RuntimeException failure) {
							// An unsupported ingredient implementation must not grant a free requirement.
							issue(holder.id().toString(), "kitchen_water_probe_failed", failure.getClass().getSimpleName());
							return ids;
						}
					}

					void indexCarriers() {
						if (carriersIndexed)
							return;
						carriersIndexed = true;
						for (var entry : itemsById.entrySet()) {
							// Technical tanks and empty containers must not replace a culinary discovery.
							if (!classificationRules.isCulinary(entry.getKey()) && !classificationRules.isEdible(entry.getKey()))
								continue;
							try {
								var contained = net.neoforged.neoforge.fluids.FluidUtil.getFluidContained(new net.minecraft.world.item.ItemStack(entry.getValue()));
								if (contained.isPresent() && !contained.get().isEmpty()) {
									String fluid = net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(contained.get().getFluid()).toString();
									carriers.computeIfAbsent(fluid, key -> new java.util.ArrayList<>()).add(entry.getKey());
								}
							} catch (RuntimeException error) {
								issue("omnichef:fluid_carriers", "carrier_inspection_failed", entry.getKey() + ": " + error.getClass().getSimpleName());
							}
						}
						for (var ids : carriers.values())
							java.util.Collections.sort(ids);
					}

					java.util.List<String> itemsForFluid(String fluid) {
						// These are discovery requirements, not quantity conversions or physical recipes.
						if (java.util.Set.of("minecraft:milk", "neoforge:milk", "forge:milk", "create:milk", "minecraft:flowing_milk", "neoforge:flowing_milk", "forge:flowing_milk", "create:flowing_milk").contains(fluid))
							return itemsById.containsKey("minecraft:milk_bucket") ? java.util.List.of("minecraft:milk_bucket") : java.util.List.of();
						if ("create:honey".equals(fluid) || "create:flowing_honey".equals(fluid))
							return itemsById.containsKey("minecraft:honey_bottle") ? java.util.List.of("minecraft:honey_bottle") : java.util.List.of();
						if ("minecraft:water".equals(fluid) || "minecraft:flowing_water".equals(fluid))
							return itemsById.containsKey("minecraft:water_bucket") ? java.util.List.of("minecraft:water_bucket") : java.util.List.of();
						indexCarriers();
						var contained = carriers.getOrDefault(fluid, java.util.List.of());
						if (!contained.isEmpty())
							return contained;
						indexFluidProduction();
						String node = "omnichef:fluid/" + fluid.replace(':', '/');
						return fluidNode(node) ? java.util.List.of(node) : java.util.List.of();
					}

					final java.util.Map<String, String> containedFluidCache = new java.util.HashMap<>();

					boolean carrierRepresents(String carrier, String output) {
						if (!itemsById.containsKey(carrier))
							return false;
						String fluid = containedFluidCache.computeIfAbsent(carrier, key -> {
							try {
								var value = net.neoforged.neoforge.fluids.FluidUtil.getFluidContained(new net.minecraft.world.item.ItemStack(itemsById.get(key)));
								if (value.isPresent() && !value.get().isEmpty())
									return net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(value.get().getFluid()).toString();
							} catch (RuntimeException unsupported) {
								// No proven fluid content means no inferred packaging equivalence.
							}
							return "";
						});
						return !fluid.isEmpty() && itemsForFluid(fluid).contains(output);
					}

					void appendVatContainer(net.minecraft.world.item.crafting.RecipeHolder<?> holder, java.util.List<java.util.List<String>> slots) {
						if (!"culturaldelights:aging".equals(classificationRules.typeId(holder)))
							return;
						try {
							Object value = holder.value().getClass().getMethod("getContainer").invoke(holder.value());
							if (!(value instanceof net.minecraft.world.item.crafting.Ingredient container))
								throw new IllegalArgumentException("Unsupported vat container");
							// VatRecipe treats an empty container ingredient as no container slot.
							if (container == net.minecraft.world.item.crafting.Ingredient.EMPTY)
								return;
							var ids = itemIds(container);
							if (ids.isEmpty())
								throw new IllegalArgumentException("Unresolved vat container");
							slots.add(ids);
							for (String id : ids)
								if (!classificationRules.isEdible(id) && !classificationRules.isCulinary(id))
									automaticContainers.add(id);
						} catch (ReflectiveOperationException | RuntimeException failure) {
							slots.add(java.util.List.of(INVALID_ITEM));
							issue(holder.id().toString(), "vat_container_unresolved", failure.getClass().getSimpleName());
						}
					}

					void appendKitchenContainer(net.minecraft.world.item.crafting.RecipeHolder<?> holder, java.util.List<java.util.List<String>> slots) {
						String method = classificationRules.typeId(holder);
						if (!"farm_and_charm:pot_cooking".equals(method) && !"farm_and_charm:roaster".equals(method))
							return;
						try {
							Object recipe = holder.value();
							if ("farm_and_charm:pot_cooking".equals(method) && !Boolean.TRUE.equals(recipe.getClass().getMethod("isContainerRequired").invoke(recipe)))
								return;
							String getter = "farm_and_charm:pot_cooking".equals(method) ? "getContainerItem" : "getContainer";
							Object value = recipe.getClass().getMethod(getter).invoke(recipe);
							if (!(value instanceof net.minecraft.world.item.ItemStack stack))
								throw new IllegalArgumentException("Unsupported kitchen container value");
							if (stack.isEmpty()) {
								if ("farm_and_charm:pot_cooking".equals(method))
									throw new IllegalArgumentException("Required kitchen container is empty");
								return;
							}
							String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
							if ("minecraft:barrier".equals(id) || !itemsById.containsKey(id))
								throw new IllegalArgumentException("Unresolved kitchen container");
							slots.add(java.util.List.of(id));
							if (!classificationRules.isEdible(id) && !classificationRules.isCulinary(id))
								automaticContainers.add(id);
						} catch (ReflectiveOperationException | RuntimeException failure) {
							slots.add(java.util.List.of(INVALID_ITEM));
							issue(holder.id().toString(), "kitchen_container_unresolved", failure.getClass().getSimpleName());
						}
					}

					java.util.List<java.util.List<String>> slots(net.minecraft.world.item.crafting.RecipeHolder<?> holder) {
						String recipeId = holder.id().toString();
						var known = cache.get(recipeId);
						if (known != null)
							return known;
						java.util.List<java.util.List<String>> result = new java.util.ArrayList<>();
						var recipe = holder.value();
						if (recipe.getIngredients() != null)
							for (var ingredient : recipe.getIngredients()) {
								// Only an actual empty crafting cell may disappear. An unresolved tag remains mandatory.
								if (ingredient == net.minecraft.world.item.crafting.Ingredient.EMPTY)
									continue;
								var ids = kitchenWaterAlternative(holder, ingredient, itemIds(ingredient));
								if (ids.isEmpty()) {
									result.add(java.util.List.of(INVALID_ITEM));
									issue(recipeId, "unresolved_item_slot", "Empty ingredient alternatives or placeholder barrier");
								} else if (!ids.stream().allMatch(classificationRules::equipment))
									result.add(ids);
							}
						var reader = fluidReaders.computeIfAbsent(recipe.getClass(), type -> {
							try {
								return java.util.Optional.of(type.getMethod("getFluidIngredients"));
							} catch (NoSuchMethodException absent) {
								return java.util.Optional.empty();
							}
						});
						String method = classificationRules.typeId(holder);
						if (reader.isPresent()) {
							try {
								Object value = reader.get().invoke(recipe);
								if (!(value instanceof java.lang.Iterable<?> fluids))
									throw new IllegalArgumentException("Unsupported fluid input collection");
								for (var input : fluids) {
									if (!(input instanceof net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient fluidInput))
										throw new IllegalArgumentException("Unsupported fluid ingredient class");
									java.util.Set<String> alternatives = new java.util.TreeSet<>();
									for (var fluidStack : fluidInput.getFluids()) {
										if (fluidStack.isEmpty())
											continue;
										String fluid = net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(fluidStack.getFluid()).toString();
										var mapped = itemsForFluid(fluid);
										alternatives.addAll(mapped);
										if (mapped.isEmpty())
											issue(recipeId, "unmapped_fluid_alternative", fluid);
										else {
											var json = new com.google.gson.JsonArray();
											for (String id : mapped)
												json.add(id);
											fluidMappings.add(fluid, json);
										}
									}
									if (alternatives.isEmpty()) {
										result.add(java.util.List.of(INVALID_FLUID));
										issue(recipeId, "unresolved_fluid_slot", "No supported culinary item represents this fluid input");
									} else
										result.add(new java.util.ArrayList<>(alternatives));
								}
							} catch (ReflectiveOperationException | RuntimeException failure) {
								result.add(java.util.List.of(INVALID_FLUID));
								issue(recipeId, "fluid_reader_failed", failure.getClass().getSimpleName());
							}
						} else if (java.util.Set.of("create:filling", "create:mixing", "create:compacting").contains(method)) {
							result.add(java.util.List.of(INVALID_FLUID));
							issue(recipeId, "fluid_reader_unavailable", method);
						}
						appendKitchenContainer(holder, result);
						appendVatContainer(holder, result);
						cache.put(recipeId, result);
						return result;
					}
				}
				CompleteRecipeInputs completeRecipeInputs = new CompleteRecipeInputs();
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
							net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef food database v7: phase={}, indexed={}, scored={}/{}, foods={}, current={}, evaluated={}, elapsed={}s", phase, indexedOutputs, scoredItems, graphNodes,
									foodsProcessed, currentFood, evaluatedRecipes, (now - generationStarted) / 1000000000L);
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
						return ingredientIds.computeIfAbsent(ingredient, completeRecipeInputs::itemIds);
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
									java.util.List<java.util.List<String>> slots = new java.util.ArrayList<>(completeRecipeInputs.slots(holder));
									boolean valid = !slots.isEmpty();
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
						// The classifier and the exported dependency graph must index the same production routes.
						java.util.List<net.minecraft.world.item.crafting.RecipeHolder<?>> producers = completeRecipeInputs.fluidNode(output)
								? completeRecipeInputs.fluidProduction.get(output)
								: recipesByOutput.getOrDefault(output, java.util.Collections.emptyList());
						for (net.minecraft.world.item.crafting.RecipeHolder<?> holder : producers) {
							var recipe = holder.value();
							java.util.List<java.util.List<String>> slots = new java.util.ArrayList<>(completeRecipeInputs.slots(holder));
							java.util.Set<String> mandatory = new java.util.HashSet<>();
							boolean valid = true;
							for (var ids : slots) {
								inputs.addAll(ids);
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
							if (completeRecipeInputs.fluidNode(output))
								indexed.add(new IndexedRecipe(holder, classificationRules.typeId(holder), 1, slots));
							else if (!result.isEmpty())
								indexed.add(new IndexedRecipe(holder, classificationRules.typeId(holder), Math.max(1, result.getCount()), slots));
						}
						for (var source : feastPortions.sources.getOrDefault(output, java.util.Collections.emptyList())) {
							for (var holder : recipesByOutput.getOrDefault(source.whole, java.util.Collections.emptyList())) {
								var recipe = holder.value();
								var result = recipe.getResultItem(level.registryAccess());
								long batch = (long) result.getCount() * source.servings;
								if (result.isEmpty() || batch < 1 || batch > 4096)
									continue;
								java.util.List<java.util.List<String>> slots = new java.util.ArrayList<>(completeRecipeInputs.slots(holder));
								java.util.Set<String> mandatory = new java.util.HashSet<>();
								boolean valid = !slots.isEmpty();
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
							if (!dish.whole.isEmpty()) {
								normalized.portionSource = dish.whole;
								normalized.servings = dish.count;
							}
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

					boolean packagingAccessory(String id) {
						// Match known empty supplies; culinary contents such as milk are never accessories.
						if (java.util.Set.of("minecraft:bowl", "minecraft:glass_bottle", "minecraft:bucket", "minecraft:stick", "minecraft:water_bucket").contains(id))
							return true;
						if (completeRecipeInputs.automaticContainers.contains(id) || classificationRules.equipment(id) || classificationRules.servingContainer(id))
							return true;
						if (classificationRules.isEdible(id) || classificationRules.isCulinary(id))
							return false;
						String path = id.substring(id.indexOf(':') + 1);
						return path.contains("empty_bottle") || path.contains("empty_bucket") || path.contains("empty_mug") || path.contains("empty_cup") || path.contains("empty_plate") || path.contains("empty_bowl");
					}

					java.util.List<String> singlePackagingMaterial(IndexedRecipe recipe) {
						java.util.List<String> material = new java.util.ArrayList<>();
						String id = null;
						for (var slot : recipe.slots) {
							if (slot.isEmpty())
								return null;
							if (slot.stream().allMatch(this::packagingAccessory))
								continue;
							// OR slots and mixed materials cannot prove a reversible storage conversion.
							if (slot.size() != 1 || !itemsById.containsKey(slot.get(0)))
								return null;
							if (id != null && !id.equals(slot.get(0)))
								return null;
							id = slot.get(0);
							material.add(id);
						}
						return material.isEmpty() ? null : material;
					}

					final java.util.Map<IndexedRecipe, Boolean> repackagingCache = new java.util.IdentityHashMap<>();

					boolean repackagingRecipe(IndexedRecipe recipe, String output) {
						Boolean cached = repackagingCache.get(recipe);
						if (cached != null)
							return cached;
						boolean result = repackagingEvidence(recipe, output);
						repackagingCache.put(recipe, result);
						return result;
					}

					boolean repackagingEvidence(IndexedRecipe recipe, String output) {
						var packingMethods = java.util.Set.of("minecraft:crafting", "create:compacting", "create:filling", "create:emptying");
						if (!packingMethods.contains(recipe.method))
							return false;
						java.util.List<java.util.List<String>> materials = new java.util.ArrayList<>();
						for (var slot : recipe.slots) {
							if (slot.isEmpty())
								return false;
							if (!slot.stream().allMatch(this::packagingAccessory))
								materials.add(slot);
						}
						if (materials.isEmpty())
							return false;
						// A sole content slot containing the output is a transfer, even when a fluid tag
						// also accepts another carrier. Extra mandatory culinary slots still mean preparation.
						if (materials.size() == 1 && materials.get(0).contains(output))
							return true;
						boolean fluidTransfer = true;
						for (var slot : materials)
							for (String input : slot)
								if (!input.equals(output) && !completeRecipeInputs.carrierRepresents(input, output))
									fluidTransfer = false;
						if (fluidTransfer)
							return true;
						// Verify every OR alternative and both quantities; names and tags alone do not prove storage.
						java.util.Set<String> candidates = new java.util.TreeSet<>(materials.get(0));
						for (var slot : materials)
							if (!candidates.equals(new java.util.TreeSet<>(slot)))
								return false;
						for (String input : candidates) {
							if (!itemsById.containsKey(input) || classificationRules.isEdible(input))
								return false;
							boolean reversed = false;
							for (var reverse : rawRecipesFor(input)) {
								if (!packingMethods.contains(reverse.method))
									continue;
								var reverseMaterials = singlePackagingMaterial(reverse);
								if (reverseMaterials == null || !output.equals(reverseMaterials.get(0)))
									continue;
								long consumed = (long) materials.size() * reverseMaterials.size();
								long produced = (long) recipe.outputCount * reverse.outputCount;
								if (consumed == produced && (materials.size() != recipe.outputCount || reverseMaterials.size() != reverse.outputCount)) {
									reversed = true;
									break;
								}
							}
							if (!reversed)
								return false;
						}
						return true;
					}

					boolean culinaryRecipe(IndexedRecipe recipe, String output) {
						// Packing, unpacking and refilling do not establish a new prepared food.
						// Explicit prepared roles and real feast portions retain their established classification.
						if (!"FOOD".equals(classificationRules.hint(output)) && !classificationRules.preparedPortions.contains(output) && repackagingRecipe(recipe, output))
							return false;
						if (!"omnichef:sushi_rolling".equals(recipe.method) && !classificationRules.matchesType(recipe.method, classificationRules.allowedTypes))
							return false;
						boolean culinaryInput = false;
						for (java.util.List<String> slot : recipe.slots) {
							for (String ingredient : slot)
								culinaryInput |= classificationRules.isCulinary(ingredient) || completeRecipeInputs.fluidNode(ingredient);
						}
						if (!culinaryInput
								&& !(classificationRules.isEdible(output) && recipe.slots.stream().flatMap(java.util.Collection::stream).anyMatch(id -> id.equals(CompleteRecipeInputs.INVALID_ITEM) || id.equals(CompleteRecipeInputs.INVALID_FLUID))))
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
							catalogDiagnostics.pass = pass;
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
								// An unresolved preparation route is still a prepared product, not a raw acquisition leaf.
								boolean preparationEvidence = usableRecipesFor(output).stream().anyMatch(recipe -> culinaryRecipe(recipe, output));
								String role = hint.isEmpty() ? (best == null && !preparationEvidence ? "INGREDIENT" : "FOOD") : hint;
								roles.put(output, role);
								roleReasons.put(output, hint.isEmpty() ? (best == null ? (preparationEvidence ? "unresolved_culinary_recipe" : "no_culinary_recipe") : "culinary_recipe") : classificationRules.hintReasons.get(output));
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
								var analysis = new RecipeAnalysis(previous.recipeHolder, previous.method, previous.outputCount, previous.preparePoints, total, roundScore((total + previous.preparePoints) / previous.outputCount),
										previous.ingredientSlots, selected);
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
				catalogDiagnostics.phase("scoring recipes");
				scoreResolver.buildScores();
				catalogDiagnostics.phase("acquisition source analysis");
				acquisitionSources.build();
				catalogDiagnostics.phase("exporting edible items");
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
					catalogDiagnostics.current = itemName;
					if (classificationRules.technicalIntermediate(itemName))
						continue;
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
						catalogDiagnostics.phase("FoodProgression.build");
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
							if (!classificationRules.natural(id) && !classificationRules.externalMaterial(id) && !classificationRules.equipment(id))
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
							catalogDiagnostics.pass = pass;
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
							catalogDiagnostics.pass = pass;
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
				catalogDiagnostics.phase("food progression");
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
						return "INGREDIENT".equals(node.kind) || "FOOD".equals(node.kind) && node.preparationTier != UNREACHABLE;
					}

					void rankGroup(String kind) {
						int count = "INGREDIENT".equals(kind) ? ingredientTierCount : tierCount;
						java.util.Comparator<Node> byRank = java.util.Comparator.comparingInt((Node n) -> n.priority).thenComparingDouble(n -> n.rank).thenComparing(n -> n.id);
						java.util.List<Node> waiting = new java.util.ArrayList<>();
						for (Node node : nodes.values())
							if (kind.equals(node.kind) && (!"FOOD".equals(kind) || node.preparationTier != UNREACHABLE))
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
						catalogDiagnostics.phase("SplitTierPlanner.build");
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
							catalogDiagnostics.pass = pass;
							boolean changed = false;
							for (Node node : nodes.values()) {
								if ("ALIAS".equals(node.kind)) {
									Node target = nodes.get(node.aliasOf);
									if (target != null && Math.max(node.minimum, target.preparationTier) < node.preparationTier) {
										node.preparationTier = Math.max(node.minimum, target.preparationTier);
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
					final java.util.Set<String> auxiliaryItems = java.util.Set.of("minecraft:bowl", "minecraft:glass_bottle", "minecraft:bucket", "minecraft:stick", "minecraft:water_bucket");
					final java.util.Map<String, com.google.gson.JsonObject> catalog = new java.util.TreeMap<>();
					final java.util.Map<String, com.google.gson.JsonArray> recipeData = new java.util.TreeMap<>();
					final java.util.Map<String, java.util.List<FoodScoreResolver.IndexedRecipe>> allowedRecipeCache = new java.util.HashMap<>();
					final java.util.Map<FoodScoreResolver.IndexedRecipe, com.google.gson.JsonObject> importedEvidence = new java.util.IdentityHashMap<>();
					final java.util.Set<String> preparedSources = new java.util.HashSet<>();
					final java.util.Set<String> referencedKitchenInputs = new java.util.TreeSet<>();
					final java.util.Map<String, String> reversibleStorage = new java.util.TreeMap<>();
					final java.util.Set<String> recipeOnlyRawProfiles = java.util.Set.of("culturaldelights:cinnamon", "culturaldelights:mint", "minecraft:ink_sac", "minecraft:seagrass", "minecraft:sunflower", "minecraft:poppy",
							"minecraft:warped_fungus", "minecraft:crimson_fungus", "minecraft:blaze_powder", "minecraft:nether_star", "minecraft:dragon_egg");
					final java.util.Set<String> recoveredIntermediateInputs = new java.util.TreeSet<>();
					final java.util.Map<String, java.util.List<FoodScoreResolver.IndexedRecipe>> evidenceRoutes = new java.util.TreeMap<>();
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
					final java.util.Map<String, Double> unlockDifficulty = new java.util.HashMap<>();
					final java.util.Map<String, Integer> unlockDepth = new java.util.HashMap<>();
					final java.util.Map<String, Integer> kitchenDepths = new java.util.HashMap<>();
					final java.util.Set<String> unverifiedAcquisition = new java.util.TreeSet<>();
					final java.util.Map<String, Integer> sourceFloors = new java.util.HashMap<>();
					final java.util.Map<String, Double> ingredientRanks = new java.util.HashMap<>();
					final java.util.Map<String, Double> ingredientBaseRanks = new java.util.HashMap<>();
					final java.util.Set<String> foundationalPromotions = new java.util.HashSet<>();
					final com.google.gson.JsonObject starterFoodPaths = new com.google.gson.JsonObject();
					final java.util.Set<String> pantryCompat = java.util.Set.of("herbsandharvest:cinnamon", "herbsandharvest:corn_kernels", "herbsandharvest:pinto_beans", "minecolonies:cornmeal", "minecolonies:creamcheese", "minecolonies:soysauce",
							"minecolonies:large_milk_bottle", "minecolonies:large_soy_milk_bottle", "productivebees:milk_bottle", "pneumaticcraft:sourdough");
					double foodRankReference = 1.0;
					SplitTierPlanner planner;

					boolean auxiliary(String id) {
						String path = id.substring(id.indexOf(':') + 1);
						return auxiliaryItems.contains(id) || classificationRules.servingContainer(id) || completeRecipeInputs.automaticContainers.contains(id)
								|| classificationRules.matchesTags(id,
										java.util.List.of(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("c:water_bottles")),
												net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("c:water_buckets"))))
								|| splitSources.containers.contains(id) || classificationRules.equipment(id) || path.contains("empty_bottle") || path.contains("empty_bucket") || path.contains("empty_mug") || path.contains("empty_cup")
								|| path.contains("empty_plate") || path.contains("empty_bowl");
					}

					String path(String id) {
						return id.substring(id.indexOf(':') + 1);
					}

					boolean plantingSeedName(String id) {
						if (classificationRules.isEdible(id))
							return false;
						String name = path(id);
						// A crop can also carry c:seeds (e.g. rice); the tag alone does not mean propagation-only.
						return name.endsWith("seeditem") || name.endsWith("_seed") || name.endsWith("_seeds") || name.endsWith("_sapling");
					}

					boolean technicalMaterial(String id) {
						return classificationRules.externalMaterial(id);
					}

					String inputKind(String id) {
						if (completeRecipeInputs.fluidNode(id))
							return "INTERMEDIATE";
						if ("minecraft:barrier".equals(id) || blocked.contains(id) || !itemsById.containsKey(id))
							return "BLOCKED";
						if (plantingSeedName(id) && !culinarySeeds.contains(id) && !classificationRules.overrides.containsKey(id))
							return "BLOCKED";
						if (auxiliary(id))
							return "AUXILIARY";
						if (technicalMaterial(id))
							return "EXTERNAL";
						if (!recipes(id).isEmpty())
							return "INTERMEDIATE";
						return "BLOCKED";
					}

					boolean botanicalKitchenInput(String id) {
						return referencedKitchenInputs.contains(id) && classificationRules.botanical(id);
					}

					boolean ingredientDiscoveryProfile(String id) {
						// Discovery roles take precedence over the recovery of missing processing nodes.
						if (classificationRules.overrides.containsKey(id))
							return "INGREDIENT".equals(classificationRules.overrides.get(id));
						if (reversibleStorage.containsKey(id))
							return false;
						if (botanicalKitchenInput(id) || referencedKitchenInputs.contains(id) && (recipeOnlyRawProfiles.contains(id) || culinarySeeds.contains(id)))
							return true;
						if (catalog.containsKey(id) && "INGREDIENT".equals(catalog.get(id).get("role").getAsString()))
							return true;
						return vanillaIngredients.contains(id) || classificationRules.pantryItems.contains(id) || pantryName(id) || rawTagged(id) || "INGREDIENT".equals(classificationRules.hint(id));
					}

					void recoverInputEvidence() {
						catalogDiagnostics.phase("FoodCatalogBuilder.recoverInputEvidence");
						java.util.ArrayDeque<String> pending = new java.util.ArrayDeque<>();
						for (var entry : catalog.entrySet())
							if ("FOOD".equals(entry.getValue().get("role").getAsString()) || pantryName(entry.getKey()))
								pending.addLast(entry.getKey());
						pending.addAll(preparedSources);
						java.util.Set<String> visited = new java.util.HashSet<>();
						while (!pending.isEmpty()) {
							String id = pending.removeFirst();
							if (!visited.add(id) || !itemsById.containsKey(id) && !completeRecipeInputs.fluidNode(id) || auxiliary(id) || technicalMaterial(id) || excludedFoods.contains(id) || classificationRules.excludedItems.contains(id))
								continue;
							if (!catalog.containsKey(id) && (plantingSeedName(id) || rawTagged(id) && !pantryName(id)))
								continue;
							var routes = allowedRecipes(id);
							if (routes.isEmpty())
								continue;
							evidenceRoutes.put(id, routes);
							for (var recipe : routes)
								for (var slot : recipe.slots) {
									if (slot.size() == 1 && plantingSeedName(slot.get(0)))
										culinarySeeds.add(slot.get(0));
									for (String input : slot) {
										referencedKitchenInputs.add(input);
										if (!catalog.containsKey(input) && (itemsById.containsKey(input) || completeRecipeInputs.fluidNode(input)) && !auxiliary(input) && !technicalMaterial(input) && !classificationRules.isEdible(input)
												&& (!rawTagged(input) || pantryName(input)) && !plantingSeedName(input))
											pending.addLast(input);
									}
								}
						}
						// Verify both directions and quantities; a bag name or storage tag is not sufficient evidence.
						for (String output : referencedKitchenInputs) {
							if (!itemsById.containsKey(output) || classificationRules.isEdible(output) || auxiliary(output) || technicalMaterial(output) || classificationRules.overrides.containsKey(output) || blocked.contains(output))
								continue;
							for (var packing : allowedRecipes(output)) {
								if (!"minecraft:crafting".equals(packing.method) || packing.outputCount != 1 || packing.slots.size() < 2)
									continue;
								String base = null;
								boolean single = true;
								for (var slot : packing.slots) {
									if (slot.size() != 1 || base != null && !base.equals(slot.get(0))) {
										single = false;
										break;
									}
									base = slot.get(0);
								}
								if (!single || base == null || output.equals(base) || !(ingredientDiscoveryProfile(base) || catalog.containsKey(base) && "INGREDIENT".equals(catalog.get(base).get("role").getAsString())))
									continue;
								for (var unpacking : allowedRecipes(base)) {
									if (!"minecraft:crafting".equals(unpacking.method) || unpacking.outputCount != packing.slots.size() || unpacking.slots.size() != 1 || !unpacking.slots.get(0).equals(java.util.List.of(output)))
										continue;
									reversibleStorage.put(output, base);
									recoveredIntermediateInputs.add(output);
									evidenceRoutes.put(output, allowedRecipes(output));
								}
							}
						}
						for (String output : reversibleStorage.keySet())
							catalog.remove(output);
						// A culinary anchor classifies an intermediate; it never satisfies the remaining mandatory slots.
						for (int pass = 0; pass < evidenceRoutes.size(); pass++) {
							catalogDiagnostics.pass = pass;
							boolean changed = false;
							for (var entry : evidenceRoutes.entrySet()) {
								String output = entry.getKey();
								if (catalog.containsKey(output) || !referencedKitchenInputs.contains(output) || recoveredIntermediateInputs.contains(output) || technicalMaterial(output) || auxiliary(output) || ingredientDiscoveryProfile(output)
										|| plantingSeedName(output) || classificationRules.overrides.containsKey(output))
									continue;
								boolean anchored = false;
								for (var recipe : entry.getValue())
									for (var slot : recipe.slots)
										for (String input : slot) {
											if (output.equals(input) || auxiliary(input) || !itemsById.containsKey(input))
												continue;
											if (catalog.containsKey(input) || ingredientDiscoveryProfile(input) || recoveredIntermediateInputs.contains(input))
												anchored = true;
										}
								if (anchored && recoveredIntermediateInputs.add(output))
									changed = true;
							}
							if (!changed)
								break;
						}
					}

					final class CulinaryDiscoveryUses {
						final java.util.Map<String, String> kinds = new java.util.TreeMap<>();
						final java.util.Map<String, com.google.gson.JsonArray> routes = new java.util.TreeMap<>();
						final java.util.Map<String, String> redirects = new java.util.TreeMap<>();
						final java.util.Set<String> roots = new java.util.TreeSet<>();
						final java.util.Set<String> ready = new java.util.HashSet<>();
						final java.util.Set<String> reachable = new java.util.HashSet<>();
						final java.util.Map<String, java.util.Set<String>> uses = new java.util.TreeMap<>();

						boolean viable(com.google.gson.JsonObject recipe) {
							if (!recipe.has("ingredientSlots"))
								return false;
							for (var slot : recipe.getAsJsonArray("ingredientSlots")) {
								boolean any = false;
								for (var input : slot.getAsJsonArray())
									if (ready.contains(input.getAsString())) {
										any = true;
										break;
									}
								if (!any)
									return false;
							}
							return true;
						}

						String physicalContext(String output, com.google.gson.JsonObject recipe, String focus) {
							var slots = new java.util.ArrayList<String>();
							for (var slot : recipe.getAsJsonArray("ingredientSlots")) {
								var alternatives = new java.util.TreeSet<String>();
								boolean focused = false;
								for (var value : slot.getAsJsonArray()) {
									String id = value.getAsString();
									if (focus.equals(id))
										focused = true;
									if (ready.contains(id))
										alternatives.add(redirects.getOrDefault(id, id));
								}
								// The alternatives in this slot are substitutes for the focus, not extra requirements.
								// Other slots remain explicit: A + sugar is not equivalent to B + an unknown tool.
								slots.add(focused ? "<ingredient>" : alternatives.toString());
							}
							java.util.Collections.sort(slots);
							// Discovery equivalence ignores recipe IDs, equipment methods and batch yield.
							// These still belong to the unchanged physical recipe and its production-cost analysis.
							return output + "|" + slots;
						}

						void index() {
							for (var e : kinds.entrySet())
								if (java.util.Set.of("INGREDIENT", "EXTERNAL", "AUXILIARY").contains(e.getValue()))
									ready.add(e.getKey());
							// Activate recipes incrementally: an unanchored cycle or blocked mandatory slot stays unavailable.
							var dependents = new java.util.HashMap<String, java.util.Set<String>>();
							for (var e : routes.entrySet())
								for (var value : e.getValue())
									for (var slot : value.getAsJsonObject().getAsJsonArray("ingredientSlots"))
										for (var input : slot.getAsJsonArray())
											dependents.computeIfAbsent(input.getAsString(), ignored -> new java.util.HashSet<>()).add(e.getKey());
							for (var e : redirects.entrySet())
								dependents.computeIfAbsent(e.getValue(), ignored -> new java.util.HashSet<>()).add(e.getKey());
							var pending = new java.util.ArrayDeque<String>(kinds.keySet());
							var queued = new java.util.HashSet<String>(kinds.keySet());
							while (!pending.isEmpty()) {
								String id = pending.removeFirst();
								queued.remove(id);
								if (ready.contains(id) || "BLOCKED".equals(kinds.get(id)))
									continue;
								boolean possible = "ALIAS".equals(kinds.get(id)) && ready.contains(redirects.get(id));
								if (!possible && java.util.Set.of("FOOD", "INTERMEDIATE").contains(kinds.get(id)))
									for (var value : routes.getOrDefault(id, new com.google.gson.JsonArray()))
										if (viable(value.getAsJsonObject())) {
											possible = true;
											break;
										}
								if (!possible)
									continue;
								ready.add(id);
								for (String next : dependents.getOrDefault(id, java.util.Collections.emptySet()))
									if (queued.add(next))
										pending.addLast(next);
							}
							pending.addAll(roots);
							while (!pending.isEmpty()) {
								String output = pending.removeFirst();
								if (!ready.contains(output) || !reachable.add(output))
									continue;
								if (redirects.containsKey(output))
									pending.addLast(redirects.get(output));
								if (java.util.Set.of("BLOCKED", "EXTERNAL", "AUXILIARY", "ALIAS").contains(kinds.get(output)))
									continue;
								for (var value : routes.getOrDefault(output, new com.google.gson.JsonArray())) {
									var recipe = value.getAsJsonObject();
									if (!viable(recipe))
										continue;
									var inputs = new java.util.TreeSet<String>();
									for (var slot : recipe.getAsJsonArray("ingredientSlots"))
										for (var input : slot.getAsJsonArray())
											if (ready.contains(input.getAsString()))
												inputs.add(input.getAsString());
									for (String input : inputs) {
										uses.computeIfAbsent(input, ignored -> new java.util.TreeSet<>()).add(physicalContext(output, recipe, input));
										pending.addLast(input);
									}
								}
							}
						}
					}

					final java.util.Set<String> culinaryRelevantIds = new java.util.HashSet<>();
					final com.google.gson.JsonObject culinaryGroupingAudit = new com.google.gson.JsonObject();

					CulinaryDiscoveryUses indexCulinaryDiscoveryUses() {
						catalogDiagnostics.phase("FoodCatalogBuilder.indexCulinaryDiscoveryUses");
						var index = new CulinaryDiscoveryUses();
						var pending = new java.util.ArrayDeque<String>();
						for (var entry : catalog.entrySet())
							if ("FOOD".equals(entry.getValue().get("role").getAsString()) || entry.getValue().get("can_serve").getAsBoolean()) {
								index.roots.add(entry.getKey());
								pending.addLast(entry.getKey());
							}
						var visited = new java.util.HashSet<String>();
						while (!pending.isEmpty()) {
							String id = pending.removeFirst();
							if (!visited.add(id))
								continue;
							catalogDiagnostics.current = id;
							String kind = aliases.containsKey(id) ? "ALIAS" : catalog.containsKey(id) ? catalog.get(id).get("role").getAsString() : inputKind(id);
							index.kinds.put(id, kind);
							if ("ALIAS".equals(kind)) {
								index.redirects.put(id, aliases.get(id));
								pending.addLast(aliases.get(id));
							}
							var exported = new com.google.gson.JsonArray();
							index.routes.put(id, exported);
							if (java.util.Set.of("BLOCKED", "AUXILIARY", "EXTERNAL", "ALIAS").contains(kind))
								continue;
							for (var recipe : recipes(id)) {
								var data = new com.google.gson.JsonObject();
								data.add("ingredientSlots", routeJson(recipe.slots));
								exported.add(data);
								for (var slot : recipe.slots)
									pending.addAll(slot);
							}
						}
						index.index();
						culinaryRelevantIds.addAll(index.reachable);
						culinaryGroupingAudit.addProperty("policy",
								"Only anchored, complete routes from Food or directly servable Ingredients. Equivalent preparation outputs share a use signature when companion slots agree; physical recipe IDs, methods, quantities and source evidence remain unchanged.");
						culinaryGroupingAudit.addProperty("indexed_nodes", index.kinds.size());
						culinaryGroupingAudit.addProperty("reachable_nodes", index.reachable.size());
						culinaryGroupingAudit.addProperty("signature_inputs", index.uses.size());
						return index;
					}

					final java.util.Map<String, String> ingredientGroupReasons = new java.util.TreeMap<>();
					final java.util.Map<String, com.google.gson.JsonObject> ingredientVariantProfiles = new java.util.TreeMap<>();

					java.util.Set<String> exactIngredientTags(String id) {
						var result = new java.util.TreeSet<String>();
						var item = itemsById.get(id);
						if (item != null)
							new net.minecraft.world.item.ItemStack(item).getTags().forEach(tag -> result.add(tag.location().toString()));
						return result;
					}

					String cropFamily(String tag) {
						String p;
						if (tag.startsWith("c:crops/"))
							p = tag.substring(8);
						else if (tag.startsWith("forge:crops/"))
							p = tag.substring(12);
						else if (tag.startsWith("c:foods/vegetables/"))
							p = tag.substring(19);
						else if (tag.startsWith("c:foods/fruits/"))
							p = tag.substring(15);
						else
							return "";
						if (p.isEmpty() || p.contains("/") || java.util.Set.of("fruit", "fruits", "vegetable", "vegetables", "grain", "grains", "cereal", "cereals", "seed", "seeds", "berry", "berries", "mushroom", "mushrooms").contains(p))
							return "";
						// Common singular/plural spellings describe one crop, not a fuzzy item-name match.
						if (p.endsWith("oes"))
							p = p.substring(0, p.length() - 2);
						else if (p.endsWith("ies"))
							p = p.substring(0, p.length() - 3) + "y";
						else if (p.endsWith("s") && !p.endsWith("ss") && !p.endsWith("us"))
							p = p.substring(0, p.length() - 1);
						return p;
					}

					boolean rawGroupCandidate(String id) {
						var entry = catalog.get(id);
						return entry != null && "INGREDIENT".equals(entry.get("role").getAsString()) && !aliases.containsKey(id) && !classificationRules.overrides.containsKey(id) && !blocked.contains(id) && !plantingSeedName(id) && !pantryName(id)
								&& !preparedName(id) && recipes(id).isEmpty();
					}

					String chooseGroupRepresentative(java.util.List<String> members) {
						var order = java.util.Comparator.comparingInt((String id) -> sourceMinimum(id, planner.ingredientTierCount)).thenComparingInt(id -> basicSupplies.contains(id) ? 0 : 1).thenComparingDouble(id -> {
							double cost = acquisitionSources.costs.getOrDefault("item|" + id, Double.POSITIVE_INFINITY);
							return Double.isFinite(cost) ? cost : Double.POSITIVE_INFINITY;
						}).thenComparing(id -> id.startsWith("minecraft:") ? 0 : 1).thenComparing(id -> id);
						return members.stream().min(order).orElseThrow();
					}

					void mergeIngredientGroup(java.util.List<String> members, String reason) {
						if (members.size() < 2)
							return;
						String base = chooseGroupRepresentative(members);
						ingredientGroupReasons.put(base, reason);
						for (String id : members) {
							var profile = catalog.get(id).deepCopy();
							profile.add("acquisition", acquisitionSources.describe(id, planner.ingredientTierCount));
							var tags = new com.google.gson.JsonArray();
							for (String tag : exactIngredientTags(id))
								tags.add(tag);
							profile.add("semantic_tags", tags);
							ingredientVariantProfiles.put(id, profile);
							if (!id.equals(base))
								aliases.put(id, base);
						}
					}

					void groupIngredientDiscoveries() {
						catalogDiagnostics.phase("FoodCatalogBuilder.groupIngredientDiscoveries");
						var culinaryIndex = indexCulinaryDiscoveryUses();
						// Families are exact semantic crop tags. Broad food tags never establish a family.
						var knownFamilies = new java.util.TreeSet<String>();
						var tagsById = new java.util.TreeMap<String, java.util.Set<String>>();
						for (String id : catalog.keySet())
							if (rawGroupCandidate(id)) {
								var tags = exactIngredientTags(id);
								tagsById.put(id, tags);
								for (String tag : tags) {
									String family = cropFamily(tag);
									if (!family.isEmpty())
										knownFamilies.add(family);
								}
							}
						var families = new java.util.TreeMap<String, java.util.List<String>>();
						for (var entry : tagsById.entrySet()) {
							String id = entry.getKey();
							// Non-edible inputs use the stricter complete culinary-use signature below.
							if (!catalog.get(id).get("edible").getAsBoolean() || !culinaryIndex.reachable.contains(id))
								continue;
							var candidates = new java.util.TreeSet<String>();
							for (String tag : entry.getValue()) {
								String family = cropFamily(tag);
								if (family.isEmpty() && tag.startsWith("c:foods/") && tag.substring(8).indexOf('/') < 0)
									family = cropFamily("c:crops/" + tag.substring(8));
								if (knownFamilies.contains(family))
									candidates.add(family);
							}
							// Do not transitively join ambiguous tags into unrelated ingredient families.
							if (candidates.size() == 1) {
								String key = candidates.first() + "|serve=" + catalog.get(id).get("can_serve").getAsBoolean();
								families.computeIfAbsent(key, ignored -> new java.util.ArrayList<>()).add(id);
							}
						}
						for (var entry : families.entrySet())
							mergeIngredientGroup(entry.getValue(), "exact_crop_family:" + entry.getKey());
						var uses = culinaryIndex.uses;
						var botanicalGroups = new java.util.TreeMap<String, java.util.List<String>>();
						for (String id : catalog.keySet())
							if (rawGroupCandidate(id) && !catalog.get(id).get("edible").getAsBoolean() && !catalog.get(id).get("can_serve").getAsBoolean() && uses.containsKey(id) && !uses.get(id).isEmpty()) {
								String signature = String.join("\n", uses.get(id));
								botanicalGroups.computeIfAbsent(signature, ignored -> new java.util.ArrayList<>()).add(id);
							}
						for (var members : botanicalGroups.values())
							mergeIngredientGroup(members, members.stream().allMatch(classificationRules::botanical) ? "equivalent_botanical_culinary_functions" : "equivalent_non_edible_culinary_functions");
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
						return java.util.Set.of("icing", "frosting", "dough", "batter", "flour", "broth", "sugar", "salt", "butter", "cream", "cheese", "mozzarella", "yeast", "vinegar", "oil", "mayonnaise", "mayonaise", "ketchup", "raw_pasta")
								.contains(p) || p.endsWith("_icing") || p.endsWith("_frosting") || p.endsWith("_dough") || p.endsWith("_batter") || p.endsWith("_flour") || p.endsWith("_broth") || p.endsWith("_sauce") || p.endsWith("_extract")
								|| p.endsWith("_oil");
					}

					boolean preparedName(String id) {
						String p = path(id);
						return p.contains("cake_slice") || p.contains("pie_slice") || p.contains("bread_slice") || p.contains("pizza_slice") || p.endsWith("cheesecake") || p.contains("charcuterie_plate") || p.contains("_plate_plain")
								|| p.contains("_plate_holiday") || p.contains("_plate_delftware") || p.endsWith("_cake") || p.endsWith("_pie") || p.endsWith("_loaf") || p.endsWith("_sandwich") || p.endsWith("_pizza") || p.endsWith("_stew")
								|| p.endsWith("_soup") || p.endsWith("_curry") || p.endsWith("_dinner") || p.endsWith("_meal") && classificationRules.isEdible(id);
					}

					boolean rawTagged(String id) {
						return classificationRules.matchesTags(id, classificationRules.ingredientTags) || !classificationRules.isEdible(id) && classificationRules.matchesTags(id, classificationRules.supplementalIngredientTags);
					}

					boolean starterCandidate(String id, String role, boolean canServe) {
						return "INGREDIENT".equals(role) && canServe && !pantryName(id) && !plantingSeedName(id) && !preparedName(id) && recipes(id).isEmpty() && basicSupplies.contains(id);
					}

					java.util.BitSet starterRecipeCoverage(com.google.gson.JsonArray recipes, java.util.Map<String, java.util.BitSet> coverage, int count) {
						var result = new java.util.BitSet(count);
						for (var value : recipes) {
							var recipe = value.getAsJsonObject();
							if (recipe.has("source_evidence") && recipe.getAsJsonObject("source_evidence").has("source_conditions"))
								continue;
							var route = new java.util.BitSet(count);
							route.set(0, count);
							for (var slot : value.getAsJsonObject().getAsJsonArray("ingredientSlots")) {
								var alternatives = new java.util.BitSet(count);
								for (var input : slot.getAsJsonArray()) {
									var known = coverage.get(input.getAsString());
									if (known != null)
										alternatives.or(known);
								}
								route.and(alternatives);
							}
							result.or(route);
						}
						return result;
					}

					boolean rawStarterCrop(String id) {
						if (pantryName(id) || preparedName(id) || plantingSeedName(id) || classificationRules.technicalIntermediate(id))
							return false;
						var tags = new java.util.ArrayList<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>>();
						for (String name : java.util.List.of("c:crops", "c:foods/fruit", "c:foods/vegetable"))
							tags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse(name)));
						if (!classificationRules.matchesTags(id, tags))
							return false;
						// A surviving crop tag is useful evidence; it does not override known progression/source gates.
						if (sourceMinimum(id, planner.tierCount) > 0)
							return false;
						for (var recipe : allowedRecipes(id)) {
							if (scoreResolver.repackagingRecipe(recipe, id))
								continue;
							boolean culinary = false;
							for (var slot : recipe.slots)
								for (String input : slot)
									if (!id.equals(input) && !auxiliary(input) && classificationRules.isCulinary(input))
										culinary = true;
							if (culinary)
								return false;
						}
						return true;
					}

					final java.util.Set<String> assuredStarterSources = new java.util.HashSet<>();
					final com.google.gson.JsonObject starterSourceReview = new com.google.gson.JsonObject();

					void indexStarterSources() {
						catalogDiagnostics.phase("FoodCatalogBuilder.indexStarterSources");
						assuredStarterSources.clear();
						for (String id : itemsById.keySet())
							if (sourceMinimum(id, planner.tierCount) == 0 && (basicSupplies.contains(id) || classificationRules.sourceTier(id) == 0))
								assuredStarterSources.add(id);
						for (int pass = 0; pass <= itemsById.size(); pass++) {
							catalogDiagnostics.pass = pass;
							boolean changed = false;
							for (String id : itemsById.keySet()) {
								if (assuredStarterSources.contains(id) || sourceMinimum(id, planner.tierCount) > 0)
									continue;
								var source = acquisitionSources.describe(id, planner.tierCount);
								if (!source.has("status") || !"estimated".equals(source.get("status").getAsString()) || !source.has("effort") || source.get("effort").getAsDouble() > 0)
									continue;
								if (!source.has("requirements") || source.has("source_conditions"))
									continue;
								var requirements = source.getAsJsonArray("requirements");
								if (requirements.isEmpty())
									continue;
								boolean complete = true;
								for (var slot : requirements) {
									boolean any = false;
									for (var input : slot.getAsJsonArray()) {
										String key = input.getAsString();
										if (key.startsWith("item|") && (assuredStarterSources.contains(key.substring(5)) || auxiliary(key.substring(5))))
											any = true;
									}
									if (!any) {
										complete = false;
										break;
									}
								}
								if (complete && assuredStarterSources.add(id))
									changed = true;
							}
							if (!changed)
								break;
						}
					}

					void expandStarterPool() {
						catalogDiagnostics.phase("FoodCatalogBuilder.expandStarterPool");
						indexStarterSources();
						// Evaluate every edible tier-zero ingredient simultaneously; Food nodes never auto-discover one another.
						var candidates = new java.util.ArrayList<SplitTierPlanner.Node>();
						for (var node : planner.nodes.values())
							if ("INGREDIENT".equals(node.kind) && node.tier == 0 && node.canServe)
								candidates.add(node);
						int count = candidates.size();
						java.util.Map<String, java.util.BitSet> coverage = new java.util.HashMap<>();
						for (var node : planner.nodes.values()) {
							catalogDiagnostics.current = node.id;
							var bits = new java.util.BitSet(count);
							if ("AUXILIARY".equals(node.kind) || "EXTERNAL".equals(node.kind))
								bits.set(0, count);
							coverage.put(node.id, bits);
						}
						for (int i = 0; i < count; i++)
							coverage.get(candidates.get(i).id).set(i);
						for (int pass = 0; pass <= planner.nodes.size(); pass++) {
							catalogDiagnostics.pass = pass;
							boolean changed = false;
							for (var node : planner.nodes.values()) {
								catalogDiagnostics.current = node.id;
								java.util.BitSet bits;
								if ("ALIAS".equals(node.kind))
									bits = coverage.get(node.aliasOf);
								else if ("INTERMEDIATE".equals(node.kind))
									bits = starterRecipeCoverage(recipeData.get(node.id), coverage, count);
								else
									continue;
								if (bits == null)
									continue;
								var next = (java.util.BitSet) coverage.get(node.id).clone();
								next.or(bits);
								if (!next.equals(coverage.get(node.id))) {
									coverage.put(node.id, next);
									changed = true;
								}
							}
							if (!changed)
								break;
						}
						var paths = new java.util.ArrayList<java.util.Set<String>>();
						for (int i = 0; i < count; i++)
							paths.add(new java.util.TreeSet<>());
						for (var node : planner.nodes.values())
							if ("FOOD".equals(node.kind) && node.tier == 0 && node.canServe) {
								var bits = starterRecipeCoverage(recipeData.get(node.id), coverage, count);
								for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1))
									paths.get(i).add(node.id);
							}
						var weak = new java.util.ArrayList<SplitTierPlanner.Node>();
						int strong = 0;
						for (int i = 0; i < count; i++) {
							var node = candidates.get(i);
							boolean hasFood = !paths.get(i).isEmpty();
							boolean nativeChoice = node.starterEligible;
							node.starterEligible = nativeChoice || hasFood && rawStarterCrop(node.id);
							if (!node.starterEligible)
								continue;
							boolean assured = assuredStarterSources.contains(node.id);
							var review = new com.google.gson.JsonObject();
							review.addProperty("has_food_path", hasFood);
							review.addProperty("source_evidence", assured ? "baseline_or_supported_anchored_route" : "unverified_source");
							starterSourceReview.add(node.id, review);
							if (hasFood && assured)
								strong++;
							else
								weak.add(node);
							var foods = new com.google.gson.JsonArray();
							for (String id : paths.get(i))
								foods.add(id);
							starterFoodPaths.add(node.id, foods);
						}
						// The caller must use starter_safe_options until one safe discovery exists.
						// Broad candidates remain available for the remaining startup choices.
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
						return !classificationRules.overrides.containsKey(id) && !classificationRules.isEdible(id) && !ingredientDiscoveryProfile(id)
								&& (recoveredIntermediateInputs.contains(id) || !pantryName(id) && (preparedSources.contains(id) || preparedName(id)));
					}

					int declaredSourceMinimum(String id, int tiers) {
						int minimum = acquisitionSources.minimumTier(id, tiers);
						// Dimension and boss ingredients must not enter the initial supply tier.
						if ("minecraft:dragon_egg".equals(id) || "minecraft:nether_star".equals(id))
							minimum = Math.max(minimum, tiers - 1);
						else if ("minecraft:blaze_powder".equals(id))
							minimum = Math.max(minimum, (int) Math.ceil((tiers - 1) * 0.6));
						else if ("minecraft:warped_fungus".equals(id) || "minecraft:crimson_fungus".equals(id))
							minimum = Math.max(minimum, (int) Math.ceil((tiers - 1) * 0.4));
						// Exact compatibility profile: these routes originate from Ad Astra's Moon cheese ore.
						if (java.util.Set.of("ad_astra:cheese", "ad_astra:cheese_block", "ad_astra:moon_cheese_ore").contains(id))
							minimum = Math.max(minimum, (int) Math.ceil((tiers - 1) * 0.75));
						return minimum;
					}

					int sourceMinimum(String id, int tiers) {
						return Math.min(tiers - 1, Math.max(declaredSourceMinimum(id, tiers), sourceFloors.getOrDefault(id, 0)));
					}

					void measureSourceFloors() {
						catalogDiagnostics.phase("FoodCatalogBuilder.measureSourceFloors");
						for (var node : planner.nodes.values()) {
							catalogDiagnostics.current = node.id;
							if ("BLOCKED".equals(node.kind) || "ALIAS".equals(node.kind))
								continue;
							if ("AUXILIARY".equals(node.kind))
								sourceFloors.put(node.id, 0);
							else if ("EXTERNAL".equals(node.kind) || "INGREDIENT".equals(node.kind) && node.routes.isEmpty())
								sourceFloors.put(node.id, declaredSourceMinimum(node.id, planner.tierCount));
						}
						for (int pass = 0; pass <= planner.nodes.size(); pass++) {
							catalogDiagnostics.pass = pass;
							boolean changed = false;
							for (var node : planner.nodes.values()) {
								catalogDiagnostics.current = node.id;
								if ("BLOCKED".equals(node.kind) || "EXTERNAL".equals(node.kind) || "AUXILIARY".equals(node.kind))
									continue;
								int best = sourceFloors.getOrDefault(node.id, SplitTierPlanner.UNREACHABLE);
								if ("ALIAS".equals(node.kind))
									best = Math.min(best, Math.max(node.minimum, sourceFloors.getOrDefault(node.aliasOf, SplitTierPlanner.UNREACHABLE)));
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
						// Hunting evidence describes acquisition effort, never a free cooking recipe.
						return source.has("requirements") && !source.has("source_conditions");
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
						if (classificationRules.technicalIntermediate(id) || auxiliary(id) || technicalMaterial(id) || excludedFoods.contains(id) || classificationRules.excludedItems.contains(id))
							return false;
						if ("FOOD".equals(classificationRules.hint(id)) && !pantryName(id) && !rawTagged(id))
							return false;
						if (botanicalKitchenInput(id) || referencedKitchenInputs.contains(id) && (recipeOnlyRawProfiles.contains(id) || culinarySeeds.contains(id)))
							return true;
						return vanillaIngredients.contains(id) || classificationRules.culinaryIds.contains(id) || classificationRules.pantryItems.contains(id) || rawTagged(id)
								|| referencedKitchenInputs.contains(id) && classificationRules.isCulinary(id) || (foodProgression.recipes.containsKey(id) && classificationRules.isCulinary(id));
					}

					boolean culinaryInput(String id) {
						return !auxiliary(id) && !technicalMaterial(id) && (catalog.containsKey(id) || vanillaIngredients.contains(id) || botanicalKitchenInput(id) || classificationRules.isCulinary(id) || classificationRules.pantryItems.contains(id)
								|| pantryName(id) || preparedSources.contains(id) || preparedName(id) || rawTagged(id) || recoveredIntermediateInputs.contains(id));
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
						if (completeRecipeInputs.fluidNode(id)) {
							for (var holder : completeRecipeInputs.fluidProduction.get(id)) {
								var slots = completeRecipeInputs.slots(holder);
								if (!slots.isEmpty())
									result.add(scoreResolver.new IndexedRecipe(holder, classificationRules.typeId(holder), 1, slots));
							}
							allowedRecipeCache.put(id, result);
							return result;
						}
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
						if (ingredientVariantProfiles.containsKey(id) && aliases.containsKey(id))
							return java.util.Collections.emptyList();
						if (completeRecipeInputs.fluidNode(id))
							return allowedRecipes(id);
						if (auxiliary(id) || technicalMaterial(id))
							return java.util.Collections.emptyList();
						boolean dish = catalog.containsKey(id) && "FOOD".equals(catalog.get(id).get("role").getAsString());
						if (classificationRules.technicalIntermediate(id) || dish || recoveredIntermediateInputs.contains(id) || preparedSources.contains(id) || preparedName(id) || classificationRules.pantryItems.contains(id) || pantryName(id))
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
						double work = unlockDifficulty.getOrDefault(id, 6.0);
						int depth = unlockDepth.getOrDefault(id, 0);
						if ("FOOD".equals(role)) {
							double difficulty = unlockDifficulty.getOrDefault(id, 6.0);
							return Math.round((difficulty + unlockDepth.getOrDefault(id, 0) * 1.5) * 100.0) / 100.0;
						}
						double source = acquisitionSources.costs.getOrDefault("item|" + id, 0.0);
						if (!Double.isFinite(source))
							source = sourceMinimum(id, planner.tierCount) * classificationRules.stepsPerTier;
						source = Math.max(0, source);
						// Availability and production effort come first; usefulness distinguishes cheap base ingredients.
						double usefulness = firstFoodUse.containsKey(id) ? 20.0 * Math.min(1.0, firstFoodUse.get(id) / foodRankReference) : 25.0;
						double result = source * 10.0 + work * 2.0 + depth * 0.5 + usefulness + (3 - Math.min(3, foodUses.getOrDefault(id, 0))) * 0.5;
						return Math.round(result * 100.0) / 100.0;
					}

					void measureIngredientRanks() {
						catalogDiagnostics.phase("FoodCatalogBuilder.measureIngredientRanks");
						// A processing step cannot rank before the easiest complete route's raw supplies.
						// This orders discovery choices; it does not require unlocking each raw input to buy/use a prepared ingredient.
						java.util.Map<String, Double> bounds = new java.util.HashMap<>();
						for (var node : planner.nodes.values()) {
							catalogDiagnostics.current = node.id;
							if ("INGREDIENT".equals(node.kind) && catalog.containsKey(node.id)) {
								double base = rank(node.id, "INGREDIENT", catalog.get(node.id));
								ingredientBaseRanks.put(node.id, base);
								if (recipeData.get(node.id).isEmpty())
									bounds.put(node.id, base);
							} else if ("AUXILIARY".equals(node.kind) || "EXTERNAL".equals(node.kind))
								bounds.put(node.id, 0.0);
						}
						for (int pass = 0; pass <= planner.nodes.size(); pass++) {
							catalogDiagnostics.pass = pass;
							boolean changed = false;
							for (var node : planner.nodes.values()) {
								catalogDiagnostics.current = node.id;
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
						catalogDiagnostics.phase("FoodCatalogBuilder.measureFoundationalPromotions");
						java.util.Set<String> ready = new java.util.HashSet<>();
						for (var node : planner.nodes.values())
							if (sourceMinimum(node.id, planner.tierCount) == 0 && (basicSupplies.contains(node.id) || "AUXILIARY".equals(node.kind) || "EXTERNAL".equals(node.kind)))
								ready.add(node.id);
						boolean changed;
						do {
							changed = false;
							for (var node : planner.nodes.values()) {
								catalogDiagnostics.current = node.id;
								if (ready.contains(node.id) || sourceMinimum(node.id, planner.tierCount) > 0)
									continue;
								if ("ALIAS".equals(node.kind)) {
									if (ready.contains(node.aliasOf) && ready.add(node.id))
										changed = true;
									continue;
								}
								boolean foundation = "INGREDIENT".equals(node.kind) && catalog.containsKey(node.id)
										&& (foundationalPantry(node.id) || pantryName(node.id) && unlockDepth.getOrDefault(node.id, 99) <= 1 && unlockDifficulty.getOrDefault(node.id, Double.POSITIVE_INFINITY) <= 4.0)
										&& unlockDifficulty.getOrDefault(node.id, Double.POSITIVE_INFINITY) <= 6.0 && unlockDepth.getOrDefault(node.id, 0) <= 3;
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
						catalogDiagnostics.phase("FoodCatalogBuilder.measureKitchenWork");
						// Recompute culinary production cost independently of nutrition and legacy farming/essence chains.
						for (var node : planner.nodes.values()) {
							catalogDiagnostics.current = node.id;
							if ("BLOCKED".equals(node.kind) || "ALIAS".equals(node.kind) && !ingredientVariantProfiles.containsKey(node.id))
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
							catalogDiagnostics.pass = pass;
							boolean changed = false;
							for (var node : planner.nodes.values()) {
								catalogDiagnostics.current = node.id;
								if ("ALIAS".equals(node.kind) && !ingredientVariantProfiles.containsKey(node.id)) {
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

					void measureUnlockDifficulty() {
						catalogDiagnostics.phase("FoodCatalogBuilder.measureUnlockDifficulty");
						// Discovery complexity is independent of batch yield and repeated copies of the same discovered ingredient.
						for (var node : planner.nodes.values()) {
							catalogDiagnostics.current = node.id;
							if ("BLOCKED".equals(node.kind) || "ALIAS".equals(node.kind))
								continue;
							boolean automatic = "AUXILIARY".equals(node.kind) || "EXTERNAL".equals(node.kind);
							if (!automatic && !recipeData.get(node.id).isEmpty())
								continue;
							if (!automatic && !"INGREDIENT".equals(node.kind))
								continue;
							double source = acquisitionSources.costs.getOrDefault("item|" + node.id, 0.0);
							if (!Double.isFinite(source))
								source = node.minimum * classificationRules.stepsPerTier;
							unlockDifficulty.put(node.id,
									"AUXILIARY".equals(node.kind) ? 0.0 : "EXTERNAL".equals(node.kind) ? kitchenCosts.getOrDefault(node.id, 1.0 + Math.min(12.0, Math.max(0.0, source))) : 1.0 + Math.min(12.0, Math.max(0.0, source)));
							unlockDepth.put(node.id, 0);
						}
						for (int pass = 0; pass <= planner.nodes.size(); pass++) {
							catalogDiagnostics.pass = pass;
							boolean changed = false;
							for (var node : planner.nodes.values()) {
								catalogDiagnostics.current = node.id;
								String base = "ALIAS".equals(node.kind) ? node.aliasOf : reversibleStorage.get(node.id);
								if (base != null && !base.isEmpty()) {
									if (unlockDifficulty.containsKey(base)) {
										double candidate = unlockDifficulty.get(base);
										int depth = unlockDepth.get(base);
										if (candidate + depth * 1.5 + 0.0001 < unlockDifficulty.getOrDefault(node.id, Double.POSITIVE_INFINITY) + unlockDepth.getOrDefault(node.id, 0) * 1.5) {
											unlockDifficulty.put(node.id, candidate);
											unlockDepth.put(node.id, depth);
											changed = true;
										}
									}
									continue;
								}
								if ("BLOCKED".equals(node.kind) || "AUXILIARY".equals(node.kind) || "EXTERNAL".equals(node.kind))
									continue;
								for (var value : recipeData.get(node.id)) {
									var recipe = value.getAsJsonObject();
									if (packingOnly(node.id, recipe))
										continue;
									double total = 0.0;
									int depth = 0;
									boolean possible = true;
									java.util.Set<String> counted = new java.util.HashSet<>();
									for (var slot : recipe.getAsJsonArray("ingredientSlots")) {
										String selected = null;
										double best = Double.POSITIVE_INFINITY;
										for (var input : slot.getAsJsonArray()) {
											String id = input.getAsString();
											double score = unlockDifficulty.getOrDefault(id, Double.POSITIVE_INFINITY) + unlockDepth.getOrDefault(id, 0) * 1.5;
											if (score < best || Double.isFinite(score) && Double.compare(score, best) == 0 && (selected == null || id.compareTo(selected) < 0)) {
												selected = id;
												best = score;
											}
										}
										if (selected == null) {
											possible = false;
											break;
										}
										// External materials have no discovery card: preserve their physical acquisition cost and quantity.
										if ("EXTERNAL".equals(planner.nodes.get(selected).kind) || counted.add(selected))
											total += unlockDifficulty.get(selected);
										depth = Math.max(depth, unlockDepth.getOrDefault(selected, 0));
									}
									if (!possible)
										continue;
									String method = recipe.get("method").getAsString();
									double step = method.contains("cutting") || method.contains("slicing")
											? 0.5
											: method.contains("smelting") || method.contains("smoking") || method.contains("campfire_cooking") ? 1.0 : method.contains("crafting") ? 1.5 : 2.0;
									double candidate = total + step;
									depth++;
									if (candidate + depth * 1.5 + 0.0001 < unlockDifficulty.getOrDefault(node.id, Double.POSITIVE_INFINITY) + unlockDepth.getOrDefault(node.id, 0) * 1.5) {
										unlockDifficulty.put(node.id, candidate);
										unlockDepth.put(node.id, depth);
										changed = true;
									}
								}
							}
							if (!changed)
								break;
						}
					}

					void measureFoodUses() {
						catalogDiagnostics.phase("FoodCatalogBuilder.measureFoodUses");
						firstFoodUse.clear();
						foodUses.clear();
						java.util.List<String> roots = new java.util.ArrayList<>();
						java.util.List<Double> ranks = new java.util.ArrayList<>();
						java.util.List<Double> sortedRanks = new java.util.ArrayList<>();
						for (var entry : catalog.entrySet())
							if ("FOOD".equals(entry.getValue().get("role").getAsString())) {
								double value = Math.max(0.1, rank(entry.getKey(), "FOOD", entry.getValue()));
								roots.add(entry.getKey());
								ranks.add(value);
								sortedRanks.add(value);
							}
						java.util.Collections.sort(sortedRanks);
						if (!sortedRanks.isEmpty())
							foodRankReference = Math.max(1.0, sortedRanks.get((int) Math.floor((sortedRanks.size() - 1) * 0.75)));
						// Each bit identifies one Food root. Shared dependencies and cycles propagate these identities together.
						java.util.Map<String, java.util.BitSet> reachedBy = new java.util.HashMap<>();
						java.util.Map<String, java.util.List<String>> adjacency = new java.util.HashMap<>();
						java.util.ArrayDeque<String> pending = new java.util.ArrayDeque<>();
						java.util.Set<String> queued = new java.util.HashSet<>();
						for (int i = 0; i < roots.size(); i++) {
							String id = roots.get(i);
							reachedBy.computeIfAbsent(id, key -> new java.util.BitSet(roots.size())).set(i);
							if (queued.add(id))
								pending.addLast(id);
						}
						long processed = 0, edges = 0;
						while (!pending.isEmpty()) {
							String id = pending.removeFirst();
							queued.remove(id);
							catalogDiagnostics.current = id;
							catalogDiagnostics.pass = (int) Math.min(Integer.MAX_VALUE, ++processed);
							var inputs = adjacency.get(id);
							if (inputs == null) {
								java.util.Set<String> unique = new java.util.LinkedHashSet<>();
								if (aliases.containsKey(id))
									unique.add(aliases.get(id));
								else {
									// Deduplicate before tag/equipment inspection, once per graph node rather than once per Food.
									java.util.Set<String> candidates = new java.util.LinkedHashSet<>();
									for (var recipe : recipes(id))
										for (var slot : recipe.slots)
											candidates.addAll(slot);
									for (String input : candidates)
										if (!auxiliary(input) && !blocked.contains(input))
											unique.add(input);
								}
								inputs = new java.util.ArrayList<>(unique);
								adjacency.put(id, inputs);
								edges += inputs.size();
							}
							var incoming = reachedBy.get(id);
							for (String input : inputs) {
								var target = reachedBy.computeIfAbsent(input, key -> new java.util.BitSet(roots.size()));
								// Work only on newly arriving root identities; one Food contributes at most once per ingredient.
								var delta = (java.util.BitSet) incoming.clone();
								delta.andNot(target);
								if (delta.isEmpty())
									continue;
								target.or(delta);
								if (queued.add(input))
									pending.addLast(input);
							}
						}
						for (var entry : reachedBy.entrySet()) {
							String id = entry.getKey();
							if (aliases.containsKey(id))
								continue;
							var ingredient = catalog.get(id);
							if (ingredient == null || !"INGREDIENT".equals(ingredient.get("role").getAsString()))
								continue;
							var bits = entry.getValue();
							double best = Double.POSITIVE_INFINITY;
							for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1))
								best = Math.min(best, ranks.get(i));
							if (!bits.isEmpty()) {
								firstFoodUse.put(id, best);
								foodUses.put(id, bits.cardinality());
							}
						}
						net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef Food-use graph: {} roots, {} shared nodes, {} unique edges, {} queue visits.", roots.size(), adjacency.size(), edges, processed);
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
						catalogDiagnostics.phase("FoodCatalogBuilder.neededIngredients");
						java.util.Set<String> visited = new java.util.HashSet<>(), needed = new java.util.HashSet<>();
						java.util.ArrayDeque<String> pending = new java.util.ArrayDeque<>();
						// Relevance is structural. An unresolved sibling slot does not erase a valid ingredient's use.
						for (var node : planner.nodes.values())
							if (node.canServe)
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
							for (var value : recipeData.getOrDefault(id, new com.google.gson.JsonArray())) {
								var recipe = value.getAsJsonObject();
								if (packingOnly(id, recipe) && !"INTERMEDIATE".equals(node.kind))
									continue;
								for (var slot : recipe.getAsJsonArray("ingredientSlots"))
									for (var input : slot.getAsJsonArray())
										if (planner.nodes.containsKey(input.getAsString()))
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
						catalogDiagnostics.phase("FoodCatalogBuilder.conditionFreeNodes");
						java.util.Set<String> usable = new java.util.HashSet<>();
						for (var node : planner.nodes.values())
							if (java.util.Set.of("INGREDIENT", "AUXILIARY", "EXTERNAL").contains(node.kind))
								usable.add(node.id);
						boolean changed;
						do {
							changed = false;
							for (var node : planner.nodes.values()) {
								catalogDiagnostics.current = node.id;
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
						catalogDiagnostics.phase("FoodCatalogBuilder.build");
						preparedSources.addAll(splitSources.wholeSources);
						for (var entry : disabledFoodsArray)
							blocked.add(entry.getAsJsonObject().get("id").getAsString());
						for (var entry : excludedFoodsArray)
							blocked.add(entry.getAsJsonObject().get("id").getAsString());
						blocked.addAll(excludedFoods);
						blocked.addAll(classificationRules.excludedItems);
						for (var value : excludedFoodsArray) {
							var entry = value.getAsJsonObject();
							String id = entry.get("id").getAsString();
							if (entry.has("reason") && "no_nutritional_value".equals(entry.get("reason").getAsString()) && !excludedFoods.contains(id) && !classificationRules.excludedItems.contains(id) && technicalMaterial(id))
								blocked.remove(id);
						}
						for (var old : allFoods) {
							String id = old.get("id").getAsString();
							if (classificationRules.technicalIntermediate(id))
								continue;
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
						recoverInputEvidence();
						java.util.Set<String> additions = new java.util.TreeSet<>();
						for (String id : itemsById.keySet())
							if (!catalog.containsKey(id) && !blocked.contains(id) && !classificationRules.isEdible(id) && !classificationRules.technicalIntermediate(id) && !auxiliary(id) && (culinaryIngredient(id) || pantryName(id))) {
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
						groupIngredientDiscoveries();
						// Runtime alias resolution is deliberately one hop.
						for (String id : new java.util.ArrayList<>(aliases.keySet())) {
							String base = aliases.get(id);
							var seen = new java.util.HashSet<String>();
							seen.add(id);
							while (aliases.containsKey(base)) {
								if (!seen.add(base))
									throw new IllegalStateException("Cyclic ingredient discovery group: " + id);
								base = aliases.get(base);
							}
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
							if (ingredientVariantProfiles.containsKey(alias.getKey())) {
								variant.addProperty("edible", original.get("edible").getAsBoolean());
								variant.add("physical_profile", ingredientVariantProfiles.get(alias.getKey()));
							}
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
							planner.add(alias.getKey(), "ALIAS", ingredientVariantProfiles.containsKey(alias.getKey()) ? sourceMinimum(alias.getKey(), splitTierCount) : 0, 0.0, false).aliasOf = alias.getValue();
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
								for (var recipe : "ALIAS".equals(node.kind) && !ingredientVariantProfiles.containsKey(id) ? allowedRecipes(id) : recipes(id)) {
									node.routes.add(recipe.slots);
									com.google.gson.JsonObject variant = new com.google.gson.JsonObject();
									variant.addProperty("method", recipe.method);
									if (recipe.holder != null)
										variant.addProperty("recipe_id", recipe.holder.id().toString());
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
											String kind = inputKind(input);
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
													String kind = inputKind(input);
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
							if ("INGREDIENT".equals(entry.getValue().get("role").getAsString()) && !entry.getValue().get("can_serve").getAsBoolean() && (!needed.contains(entry.getKey()) || !culinaryRelevantIds.contains(entry.getKey()))
									&& !classificationRules.overrides.containsKey(entry.getKey()))
								unnecessary.add(entry.getKey());
						for (String id : unnecessary) {
							catalog.remove(id);
							com.google.gson.JsonObject removed = new com.google.gson.JsonObject();
							removed.addProperty("id", id);
							removed.addProperty("reason", !culinaryRelevantIds.contains(id) ? "no_complete_culinary_path_to_servable_product" : "no_non_packing_path_to_servable_product");
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
						measureUnlockDifficulty();
						measureFoodUses();
						measureIngredientRanks();
						measureFoundationalPromotions();
						planner = new SplitTierPlanner(ingredientTierCount, splitTierCount);
						for (var old : candidatePlanner.nodes.values()) {
							if (!retainedNodes.contains(old.id))
								continue;
							boolean removed = "INGREDIENT".equals(old.kind) && !catalog.containsKey(old.id);
							String kind = removed ? (old.routes.isEmpty() ? "BLOCKED" : "INTERMEDIATE") : old.kind;
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
						var excludedProgression = new com.google.gson.JsonArray();
						var excludedProgressionIds = new java.util.HashSet<String>();
						for (var node : planner.nodes.values())
							if ("FOOD".equals(node.kind) && node.preparationTier == SplitTierPlanner.UNREACHABLE) {
								var entry = catalog.get(node.id).deepCopy();
								entry.addProperty("id", node.id);
								entry.addProperty("role", "FOOD");
								entry.addProperty("tier", -1);
								entry.addProperty("can_serve", false);
								entry.addProperty("starter_eligible", false);
								entry.addProperty("unlock_status", "excluded_unresolved_recipe");
								entry.addProperty("reason", unverifiedAcquisition.contains(node.id) ? "no_supported_recipe_or_verified_acquisition" : "blocked_or_cyclic_mandatory_requirement");
								entry.add("recipes", recipeData.get(node.id).deepCopy());
								excludedProgression.add(entry);
								excludedProgressionIds.add(node.id);
							}
						for (String id : excludedProgressionIds)
							catalog.remove(id);
						foodCount -= excludedProgressionIds.size();
						expandStarterPool();
						var conditionFree = conditionFreeNodes();
						var conditionalFoods = new com.google.gson.JsonArray();
						com.google.gson.JsonObject graph = new com.google.gson.JsonObject();
						com.google.gson.JsonObject ingredientTiers = new com.google.gson.JsonObject(), foodTiers = new com.google.gson.JsonObject();
						for (int tier = 0; tier < ingredientTierCount; tier++)
							ingredientTiers.add("" + tier, new com.google.gson.JsonArray());
						for (int tier = 0; tier < splitTierCount; tier++)
							foodTiers.add("" + tier, new com.google.gson.JsonArray());
						com.google.gson.JsonArray starters = new com.google.gson.JsonArray(), unresolved = new com.google.gson.JsonArray();
						var safeStarters = new com.google.gson.JsonArray();
						for (String id : new java.util.TreeSet<>(excludedProgressionIds))
							unresolved.add(id);
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
								if (ingredientGroupReasons.containsKey(node.id)) {
									var group = new com.google.gson.JsonObject();
									group.addProperty("reason", ingredientGroupReasons.get(node.id));
									group.addProperty("representative_id", node.id);
									group.addProperty("member_count", discoveryVariants.getAsJsonArray(node.id).size() + 1);
									group.addProperty("recipes_keep_physical_item_ids", true);
									group.addProperty("ranking_policy", "representative_is_lowest_access_floor_then_basic_supply_then_lowest_estimated_acquisition_effort; not_a_rarity_guarantee");
									group.add("representative_physical_profile", ingredientVariantProfiles.get(node.id));
									entry.add("discovery_group", group);
								}
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
								if (unlockDifficulty.containsKey(node.id))
									analysis.addProperty("unlock_difficulty", unlockDifficulty.get(node.id));
								analysis.addProperty("unlock_depth", unlockDepth.getOrDefault(node.id, 0));
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
								analysis.add("acquisition", acquisitionSources.describe(node.id, splitTierCount));
								entry.add("analysis", analysis);
								("INGREDIENT".equals(node.kind) ? ingredientTiers : foodTiers).getAsJsonArray("" + node.tier).add(entry);
								if (starter) {
									starters.add(node.id);
									if (assuredStarterSources.contains(node.id) && starterFoodPaths.has(node.id) && !starterFoodPaths.getAsJsonArray(node.id).isEmpty())
										safeStarters.add(node.id);
								}
								if (node.preparationTier == SplitTierPlanner.UNREACHABLE)
									unresolved.add(node.id);
							}
							com.google.gson.JsonObject graphNode = new com.google.gson.JsonObject();
							boolean excludedNode = !"INGREDIENT".equals(node.kind) && !"AUXILIARY".equals(node.kind) && !"EXTERNAL".equals(node.kind) && node.preparationTier == SplitTierPlanner.UNREACHABLE;
							graphNode.addProperty("kind", excludedNode ? "BLOCKED" : node.kind);
							if (completeRecipeInputs.fluidNode(node.id)) {
								graphNode.addProperty("fluid_id", completeRecipeInputs.fluidNodeIds.get(node.id));
								graphNode.addProperty("technical_node", true);
							}
							if (!excludedNode && "ALIAS".equals(node.kind))
								graphNode.addProperty("discovery_id", node.aliasOf);
							graphNode.addProperty("minimum_tier", node.minimum);
							if (planner.isCatalog(node))
								graphNode.addProperty("tier", node.tier);
							graphNode.addProperty("ingredient_unlock_required", "INGREDIENT".equals(node.kind));
							graphNode.add("recipes", excludedNode ? new com.google.gson.JsonArray() : recipeData.get(node.id));
							graph.add(node.id, graphNode);
							if ("EXTERNAL".equals(node.kind))
								external.add(node.id);
							if ("AUXILIARY".equals(node.kind))
								auxiliaries.add(node.id);
							if ("BLOCKED".equals(node.kind)) {
								String reason = itemsById.containsKey(node.id) ? "unverified_non_automatic_input" : "unregistered_item";
								if (CompleteRecipeInputs.INVALID_FLUID.equals(node.id))
									reason = "unresolved_mandatory_fluid";
								if (CompleteRecipeInputs.INVALID_ITEM.equals(node.id))
									reason = "unresolved_mandatory_item";
								if (plantingSeedName(node.id))
									reason = "propagation_seed_without_discovery";
								if ("minecraft:barrier".equals(node.id))
									reason = "invalid_recipe_placeholder";
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
						result.addProperty("split_generator_version", 34);
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
						result.addProperty("tier_numbering", "zero_based; JSON and summary tiers start at 0");
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
						result.add("starter_safe_options", safeStarters);
						result.addProperty("starter_selection_policy", "require_safe_discovery_before_broad_choices");
						result.addProperty("starter_choices_required", Math.min(3, starters.size()));
						result.addProperty("starter_status", safeStarters.isEmpty() ? "blocked_no_safe_starter" : starters.size() >= 3 ? "ready" : "reduced_pool");
						com.google.gson.JsonObject diagnostics = new com.google.gson.JsonObject();
						for (String role : java.util.List.of("INGREDIENT", "FOOD")) {
							com.google.gson.JsonArray sizes = new com.google.gson.JsonArray();
							for (int size : planner.groupSizes.get(role))
								sizes.add(size);
							diagnostics.add(role.toLowerCase(java.util.Locale.ROOT) + "_tier_sizes", sizes);
						}
						diagnostics.add("unresolved_foods", unresolved);
						diagnostics.add("excluded_progression", excludedProgression);
						diagnostics.add("starter_source_review", starterSourceReview);
						diagnostics.addProperty("unresolved_policy", "Unreachable Food is excluded before tier quotas; blocked graph nodes never become free inputs. Diagnostic records retain original recipe evidence.");
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
						var technicalItems = new com.google.gson.JsonArray();
						for (String id : classificationRules.technicalProcessingItems)
							technicalItems.add(id);
						diagnostics.add("technical_processing_items", technicalItems);
						var bakeryAdapter = new com.google.gson.JsonObject();
						bakeryAdapter.addProperty("completion_routes", culinaryAdapters.bakeryInteractions);
						bakeryAdapter.add("issues", culinaryAdapters.bakeryIssues);
						diagnostics.add("bakery_interactions", bakeryAdapter);
						diagnostics.add("kitchen_water_requirements", completeRecipeInputs.kitchenWaterRequirements);
						var storageEvidence = new com.google.gson.JsonObject();
						for (var storage : reversibleStorage.entrySet())
							storageEvidence.addProperty(storage.getKey(), storage.getValue());
						diagnostics.add("reversible_storage_inputs", storageEvidence);
						var fluidEvidence = new com.google.gson.JsonObject();
						for (var fluid : completeRecipeInputs.fluidNodeIds.entrySet())
							if (graph.has(fluid.getKey()))
								fluidEvidence.addProperty(fluid.getKey(), fluid.getValue());
						diagnostics.add("fluid_preparation_nodes", fluidEvidence);
						var recovered = new com.google.gson.JsonArray();
						for (String id : recoveredIntermediateInputs)
							if (graph.has(id))
								recovered.add(id);
						diagnostics.add("recovered_preparation_intermediates", recovered);
						var tagFallbacks = new com.google.gson.JsonObject();
						for (var entry : classificationRules.tagFallbackMatches.entrySet()) {
							var values = new com.google.gson.JsonArray();
							for (String value : entry.getValue())
								values.add(value);
							tagFallbacks.add(entry.getKey(), values);
						}
						diagnostics.add("tag_fallback_matches", tagFallbacks);
						diagnostics.add("recipe_requirement_issues", completeRecipeInputs.issues);
						diagnostics.add("fluid_discovery_mappings", completeRecipeInputs.fluidMappings);
						diagnostics.add("conditional_source_foods", conditionalFoods);
						diagnostics.addProperty("starter_policy",
								"Native starter profiles are preserved when safe. Additional edible tier-zero raw crops require positive crop/fruit/vegetable tags, no recognized source gate or culinary processing route, and an independently satisfiable tier-zero Food recipe. The first safe discovery must come from starter_safe_options. Afterwards starter_options provides broad raw-crop choices. The card generator must enforce this state-dependent policy. Crop tags alone do not prove acquisition; source evidence remains in starter_source_review. Baseline profiles are assumptions about ordinary survival, not guarantees under arbitrary pack removals.");
						diagnostics.add("starter_food_paths", starterFoodPaths);
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
						diagnostics.addProperty("graph_boundary_policy",
								"Explicit ingredient overrides, vanilla ingredient profiles, pantry profiles and ingredient tags retain discovery priority. Other supported non-edible processing routes used by culinary recipes are expanded from known culinary anchors, retaining every mandatory slot. Unknown inputs are BLOCKED. Verified non-culinary remainders from eating or crafting with culinary items are empty serving supplies. Recognized technical materials stop expansion before their crafting/recycling recipes. Referenced flower-tagged kitchen inputs retain ingredient discoveries; raw_materials/dyes never establishes an external material by itself. Other raw materials require no competing culinary declaration. Nutritional foods and explicit culinary profiles retain discovery priority over generic material tags. Only no-nutrition technical exclusions may become EXTERNAL; explicit exclusions remain blocked. Unknown removed discoveries never become free EXTERNAL inputs.");
						diagnostics.addProperty("ranking_policy",
								"Ingredients: source effort, yield-independent preparation difficulty/depth, earliest Food usefulness and easiest complete input-route rank floor. Early pantry priority covers basic flour/dough/salt/sugar/pasta and recognized simple one-step pantry preparations with zero source floor. Basic vanilla supplies include eggs, milk and sugar cane. Ingredient input floors affect ordering, not a new requirement to discover upstream ingredients. Food tiers use distinct-input preparation difficulty and processing depth without division by output yield. Production cost per portion remains separate; ingredient usefulness follows yield-independent Food difficulty. Per-portion production cost remains a separate production metric.");
						diagnostics.addProperty("variant_policy",
								"Known cosmetic variants, exact unambiguous semantic crop families, and non-edible raw leaves with equivalent culinary functions on anchored complete paths to servable products share discoveries. Broad food/seed tags, name resemblance, ambiguous families, processed ingredients and explicit overrides never establish a new group. Physical recipe IDs and per-variant source evidence are preserved. Tiers count discoveries, not variant IDs.");
						var grouping = new com.google.gson.JsonObject();
						for (var entry : ingredientGroupReasons.entrySet())
							if (catalog.containsKey(entry.getKey())) {
								var detail = new com.google.gson.JsonObject();
								detail.addProperty("reason", entry.getValue());
								detail.add("variants", discoveryVariants.get(entry.getKey()).deepCopy());
								grouping.add(entry.getKey(), detail);
							}
						diagnostics.add("ingredient_discovery_groups", grouping);
						diagnostics.add("culinary_grouping_audit", culinaryGroupingAudit);
						diagnostics.addProperty("seed_policy", "Propagation-named items require explicit singleton culinary use. The c:seeds tag alone does not exclude a crop from cooking. Unapproved propagation alternatives remain BLOCKED.");
						diagnostics.addProperty("requirements_policy",
								"Every item and supported fluid slot needs one usable alternative. Missing tags, placeholder barriers and unrepresented fluids retain BLOCKED mandatory requirements. FOOD intermediates are expanded; ingredient nodes require discovery. Unanchored cycles remain unresolved.");
						diagnostics.addProperty("live_catalog", true);
						com.google.gson.JsonObject technical = new com.google.gson.JsonObject();
						technical.add("diagnostics", diagnostics);
						technical.add("recipe_nodes", graph);
						result.add("technical", technical);
						return result;
					}

					String summary(com.google.gson.JsonObject database) {
						StringBuilder text = new StringBuilder("OMNICHEF — KATALOG INGREDIENTS / FOOD v34\nTiery ponizej oraz indeksy w JSON zaczynaja sie od 0.\n\n");
						text.append("Ingredients: ").append(database.get("ingredient_count")).append(" / tiery: ").append(database.get("ingredient_tier_count")).append('\n');
						text.append("Food: ").append(database.get("food_count")).append(" / tiery: ").append(database.get("food_tier_count")).append("\n\n");
						text.append("Warianty wspolnych odkryc: ").append(database.get("variant_count")).append("\n\n");
						for (String key : java.util.List.of("ingredients", "foods")) {
							text.append(key.toUpperCase(java.util.Locale.ROOT)).append('\n');
							for (var tier : database.getAsJsonObject(key).getAsJsonObject("tiers").entrySet()) {
								text.append("Tier ").append(Integer.parseInt(tier.getKey())).append(" (pozycji: ").append(tier.getValue().getAsJsonArray().size()).append(")\n");
								for (var value : tier.getValue().getAsJsonArray()) {
									var item = value.getAsJsonObject();
									text.append("  ").append(item.get("id").getAsString());
									text.append(item.get("can_serve").getAsBoolean() ? " | mozna podawac" : " | tylko do receptur");
									if (item.get("starter_eligible").getAsBoolean())
										text.append(" | wybor startowy");
									if (item.has("discovery_group"))
										text.append(" | grupa: ").append(item.getAsJsonObject("discovery_group").get("member_count")).append(" pozycji");
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
						text.append("Wykluczone z progresji (nierozwiazane potrawy): ").append(database.getAsJsonObject("technical").getAsJsonObject("diagnostics").get("unresolved_foods")).append('\n');
						text.append("Food bez receptury lub potwierdzonej sciezki pozyskania: ").append(database.getAsJsonObject("technical").getAsJsonObject("diagnostics").get("unverified_acquisition_foods")).append('\n');
						text.append("Pominiete odkrycia bez zastosowania: ").append(database.getAsJsonObject("technical").getAsJsonObject("diagnostics").get("filtered_ingredients")).append('\n');
						var plantAudit = database.getAsJsonObject("technical").getAsJsonObject("diagnostics").getAsJsonObject("plant_source_audit");
						if (plantAudit != null) {
							text.append("\nDIAGNOSTYKA ZRODEL ROSLIN — bez wplywu na ranking\n");
							text.append("Status: ").append(plantAudit.get("status")).append("; czas ms: ").append(plantAudit.get("elapsed_ms")).append('\n');
							text.append("Ingredients ze sladem roslinnym: ").append(plantAudit.get("with_plant_evidence")).append(" / ").append(plantAudit.get("ingredient_count")).append('\n');
							text.append("Ingredients z potencjalna generacja: ").append(plantAudit.get("with_potential_worldgen")).append('\n');
							text.append("Sa to kandydaci z definicji, nie gwarantowane dropy ani potwierdzona dostepnosc.\n");
							text.append("Pelne sciezki, warunki i braki dla kazdego Ingredient: technical.diagnostics.plant_source_audit w FoodDatabase.json.\n");
						}
						try {
							int tierCountForStats = Math.max(1, database.get("tier_count").getAsInt());
							java.util.Map<String, Integer> statusCounts = new java.util.TreeMap<>(), driverCounts = new java.util.TreeMap<>(), noEvidenceByNamespace = new java.util.TreeMap<>();
							int total = 0;
							for (String key : java.util.List.of("ingredients", "foods"))
								for (var tier : database.getAsJsonObject(key).getAsJsonObject("tiers").entrySet())
									for (var value : tier.getValue().getAsJsonArray()) {
										var item = value.getAsJsonObject();
										String id = item.get("id").getAsString();
										var source = acquisitionSources.describe(id, tierCountForStats);
										total++;
										String status = source.get("status").getAsString();
										statusCounts.merge(status, 1, Integer::sum);
										driverCounts.merge(source.get("tier_driver").getAsString(), 1, Integer::sum);
										if (status.equals("assumed_base") || status.equals("unrecognized") || status.equals("unresolved") || status.equals("disabled")) {
											int colon = id.indexOf(':');
											noEvidenceByNamespace.merge(colon > 0 ? id.substring(0, colon) : id, 1, Integer::sum);
										}
									}
							text.append("\nSTATYSTYKA ZRODEL — diagnostyka, bez wplywu na tiery (pozycji: ").append(total).append(")\n");
							text.append("Status szacunku: ").append(statusCounts).append('\n');
							text.append("Co ustala tier (declared_gate = reczna/kategoria/profil; inherited_or_computed = odziedziczony lub policzony wysilek, zawiera tez deklarowane minima skladnikow; unresolved_fallback = awaryjny tier): ")
									.append(driverCounts).append('\n');
							text.append("Pozycje bez dowodu zrodla wg moda (assumed_base/unrecognized/unresolved): ").append(noEvidenceByNamespace).append('\n');
							var audit = database.getAsJsonObject("technical").getAsJsonObject("diagnostics").getAsJsonObject("renewability_audit");
							if (audit != null && audit.has("summary_lines")) {
								for (var line : audit.getAsJsonArray("summary_lines"))
									text.append(line.getAsString()).append('\n');
							} else {
								text.append("\nODNAWIALNOSC: brak wynikow analizy.\n");
							}
						} catch (RuntimeException failure) {
							text.append("\nSTATYSTYKA ZRODEL: blad diagnostyki (").append(failure.getClass().getSimpleName()).append(")\n");
						}
						text.append("Gra korzysta z tego katalogu w FoodDatabase.json z oddzielnym postepem Ingredients/Food.\n");
						return text.toString();
					}
				}
				class PlantEvidenceIndex {
					final java.util.Map<String, com.google.gson.JsonObject> placed = new java.util.TreeMap<>(), configured = new java.util.TreeMap<>(), tables = new java.util.TreeMap<>();
					final java.util.Map<String, java.util.Set<String>> seeds = new java.util.TreeMap<>(), featureBiomes = new java.util.TreeMap<>(), biomeDimensions = new java.util.TreeMap<>();
					final java.util.Map<String, java.util.Map<String, String>> states = new java.util.TreeMap<>();
					final java.util.Map<String, String> blockTables = new java.util.TreeMap<>();
					final java.util.Map<String, java.util.Set<String>> plantingEvidence = new java.util.TreeMap<>();
					final java.util.Set<String> crops = new java.util.TreeSet<>();
					final java.util.Map<String, java.util.Set<String>> problems = new java.util.TreeMap<>();
					final java.util.Map<String, java.util.Set<String>> featureBlocksCache = new java.util.HashMap<>();
					final java.util.Map<String, com.google.gson.JsonArray> locations = new java.util.TreeMap<>(), drops = new java.util.TreeMap<>();
					int visits;

					void issue(String id, String reason) {
						problems.computeIfAbsent(id, k -> new java.util.TreeSet<>()).add(reason);
					}

					void link(java.util.Map<String, java.util.Set<String>> map, String key, String value) {
						map.computeIfAbsent(key, k -> new java.util.TreeSet<>()).add(value);
					}

					com.google.gson.JsonArray array(java.util.Collection<String> values) {
						var a = new com.google.gson.JsonArray();
						for (String value : values)
							a.add(value);
						return a;
					}

					String str(com.google.gson.JsonObject o, String key) {
						return o.has(key) && o.get(key).isJsonPrimitive() ? o.get(key).getAsString() : "";
					}

					void provider(com.google.gson.JsonElement value, java.util.Set<String> blocks, String owner) {
						if (value == null || !value.isJsonObject()) {
							issue(owner, "unsupported_state_provider");
							return;
						}
						var o = value.getAsJsonObject();
						String type = str(o, "type");
						if ("minecraft:simple_state_provider".equals(type)) {
							if (o.has("state") && o.get("state").isJsonObject()) {
								String id = str(o.getAsJsonObject("state"), "Name");
								if (!id.isEmpty())
									blocks.add(id);
							}
						} else if ("minecraft:weighted_state_provider".equals(type) && o.has("entries")) {
							for (var e : o.getAsJsonArray("entries")) {
								var entry = e.getAsJsonObject();
								if (entry.has("data") && entry.get("data").isJsonObject()) {
									String id = str(entry.getAsJsonObject("data"), "Name");
									if (!id.isEmpty())
										blocks.add(id);
								}
							}
						} else
							issue(owner, "unsupported_state_provider:" + type);
					}

					void placedBlocks(com.google.gson.JsonElement value, java.util.Set<String> blocks, java.util.Set<String> visiting, String owner) {
						if (++visits > 200000 || visiting.size() > 48) {
							issue(owner, "feature_analysis_budget_exceeded");
							return;
						}
						if (value == null) {
							issue(owner, "missing_placed_feature");
							return;
						}
						if (value.isJsonPrimitive()) {
							String id = value.getAsString();
							if (!visiting.add("p|" + id)) {
								issue(owner, "feature_reference_cycle");
								return;
							}
							var o = placed.get(id);
							if (o == null)
								issue(owner, "unresolved_placed_feature:" + id);
							else
								configuredBlocks(o.get("feature"), blocks, visiting, owner);
							visiting.remove("p|" + id);
						} else if (value.isJsonObject())
							configuredBlocks(value.getAsJsonObject().get("feature"), blocks, visiting, owner);
						else
							issue(owner, "unsupported_placed_reference");
					}

					void configuredBlocks(com.google.gson.JsonElement value, java.util.Set<String> blocks, java.util.Set<String> visiting, String owner) {
						if (++visits > 200000 || visiting.size() > 48) {
							issue(owner, "feature_analysis_budget_exceeded");
							return;
						}
						if (value == null) {
							issue(owner, "missing_configured_feature");
							return;
						}
						if (value.isJsonPrimitive()) {
							String id = value.getAsString();
							if (!visiting.add("c|" + id)) {
								issue(owner, "feature_reference_cycle");
								return;
							}
							var o = configured.get(id);
							if (o == null)
								issue(owner, "unresolved_configured_feature:" + id);
							else
								configuredBlocks(o, blocks, visiting, owner);
							visiting.remove("c|" + id);
							return;
						}
						if (!value.isJsonObject()) {
							issue(owner, "unsupported_configured_reference");
							return;
						}
						var o = value.getAsJsonObject();
						String type = str(o, "type");
						if (!o.has("config") || !o.get("config").isJsonObject()) {
							issue(owner, "missing_feature_config:" + type);
							return;
						}
						var config = o.getAsJsonObject("config");
						if (java.util.Set.of("minecraft:random_patch", "minecraft:flower", "minecraft:no_bonemeal_flower").contains(type))
							placedBlocks(config.get("feature"), blocks, visiting, owner);
						else if ("farmersdelight:wild_crop".equals(type)) {
							// WildCropConfiguration executes primary, secondary and optional floor placed features.
							if (!config.has("primary_feature") || !config.has("secondary_feature"))
								issue(owner, "unsupported_fd_wild_crop_schema");
							else {
								placedBlocks(config.get("primary_feature"), blocks, visiting, owner);
								placedBlocks(config.get("secondary_feature"), blocks, visiting, owner);
								if (config.has("floor_feature"))
									placedBlocks(config.get("floor_feature"), blocks, visiting, owner);
							}
						} else if ("farmersdelight:wild_rice".equals(type)) {
							// WildRiceFeature places ModBlocks.WILD_RICE directly, not config.feature().
							if (config.has("tries") && config.has("xz_spread") && config.has("y_spread")) {
								blocks.add("farmersdelight:wild_rice");
								issue(owner, "fd_wild_rice_water_surface_and_survival_conditions_not_simulated");
							} else
								issue(owner, "unsupported_fd_wild_rice_schema");
						} else if ("minecraft:simple_block".equals(type))
							provider(config.get("to_place"), blocks, owner);
						else if ("minecraft:tree".equals(type)) {
							provider(config.get("trunk_provider"), blocks, owner);
							provider(config.get("foliage_provider"), blocks, owner);
							if (config.has("decorators") && !config.getAsJsonArray("decorators").isEmpty())
								issue(owner, "tree_decorator_outputs_not_interpreted");
						} else if ("minecraft:random_selector".equals(type)) {
							placedBlocks(config.get("default"), blocks, visiting, owner);
							for (var e : config.getAsJsonArray("features"))
								placedBlocks(e.getAsJsonObject().get("feature"), blocks, visiting, owner);
						} else if ("minecraft:simple_random_selector".equals(type) && config.has("features") && config.get("features").isJsonArray()) {
							for (var e : config.getAsJsonArray("features"))
								placedBlocks(e, blocks, visiting, owner);
						} else
							issue(owner, "unsupported_feature_type:" + type);
					}

					// 1=true under the chosen plant state, 0=false, -1=requires external context or unsupported.
					int condition(com.google.gson.JsonObject c, String block) {
						String type = str(c, "condition");
						if ("minecraft:block_state_property".equals(type)) {
							if (!str(c, "block").equals(block))
								return 0;
							var state = states.getOrDefault(block, java.util.Collections.emptyMap());
							if (!c.has("properties"))
								return 1;
							int result = 1;
							for (var e : c.getAsJsonObject("properties").entrySet()) {
								String actual = state.get(e.getKey());
								if (actual == null) {
									result = -1;
									continue;
								}
								if (e.getValue().isJsonPrimitive()) {
									if (!actual.equals(e.getValue().getAsString()))
										return 0;
								} else {
									try {
										int n = Integer.parseInt(actual);
										var range = e.getValue().getAsJsonObject();
										if (range.has("min") && n < range.get("min").getAsInt() || range.has("max") && n > range.get("max").getAsInt())
											return 0;
									} catch (RuntimeException unsupported) {
										result = -1;
									}
								}
							}
							return result;
						}
						if ("minecraft:inverted".equals(type) && c.has("term")) {
							int v = condition(c.getAsJsonObject("term"), block);
							return v < 0 ? -1 : 1 - v;
						}
						if (java.util.Set.of("minecraft:all_of", "minecraft:any_of").contains(type) && c.has("terms")) {
							if (c.getAsJsonArray("terms").isEmpty())
								return -1;
							boolean any = "minecraft:any_of".equals(type), unknown = false;
							for (var e : c.getAsJsonArray("terms")) {
								int v = condition(e.getAsJsonObject(), block);
								if (v == (any ? 1 : 0))
									return v;
								if (v < 0)
									unknown = true;
							}
							return unknown ? -1 : any ? 0 : 1;
						}
						return -1;
					}

					void loot(com.google.gson.JsonElement value, String block, String table, com.google.gson.JsonArray inheritedConditions, com.google.gson.JsonArray inheritedFunctions, java.util.Set<String> visiting, int depth,
							com.google.gson.JsonArray inheritedSelection) {
						if (value == null)
							return;
						if (depth > 48 || ++visits > 400000) {
							issue(block, "loot_analysis_budget_exceeded");
							return;
						}
						if (value.isJsonArray()) {
							for (var e : value.getAsJsonArray())
								loot(e, block, table, inheritedConditions, inheritedFunctions, visiting, depth + 1, inheritedSelection);
							return;
						}
						if (!value.isJsonObject())
							return;
						var o = value.getAsJsonObject();
						var selection = inheritedSelection.deepCopy();
						var scope = new com.google.gson.JsonObject();
						for (String key : java.util.List.of("type", "weight", "quality", "rolls", "bonus_rolls"))
							if (o.has(key))
								scope.add(key, o.get(key).deepCopy());
						if (!scope.isEmpty())
							selection.add(scope);
						if (o.has("rolls") && o.get("rolls").isJsonPrimitive() && o.get("rolls").getAsDouble() <= 0)
							return;
						var conditions = inheritedConditions.deepCopy();
						var functions = inheritedFunctions.deepCopy();
						if (o.has("conditions"))
							for (var e : o.getAsJsonArray("conditions")) {
								if (condition(e.getAsJsonObject(), block) == 0)
									return;
								conditions.add(e.deepCopy());
							}
						if (o.has("functions"))
							for (var e : o.getAsJsonArray("functions"))
								functions.add(e.deepCopy());
						String type = str(o, "type");
						if ("minecraft:item".equals(type)) {
							String id = str(o, "name");
							if (!id.isEmpty()) {
								var r = new com.google.gson.JsonObject();
								r.addProperty("plant_block", block);
								r.addProperty("loot_table", table);
								r.addProperty("mode", "block_break_declared_candidate");
								r.addProperty("maturity", crops.contains(block) ? "CropBlock_max_age" : "default_state_only");
								r.add("state", new com.google.gson.Gson().toJsonTree(states.getOrDefault(block, java.util.Collections.emptyMap())));
								r.add("selection_context", selection);
								r.addProperty("root_loot_table", blockTables.getOrDefault(block, table));
								r.add("conditions", conditions);
								r.add("functions", functions);
								boolean context = false;
								for (var c : conditions)
									if (condition(c.getAsJsonObject(), block) < 0)
										context = true;
								r.addProperty("requires_context_validation", context);
								r.addProperty("requires_function_interpretation", !functions.isEmpty());
								r.addProperty("exact_yield_computed", false);
								r.addProperty("right_click_verified", false);
								var list = drops.computeIfAbsent(id, k -> new com.google.gson.JsonArray());
								if (!list.contains(r))
									list.add(r);
							}
						} else if ("minecraft:loot_table".equals(type)) {
							String id = str(o, "value");
							if (id.isEmpty())
								id = str(o, "name");
							if (id.isEmpty() || !visiting.add(id)) {
								issue(block, "unsupported_or_cyclic_nested_loot_table");
								return;
							}
							var nested = tables.get(id);
							if (nested == null)
								issue(block, "unresolved_nested_loot_table:" + id);
							else
								loot(nested, block, id, conditions, functions, visiting, depth + 1, selection);
							visiting.remove(id);
						} else if ("minecraft:tag".equals(type) || "minecraft:dynamic".equals(type))
							issue(block, "unsupported_loot_entry:" + type);
						else if (!type.isEmpty() && !java.util.Set.of("minecraft:block", "minecraft:empty", "minecraft:alternatives", "minecraft:group", "minecraft:sequence").contains(type))
							issue(block, "unsupported_loot_type:" + type);
						// Branch conditions and weights are preserved as candidates, never claimed as guaranteed drops.
						for (String key : java.util.List.of("pools", "entries", "children"))
							if (o.has(key))
								loot(o.get(key), block, table, conditions, functions, visiting, depth + 1, selection);
					}

					void index() {
						for (var e : featureBiomes.entrySet()) {
							var blocks = featureBlocksCache.get(e.getKey());
							if (blocks == null) {
								blocks = new java.util.TreeSet<>();
								placedBlocks(new com.google.gson.JsonPrimitive(e.getKey()), blocks, new java.util.HashSet<>(), e.getKey());
								featureBlocksCache.put(e.getKey(), blocks);
							}
							for (String block : blocks) {
								var r = new com.google.gson.JsonObject();
								r.add("biomes", array(e.getValue()));
								r.addProperty("placed_feature", e.getKey());
								var dimensionSet = new java.util.TreeSet<String>();
								var memberships = new com.google.gson.JsonObject();
								for (String biome : e.getValue()) {
									var ds = biomeDimensions.getOrDefault(biome, java.util.Collections.emptySet());
									dimensionSet.addAll(ds);
									memberships.add(biome, array(ds));
								}
								r.add("biome_dimensions", memberships);
								r.add("dimensions", array(dimensionSet));
								r.addProperty("evidence", "potential_generation_from_registered_feature");
								r.addProperty("placement_conditions_evaluated", false);
								r.add("analysis_notes", array(problems.getOrDefault(e.getKey(), java.util.Collections.emptySet())));
								var p = placed.get(e.getKey());
								if (p != null && p.has("placement"))
									r.add("placement", p.get("placement").deepCopy());
								locations.computeIfAbsent(block, k -> new com.google.gson.JsonArray()).add(r);
							}
						}
						visits = 0;
						for (var e : blockTables.entrySet()) {
							var t = tables.get(e.getValue());
							if (t == null) {
								issue(e.getKey(), "missing_or_unserializable_live_loot_table");
								continue;
							}
							loot(t, e.getKey(), e.getValue(), new com.google.gson.JsonArray(), new com.google.gson.JsonArray(), new java.util.HashSet<>(java.util.List.of(e.getValue())), 0, new com.google.gson.JsonArray());
						}
					}

					com.google.gson.JsonObject report(com.google.gson.JsonObject database) {
						var result = new com.google.gson.JsonObject();
						result.addProperty("reader_version", 3);
						result.addProperty("diagnostic_only", true);
						result.addProperty("ranking_applied", false);
						result.addProperty("world_chunks_generated", false);
						result.addProperty("policy",
								"Declared block-loot candidates and potential registered worldgen only; conditions/functions retained, no guaranteed yield, no rarity estimate, no right-click simulation, no arbitrary custom-code or global-loot-modifier execution. A missing route is unknown, not proof of absence.");
						var items = new com.google.gson.JsonObject();
						int withPlant = 0, withLocation = 0;
						var ingredientIds = new java.util.TreeSet<String>();
						for (var tier : database.getAsJsonObject("ingredients").getAsJsonObject("tiers").entrySet())
							for (var e : tier.getValue().getAsJsonArray())
								ingredientIds.add(e.getAsJsonObject().get("id").getAsString());
						int discoveryCount = ingredientIds.size();
						var variantOwners = new java.util.TreeMap<String, String>();
						if (database.has("discovery_variants"))
							for (var group : database.getAsJsonObject("discovery_variants").entrySet())
								if (ingredientIds.contains(group.getKey()))
									for (var member : group.getValue().getAsJsonArray()) {
										String id = member.getAsJsonObject().get("id").getAsString();
										ingredientIds.add(id);
										variantOwners.put(id, group.getKey());
									}
						result.addProperty("discovery_count", discoveryCount);
						result.addProperty("physical_item_count", ingredientIds.size());
						for (String id : ingredientIds) {
							var entry = new com.google.gson.JsonObject();
							entry.addProperty("discovery_id", variantOwners.getOrDefault(id, id));
							var harvest = drops.getOrDefault(id, new com.google.gson.JsonArray()).deepCopy();
							var planting = new com.google.gson.JsonArray();
							var first = new com.google.gson.JsonArray();
							var related = new java.util.TreeSet<String>();
							for (var e : harvest)
								related.add(e.getAsJsonObject().get("plant_block").getAsString());
							for (var e : seeds.entrySet())
								if (e.getValue().contains(id)) {
									planting.add(e.getKey());
									related.add(e.getKey());
								}
							for (String block : related) {
								var ls = locations.getOrDefault(block, new com.google.gson.JsonArray());
								for (var l : ls) {
									var r = l.getAsJsonObject().deepCopy();
									r.addProperty("plant_block", block);
									first.add(r);
								}
							}
							// First seeds may drop from a different wild plant than the cultivated crop.
							for (String crop : related)
								for (String seed : seeds.getOrDefault(crop, java.util.Collections.emptySet()))
									for (var seedDrop : drops.getOrDefault(seed, new com.google.gson.JsonArray())) {
										String wild = seedDrop.getAsJsonObject().get("plant_block").getAsString();
										for (var location : locations.getOrDefault(wild, new com.google.gson.JsonArray())) {
											var r = location.getAsJsonObject().deepCopy();
											r.addProperty("plant_block", wild);
											r.addProperty("target_crop", crop);
											r.addProperty("via_planting_item", seed);
											r.add("seed_drop_evidence", seedDrop.deepCopy());
											if (!first.contains(r))
												first.add(r);
										}
									}
							var gaps = new com.google.gson.JsonArray();
							if (harvest.isEmpty())
								gaps.add("no_recognized_plant_drop_for_item");
							if (first.isEmpty())
								gaps.add("first_acquisition_not_located_by_supported_worldgen");
							boolean dim = false;
							for (var e : first)
								if (!e.getAsJsonObject().getAsJsonArray("dimensions").isEmpty())
									dim = true;
							if (!first.isEmpty() && !dim)
								gaps.add("biome_found_but_dimension_not_mapped");
							for (String block : related) {
								if (seeds.getOrDefault(block, java.util.Collections.emptySet()).isEmpty())
									gaps.add("planting_item_not_identified:" + block);
								for (String issue : problems.getOrDefault(block, java.util.Collections.emptySet()))
									gaps.add(issue);
							}
							entry.add("planting_blocks", planting);
							entry.add("harvest_candidates", harvest);
							entry.add("potential_first_acquisition", first);
							entry.add("gaps", gaps);
							entry.addProperty("status", first.isEmpty() ? harvest.isEmpty() && planting.isEmpty() ? "no_plant_evidence" : "cultivation_or_drop_only" : "potential_worldgen_path");
							var supplies = new com.google.gson.JsonObject();
							for (String block : related)
								supplies.add(block, array(seeds.getOrDefault(block, java.util.Collections.emptySet())));
							entry.add("planting_items_by_block", supplies);
							var seedEvidence = new com.google.gson.JsonObject();
							for (String block : related)
								seedEvidence.add(block, array(plantingEvidence.getOrDefault(block, java.util.Collections.emptySet())));
							entry.add("planting_link_evidence", seedEvidence);
							items.add(id, entry);
							if (!harvest.isEmpty() || !planting.isEmpty())
								withPlant++;
							if (!first.isEmpty())
								withLocation++;
						}
						result.add("ingredients", items);
						result.addProperty("ingredient_count", ingredientIds.size());
						result.addProperty("with_plant_evidence", withPlant);
						result.addProperty("with_potential_worldgen", withLocation);
						var issues = new com.google.gson.JsonObject();
						for (var e : problems.entrySet())
							issues.add(e.getKey(), array(e.getValue()));
						result.add("issues", issues);
						return result;
					}
				}
				class PlantSourceDiagnostics {
					final PlantEvidenceIndex index = new PlantEvidenceIndex();
					static final int MAX_FEATURES = 16000, MAX_BLOCKS = 16000, MAX_TABLES = 16000;
					final com.google.gson.JsonObject stages = new com.google.gson.JsonObject();

					<T> void encoded(java.util.Map<String, com.google.gson.JsonObject> target, String id, com.mojang.serialization.Codec<T> codec, T value) {
						try {
							var ops = net.minecraft.resources.RegistryOps.create(com.mojang.serialization.JsonOps.INSTANCE, level.registryAccess());
							var result = codec.encodeStart(ops, value);
							var json = result.result();
							if (json.isPresent() && json.get().isJsonObject())
								target.put(id, json.get().getAsJsonObject());
							else
								index.issue(id, "live_codec_serialization_failed");
						} catch (RuntimeException failure) {
							index.issue(id, "live_codec_serialization_exception:" + failure.getClass().getSimpleName());
						}
					}

					void dimensions() {
						catalogDiagnostics.phase("plant diagnostics: dimension biome membership");
						try {
							var registry = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.LEVEL_STEM);
							for (var e : registry.entrySet())
								for (var biome : e.getValue().generator().getBiomeSource().possibleBiomes()) {
									var key = biome.unwrapKey();
									if (key.isPresent())
										index.link(index.biomeDimensions, key.get().location().toString(), e.getKey().location().toString());
								}
							stages.addProperty("dimension_definitions", "read_live_registry");
						} catch (RuntimeException failure) {
							stages.addProperty("dimension_definitions", "partial_or_unavailable");
							index.issue("dimensions", failure.getClass().getSimpleName());
						}
						for (var dimension : level.getServer().getAllLevels())
							try {
								for (var biome : dimension.getChunkSource().getGenerator().getBiomeSource().possibleBiomes()) {
									var key = biome.unwrapKey();
									if (key.isPresent())
										index.link(index.biomeDimensions, key.get().location().toString(), dimension.dimension().location().toString());
								}
							} catch (RuntimeException failure) {
								index.issue("dimensions", failure.getClass().getSimpleName());
							}
					}

					void worldgen() {
						catalogDiagnostics.phase("plant diagnostics: live worldgen definitions");
						var placed = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.PLACED_FEATURE);
						int count = 0;
						for (var e : placed.entrySet()) {
							if (++count > MAX_FEATURES) {
								index.issue("worldgen", "placed_feature_budget_exceeded");
								break;
							}
							String id = e.getKey().location().toString();
							catalogDiagnostics.current = id;
							encoded(index.placed, id, net.minecraft.world.level.levelgen.placement.PlacedFeature.DIRECT_CODEC, e.getValue());
						}
						var configured = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.CONFIGURED_FEATURE);
						count = 0;
						for (var e : configured.entrySet()) {
							if (++count > MAX_FEATURES) {
								index.issue("worldgen", "configured_feature_budget_exceeded");
								break;
							}
							String id = e.getKey().location().toString();
							catalogDiagnostics.current = id;
							encoded(index.configured, id, net.minecraft.world.level.levelgen.feature.ConfiguredFeature.DIRECT_CODEC, e.getValue());
						}
						var biomes = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME);
						for (var e : biomes.entrySet())
							for (var step : e.getValue().getGenerationSettings().features())
								for (var feature : step) {
									var key = feature.unwrapKey();
									String id;
									if (key.isPresent())
										id = key.get().location().toString();
									else {
										id = "inline_placed_" + index.placed.size();
										encoded(index.placed, id, net.minecraft.world.level.levelgen.placement.PlacedFeature.DIRECT_CODEC, feature.value());
									}
									index.link(index.featureBiomes, id, e.getKey().location().toString());
								}
						stages.addProperty("worldgen", "live_effective_biome_features_and_registry_codecs");
					}

					void nestedTables(com.google.gson.JsonElement value, java.util.Set<String> ids) {
						if (value == null)
							return;
						if (value.isJsonArray()) {
							for (var e : value.getAsJsonArray())
								nestedTables(e, ids);
							return;
						}
						if (!value.isJsonObject())
							return;
						var o = value.getAsJsonObject();
						if ("minecraft:loot_table".equals(index.str(o, "type"))) {
							String id = index.str(o, "value");
							if (id.isEmpty())
								id = index.str(o, "name");
							if (!id.isEmpty())
								ids.add(id);
						}
						for (var e : o.entrySet())
							if (!e.getKey().equals("functions") && !e.getKey().equals("conditions"))
								nestedTables(e.getValue(), ids);
					}

					void plants() {
						catalogDiagnostics.phase("plant diagnostics: planting and block loot");
						for (var e : itemsById.entrySet())
							if (e.getValue() instanceof net.minecraft.world.item.BlockItem item) {
								String block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(item.getBlock()).toString();
								index.link(index.seeds, block, e.getKey());
								index.link(index.plantingEvidence, block, e.getKey() + " | direct_BlockItem_target");
							}
						// Collect potential worldgen blocks once, without constructing or placing any feature.
						for (String feature : index.featureBiomes.keySet()) {
							var blocks = new java.util.TreeSet<String>();
							index.placedBlocks(new com.google.gson.JsonPrimitive(feature), blocks, new java.util.HashSet<>(), feature);
							index.featureBlocksCache.put(feature, blocks);
						}
						var generated = new java.util.HashSet<String>();
						for (var blocks : index.featureBlocksCache.values())
							generated.addAll(blocks);
						int count = 0;
						for (var block : net.minecraft.core.registries.BuiltInRegistries.BLOCK) {
							String id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
							boolean plant = block instanceof net.minecraft.world.level.block.BushBlock || block instanceof net.minecraft.world.level.block.LeavesBlock || block instanceof net.minecraft.world.level.block.GrowingPlantBlock
									|| block instanceof net.minecraft.world.level.block.SugarCaneBlock || block instanceof net.minecraft.world.level.block.CactusBlock || generated.contains(id);
							if (!plant)
								continue;
							if (++count > MAX_BLOCKS) {
								index.issue("plants", "plant_block_budget_exceeded");
								break;
							}
							catalogDiagnostics.current = id;
							try {
								// Block#asItem uses the registered block-to-item map. It can associate a seed
								// with mature/rope stages even when BlockItem#getBlock targets the initial stage.
								var registeredItem = block.asItem();
								if (registeredItem instanceof net.minecraft.world.item.BlockItem plantingItem) {
									String itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(registeredItem).toString();
									if (itemsById.get(itemId) == registeredItem) {
										index.link(index.seeds, id, itemId);
										index.link(index.plantingEvidence, id, itemId + (plantingItem.getBlock() == block ? " | direct_BlockItem_target" : " | registered_growth_stage_alias_not_direct_placement"));
									}
								}
								var state = block.defaultBlockState();
								if (block instanceof net.minecraft.world.level.block.CropBlock crop) {
									state = crop.getStateForAge(crop.getMaxAge());
									index.crops.add(id);
								}
								var values = new java.util.TreeMap<String, String>();
								state.getValues().forEach((property, value) -> values.put(property.getName(), String.valueOf(value)));
								index.states.put(id, values);
								index.blockTables.put(id, block.getLootTable().location().toString());
								if (!(block instanceof net.minecraft.world.level.block.CropBlock))
									index.issue(id, "maturity_not_proven_default_state_only");
								if (block instanceof net.minecraft.world.level.block.LeavesBlock)
									index.issue(id, "tree_growth_and_sapling_relationship_not_interpreted");
								index.issue(id, "custom_getDrops_and_right_click_not_executed");
							} catch (RuntimeException failure) {
								index.issue(id, "block_metadata_failed:" + failure.getClass().getSimpleName());
							}
						}
						var pending = new java.util.ArrayDeque<String>(new java.util.TreeSet<>(index.blockTables.values()));
						var visited = new java.util.HashSet<String>();
						while (!pending.isEmpty()) {
							String id = pending.removeFirst();
							if (!visited.add(id))
								continue;
							if (visited.size() > MAX_TABLES) {
								index.issue("loot_tables", "loot_table_budget_exceeded");
								break;
							}
							catalogDiagnostics.current = id;
							try {
								var key = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, net.minecraft.resources.ResourceLocation.parse(id));
								var table = level.getServer().reloadableRegistries().getLootTable(key);
								if (table == net.minecraft.world.level.storage.loot.LootTable.EMPTY) {
									index.issue(id, "empty_or_missing_live_loot_table");
									continue;
								}
								encoded(index.tables, id, net.minecraft.world.level.storage.loot.LootTable.DIRECT_CODEC, table);
								var nested = new java.util.TreeSet<String>();
								nestedTables(index.tables.get(id), nested);
								pending.addAll(nested);
							} catch (RuntimeException failure) {
								index.issue(id, "loot_metadata_failed:" + failure.getClass().getSimpleName());
							}
						}
						int aliases = 0;
						for (var evidence : index.plantingEvidence.values())
							for (String value : evidence)
								if (value.contains("registered_growth_stage_alias"))
									aliases++;
						stages.addProperty("planting_stage_aliases", aliases);
						stages.addProperty("plant_blocks_inspected", index.blockTables.size());
						stages.addProperty("loot_tables_serialized", index.tables.size());
					}

					void attach(com.google.gson.JsonObject database) {
						long started = System.nanoTime();
						String status = "completed_with_explicit_coverage_limits";
						for (int stage = 0; stage < 4; stage++)
							try {
								if (stage == 0)
									dimensions();
								else if (stage == 1)
									worldgen();
								else if (stage == 2)
									plants();
								else {
									catalogDiagnostics.phase("plant diagnostics: joining ingredient source evidence");
									index.visits = 0;
									index.index();
								}
							} catch (RuntimeException failure) {
								status = "partial_failure";
								index.issue("diagnostic_stage_" + stage, failure.getClass().getSimpleName());
								net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef plant diagnostics stage {} incomplete: {}", stage, failure.toString());
							}
						var report = index.report(database);
						report.addProperty("status", status);
						report.add("collection", stages);
						report.addProperty("elapsed_ms", (System.nanoTime() - started) / 1000000L);
						database.getAsJsonObject("technical").getAsJsonObject("diagnostics").add("plant_source_audit", report);
						net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef plant diagnostics: {} physical Ingredient items (including discovery variants), {} with plant evidence, {} with potential worldgen; {} ms. Diagnostic only.",
								report.get("ingredient_count"), report.get("with_plant_evidence"), report.get("with_potential_worldgen"), report.get("elapsed_ms"));
					}
				}
				class RenewabilityAnalysis {
					// Diagnostic only (v35b). Results never feed tiers, Recipe Discovery cards, menus or orders.
					// Least fixed point over ALL acquisition routes (not only the cheapest selected one): a node is renewable when ANY route
					// has every required slot satisfied by at least one renewable alternative. Iteration instead of recursion, so cycles
					// cannot overflow the stack and results do not depend on traversal order.
					// Base facts seed the graph: self-replanting crops, bee nodes, saplings and tagged plants.
					final int maxPasses = 500;
					final PlantEvidenceIndex plants;
					final java.util.Map<String, Integer> value = new java.util.HashMap<>();
					final java.util.Map<String, String> basis = new java.util.HashMap<>();
					final java.util.Set<String> limited = new java.util.HashSet<>();
					final java.util.List<String> allNodes = new java.util.ArrayList<>();
					final java.util.List<String> routedNodes = new java.util.ArrayList<>();
					final com.google.gson.JsonArray selfReplanting = new com.google.gson.JsonArray();
					int passes = 0;
					boolean converged = false;

					RenewabilityAnalysis(PlantEvidenceIndex plants) {
						this.plants = plants;
						allNodes.addAll(acquisitionSources.nodes);
						java.util.Collections.sort(allNodes);
						routedNodes.addAll(acquisitionSources.routes.keySet());
						java.util.Collections.sort(routedNodes);
					}

					// Ranks: 3 = renewable, 2 = assumed_renewable, 0 = nothing proven yet.
					boolean raise(String node, int rank, String why) {
						if (rank <= value.getOrDefault(node, 0))
							return false;
						value.put(node, rank);
						basis.put(node, why);
						return true;
					}

					boolean hasLocation(String block) {
						var array = plants.locations.get(block);
						return array != null && !array.isEmpty();
					}

					void baseFacts() {
						for (String node : allNodes) {
							if (node.startsWith("bee|"))
								raise(node, 2, "base:bee_node_first_access_unverified");
							else if (acquisitionSources.treeSaplings.contains(node))
								raise(node, 2, "base:sapling_first_access_unverified");
							else if (node.startsWith("item|") && !acquisitionSources.routes.containsKey(node)) {
								String id = acquisitionSources.nodeItems.get(node);
								if (id != null && acquisitionSources.naturalPlant(id))
									raise(node, 2, "base:natural_plant_tag_assumption");
							}
						}
					}

					// A CropBlock whose own planting item is also among its harvest drops can sustain itself once the first seed exists.
					// Direct BlockItem cycles (place a block, break it, get the same item) are excluded because only CropBlocks are checked.
					void detectSelfReplantingCrops() {
						var dropsByBlock = new java.util.TreeMap<String, java.util.TreeSet<String>>();
						for (var entry : plants.drops.entrySet())
							for (var element : entry.getValue()) {
								if (!element.isJsonObject())
									continue;
								var drop = element.getAsJsonObject();
								if (drop.has("plant_block"))
									dropsByBlock.computeIfAbsent(drop.get("plant_block").getAsString(), k -> new java.util.TreeSet<>()).add(entry.getKey());
							}
						for (String block : new java.util.TreeSet<>(plants.crops)) {
							var planting = plants.seeds.get(block);
							var dropped = dropsByBlock.get(block);
							if (planting == null || dropped == null)
								continue;
							var cycle = new java.util.TreeSet<String>();
							for (String seed : planting)
								if (dropped.contains(seed))
									cycle.add(seed);
							if (cycle.isEmpty())
								continue;
							boolean located = hasLocation(block);
							if (!located)
								for (String seed : planting) {
									var seedDrops = plants.drops.get(seed);
									if (seedDrops == null)
										continue;
									for (var element : seedDrops) {
										if (element.isJsonObject() && element.getAsJsonObject().has("plant_block") && hasLocation(element.getAsJsonObject().get("plant_block").getAsString()))
											located = true;
									}
								}
							int rank = located ? 3 : 2;
							String why = "base:self_replanting_crop:" + block + (located ? "" : ":first_access_unverified");
							for (String item : dropped)
								raise("item|" + item, rank, why);
							var record = new com.google.gson.JsonObject();
							record.addProperty("block", block);
							record.addProperty("first_access", located ? "worldgen_located" : "unverified");
							var planted = new com.google.gson.JsonArray();
							for (String seed : cycle)
								planted.add(seed);
							record.add("self_planting_items", planted);
							var produced = new com.google.gson.JsonArray();
							for (String item : dropped)
								produced.add(item);
							record.add("harvest_items", produced);
							selfReplanting.add(record);
						}
					}

					int routeValue(AcquisitionSources.SourceRoute route) {
						if ("omnichef:mob_loot".equals(route.system)) {
							var evidence = acquisitionSources.mobEvidence.get(route);
							boolean listed = evidence != null && evidence.has("availability") && "listed_in_registered_biome_spawns".equals(evidence.get("availability").getAsString());
							return listed ? 3 : 0;
						}
						if (route.requirements.isEmpty())
							return 0;
						int worst = 3;
						for (var slot : route.requirements) {
							int best = 0;
							for (String alternative : slot)
								best = Math.max(best, value.getOrDefault(alternative, 0));
							worst = Math.min(worst, best);
							if (worst == 0)
								return 0;
						}
						return worst;
					}

					boolean routeLimited(AcquisitionSources.SourceRoute route) {
						if ("omnichef:mob_loot".equals(route.system))
							return routeValue(route) == 0;
						for (var slot : route.requirements) {
							boolean every = !slot.isEmpty();
							for (String alternative : slot)
								if (!limited.contains(alternative)) {
									every = false;
									break;
								}
							if (every)
								return true;
						}
						return false;
					}

					void run() {
						baseFacts();
						detectSelfReplantingCrops();
						boolean changed = true;
						while (changed && passes < maxPasses) {
							changed = false;
							passes++;
							for (String node : routedNodes) {
								var list = acquisitionSources.routes.get(node);
								if (list == null)
									continue;
								for (var route : list)
									if (raise(node, routeValue(route), "route:" + route.system))
										changed = true;
							}
						}
						converged = !changed;
						// "limited": every recognized route is blocked by a slot with no non-limited alternative, and nothing renewable was found.
						boolean limitedChanged = true;
						int guard = 0;
						while (limitedChanged && guard++ < maxPasses) {
							limitedChanged = false;
							for (String node : routedNodes) {
								if (limited.contains(node) || value.getOrDefault(node, 0) > 0)
									continue;
								var list = acquisitionSources.routes.get(node);
								if (list == null || list.isEmpty())
									continue;
								boolean every = true;
								for (var route : list)
									if (!routeLimited(route)) {
										every = false;
										break;
									}
								if (every) {
									limited.add(node);
									limitedChanged = true;
								}
							}
						}
					}

					String classify(String node) {
						int rank = value.getOrDefault(node, 0);
						return rank >= 3 ? "renewable" : rank == 2 ? "assumed_renewable" : limited.contains(node) ? "limited" : "unknown";
					}

					String countsLine(java.util.Map<String, Integer> counts, String prefix) {
						StringBuilder line = new StringBuilder();
						for (String status : java.util.List.of("renewable", "assumed_renewable", "unknown", "limited")) {
							if (line.length() > 0)
								line.append(", ");
							line.append(status).append('=').append(counts.getOrDefault(prefix + status, 0));
						}
						return line.toString();
					}

					com.google.gson.JsonObject report(com.google.gson.JsonObject database) {
						var result = new com.google.gson.JsonObject();
						result.addProperty("status", converged ? "completed" : "pass_limit_reached");
						result.addProperty("policy",
								"Diagnostic only. Does not affect tiers, Recipe Discovery cards, menus or orders. renewable = some route exists whose every required slot has a renewable alternative (any route, not only the cheapest). assumed_renewable = depends on an assumption: unverified first access, bee or sapling nodes, or tagged plants. limited = every recognized route lacks repeatable-source evidence; this is not proof that the item cannot be renewed elsewhere in a modpack. unknown = no usable evidence. Nothing here should block an item until the analyzer is trusted.");
						result.addProperty("passes", passes);
						result.addProperty("nodes_with_routes", routedNodes.size());
						var counts = new java.util.TreeMap<String, Integer>();
						var unknownByNamespace = new java.util.TreeMap<String, Integer>();
						var items = new com.google.gson.JsonObject();
						for (String group : java.util.List.of("ingredients", "foods")) {
							for (var tier : database.getAsJsonObject(group).getAsJsonObject("tiers").entrySet())
								for (var element : tier.getValue().getAsJsonArray()) {
									var item = element.getAsJsonObject();
									String id = item.get("id").getAsString();
									String node = "item|" + id;
									boolean servable = item.has("can_serve") && item.get("can_serve").getAsBoolean();
									String status = classify(node);
									counts.merge(group + " all " + status, 1, Integer::sum);
									if (servable)
										counts.merge(group + " servable " + status, 1, Integer::sum);
									if (!status.equals("renewable")) {
										var routeList = acquisitionSources.routes.get(node);
										var entry = new com.google.gson.JsonObject();
										entry.addProperty("status", status);
										entry.addProperty("group", group);
										entry.addProperty("servable", servable);
										entry.addProperty("basis", basis.getOrDefault(node, limited.contains(node) ? "all_recognized_routes_limited" : routeList != null ? "routes_without_renewable_inputs" : "no_route_and_no_base_fact"));
										entry.addProperty("route_count", routeList == null ? 0 : routeList.size());
										items.add(id, entry);
										if (status.equals("unknown") && servable) {
											int colon = id.indexOf(':');
											unknownByNamespace.merge(colon > 0 ? id.substring(0, colon) : id, 1, Integer::sum);
										}
									}
								}
						}
						var countsJson = new com.google.gson.JsonObject();
						for (var entry : counts.entrySet())
							countsJson.addProperty(entry.getKey(), entry.getValue());
						result.add("counts", countsJson);
						result.add("self_replanting_crops", selfReplanting);
						result.add("items", items);
						var ranked = new java.util.ArrayList<>(unknownByNamespace.entrySet());
						ranked.sort((a, b) -> b.getValue() - a.getValue());
						StringBuilder top = new StringBuilder();
						for (int i = 0; i < Math.min(15, ranked.size()); i++) {
							if (i > 0)
								top.append(", ");
							top.append(ranked.get(i).getKey()).append('=').append(ranked.get(i).getValue());
						}
						var lines = new com.google.gson.JsonArray();
						lines.add("");
						lines.add("ODNAWIALNOSC ZRODEL — diagnostyka, nic nie blokuje (menu, karty i zamowienia bez zmian)");
						lines.add("Status analizy: " + result.get("status").getAsString() + "; przebiegi: " + passes + "; wezlow z trasami: " + routedNodes.size());
						lines.add("Wykryte samopodtrzymujace sie uprawy (CropBlock sadzony wlasnym dropem): " + selfReplanting.size());
						lines.add("Ingredients, wszystkie: " + countsLine(counts, "ingredients all "));
						lines.add("Ingredients, do podawania: " + countsLine(counts, "ingredients servable "));
						lines.add("Food, wszystkie: " + countsLine(counts, "foods all "));
						lines.add("Food, do podawania: " + countsLine(counts, "foods servable "));
						lines.add("Pozycje do podawania ze statusem unknown wg moda (top 15): " + (top.length() == 0 ? "brak" : top.toString()));
						lines.add("limited = brak dowodu na powtarzalne zrodlo, a nie dowod, ze nie da sie go odnowic. unknown = za malo danych. Szczegoly: technical.diagnostics.renewability_audit w FoodDatabase.json.");
						result.add("summary_lines", lines);
						return result;
					}

					void attach(com.google.gson.JsonObject database) {
						long started = System.nanoTime();
						run();
						var report = report(database);
						report.addProperty("elapsed_ms", (System.nanoTime() - started) / 1000000L);
						database.getAsJsonObject("technical").getAsJsonObject("diagnostics").add("renewability_audit", report);
						net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef renewability diagnostics: {} passes, status {}, {} self-replanting crops, {} ms. Diagnostic only.", passes, report.get("status").getAsString(), selfReplanting.size(),
								report.get("elapsed_ms"));
					}
				}
				java.nio.file.Path splitPreviewFile = net.neoforged.fml.loading.FMLPaths.GAMEDIR.get().resolve("config/omnichef/" + net.mcreator.omnichef.network.OmnichefModVariables.MapVariables.get(world).FoodDatabase_File_Name);
				java.nio.file.Path splitPreviewTemp = null;
				try {
					FoodCatalogBuilder splitBuilder = new FoodCatalogBuilder();
					catalogDiagnostics.phase("building split catalog");
					com.google.gson.JsonObject splitPreview = splitBuilder.build();
					PlantSourceDiagnostics plantDiagnostics = new PlantSourceDiagnostics();
					plantDiagnostics.attach(splitPreview);
					catalogDiagnostics.phase("renewability diagnostics");
					try {
						new RenewabilityAnalysis(plantDiagnostics.index).attach(splitPreview);
					} catch (RuntimeException | StackOverflowError renewabilityFailure) {
						net.mcreator.omnichef.OmnichefMod.LOGGER.warn("Omnichef renewability diagnostics failed; catalog is unaffected: {}", renewabilityFailure.toString());
						var failedAudit = new com.google.gson.JsonObject();
						failedAudit.addProperty("status", "failed:" + renewabilityFailure.getClass().getSimpleName());
						splitPreview.getAsJsonObject("technical").getAsJsonObject("diagnostics").add("renewability_audit", failedAudit);
					}
					catalogDiagnostics.phase("writing catalog files");
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
					net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef split catalog: {} ingredients ({} non-edible), {} foods, {} starter options; saved to {}.", splitPreview.get("ingredient_count"),
							splitPreview.get("non_edible_ingredient_count"), splitPreview.get("food_count"), splitPreview.getAsJsonArray("starter_options").size(), splitPreviewFile);
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
			} finally {
				catalogDiagnostics.close();
			}
		}
	}
}