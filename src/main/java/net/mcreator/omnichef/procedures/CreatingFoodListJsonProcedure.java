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
								for (var entry : tagMinimumTiers.entrySet())
									if (matchesTags(key, java.util.List.of(entry.getKey())))
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
						indexRecipeUsage();
						// Legacy items can implement drinking remainders in finishUsingItem rather
						// than FoodProperties. Never invoke that game-world callback during generation.
						// Require convergent evidence: a manufactured non-food input, exclusively
						// used across multiple distinct drink outputs, with a non-culinary material route.
						for (var entry : recipeUsage.entrySet()) {
							String id = entry.getKey();
							if (entry.getValue().size() < 2 || isCulinary(id) || overrides.containsKey(id) || !drinkUsage.getOrDefault(id, java.util.Collections.emptySet()).equals(entry.getValue()))
								continue;
							for (var holder : recipesByOutput.getOrDefault(id, java.util.Collections.emptyList())) {
								if (!matchesType(typeId(holder), allowedTypes))
									continue;
								boolean material = false, valid = true, hasInput = false;
								try {
									for (var ingredient : holder.value().getIngredients()) {
										if (ingredient.isEmpty())
											continue;
										hasInput = true;
										var alternatives = ingredient.getItems();
										if (alternatives.length == 0) {
											valid = false;
											break;
										}
										boolean slotMaterial = true;
										for (var input : alternatives) {
											String candidate = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(input.getItem()).toString();
											if (id.equals(candidate) || isCulinary(candidate))
												valid = false;
											slotMaterial &= externalMaterial(candidate);
										}
										material |= slotMaterial;
									}
								} catch (RuntimeException unsupported) {
									valid = false;
								}
								if (valid && hasInput && material) {
									servingContainers.add(id);
									inferredServingContainers.add(id);
									break;
								}
							}
						}
					}

					final java.util.Map<String, java.util.Set<String>> recipeUsage = new java.util.HashMap<>();
					final java.util.Map<String, java.util.Set<String>> drinkUsage = new java.util.HashMap<>();
					final java.util.Set<String> inferredServingContainers = new java.util.TreeSet<>();
					boolean recipeUsageIndexed = false;

					void indexRecipeUsage() {
						if (recipeUsageIndexed)
							return;
						recipeUsageIndexed = true;
						for (var holder : recipeManager.getRecipes()) {
							if (!matchesType(typeId(holder), allowedTypes))
								continue;
							try {
								var output = holder.value().getResultItem(level.registryAccess());
								if (output.isEmpty() || output.getItem().getFoodProperties(output, null) == null)
									continue;
								String result = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(output.getItem()).toString();
								boolean drink = output.getItem().getUseAnimation(output) == net.minecraft.world.item.UseAnim.DRINK;
								for (var ingredient : holder.value().getIngredients()) {
									if (ingredient.isEmpty())
										continue;
									for (var input : ingredient.getItems()) {
										String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(input.getItem()).toString();
										if (id.equals(result) || !itemsById.containsKey(id))
											continue;
										recipeUsage.computeIfAbsent(id, ignored -> new java.util.TreeSet<>()).add(result);
										if (drink)
											drinkUsage.computeIfAbsent(id, ignored -> new java.util.TreeSet<>()).add(result);
									}
								}
							} catch (RuntimeException unsupported) {
								/* No inferred role from unsupported recipes. */ }
						}
					}

					boolean inferredPantryPreparation(String id) {
						var item = itemsById.get(id);
						if (item == null || !isEdible(id) || overrides.containsKey(id) || matchesTags(id, preparedTags))
							return false;
						var stack = new net.minecraft.world.item.ItemStack(item);
						var food = item.getFoodProperties(stack, null);
						if (food == null || food.nutrition() > 3 || item.getUseAnimation(stack) == net.minecraft.world.item.UseAnim.DRINK || !recipesByOutput.containsKey(id))
							return false;
						indexRecipeUsage();
						// Low-nutrition prepared components recurring in multiple distinct outputs
						// are side ingredients; no name/namespace matching or one-off use is sufficient.
						return recipeUsage.getOrDefault(id, java.util.Collections.emptySet()).size() >= 3;
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

					final java.util.Map<String, Boolean> reusableCraftingInputs = new java.util.HashMap<>();

					boolean reusableCraftingInput(String id) {
						return reusableCraftingInputs.computeIfAbsent(id, key -> {
							var item = itemsById.get(key);
							if (item == null || isEdible(key) || overrides.containsKey(key) || culinaryIds.contains(key) || pantryItems.contains(key) || matchesTags(key, ingredientTags))
								return false;
							try {
								var stack = new net.minecraft.world.item.ItemStack(item);
								if (!stack.hasCraftingRemainingItem())
									return false;
								var remainder = stack.getCraftingRemainingItem();
								// A container consumed into a different item is not reusable equipment.
								return remainder != null && !remainder.isEmpty() && remainder.getItem() == item;
							} catch (RuntimeException unsupported) {
								return false;
							}
						});
					}

					boolean equipment(String id) {
						return java.util.Set.of("pamhc2foodcore:potitem", "pamhc2foodcore:skilletitem", "pamhc2foodcore:cuttingboarditem", "pamhc2foodcore:grinderitem", "pamhc2foodcore:bakewareitem", "pamhc2foodcore:saucepanitem",
								"pamhc2foodcore:mixingbowlitem", "pamhc2foodcore:juiceritem", "pamhc2foodcore:rolleritem", "croptopia:cooking_pot", "croptopia:frying_pan", "croptopia:food_press", "croptopia:knife", "croptopia:mortar_and_pestle")
								.contains(id) || reusableCraftingInput(id) || !isEdible(id) && !isCulinary(id) && matchesTags(id, toolTags);
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
								if (!Boolean.TRUE.equals(call(entry.getValue(), "hasFruit"))) {
									treeCount++;
									continue;
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
						// Test the actual ingredient's component predicate, independent of recipe namespace.
						// The audit keeps the physical bottle requirement; the discovery graph uses the water supply proxy.
						if (!ids.contains("minecraft:potion") || !itemsById.containsKey("minecraft:water_bucket"))
							return ids;
						String method = classificationRules.typeId(holder);
						boolean supportedKitchen = classificationRules.matchesType(method, classificationRules.allowedTypes)
								|| java.util.Set.of("farm_and_charm:pot_cooking", "farm_and_charm:stove", "farm_and_charm:roaster", "farm_and_charm:crafting_bowl").contains(method);
						if (!supportedKitchen)
							return ids;
						try {
							var output = holder.value().getResultItem(level.registryAccess());
							if (output.isEmpty())
								return ids;
							// Restrict this proxy to food outputs or known culinary preparations.
							String outputId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(output.getItem()).toString();
							classificationRules.indexRecipeUsage();
							if (!classificationRules.isCulinary(outputId) && !classificationRules.isEdible(outputId) && !classificationRules.recipeUsage.containsKey(outputId))
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
						// A supported transformation yielding edible food is preparation evidence even when
						// its inputs lack food tags. This does NOT prove that any input is obtainable.
						// Reversible packaging and explicit raw roles still take precedence.
						if (!culinaryInput && !(classificationRules.isEdible(output) && !recipe.slots.isEmpty() && recipe.slots.stream().anyMatch(slot -> slot.stream().anyMatch(id -> !packagingAccessory(id)))))
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
								boolean preparationEvidence = rawRecipesFor(output).stream().anyMatch(recipe -> culinaryRecipe(recipe, output));
								String role = hint.isEmpty() ? (best == null && !preparationEvidence ? "INGREDIENT" : "FOOD") : hint;
								roles.put(output, role);
								roleReasons.put(output, hint.isEmpty() ? (best == null ? (preparationEvidence ? "unresolved_culinary_recipe" : "no_culinary_recipe") : "culinary_recipe") : classificationRules.hintReasons.get(output));
								if (best == null && "FOOD".equals(role) && !preparationEvidence) {
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
						final java.util.Set<String> unlockParents = new java.util.TreeSet<>();
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
						if ("INGREDIENT".equals(kind) && nodes.values().stream().anyMatch(n -> !n.unlockParents.isEmpty())) {
							rankIngredientsWithParents();
							return;
						}
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

					boolean parentReady(Node node, int tier) {
						return node.unlockParents.isEmpty() || node.unlockParents.stream().anyMatch(id -> {
							Node parent = nodes.get(id);
							return parent != null && parent.tier >= 0 && parent.tier <= tier;
						});
					}

					// Assign discoveries topologically. The same tier is legal but the parent must be assigned first.
					void rankIngredientsWithParents() {
						var byRank = java.util.Comparator.comparingInt((Node n) -> n.priority).thenComparingDouble(n -> n.rank).thenComparing(n -> n.id);
						var waiting = new java.util.TreeSet<Node>(byRank);
						for (Node n : nodes.values())
							if ("INGREDIENT".equals(n.kind))
								waiting.add(n);
						int[] sizes = new int[ingredientTierCount];
						for (int tier = 0; tier < ingredientTierCount; tier++) {
							int target = (waiting.size() + ingredientTierCount - tier - 1) / (ingredientTierCount - tier);
							if (tier == 0) {
								var starters = new java.util.ArrayList<Node>();
								for (Node n : waiting)
									if (n.minimum == 0 && n.canServe && n.starterEligible && n.unlockParents.isEmpty())
										starters.add(n);
								for (int i = 0; i < Math.min(3, starters.size()); i++) {
									Node n = starters.get(i);
									waiting.remove(n);
									n.tier = 0;
									sizes[0]++;
								}
							}
							while (!waiting.isEmpty() && (sizes[tier] < target || tier == ingredientTierCount - 1)) {
								Node selected = null;
								for (Node n : waiting)
									if (n.minimum <= tier && parentReady(n, tier)) {
										selected = n;
										break;
									}
								if (selected == null)
									break;
								waiting.remove(selected);
								selected.tier = tier;
								sizes[tier]++;
							}
						}
						if (!waiting.isEmpty())
							throw new IllegalStateException("Unassigned ingredient dependencies: " + waiting.stream().map(n -> n.id).toList());
						groupSizes.put("INGREDIENT", sizes);
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
								node.preparationTier = node.minimum;
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
					final java.util.Set<String> policyBlocked;
					java.util.Map<String, Integer> firstAccess = java.util.Collections.emptyMap();
					java.util.Map<String, Integer> repeatAccess = java.util.Collections.emptyMap();
					java.util.Map<String, Double> sourceRarity = java.util.Collections.emptyMap();
					java.util.Map<String, Integer> evidenceMinimumTiers = java.util.Collections.emptyMap();
					java.util.Map<String, java.util.Set<String>> approvedRecipes = java.util.Collections.emptyMap();
					java.util.Map<String, java.util.Set<String>> harvestPlanting = java.util.Collections.emptyMap();
					java.util.Set<String> independentIngredientSources = java.util.Collections.emptySet();
					java.util.Set<String> renewablePlantInputs = java.util.Collections.emptySet();
					final java.util.Set<String> hiddenRawSupplies = new java.util.HashSet<>();
					boolean strictPolicy = false;

					FoodCatalogBuilder() {
						this(java.util.Collections.emptySet());
					}

					FoodCatalogBuilder(java.util.Set<String> policyBlocked) {
						this.policyBlocked = java.util.Set.copyOf(policyBlocked);
					}

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
					final java.util.Map<String, Double> externalUnitCosts = new java.util.HashMap<>();
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
						if (policyBlocked.contains(id))
							return "BLOCKED";
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

					boolean verifiedRawKitchenInput(String id) {
						// A real independent renewable acquisition route, not a name, tag, or
						// the mere existence of an output recipe, establishes a new raw discovery.
						return strictPolicy && referencedKitchenInputs.contains(id) && independentIngredientSources.contains(id) && !auxiliary(id) && !technicalMaterial(id) && !classificationRules.excludedItems.contains(id);
					}

					boolean inferredRecipeOnlyPantryPreparation(String id) {
						// A non-edible preparation used across several independent culinary outputs
						// is a discoverable pantry component, not just a hidden assembly node.
						// One-purpose portion bases and reversible storage remain intermediates.
						if (!recoveredIntermediateInputs.contains(id) || !referencedKitchenInputs.contains(id) || classificationRules.isEdible(id) || classificationRules.overrides.containsKey(id) || reversibleStorage.containsKey(id)
								|| preparedSources.contains(id) || preparedName(id) || classificationRules.technicalIntermediate(id) || auxiliary(id) || technicalMaterial(id))
							return false;
						classificationRules.indexRecipeUsage();
						if (classificationRules.recipeUsage.getOrDefault(id, java.util.Collections.emptySet()).size() < 3)
							return false;
						for (var recipe : evidenceRoutes.getOrDefault(id, java.util.Collections.emptyList()))
							if (!recipe.slots.isEmpty() && !scoreResolver.repackagingRecipe(recipe, id))
								return true;
						return false;
					}

					boolean ingredientDiscoveryProfile(String id) {
						// Discovery roles take precedence over the recovery of missing processing nodes.
						if (classificationRules.overrides.containsKey(id))
							return "INGREDIENT".equals(classificationRules.overrides.get(id));
						if (reversibleStorage.containsKey(id))
							return false;
						if (inferredRecipeOnlyPantryPreparation(id) || verifiedRawKitchenInput(id) || botanicalKitchenInput(id) || referencedKitchenInputs.contains(id) && (recipeOnlyRawProfiles.contains(id) || culinarySeeds.contains(id)))
							return true;
						if (catalog.containsKey(id) && "INGREDIENT".equals(catalog.get(id).get("role").getAsString()))
							return true;
						return vanillaIngredients.contains(id) || classificationRules.pantryItems.contains(id) || pantryName(id) || rawTagged(id) || "INGREDIENT".equals(classificationRules.hint(id));
					}

					void recoverInputEvidence() {
						catalogDiagnostics.phase("FoodCatalogBuilder.recoverInputEvidence");
						java.util.ArrayDeque<String> pending = new java.util.ArrayDeque<>();
						for (var entry : catalog.entrySet())
							if ("FOOD".equals(entry.getValue().get("role").getAsString()) || pantryName(entry.getKey()) || classificationRules.inferredPantryPreparation(entry.getKey()))
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
						var order = java.util.Comparator.comparingInt((String id) -> sourceMinimum(id, planner.ingredientTierCount)).thenComparingInt(id -> strictPolicy ? firstAccess.getOrDefault("item|" + id, 99) : 0)
								.thenComparingInt(id -> strictPolicy ? repeatAccess.getOrDefault("item|" + id, 99) : 0).thenComparingInt(id -> !strictPolicy && basicSupplies.contains(id) ? 0 : 1).thenComparingDouble(id -> {
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
						if (strictPolicy) {
							var seeds = new java.util.TreeSet<String>();
							for (var parents : harvestPlanting.values())
								seeds.addAll(parents);
							var seedGroups = new java.util.TreeMap<String, java.util.List<String>>();
							for (String id : seeds) {
								var row = catalog.get(id);
								if (row == null || !"INGREDIENT".equals(row.get("role").getAsString()) || aliases.containsKey(id) || blocked.contains(id) || classificationRules.overrides.containsKey(id) || !seedNameShape(id) || !uses.containsKey(id)
										|| uses.get(id).isEmpty())
									continue;
								String signature = row.get("edible") + "|" + row.get("can_serve") + "|" + String.join("\n", uses.get(id));
								seedGroups.computeIfAbsent(signature, k -> new java.util.ArrayList<>()).add(id);
							}
							for (var group : seedGroups.values())
								mergeIngredientGroup(group, "equivalent_planting_seed_culinary_functions");
						}
						var botanicalGroups = new java.util.TreeMap<String, java.util.List<String>>();
						for (String id : catalog.keySet())
							if (rawGroupCandidate(id) && !catalog.get(id).get("edible").getAsBoolean() && !catalog.get(id).get("can_serve").getAsBoolean() && uses.containsKey(id) && !uses.get(id).isEmpty()) {
								String signature = String.join("\n", uses.get(id));
								botanicalGroups.computeIfAbsent(signature, ignored -> new java.util.ArrayList<>()).add(id);
							}
						for (var members : botanicalGroups.values())
							mergeIngredientGroup(members, members.stream().allMatch(classificationRules::botanical) ? "equivalent_botanical_culinary_functions" : "equivalent_non_edible_culinary_functions");
					}

					boolean seedNameShape(String id) {
						String p = path(id);
						return p.endsWith("seeditem") || p.endsWith("_seed") || p.endsWith("_seeds") || p.endsWith("_sapling");
					}

					void connectHarvestDiscoveries() {
						if (!strictPolicy)
							return;
						// Only actual retained discoveries are prerequisites. Physical planting inputs do not become recipes.
						var parentsByDiscovery = new java.util.TreeMap<String, java.util.Set<String>>();
						var independent = new java.util.HashSet<String>();
						for (var entry : harvestPlanting.entrySet()) {
							String physical = entry.getKey(), output = aliases.getOrDefault(physical, physical);
							var target = planner.nodes.get(output);
							if (target == null || !"INGREDIENT".equals(target.kind) || seedNameShape(physical))
								continue;
							var parents = new java.util.TreeSet<String>();
							for (String seed : entry.getValue()) {
								String parent = aliases.getOrDefault(seed, seed);
								var node = planner.nodes.get(parent);
								if (!seedNameShape(seed) || output.equals(parent) || node == null || !"INGREDIENT".equals(node.kind))
									continue;
								parents.add(parent);
							}
							if (parents.isEmpty())
								independent.add(output);
							else
								parentsByDiscovery.computeIfAbsent(output, k -> new java.util.TreeSet<>()).addAll(parents);
						}
						// A grouped physical variant with no culinary seed must not be locked by a different variant.
						for (var entry : aliases.entrySet())
							if (!harvestPlanting.containsKey(entry.getKey()))
								independent.add(entry.getValue());
						for (String output : parentsByDiscovery.keySet())
							if (!harvestPlanting.containsKey(output))
								independent.add(output);
						for (var entry : parentsByDiscovery.entrySet()) {
							if (independent.contains(entry.getKey()))
								continue;
							var target = planner.nodes.get(entry.getKey());
							target.unlockParents.addAll(entry.getValue());
							target.starterEligible = false;
						}
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
						if (strictPolicy)
							return "INGREDIENT".equals(role) && canServe && sourceMinimum(id, planner.ingredientTierCount) == 0 && firstAccess.getOrDefault("item|" + id, 99) == 0 && repeatAccess.getOrDefault("item|" + id, 99) == 0
									&& recipes(id).isEmpty() && !pantryName(id) && !plantingSeedName(id) && !preparedName(id);
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
						if (strictPolicy) {
							for (String id : catalog.keySet())
								if (firstAccess.getOrDefault("item|" + id, 99) == 0 && repeatAccess.getOrDefault("item|" + id, 99) == 0)
									assuredStarterSources.add(id);
							return;
						}
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
							if ("INGREDIENT".equals(node.kind) && node.tier == 0 && node.canServe && node.unlockParents.isEmpty())
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
						if (classificationRules.inferredPantryPreparation(id) && "FOOD".equals(oldRole))
							return "INGREDIENT";
						return oldRole;
					}

					boolean portionIntermediate(String id) {
						return !classificationRules.overrides.containsKey(id) && !classificationRules.isEdible(id) && !ingredientDiscoveryProfile(id)
								&& (recoveredIntermediateInputs.contains(id) || !pantryName(id) && (preparedSources.contains(id) || preparedName(id)));
					}

					int declaredSourceMinimum(String id, int tiers) {
						if (strictPolicy) {
							String key = id.startsWith("omnichef:fluid/") ? id : "item|" + id;
							int access = Math.max(firstAccess.getOrDefault(key, 3), repeatAccess.getOrDefault(key, 3));
							int floor = (int) Math.ceil(access * (tiers - 1) / 3.0);
							int category = classificationRules.categoryMinimum(id);
							var golden = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("c:foods/golden"));
							if (classificationRules.matchesTags(id, java.util.List.of(golden)))
								category = Math.max(category, 2);
							int declared = Math.max(classificationRules.minimumTiers.getOrDefault(id, 0), classificationRules.progressionMinimumTiers.getOrDefault(id, 0));
							return Math.min(tiers - 1, Math.max(Math.max(floor, declared), Math.max(category, evidenceMinimumTiers.getOrDefault(id, 0))));
						}
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
						return strictPolicy ? declaredSourceMinimum(id, tiers) : Math.min(tiers - 1, Math.max(declaredSourceMinimum(id, tiers), sourceFloors.getOrDefault(id, 0)));
					}

					void measureSourceFloors() {
						catalogDiagnostics.phase("FoodCatalogBuilder.measureSourceFloors");
						for (var node : planner.nodes.values()) {
							catalogDiagnostics.current = node.id;
							if ("BLOCKED".equals(node.kind) || "ALIAS".equals(node.kind))
								continue;
							if ("AUXILIARY".equals(node.kind))
								sourceFloors.put(node.id, strictPolicy ? declaredSourceMinimum(node.id, planner.tierCount) : 0);
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
						if (inferredRecipeOnlyPantryPreparation(id) || verifiedRawKitchenInput(id) || botanicalKitchenInput(id) || referencedKitchenInputs.contains(id) && (recipeOnlyRawProfiles.contains(id) || culinarySeeds.contains(id)))
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
						if (inferredRecipeOnlyPantryPreparation(id))
							entry.addProperty("role_reason", "recovered_recipe_only_pantry_preparation");
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
							result = filterPolicyRecipes(id, result);
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
						result = filterPolicyRecipes(id, result);
						allowedRecipeCache.put(id, result);
						return result;
					}

					java.util.List<java.util.List<String>> filterPolicyRoute(java.util.List<java.util.List<String>> route) {
						if (policyBlocked.isEmpty())
							return route;
						var filtered = new java.util.ArrayList<java.util.List<String>>();
						for (var slot : route) {
							var alternatives = new java.util.ArrayList<String>();
							for (String input : slot)
								if (!policyBlocked.contains(input))
									alternatives.add(input);
							if (alternatives.isEmpty())
								return null;
							filtered.add(alternatives);
						}
						return filtered;
					}

					java.util.List<FoodScoreResolver.IndexedRecipe> filterPolicyRecipes(String output, java.util.List<FoodScoreResolver.IndexedRecipe> source) {
						if (policyBlocked.isEmpty() && !strictPolicy)
							return source;
						var filtered = new java.util.ArrayList<FoodScoreResolver.IndexedRecipe>();
						for (var recipe : source) {
							if (strictPolicy) {
								var ids = approvedRecipes.getOrDefault(output, java.util.Collections.emptySet());
								String recipeId = recipe.holder == null ? "" : recipe.holder.id().toString();
								if (recipe.holder == null && importedEvidence.containsKey(recipe)) {
									var evidence = importedEvidence.get(recipe);
									if (evidence.has("resource"))
										recipeId = evidence.get("resource").getAsString();
								}
								boolean approved = ids.contains(recipe.method + "|" + recipeId);
								if (!approved && !recipe.portionSource.isEmpty() && recipe.holder != null)
									approved = approvedRecipes.getOrDefault(recipe.portionSource, java.util.Collections.emptySet()).contains(recipe.method + "|" + recipeId);
								if (!approved && recipe.holder == null && recipeId.isEmpty())
									approved = ids.stream().anyMatch(key -> key.startsWith(recipe.method + "|"));
								if (!approved)
									continue;
							}
							var route = filterPolicyRoute(recipe.slots);
							if (route == null)
								continue;
							var copy = scoreResolver.new IndexedRecipe(recipe.holder, recipe.method, recipe.outputCount, route);
							copy.portionSource = recipe.portionSource;
							copy.servings = recipe.servings;
							if (importedEvidence.containsKey(recipe))
								importedEvidence.put(copy, importedEvidence.get(recipe));
							filtered.add(copy);
						}
						return filtered;
					}

					java.util.List<FoodScoreResolver.IndexedRecipe> recipes(String id) {
						if (ingredientVariantProfiles.containsKey(id) && aliases.containsKey(id))
							return java.util.Collections.emptyList();
						if (completeRecipeInputs.fluidNode(id))
							return allowedRecipes(id);
						if (auxiliary(id) || technicalMaterial(id))
							return java.util.Collections.emptyList();
						boolean dish = catalog.containsKey(id) && "FOOD".equals(catalog.get(id).get("role").getAsString());
						if (classificationRules.technicalIntermediate(id) || dish || recoveredIntermediateInputs.contains(id) || preparedSources.contains(id) || preparedName(id) || classificationRules.pantryItems.contains(id) || pantryName(id)
								|| classificationRules.inferredPantryPreparation(id))
							return allowedRecipes(id);
						// A zero-nutrition edible intermediate may still have a real culinary recipe.
						// Retain that recipe instead of silently treating the item as a natural resource.
						if (catalog.containsKey(id) && !catalog.get(id).get("can_serve").getAsBoolean() && classificationRules.isEdible(id) && !rawTagged(id))
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
							double effects = entry.has("positiveEffects") && entry.get("positiveEffects").isJsonPrimitive() ? Math.min(4.0, Math.max(0.0, entry.get("positiveEffects").getAsDouble()) * 0.5) : 0.0;
							return Math.round((difficulty + unlockDepth.getOrDefault(id, 0) * 1.5 + effects) * 100.0) / 100.0;
						}
						if (strictPolicy) {
							String key = "item|" + id;
							double first = firstAccess.getOrDefault(key, 3);
							double repeat = repeatAccess.getOrDefault(key, 3);
							double nutrition = entry.has("nutrition") ? entry.get("nutrition").getAsDouble() : 0;
							double saturation = entry.has("saturation") ? entry.get("saturation").getAsDouble() : 0;
							double result = first * 32 + repeat * 24 + work * 3 + depth * 2 + Math.min(16, Math.max(0, nutrition + saturation) * 0.8) + sourceRarity.getOrDefault(id, 0.0) * 5.0 + (3 - Math.min(3, foodUses.getOrDefault(id, 0))) * 0.5;
							return Math.round(result * 100.0) / 100.0;
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
							} else if ("AUXILIARY".equals(node.kind) || "EXTERNAL".equals(node.kind) || hiddenRawSupplies.contains(node.id))
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

					String exactMaterial(FoodScoreResolver.IndexedRecipe recipe) {
						String input = null;
						for (var slot : recipe.slots) {
							if (slot.size() != 1 || input != null && !input.equals(slot.get(0)))
								return null;
							input = slot.get(0);
						}
						return input;
					}

					void normalizeExternalUnitCosts() {
						externalUnitCosts.clear();
						for (var node : planner.nodes.values())
							if ("EXTERNAL".equals(node.kind))
								externalUnitCosts.put(node.id, kitchenCosts.getOrDefault(node.id, 1.0));
						// Only mathematically reversible, exact single-material conversions are
						// equivalent units. No generic metal or item-name assumption.
						var ratios = new java.util.ArrayList<java.util.Map.Entry<String, FoodScoreResolver.IndexedRecipe>>();
						for (String output : externalUnitCosts.keySet())
							for (var route : allowedRecipes(output)) {
								if (!"minecraft:crafting".equals(route.method) || route.outputCount <= 0)
									continue;
								String input = exactMaterial(route);
								if (input == null || input.equals(output) || !externalUnitCosts.containsKey(input))
									continue;
								for (var reverse : allowedRecipes(input)) {
									if (!"minecraft:crafting".equals(reverse.method) || !output.equals(exactMaterial(reverse)))
										continue;
									if ((long) route.slots.size() * reverse.slots.size() != (long) route.outputCount * reverse.outputCount)
										continue;
									ratios.add(java.util.Map.entry(output, route));
									break;
								}
							}
						for (int pass = 0; pass < externalUnitCosts.size(); pass++) {
							boolean changed = false;
							for (var ratio : ratios) {
								var route = ratio.getValue();
								String output = ratio.getKey(), input = exactMaterial(route);
								double candidate = externalUnitCosts.get(input) * route.slots.size() / route.outputCount;
								if (candidate > 0.0001 && candidate + 0.000001 < externalUnitCosts.get(output)) {
									externalUnitCosts.put(output, candidate);
									changed = true;
								}
							}
							if (!changed)
								break;
						}
					}

					void measureUnlockDifficulty() {
						catalogDiagnostics.phase("FoodCatalogBuilder.measureUnlockDifficulty");
						normalizeExternalUnitCosts();
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
							double source = strictPolicy ? firstAccess.getOrDefault("item|" + node.id, 3) * 2.0 + repeatAccess.getOrDefault("item|" + node.id, 3) * 2.0 : acquisitionSources.costs.getOrDefault("item|" + node.id, 0.0);
							if (!Double.isFinite(source))
								source = node.minimum * classificationRules.stepsPerTier;
							unlockDifficulty.put(node.id,
									"AUXILIARY".equals(node.kind)
											? 0.0
											: "EXTERNAL".equals(node.kind) ? externalUnitCosts.getOrDefault(node.id, kitchenCosts.getOrDefault(node.id, 1.0 + Math.min(12.0, Math.max(0.0, source)))) : 1.0 + Math.min(12.0, Math.max(0.0, source)));
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
										var inputNode = planner.nodes.get(selected);
										if (inputNode != null && "EXTERNAL".equals(inputNode.kind))
											total += unlockDifficulty.get(selected);
										else if (counted.add(selected))
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

					// Discovery requirements are exported separately from the physical recipes.
					// Reversible storage recipes (wheat <-> hay, etc.) never establish a discovery gate.
					com.google.gson.JsonArray ingredientDiscoveryRecipes(String id) {
						var result = new com.google.gson.JsonArray();
						for (var value : recipeData.getOrDefault(id, new com.google.gson.JsonArray())) {
							var recipe = value.getAsJsonObject();
							if (recipe.getAsJsonArray("ingredientSlots").isEmpty() || packingOnly(id, recipe))
								continue;
							result.add(recipe.deepCopy());
						}
						return result;
					}

					boolean requiresIngredientRecipe(SplitTierPlanner.Node node, boolean starter, com.google.gson.JsonArray recipes) {
						// Explicit progression rule: sugar is a preparation, not an independent loot discovery.
						if ("minecraft:sugar".equals(node.id))
							return true;
						if (starter || recipes.isEmpty() || seedNameShape(node.id) || plantingSeedName(node.id))
							return false;
						if (independentIngredientSources.contains(node.id))
							return false;
						for (var alias : aliases.entrySet())
							if (node.id.equals(alias.getValue()) && independentIngredientSources.contains(alias.getKey()))
								return false;
						return strictPolicy || pantryName(node.id) || classificationRules.pantryItems.contains(node.id) || classificationRules.inferredPantryPreparation(node.id) || inferredRecipeOnlyPantryPreparation(node.id)
								|| classificationRules.isEdible(node.id) && !node.canServe;
					}

					// Diagnostic only: simulate eventual discovery with the new rules, without changing tiers or pools.
					java.util.Set<String> ingredientDiscoveryReachable(com.google.gson.JsonObject graph, int maxTier) {
						var reachable = new java.util.HashSet<String>();
						boolean changed;
						do {
							changed = false;
							for (var entry : graph.entrySet()) {
								String id = entry.getKey();
								if (reachable.contains(id))
									continue;
								var node = entry.getValue().getAsJsonObject();
								String kind = node.get("kind").getAsString();
								if ("BLOCKED".equals(kind))
									continue;
								if (node.has("tier") && node.get("tier").getAsInt() > maxTier)
									continue;
								if ("ALIAS".equals(kind)) {
									if (reachable.contains(node.get("discovery_id").getAsString()))
										changed |= reachable.add(id);
									continue;
								}
								if (node.has("unlock_requires_any")) {
									boolean parentReady = false;
									for (var parent : node.getAsJsonArray("unlock_requires_any"))
										if (reachable.contains(parent.getAsString())) {
											parentReady = true;
											break;
										}
									if (!parentReady)
										continue;
								}
								boolean gate = node.has("unlock_requires_recipe") && node.get("unlock_requires_recipe").getAsBoolean();
								if ("AUXILIARY".equals(kind) || "EXTERNAL".equals(kind) || "INGREDIENT".equals(kind) && !gate) {
									changed |= reachable.add(id);
									continue;
								}
								var recipes = node.getAsJsonArray(gate ? "unlock_recipes" : "recipes");
								if (recipes == null)
									continue;
								for (var value : recipes) {
									var slots = value.getAsJsonObject().getAsJsonArray("ingredientSlots");
									if (slots.isEmpty())
										continue;
									boolean possible = true;
									for (var slot : slots) {
										boolean any = false;
										for (var input : slot.getAsJsonArray())
											if (reachable.contains(input.getAsString())) {
												any = true;
												break;
											}
										if (!any) {
											possible = false;
											break;
										}
									}
									if (possible) {
										changed |= reachable.add(id);
										break;
									}
								}
							}
						} while (changed);
						return reachable;
					}

					com.google.gson.JsonObject ingredientDiscoveryAudit(com.google.gson.JsonObject graph) {
						var reachable = ingredientDiscoveryReachable(graph, Integer.MAX_VALUE);
						var tierZeroReachable = ingredientDiscoveryReachable(graph, 0);
						var gated = new com.google.gson.JsonArray();
						var unreachable = new com.google.gson.JsonArray();
						var tierZeroDeferred = new com.google.gson.JsonArray();
						for (var entry : graph.entrySet()) {
							var node = entry.getValue().getAsJsonObject();
							String kind = node.get("kind").getAsString();
							if (node.has("unlock_requires_recipe") && node.get("unlock_requires_recipe").getAsBoolean())
								gated.add(entry.getKey());
							if (("INGREDIENT".equals(kind) || "FOOD".equals(kind)) && !reachable.contains(entry.getKey()))
								unreachable.add(entry.getKey());
							if (("INGREDIENT".equals(kind) || "FOOD".equals(kind)) && node.has("tier") && node.get("tier").getAsInt() == 0 && !tierZeroReachable.contains(entry.getKey()))
								tierZeroDeferred.add(entry.getKey());
						}
						var report = new com.google.gson.JsonObject();
						report.add("recipe_gated_ingredients", gated);
						report.add("unreachable_discoveries", unreachable);
						report.add("tier_zero_deferred", tierZeroDeferred);
						report.addProperty("scope", "eventual_discovery_ignores_tier_and_exp_limits; tier_zero_deferred_uses_both_tier_zero_pools");
						return report;
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

					boolean singleStepRawPrecursor(String id) {
						if (!strictPolicy || !renewablePlantInputs.contains(id) || classificationRules.isEdible(id) || classificationRules.overrides.containsKey(id) || aliases.containsValue(id) || aliases.containsKey(id))
							return false;
						var node = planner.nodes.get(id);
						if (node == null || node.starterEligible || !node.routes.isEmpty())
							return false;
						boolean used = false;
						for (var entry : recipeData.entrySet())
							for (var raw : entry.getValue()) {
								var recipe = raw.getAsJsonObject();
								var slots = recipe.getAsJsonArray("ingredientSlots");
								boolean contains = false;
								for (var slot : slots)
									for (var item : slot.getAsJsonArray())
										if (id.equals(item.getAsString()))
											contains = true;
								if (!contains)
									continue;
								var target = catalog.get(entry.getKey());
								if (target == null || !"INGREDIENT".equals(target.get("role").getAsString()) || classificationRules.isEdible(entry.getKey()) || slots.size() != 1)
									return false;
								if (!independentIngredientSources.contains(id))
									return false;
								used = true;
							}
						return used;
					}

					boolean restorableInput(String id, java.util.Set<String> referenced) {
						if (policyBlocked.contains(id))
							return false;
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
							if (policyBlocked.contains(id))
								continue;
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
						blocked.addAll(policyBlocked);
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
							if ("INGREDIENT".equals(role) && (strictPolicy ? node.starterEligible : basicSupplies.contains(id)))
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
								if (!strictPolicy && "FOOD".equals(node.kind) && variants.isEmpty() && scoreResolver.rawRecipesFor(id).isEmpty()) {
									var slots = evidencedAcquisition(id) ? acquisitionInputs(id) : null;
									if (slots != null)
										slots = filterPolicyRoute(slots);
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
						if (strictPolicy) {
							// Remove dead alternatives before counting needed Ingredients and calculating tiers.
							var reachable = new java.util.HashSet<String>();
							for (var node : planner.nodes.values())
								if (java.util.Set.of("INGREDIENT", "AUXILIARY", "EXTERNAL").contains(node.kind))
									reachable.add(node.id);
							boolean changed;
							do {
								changed = false;
								for (var node : planner.nodes.values()) {
									if (reachable.contains(node.id) || "BLOCKED".equals(node.kind))
										continue;
									if ("ALIAS".equals(node.kind)) {
										if (reachable.contains(node.aliasOf))
											changed |= reachable.add(node.id);
										continue;
									}
									for (var route : node.routes)
										if (!route.isEmpty() && route.stream().allMatch(slot -> slot.stream().anyMatch(reachable::contains))) {
											changed |= reachable.add(node.id);
											break;
										}
								}
							} while (changed);
							for (var node : planner.nodes.values()) {
								var valid = new com.google.gson.JsonArray();
								node.routes.clear();
								for (var value : recipeData.get(node.id)) {
									var row = value.getAsJsonObject();
									var slots = new java.util.ArrayList<java.util.List<String>>();
									for (var slot : row.getAsJsonArray("ingredientSlots")) {
										var alternatives = new java.util.ArrayList<String>();
										for (var item : slot.getAsJsonArray())
											if (reachable.contains(item.getAsString()))
												alternatives.add(item.getAsString());
										if (alternatives.isEmpty()) {
											slots.clear();
											break;
										}
										slots.add(alternatives);
									}
									if (!slots.isEmpty()) {
										row.add("ingredientSlots", routeJson(slots));
										node.routes.add(slots);
										valid.add(row);
									}
								}
								recipeData.put(node.id, valid);
							}
						}
						var needed = neededIngredients();
						needed.addAll(aliases.values());
						com.google.gson.JsonArray filteredIngredients = new com.google.gson.JsonArray();
						java.util.List<String> unnecessary = new java.util.ArrayList<>();
						for (var entry : catalog.entrySet())
							if ("INGREDIENT".equals(entry.getValue().get("role").getAsString()) && !entry.getValue().get("can_serve").getAsBoolean() && singleStepRawPrecursor(entry.getKey()))
								hiddenRawSupplies.add(entry.getKey());
						for (var entry : catalog.entrySet())
							if ("INGREDIENT".equals(entry.getValue().get("role").getAsString()) && !entry.getValue().get("can_serve").getAsBoolean()
									&& (hiddenRawSupplies.contains(entry.getKey()) || !needed.contains(entry.getKey()) || !culinaryRelevantIds.contains(entry.getKey())) && !classificationRules.overrides.containsKey(entry.getKey()))
								unnecessary.add(entry.getKey());
						for (String id : unnecessary) {
							catalog.remove(id);
							com.google.gson.JsonObject removed = new com.google.gson.JsonObject();
							removed.addProperty("id", id);
							removed.addProperty("reason", hiddenRawSupplies.contains(id) ? "single_step_raw_precursor" : !culinaryRelevantIds.contains(id) ? "no_complete_culinary_path_to_servable_product" : "no_non_packing_path_to_servable_product");
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
							String kind = removed ? (hiddenRawSupplies.contains(old.id) ? "EXTERNAL" : old.routes.isEmpty() ? "BLOCKED" : "INTERMEDIATE") : old.kind;
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
						connectHarvestDiscoveries();
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
							boolean ingredient = "INGREDIENT".equals(node.kind) && planner.isCatalog(node);
							boolean starterIngredient = ingredient && node.canServe && node.starterEligible && node.tier == 0;
							var discoveryRecipes = ingredient ? ingredientDiscoveryRecipes(node.id) : new com.google.gson.JsonArray();
							boolean recipeGate = ingredient && requiresIngredientRecipe(node, starterIngredient, discoveryRecipes);
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
								if (ingredient) {
									entry.addProperty("unlock_requires_recipe", recipeGate);
									if (recipeGate)
										entry.add("unlock_recipes", discoveryRecipes.deepCopy());
								}
								var analysis = new com.google.gson.JsonObject();
								analysis.addProperty("ranking_score", node.rank);
								if (!node.unlockParents.isEmpty()) {
									var parents = new com.google.gson.JsonArray();
									for (String parent : node.unlockParents)
										parents.add(parent);
									analysis.add("unlock_requires_any", parents);
								}
								if (strictPolicy) {
									analysis.addProperty("first_access_class", firstAccess.getOrDefault("item|" + node.id, -1));
									analysis.addProperty("recurring_access_class", repeatAccess.getOrDefault("item|" + node.id, -1));
								}
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
							if (!node.unlockParents.isEmpty()) {
								var parents = new com.google.gson.JsonArray();
								for (String parent : node.unlockParents)
									parents.add(parent);
								graphNode.add("unlock_requires_any", parents);
							}
							graphNode.add("recipes", excludedNode ? new com.google.gson.JsonArray() : recipeData.get(node.id));
							if (ingredient) {
								graphNode.addProperty("unlock_requires_recipe", recipeGate);
								if (recipeGate)
									graphNode.add("unlock_recipes", discoveryRecipes);
							}
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
						result.addProperty("split_generator_version", 38);
						result.addProperty("split_generator_revision", 4);
						result.addProperty("mode", "split_catalog");
						result.addProperty("generated_at_epoch_ms", System.currentTimeMillis());
						result.addProperty("source_commit", "414d8821558f7de0c22ef0763f16944c5657c59a");
						result.addProperty("generator_patch", "v38.4");
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
						var inferredContainers = new com.google.gson.JsonArray();
						for (String id : classificationRules.inferredServingContainers)
							inferredContainers.add(id);
						diagnostics.add("inferred_serving_containers", inferredContainers);
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
						diagnostics.addProperty("ranking_policy", strictPolicy
								? "Verified first and recurring access, category floors, estimated loot rarity and habitat coverage, then preparation difficulty and depth. Starter priority requires eligibility. Food counts distinct culinary discoveries but every external material unit, with reciprocal crafting unit normalization and bounded positive effect bonus. Production cost per portion remains separate."
								: "Baseline source effort and yield-independent preparation difficulty/depth; production cost per portion remains separate.");
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
						diagnostics.add("ingredient_discovery_audit", ingredientDiscoveryAudit(graph));
						diagnostics.addProperty("ingredient_discovery_policy",
								"Recipe-only Ingredient preparations require one complete non-packing recipe. Independent renewable sources, planting seeds and existing starter choices stay independent of crafting. Sugar explicitly requires a recipe. Existing harvest parent requirements remain separate.");
						com.google.gson.JsonObject technical = new com.google.gson.JsonObject();
						technical.add("diagnostics", diagnostics);
						technical.add("recipe_nodes", graph);
						result.add("technical", technical);
						return result;
					}

					String summary(com.google.gson.JsonObject database) {
						StringBuilder text = new StringBuilder("OMNICHEF — KATALOG INGREDIENTS / FOOD v38.4\nTiery ponizej oraz indeksy w JSON zaczynaja sie od 0.\n\n");
						text.append("Ingredient discovery audit: ").append(database.getAsJsonObject("technical").getAsJsonObject("diagnostics").get("ingredient_discovery_audit")).append('\n');
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
					final java.util.Map<String, java.util.List<java.util.Map<String, String>>> growthStateAlternatives = new java.util.TreeMap<>();
					final java.util.Map<String, String> blockTables = new java.util.TreeMap<>();
					final java.util.Map<String, java.util.Set<String>> plantingEvidence = new java.util.TreeMap<>();
					final java.util.Set<String> crops = new java.util.TreeSet<>();
					// v36: growth evidence beyond CropBlock. ageBlocks = blocks with an "age" IntegerProperty (scanned at max age),
					// spreading = classes that regrow by spreading (sugar cane, cactus, kelp-like, mushrooms), stems = StemBlock instances.
					final java.util.Set<String> ageBlocks = new java.util.TreeSet<>(), spreading = new java.util.TreeSet<>(), stems = new java.util.TreeSet<>();
					final java.util.Map<String, java.util.Set<String>> problems = new java.util.TreeMap<>();
					final java.util.Map<String, java.util.Set<String>> featureBlocksCache = new java.util.HashMap<>();
					final java.util.Map<String, java.util.Set<String>> templateBlocksCache = new java.util.HashMap<>();
					final java.util.Map<String, com.google.gson.JsonArray> locations = new java.util.TreeMap<>(), drops = new java.util.TreeMap<>();
					int visits;

					// Pam's Trees 1.0.9: each of these exact foliage codecs places its own
					// registered BlockRegistration fruit block, in addition to vanilla leaves.
					String pamPlacerFruit(String type) {
						if (!type.startsWith("pamhc2trees:") || !type.endsWith("_fruit_placer"))
							return "";
						String name = type.substring("pamhc2trees:".length(), type.length() - "_fruit_placer".length());
						if (!java.util.Set.of("acorn", "almond", "apple", "apricot", "avocado", "banana", "breadfruit", "candlenut", "cashew", "cherry", "chestnut", "cinnamon", "coconut", "date", "dragonfruit", "durian", "fig", "gooseberry",
								"grapefruit", "guava", "hazelnut", "jackfruit", "lemon", "lime", "lychee", "mango", "maple", "nutmeg", "olive", "orange", "papaya", "paperbark", "passionfruit", "pawpaw", "peach", "pear", "pecan", "peppercorn",
								"persimmon", "pine", "pistachio", "plum", "pomegranate", "rambutan", "soursop", "spiderweb", "starfruit", "tamarind", "vanillabean", "walnut").contains(name))
							return "";
						return "pamhc2trees:pam" + ("pine".equals(name) ? "pinenut" : name);
					}

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
								if (entry.has("weight") && entry.get("weight").getAsInt() <= 0)
									continue;
								if (entry.has("data") && entry.get("data").isJsonObject()) {
									String id = str(entry.getAsJsonObject("data"), "Name");
									if (!id.isEmpty())
										blocks.add(id);
								}
							}
						} else if ("minecraft:randomized_int_state_provider".equals(type) && o.has("source")) {
							// Randomizing a property's integer value does not change the source block.
							provider(o.get("source"), blocks, owner);
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
						else if ("minecraft:nether_forest_vegetation".equals(type))
							provider(config.get("state_provider"), blocks, owner);
						else if ("minecraft:vegetation_patch".equals(type) || "minecraft:waterlogged_vegetation_patch".equals(type)) {
							provider(config.get("ground_state"), blocks, owner);
							placedBlocks(config.get("vegetation_feature"), blocks, visiting, owner);
						} else if ("minecraft:root_system".equals(type)) {
							placedBlocks(config.get("feature"), blocks, visiting, owner);
							if (config.has("root_state_provider"))
								provider(config.get("root_state_provider"), blocks, owner);
							if (config.has("hanging_root_state_provider"))
								provider(config.get("hanging_root_state_provider"), blocks, owner);
						} else if ("minecraft:random_boolean_selector".equals(type)) {
							placedBlocks(config.get("feature_true"), blocks, visiting, owner);
							placedBlocks(config.get("feature_false"), blocks, visiting, owner);
						} else if ("minecraft:sea_pickle".equals(type) && config.has("count")) {
							// This vanilla feature codec has a fixed block output instead of a provider.
							// Resolve that block through its registered feature identity and validate its type.
							var block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse(type));
							if (block instanceof net.minecraft.world.level.block.SeaPickleBlock)
								blocks.add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString());
						} else if ("minecraft:tree".equals(type)) {
							provider(config.get("trunk_provider"), blocks, owner);
							provider(config.get("foliage_provider"), blocks, owner);
							if (config.has("foliage_placer") && config.get("foliage_placer").isJsonObject()) {
								String fruit = pamPlacerFruit(str(config.getAsJsonObject("foliage_placer"), "type"));
								if (!fruit.isEmpty()) {
									var block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse(fruit));
									String cls = block.getClass().getName();
									if (cls.equals("com.pam.pamhc2trees.blocks.BlockPamFruit") || cls.equals("com.pam.pamhc2trees.blocks.BlockPamLogFruit"))
										blocks.add(fruit);
									else
										issue(owner, "pam_fruit_placer_block_unverified:" + fruit);
								}
							}
							if (config.has("decorators"))
								for (var element : config.getAsJsonArray("decorators")) {
									if (!element.isJsonObject())
										continue;
									var decorator = element.getAsJsonObject();
									String kind = str(decorator, "type");
									if ("productivetrees:cocoa_pod".equals(kind)) {
										if (decorator.has("chance") && decorator.get("chance").getAsDouble() > 0)
											blocks.add("minecraft:cocoa");
										continue;
									}
									if (java.util.Set.of("productivetrees:fruit_leaf_replacer", "productivetrees:fruit_leaf_placer", "productivetrees:fruit_trunk_dangler").contains(kind)) {
										// verified decorators place a fruit state from their serialized provider
										if (decorator.has("density") && decorator.get("density").isJsonPrimitive() && decorator.get("density").getAsDouble() <= 0)
											continue;
										if (decorator.has("max_fruits") && decorator.get("max_fruits").isJsonPrimitive() && decorator.get("max_fruits").getAsInt() <= 0)
											continue;
										if (decorator.has("fruit_provider"))
											provider(decorator.get("fruit_provider"), blocks, owner);
										else
											issue(owner, "productive_fruit_decorator_without_provider:" + kind);
										continue;
									}
									if (!java.util.Set.of("minecraft:cocoa", "minecraft:beehive").contains(kind)) {
										issue(owner, "tree_decorator_outputs_not_interpreted:" + kind);
										continue;
									}
									if (!decorator.has("probability") || !decorator.get("probability").isJsonPrimitive() || !decorator.get("probability").getAsJsonPrimitive().isNumber() || decorator.get("probability").getAsDouble() <= 0)
										continue;
									if ("minecraft:cocoa".equals(kind))
										blocks.add("minecraft:cocoa");
									else if ("minecraft:beehive".equals(kind))
										blocks.add("minecraft:bee_nest");
									else
										issue(owner, "tree_decorator_outputs_not_interpreted:" + kind);
								}
						} else if ("productivetrees:template_tree".equals(type) && config.has("templates") && config.get("templates").isJsonArray()) {
							// Read the actual registered structure palettes; never place a structure or generate a chunk.
							for (var element : config.getAsJsonArray("templates")) {
								if (!element.isJsonPrimitive())
									continue;
								String templateId = element.getAsString();
								var cached = templateBlocksCache.get(templateId);
								if (cached != null) {
									blocks.addAll(cached);
									continue;
								}
								try {
									var template = level.getStructureManager().get(net.minecraft.resources.ResourceLocation.parse(templateId));
									if (template.isEmpty()) {
										issue(owner, "missing_tree_template:" + templateId);
										continue;
									}
									var field = template.get().getClass().getDeclaredField("palettes");
									if (!field.trySetAccessible())
										continue;
									var found = new java.util.TreeSet<String>();
									if (field.get(template.get()) instanceof java.lang.Iterable<?> palettes)
										for (Object palette : palettes) {
											var method = palette.getClass().getDeclaredMethod("blocks");
											if (!method.trySetAccessible())
												continue;
											if (method.invoke(palette) instanceof java.lang.Iterable<?> entries)
												for (Object entry : entries) {
													var stateMethod = entry.getClass().getMethod("state");
													if (stateMethod.invoke(entry) instanceof net.minecraft.world.level.block.state.BlockState state)
														found.add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
												}
										}
									found.remove("minecraft:air");
									templateBlocksCache.put(templateId, found);
									blocks.addAll(found);
								} catch (ReflectiveOperationException | RuntimeException failure) {
									issue(owner, "tree_template_unresolved:" + templateId);
								}
							}
						} else if ("minecraft:block_column".equals(type) && config.has("layers")) {
							for (var element : config.getAsJsonArray("layers"))
								if (element.isJsonObject()) {
									var layer = element.getAsJsonObject();
									if (layer.has("height") && layer.get("height").isJsonPrimitive() && layer.get("height").getAsInt() <= 0)
										continue;
									provider(layer.get("provider"), blocks, owner);
								}
						} else if ("minecraft:ore".equals(type) && config.has("targets")) {
							if (config.has("size") && config.get("size").getAsInt() > 0)
								for (var target : config.getAsJsonArray("targets"))
									if (target.isJsonObject() && target.getAsJsonObject().has("state")) {
										String id = str(target.getAsJsonObject().getAsJsonObject("state"), "Name");
										if (!id.isEmpty())
											blocks.add(id);
									}
						} else if ("minecraft:kelp".equals(type)) {
							blocks.add("minecraft:kelp");
							blocks.add("minecraft:kelp_plant");
						} else if ("minecraft:chorus_plant".equals(type)) {
							blocks.add("minecraft:chorus_plant");
							blocks.add("minecraft:chorus_flower");
						} else if ("minecraft:seagrass".equals(type)) {
							blocks.add("minecraft:seagrass");
							blocks.add("minecraft:tall_seagrass");
						} else if ("minecraft:bamboo".equals(type))
							blocks.add("minecraft:bamboo");
						else if ("minecraft:random_selector".equals(type)) {
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
						return condition(c, block, states.getOrDefault(block, java.util.Collections.emptyMap()));
					}

					int condition(com.google.gson.JsonObject c, String block, java.util.Map<String, String> state) {
						String type = str(c, "condition");
						if ("minecraft:block_state_property".equals(type)) {
							if (!str(c, "block").equals(block))
								return 0;
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
							int v = condition(c.getAsJsonObject("term"), block, state);
							return v < 0 ? -1 : 1 - v;
						}
						if (java.util.Set.of("minecraft:all_of", "minecraft:any_of").contains(type) && c.has("terms")) {
							if (c.getAsJsonArray("terms").isEmpty())
								return -1;
							boolean any = "minecraft:any_of".equals(type), unknown = false;
							for (var e : c.getAsJsonArray("terms")) {
								int v = condition(e.getAsJsonObject(), block, state);
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
								r.addProperty("maturity", crops.contains(block) ? "CropBlock_max_age" : ageBlocks.contains(block) ? "age_property_max_age" : "default_state_only");
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
							var original = states.get(e.getKey());
							var alternatives = growthStateAlternatives.getOrDefault(e.getKey(), java.util.List.of(original));
							try {
								for (var candidate : alternatives) {
									states.put(e.getKey(), candidate);
									loot(t, e.getKey(), e.getValue(), new com.google.gson.JsonArray(), new com.google.gson.JsonArray(), new java.util.HashSet<>(java.util.List.of(e.getValue())), 0, new com.google.gson.JsonArray());
								}
							} finally {
								states.put(e.getKey(), original);
							}
						}
					}

					com.google.gson.JsonObject report(com.google.gson.JsonObject database) {
						var result = new com.google.gson.JsonObject();
						result.addProperty("reader_version", 4);
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
						// v36: a StemBlock named <x>_stem conventionally grows the fruit block <x>; scan those fruit blocks too.
						var stemFruits = new java.util.HashSet<String>();
						for (var stemCandidate : net.minecraft.core.registries.BuiltInRegistries.BLOCK)
							if (stemCandidate instanceof net.minecraft.world.level.block.StemBlock) {
								String stemId = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(stemCandidate).toString();
								index.stems.add(stemId);
								if (stemId.endsWith("_stem"))
									stemFruits.add(stemId.substring(0, stemId.length() - 5));
							}
						int count = 0;
						for (var block : net.minecraft.core.registries.BuiltInRegistries.BLOCK) {
							String id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
							boolean plant = block instanceof net.minecraft.world.level.block.BushBlock || block instanceof net.minecraft.world.level.block.LeavesBlock || block instanceof net.minecraft.world.level.block.GrowingPlantBlock
									|| index.seeds.containsKey(id) && (block instanceof net.minecraft.world.level.block.CocoaBlock || block instanceof net.minecraft.world.level.block.SaplingBlock)
									|| block instanceof net.minecraft.world.level.block.SugarCaneBlock || block instanceof net.minecraft.world.level.block.CactusBlock || block instanceof net.minecraft.world.level.block.SnowLayerBlock
									|| generated.contains(id) || stemFruits.contains(id);
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
								} else {
									for (var stateProperty : state.getProperties())
										if (stateProperty.getName().equals("age") && stateProperty instanceof net.minecraft.world.level.block.state.properties.IntegerProperty ageProperty) {
											int maxAge = 0;
											for (int candidateAge : ageProperty.getPossibleValues())
												maxAge = Math.max(maxAge, candidateAge);
											state = state.setValue(ageProperty, maxAge);
											index.ageBlocks.add(id);
											break;
										}
								}
								if (block instanceof net.minecraft.world.level.block.SugarCaneBlock || block instanceof net.minecraft.world.level.block.CactusBlock || block instanceof net.minecraft.world.level.block.GrowingPlantBlock
										|| block instanceof net.minecraft.world.level.block.MushroomBlock)
									index.spreading.add(id);
								var values = new java.util.TreeMap<String, String>();
								state.getValues().forEach((property, value) -> values.put(property.getName(), String.valueOf(value)));
								index.states.put(id, values);
								// Some growing plants encode harvest readiness as a boolean instead
								// of age (or alongside age). Enumerate only registered boolean states
								// of growth-capable blocks, bounded to avoid exponential modded scans.
								if (block instanceof net.minecraft.world.level.block.GrowingPlantBlock || index.ageBlocks.contains(id)) {
									java.util.List<java.util.Map<String, String>> alternatives = new java.util.ArrayList<>();
									alternatives.add(new java.util.TreeMap<>(values));
									int flags = 0;
									for (var property : state.getProperties()) {
										if (!(property instanceof net.minecraft.world.level.block.state.properties.BooleanProperty))
											continue;
										if (++flags > 4) {
											index.issue(id, "growth_boolean_state_budget_exceeded");
											break;
										}
										var next = new java.util.ArrayList<java.util.Map<String, String>>();
										for (var prior : alternatives)
											for (boolean flag : new boolean[]{false, true}) {
												var variant = new java.util.TreeMap<>(prior);
												variant.put(property.getName(), String.valueOf(flag));
												next.add(variant);
											}
										alternatives = next;
									}
									index.growthStateAlternatives.put(id, alternatives);
								}
								index.blockTables.put(id, block.getLootTable().location().toString());
								if (!(block instanceof net.minecraft.world.level.block.CropBlock) && !index.ageBlocks.contains(id))
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
					// v36.2 — diagnostic only. Nothing here feeds tiers, Recipe Discovery cards, menus or orders, and the acquisition cost graph is not modified.
					// The analysis builds its OWN route set so that evidence can be checked, instead of trusting routes that silently dropped inputs:
					// * recipe routes use COMPLETE inputs (items + fluids); a recipe whose inputs cannot be read is unusable, never "cheaper";
					// * recipes with an empty standard result or empty item inputs get a strictly typed reflective fallback, capped at assumed_renewable;
					// * mob drops and fishing loot are parsed with explicit condition semantics; any condition not understood removes the drop (and is counted);
					// * tool conditions become tool nodes that need an obtainable, reusable item; they cap the result at assumed_renewable.
					// Ranks: 3 = renewable, 2 = assumed_renewable, 0 = nothing proven. Least fixed point over ALL routes (OR between routes, AND between slots).
					final class RenewRoute {
						final String system;
						final String recipeId;
						final java.util.List<java.util.List<String>> slots;
						final int cap;
						final String evidence;

						RenewRoute(String system, String recipeId, java.util.List<java.util.List<String>> slots, int cap, String evidence) {
							this.system = system;
							this.recipeId = recipeId;
							this.slots = slots;
							this.cap = cap;
							this.evidence = evidence.length() > 320 ? evidence.substring(0, 320) : evidence;
						}
					}

					final class DropPath {
						final java.util.List<String> toolNodes = new java.util.ArrayList<>();
						final java.util.List<String> kinds = new java.util.ArrayList<>();
						double maxCount = 1.0; // upper bound on the stack size after known count functions
						double relativeChance = 1.0; // ranking estimate only, never renewability evidence
						int cap = 3; // lowered when a condition is satisfiable but not fully validated

						DropPath copy() {
							var copy = new DropPath();
							copy.toolNodes.addAll(toolNodes);
							copy.kinds.addAll(kinds);
							copy.maxCount = maxCount;
							copy.relativeChance = relativeChance;
							copy.cap = cap;
							return copy;
						}
					}

					final int maxPasses = 500;
					final String unresolved = "omnichef:unresolved_requirement";
					final java.util.Set<String> specialRecipeClasses = java.util.Set.of("cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe", "cy.jdkdigital.productivebees.common.recipe.BeeBreedingRecipe",
							"cy.jdkdigital.productivebees.common.recipe.BeeConversionRecipe", "cy.jdkdigital.productivebees.common.recipe.AdvancedBeehiveRecipe");
					final PlantEvidenceIndex plants;
					final java.util.Map<String, Integer> value = new java.util.HashMap<>();
					// Independent least fixed points: first access may use finite stock; recurring access may not.
					final java.util.Map<String, Integer> firstAccess = new java.util.HashMap<>();
					final java.util.Map<String, Double> sourceRarity = new java.util.HashMap<>();
					final java.util.Map<String, Integer> evidenceMinimumTiers = new java.util.HashMap<>();
					final java.util.Map<RenewRoute, Double> routeRelativeChance = new java.util.IdentityHashMap<>();
					final java.util.Map<String, Integer> mobBiomeCoverage = new java.util.HashMap<>();
					final java.util.Map<String, Integer> structureCropAccess = new java.util.TreeMap<>();
					final java.util.Map<String, Integer> repeatAccess = new java.util.HashMap<>();
					final java.util.Map<String, Integer> directAccess = new java.util.HashMap<>();
					final java.util.Map<String, Integer> directRepeatAccess = new java.util.HashMap<>();
					final java.util.Map<String, java.util.Set<String>> harvestPlanting = new java.util.TreeMap<>();
					final java.util.Map<String, Integer> growthAccessSeen = new java.util.HashMap<>(), regrowthAccessSeen = new java.util.HashMap<>();
					final java.util.Set<String> chestModifierItems = new java.util.HashSet<>();
					final java.util.Map<String, java.util.List<String>> equipmentAccess = new java.util.HashMap<>();
					final java.util.Map<String, String> basis = new java.util.HashMap<>();
					final java.util.Map<String, RenewRoute> bestRoute = new java.util.HashMap<>();
					final java.util.Set<String> unverified = new java.util.HashSet<>();
					final java.util.Set<String> allNodeSet = new java.util.HashSet<>();
					final java.util.List<String> probeNodes = new java.util.ArrayList<>();
					final java.util.List<String> routedNodes = new java.util.ArrayList<>();
					final java.util.Map<String, java.util.List<RenewRoute>> renewRoutes = new java.util.HashMap<>();
					final java.util.Set<String> structureSpawnEntities = new java.util.TreeSet<>();
					final java.util.Set<String> raidEntities = new java.util.TreeSet<>();
					final java.util.Set<String> standardTypes = new java.util.TreeSet<>();
					final java.util.Set<String> producedFluids = new java.util.TreeSet<>();
					final java.util.Set<String> markedEntities = new java.util.HashSet<>();
					final java.util.Set<String> registeredTools = new java.util.TreeSet<>();
					final java.util.Map<String, String> entityByLootTable = new java.util.TreeMap<>();
					final java.util.Map<Class<?>, java.util.Optional<java.lang.reflect.Method>> fluidInputReaders = new java.util.HashMap<>();
					final java.util.Map<Class<?>, java.util.List<java.lang.reflect.Method>> fluidOutputMethods = new java.util.HashMap<>();
					final java.util.Map<java.util.List<Object>, String> auditVariantNodes = new java.util.HashMap<>();
					final java.util.Map<Class<?>, Boolean> unreadFluidInput = new java.util.HashMap<>();
					final java.util.Map<String, int[]> recipeCensus = new java.util.TreeMap<>();
					final java.util.Map<String, Integer> adapterCounts = new java.util.TreeMap<>();
					final java.util.Map<String, Integer> notes = new java.util.TreeMap<>();
					final java.util.Map<String, Integer> unrecognizedCounts = new java.util.TreeMap<>();
					final java.util.Map<String, java.util.List<String>> unrecognizedSamples = new java.util.TreeMap<>();
					final java.util.Map<String, Integer> growthKinds = new java.util.TreeMap<>();
					final java.util.Map<String, java.util.Set<String>> rootMemo = new java.util.HashMap<>();
					final com.google.gson.JsonArray selfReplanting = new com.google.gson.JsonArray();
					final java.util.List<String> plantTags = java.util.List.of("c:crops", "c:foods/fruit", "c:foods/vegetable", "c:foods/berry", "minecraft:flowers", "minecraft:saplings");
					final java.util.List<String> livestockTags = java.util.List.of("c:eggs", "c:buckets/milk");
					// Ores and ingots are finite in principle but assumed unlimited (product decision). Salt is NOT assumed: it is usually a processed product.
					final java.util.List<String> resourceTags = java.util.List.of("c:ores", "c:raw_materials", "c:ingots", "c:nuggets", "c:gems", "c:dusts");
					int passes = 0;
					int mobEntitiesScanned = 0;
					int structuresScanned = 0;
					int skippedTotal = 0;
					boolean converged = false;
					boolean fishingContext = false;
					String activeLootEntity = "";
					boolean testMode = false;
					final java.util.List<String> fishingRoots = new java.util.ArrayList<>();
					final com.google.gson.JsonArray mechanismAccepted = new com.google.gson.JsonArray();
					final com.google.gson.JsonArray mechanismRejected = new com.google.gson.JsonArray();
					final java.util.Set<String> itemNamespaces = new java.util.TreeSet<>();
					final java.util.Map<String, Integer> mechanismReasons = new java.util.TreeMap<>();
					final java.util.List<MechanismRecord> mechanismRecords = new java.util.ArrayList<>();
					int selfTestsPassed = 0;
					int selfTestsFailed = 0;
					final com.google.gson.JsonArray selfTests = new com.google.gson.JsonArray();
					final java.util.Map<String, String> toolSatisfiedBy = new java.util.TreeMap<>();
					final java.util.Map<String, Integer> skippedByReason = new java.util.TreeMap<>();
					final com.google.gson.JsonArray skippedSources = new com.google.gson.JsonArray();

					RenewabilityAnalysis(PlantEvidenceIndex plants) {
						this.plants = plants;
					}

					// ---------- bookkeeping ----------
					void note(String key) {
						notes.merge(key, 1, Integer::sum);
					}

					void count(String key) {
						adapterCounts.merge(key, 1, Integer::sum);
					}

					void countUnrecognized(String kind, String sample) {
						unrecognizedCounts.merge(kind, 1, Integer::sum);
						var samples = unrecognizedSamples.computeIfAbsent(kind, ignored -> new java.util.ArrayList<>());
						if (samples.size() < 3)
							samples.add(sample);
					}

					// Records a source that could not be inspected. It contributes no evidence and is never marked renewable.
					void skip(String kind, String id, Throwable failure) {
						skippedTotal++;
						skippedByReason.merge(kind + ":" + failure.getClass().getSimpleName(), 1, Integer::sum);
						if (skippedSources.size() < 40) {
							var record = new com.google.gson.JsonObject();
							record.addProperty("kind", kind);
							record.addProperty("id", id);
							record.addProperty("exception", failure.getClass().getName());
							String message = String.valueOf(failure.getMessage());
							record.addProperty("message", message.length() > 160 ? message.substring(0, 160) : message);
							skippedSources.add(record);
						}
					}

					boolean raise(String node, int rank, String why) {
						if (rank <= value.getOrDefault(node, 0))
							return false;
						value.put(node, rank);
						basis.put(node, why);
						return true;
					}

					void firstRoot(String node, int floor) {
						directAccess.merge(node, floor, Math::min);
					}

					void repeatRoot(String node, int floor) {
						firstRoot(node, floor);
						directRepeatAccess.merge(node, floor, Math::min);
					}

					int dimensionFloor(String id) {
						return "minecraft:overworld".equals(id) ? 0 : "minecraft:the_nether".equals(id) ? 1 : 3;
					}

					int locationFloor(String block) {
						int floor = 99;
						for (var row : plants.locations.getOrDefault(block, new com.google.gson.JsonArray())) {
							var data = row.getAsJsonObject();
							for (var dim : data.getAsJsonArray("dimensions")) {
								int candidate = dimensionFloor(dim.getAsString());
								if (candidate == 0 && data.getAsJsonArray("biomes").size() <= 2)
									candidate = 1;
								floor = Math.min(floor, candidate);
							}
						}
						return floor;
					}

					int entityFloor(String id) {
						int floor = 99;
						try {
							var entity = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(net.minecraft.resources.ResourceLocation.parse(id));
							var registry = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME);
							int overworldBiomes = 0;
							for (var biome : registry) {
								boolean present = false;
								for (var category : net.minecraft.world.entity.MobCategory.values())
									for (var spawn : biome.getMobSettings().getMobs(category).unwrap())
										if (spawn.type == entity)
											present = true;
								if (!present)
									continue;
								var key = registry.getKey(biome);
								if (key == null)
									continue;
								for (String dim : plants.biomeDimensions.getOrDefault(key.toString(), java.util.Collections.emptySet())) {
									floor = Math.min(floor, dimensionFloor(dim));
									if ("minecraft:overworld".equals(dim))
										overworldBiomes++;
								}
							}
							mobBiomeCoverage.put(id, overworldBiomes);
							if (floor == 0 && (overworldBiomes <= 2 || entity.getCategory() == net.minecraft.world.entity.MobCategory.MONSTER))
								floor = 1;
						} catch (RuntimeException | LinkageError failure) {
							note("entity_access_unresolved");
						}
						if (floor == 99 && (structureSpawnEntities.contains(id) || raidEntities.contains(id)))
							floor = 2;
						return floor;
					}

					int methodFloor(String system) {
						if (system.startsWith("minecraft:") || system.startsWith("farmersdelight:") || system.startsWith("omnichef:"))
							return 0;
						// Unknown modded processing stations cannot be treated as hand crafting.
						return 1;
					}

					void appendStationRequirements(String type, java.util.List<java.util.List<String>> slots) {
						// Recipe-family adapters describe equipment, not per-Food tier exceptions.
						var stations = switch (type) {
							case "minecraft:smelting" -> java.util.List.of("minecraft:furnace");
							case "minecraft:smoking" -> java.util.List.of("minecraft:smoker");
							case "minecraft:blasting" -> java.util.List.of("minecraft:blast_furnace");
							case "minecraft:campfire_cooking" -> java.util.List.of("minecraft:campfire");
							case "farmersdelight:cooking" -> java.util.List.of("farmersdelight:cooking_pot");
							case "farmersdelight:cutting" -> java.util.List.of("farmersdelight:cutting_board");
							case "create:mixing" -> java.util.List.of("create:basin", "create:mechanical_mixer");
							case "create:compacting" -> java.util.List.of("create:basin", "create:mechanical_press");
							case "create:cutting" -> java.util.List.of("create:mechanical_saw");
							case "create:filling" -> java.util.List.of("create:spout");
							case "create:milling" -> java.util.List.of("create:millstone");
							case "create:crushing" -> java.util.List.of("create:crushing_wheel");
							default -> java.util.List.<String>of();
						};
						for (String station : stations) {
							if (!itemsById.containsKey(station)) {
								slots.add(java.util.List.of(unresolved));
								continue;
							}
							slots.add(java.util.List.of("tool|" + station));
						}
					}

					int routeAccess(RenewRoute route, java.util.Map<String, Integer> access, boolean recurring) {
						if (route.slots.isEmpty())
							return 99;
						int floor = methodFloor(route.system);
						for (var slot : route.slots) {
							int best = 99;
							for (String alternative : slot) {
								var source = recurring && equipmentAccess.containsKey(alternative) ? firstAccess : access;
								best = Math.min(best, source.getOrDefault(alternative, 99));
							}
							if (best == 99)
								return 99;
							floor = Math.max(floor, best);
						}
						return floor;
					}

					void solveAccess(boolean recurring) {
						var access = recurring ? repeatAccess : firstAccess;
						for (var root : (recurring ? directRepeatAccess : directAccess).entrySet())
							access.merge(root.getKey(), root.getValue(), Math::min);
						for (int pass = 0; pass < maxPasses; pass++) {
							boolean changed = false;
							for (var eq : equipmentAccess.entrySet()) {
								int best = 99;
								for (String item : eq.getValue())
									best = Math.min(best, firstAccess.getOrDefault(item, 99));
								if (best < access.getOrDefault(eq.getKey(), 99)) {
									access.put(eq.getKey(), best);
									changed = true;
								}
							}
							for (String node : routedNodes)
								for (var route : renewRoutes.get(node)) {
									if (recurring && routeValue(route) < 2)
										continue;
									int best = routeAccess(route, access, recurring);
									if (best < access.getOrDefault(node, 99)) {
										access.put(node, best);
										changed = true;
									}
								}
							if (!changed)
								return;
						}
						throw new IllegalStateException("Access fixed point did not converge");
					}

					String string(com.google.gson.JsonObject object, String key) {
						return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString() : "";
					}

					boolean hasLocation(String block) {
						var array = plants.locations.get(block);
						return array != null && !array.isEmpty();
					}

					boolean anyTag(String id, java.util.List<String> names) {
						for (String name : names) {
							var key = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse(name));
							if (classificationRules.matchesTags(id, java.util.List.of(key)))
								return true;
						}
						return false;
					}

					String fluidNode(String fluid) {
						return "omnichef:fluid/" + fluid.replace(':', '/');
					}

					void addRoute(String output, RenewRoute route) {
						allNodeSet.add(output);
						renewRoutes.computeIfAbsent(output, ignored -> new java.util.ArrayList<>()).add(route);
					}

					// ---------- entity evidence (no codec encoding; see v36.1 note about Mo' Structures) ----------
					void indexStructureAndRaidSpawns() {
						net.minecraft.core.Registry<net.minecraft.world.level.levelgen.structure.Structure> registry = null;
						try {
							registry = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.STRUCTURE);
						} catch (RuntimeException | LinkageError failure) {
							skip("structure_registry", "minecraft:worldgen/structure", failure);
						}
						if (registry != null)
							for (var structure : registry) {
								String structureId = "unknown";
								try {
									structureId = String.valueOf(registry.getKey(structure));
									for (var spawnOverride : structure.spawnOverrides().values())
										for (var spawn : spawnOverride.spawns().unwrap())
											structureSpawnEntities.add(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(spawn.type).toString());
									structuresScanned++;
								} catch (RuntimeException | LinkageError failure) {
									skip("structure", structureId, failure);
								}
							}
						var raiders = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, net.minecraft.resources.ResourceLocation.parse("minecraft:raiders"));
						for (var entity : net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE) {
							String entityId = "unknown";
							try {
								entityId = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity).toString();
								if (entity.is(raiders))
									raidEntities.add(entityId);
							} catch (RuntimeException | LinkageError failure) {
								skip("raider_tag", entityId, failure);
							}
						}
					}

					// A listed structure spawn or raid membership says the mob can appear again, not that it always does: assumed only.
					int entityRank(String entity) {
						if (acquisitionSources.biomeSpawnIndexReady && acquisitionSources.biomeSpawnEntities.contains(entity))
							return 3;
						if (structureSpawnEntities.contains(entity))
							return 2;
						if (raidEntities.contains(entity))
							return 2;
						return 0;
					}

					String entityClass(String entity) {
						if (acquisitionSources.biomeSpawnIndexReady && acquisitionSources.biomeSpawnEntities.contains(entity))
							return "biome_spawn";
						if (structureSpawnEntities.contains(entity))
							return "structure_spawn_listed";
						if (raidEntities.contains(entity))
							return "raid_member";
						return "unverified";
					}

					void markEntity(String entityId) {
						if (!markedEntities.add(entityId))
							return;
						int rank = entityRank(entityId);
						if (rank > 0) {
							int floor = entityFloor(entityId);
							if (floor < 99) {
								raise("entity|" + entityId, rank, "base:entity_spawn:" + entityClass(entityId));
								repeatRoot("entity|" + entityId, floor);
							}
						} else
							unverified.add("entity|" + entityId);
					}

					// ---------- recipe routes with COMPLETE inputs ----------
					String outputNode(net.minecraft.world.item.ItemStack stack) {
						String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
						if (net.minecraft.world.item.ItemStack.isSameItemSameComponents(stack, new net.minecraft.world.item.ItemStack(stack.getItem())))
							return "item|" + id;
						// Read-only lookup: never register new variant nodes in the acquisition graph.
						var key = java.util.List.<Object>of(id, stack.copy().getComponents());
						String existing = acquisitionSources.variantNodes.get(key);
						if (existing != null)
							return existing;
						// Keep exact component identity in the audit only, without mutating the acquisition or tier graph.
						if (!classificationRules.isCulinary(id) && !java.util.Set.of("minecraft:potion", "minecraft:splash_potion", "minecraft:lingering_potion").contains(id))
							return null;
						return auditVariantNodes.computeIfAbsent(key, ignored -> "audit_variant|" + id + "|" + auditVariantNodes.size());
					}

					java.util.List<String> itemNodes(java.util.List<String> ids) {
						var nodes = new java.util.ArrayList<String>();
						for (String id : ids)
							nodes.add("item|" + id);
						return nodes;
					}

					// Audit only: keep component-sensitive inputs separate from their generic item ID.
					// A real WATER stack must be accepted by the live Ingredient; no potion-name heuristic.
					java.util.List<String> auditedIngredientNodes(net.minecraft.world.item.crafting.Ingredient ingredient) {
						var nodes = new java.util.TreeSet<String>();
						boolean potionCandidate = false;
						for (var stack : ingredient.getItems()) {
							if (stack == null || stack.isEmpty())
								continue;
							String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
							if ("minecraft:barrier".equals(id))
								continue;
							if (id.equals("minecraft:potion") || id.equals("minecraft:splash_potion") || id.equals("minecraft:lingering_potion"))
								potionCandidate = true;
							if (!ingredient.test(stack))
								continue;
							String node = outputNode(stack);
							if (node != null)
								nodes.add(node);
						}
						var potion = itemsById.get("minecraft:potion");
						if (potionCandidate && potion != null && itemsById.containsKey("minecraft:glass_bottle") && itemsById.containsKey("minecraft:water_bucket")) {
							var water = net.minecraft.world.item.alchemy.PotionContents.createItemStack(potion, net.minecraft.world.item.alchemy.Potions.WATER);
							if (ingredient.test(water)) {
								String node = "omnichef:audit/water_bottle";
								if (!renewRoutes.containsKey(node))
									addRoute(node, new RenewRoute("omnichef:component_water_bottle", "vanilla_fill_water_bottle", java.util.List.of(java.util.List.of("item|minecraft:glass_bottle"), java.util.List.of("water|minecraft:water")), 2,
											"live ingredient accepts ordinary water bottle; requires glass bottle and reusable water source"));
								nodes.add(node);
								count("component_water_input_accepted");
							}
						}
						return new java.util.ArrayList<>(nodes);
					}

					java.util.Optional<java.lang.reflect.Method> fluidInputReader(Class<?> type) {
						return fluidInputReaders.computeIfAbsent(type, ignored -> {
							try {
								return java.util.Optional.of(type.getMethod("getFluidIngredients"));
							} catch (NoSuchMethodException absent) {
								return java.util.Optional.empty();
							}
						});
					}

					// A fluid input accessor we do not read means the inputs are incomplete: such a recipe must not look cheaper than it is.
					boolean exposesUnreadFluidInput(Class<?> type) {
						return unreadFluidInput.computeIfAbsent(type, ignored -> {
							for (var method : type.getMethods()) {
								if (method.getParameterCount() != 0)
									continue;
								String name = method.getName().toLowerCase(java.util.Locale.ROOT);
								if (name.equals("getfluidingredients"))
									continue;
								if (name.contains("fluid") && (name.contains("input") || name.contains("ingredient")))
									return true;
							}
							return false;
						});
					}

					java.util.List<java.lang.reflect.Method> fluidOutputReaders(Class<?> type) {
						return fluidOutputMethods.computeIfAbsent(type, ignored -> {
							var methods = new java.util.ArrayList<java.lang.reflect.Method>();
							for (var method : type.getMethods()) {
								if (method.getParameterCount() != 0)
									continue;
								String name = method.getName().toLowerCase(java.util.Locale.ROOT);
								if (!name.contains("fluid") || !(name.contains("result") || name.contains("output")))
									continue;
								Class<?> returned = method.getReturnType();
								if (net.neoforged.neoforge.fluids.FluidStack.class.isAssignableFrom(returned) || java.lang.Iterable.class.isAssignableFrom(returned))
									methods.add(method);
							}
							return methods;
						});
					}

					// Typed reads only: a FluidStack, or an Iterable that actually contains FluidStacks.
					java.util.List<String> fluidOutputs(Object recipe) {
						var fluids = new java.util.TreeSet<String>();
						for (var method : fluidOutputReaders(recipe.getClass())) {
							Object read;
							try {
								read = method.invoke(recipe);
							} catch (ReflectiveOperationException | RuntimeException failure) {
								count("fluid_output_reader_failed");
								continue;
							}
							if (read instanceof net.neoforged.neoforge.fluids.FluidStack stack) {
								if (!stack.isEmpty())
									fluids.add(net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString());
							} else if (read instanceof java.lang.Iterable<?> many) {
								for (var element : many)
									if (element instanceof net.neoforged.neoforge.fluids.FluidStack stack && !stack.isEmpty())
										fluids.add(net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString());
							}
						}
						return new java.util.ArrayList<>(fluids);
					}

					// Fallbacks for recipe classes that do not expose results/inputs through the standard Recipe API. Strict types, never guessed; capped at assumed.
					net.minecraft.world.item.ItemStack reflectiveResult(Object recipe) {
						for (String name : java.util.List.of("getResult", "result", "getOutput", "output", "getResultStack", "getOutputItem")) {
							try {
								var method = recipe.getClass().getMethod(name);
								if (!net.minecraft.world.item.ItemStack.class.isAssignableFrom(method.getReturnType()))
									continue;
								var read = method.invoke(recipe);
								if (read instanceof net.minecraft.world.item.ItemStack stack && !stack.isEmpty())
									return stack;
							} catch (NoSuchMethodException absent) {
								// Accessor not present on this class.
							} catch (ReflectiveOperationException | RuntimeException failure) {
								count("reflective_result_failed");
							}
						}
						return null;
					}

					java.util.List<net.minecraft.world.item.crafting.Ingredient> reflectiveIngredients(Object recipe) {
						for (String name : java.util.List.of("getIngredient", "ingredient", "getInput", "input", "getInputs", "inputs")) {
							try {
								var method = recipe.getClass().getMethod(name);
								var read = method.invoke(recipe);
								var found = new java.util.ArrayList<net.minecraft.world.item.crafting.Ingredient>();
								if (read instanceof net.minecraft.world.item.crafting.Ingredient single)
									found.add(single);
								else if (read instanceof java.lang.Iterable<?> many) {
									for (var element : many) {
										if (element instanceof net.minecraft.world.item.crafting.Ingredient ingredient)
											found.add(ingredient);
										else {
											found.clear();
											break;
										}
									}
								}
								if (!found.isEmpty())
									return found;
							} catch (NoSuchMethodException absent) {
								// Accessor not present on this class.
							} catch (ReflectiveOperationException | RuntimeException failure) {
								count("reflective_ingredients_failed");
							}
						}
						return null;
					}

					java.util.List<java.util.List<String>> completeSlots(net.minecraft.world.item.crafting.RecipeHolder<?> holder, String typeId, java.util.Set<String> problems) {
						var recipe = holder.value();
						var slots = new java.util.ArrayList<java.util.List<String>>();
						var ingredients = new java.util.ArrayList<net.minecraft.world.item.crafting.Ingredient>();
						if (recipe.getIngredients() != null)
							ingredients.addAll(recipe.getIngredients());
						boolean standardEmpty = ingredients.isEmpty();
						if (standardEmpty) {
							var adapted = reflectiveIngredients(recipe);
							if (adapted != null) {
								ingredients.addAll(adapted);
								problems.add("adapted_inputs");
								count("reflective_ingredients_used");
							}
						}
						for (var ingredient : ingredients) {
							if (ingredient == net.minecraft.world.item.crafting.Ingredient.EMPTY)
								continue;
							var ids = completeRecipeInputs.itemIds(ingredient);
							var nodes = auditedIngredientNodes(ingredient);
							if (nodes.isEmpty()) {
								slots.add(java.util.List.of(unresolved));
								problems.add("unresolved_item_slot");
							} else if (!ids.isEmpty() && ids.stream().allMatch(classificationRules::equipment)) {
								// Only a readable, craftable tool is an assumption. Do not drop an equipment slot.
								var tools = new java.util.ArrayList<String>();
								for (String id : ids) {
									var item = itemsById.get(id);
									if (item != null)
										tools.add("tool|" + id);
								}
								slots.add(tools);
							} else
								slots.add(nodes);
						}
						if (exposesUnreadFluidInput(recipe.getClass())) {
							slots.add(java.util.List.of(unresolved));
							problems.add("unread_fluid_input_accessor");
						}
						var reader = fluidInputReader(recipe.getClass());
						if (reader.isPresent()) {
							try {
								Object read = reader.get().invoke(recipe);
								if (!(read instanceof java.lang.Iterable<?> fluidInputs))
									throw new IllegalArgumentException("Unsupported fluid input collection");
								for (var input : fluidInputs) {
									if (!(input instanceof net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient fluidInput))
										throw new IllegalArgumentException("Unsupported fluid ingredient class");
									var alternatives = new java.util.TreeSet<String>();
									for (var stack : fluidInput.getFluids()) {
										if (stack.isEmpty())
											continue;
										String fluid = net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString();
										var mapped = completeRecipeInputs.itemsForFluid(fluid);
										if (!mapped.isEmpty()) {
											for (String id : mapped)
												alternatives.add(id.startsWith("omnichef:") ? id : "item|" + id);
											count("fluid_input_mapped");
										} else if (producedFluids.contains(fluid)) {
											alternatives.add(fluidNode(fluid));
											count("fluid_input_mapped_to_produced_fluid");
										} else
											count("fluid_input_unmapped");
									}
									if (alternatives.isEmpty()) {
										slots.add(java.util.List.of(unresolved));
										problems.add("unmapped_fluid_input");
									} else
										slots.add(new java.util.ArrayList<>(alternatives));
								}
							} catch (ReflectiveOperationException | RuntimeException failure) {
								slots.add(java.util.List.of(unresolved));
								problems.add("fluid_reader_failed");
							}
						} else if (java.util.Set.of("create:filling", "create:mixing", "create:compacting").contains(typeId)) {
							slots.add(java.util.List.of(unresolved));
							problems.add("fluid_reader_unavailable");
						}
						var containers = new java.util.ArrayList<java.util.List<String>>();
						completeRecipeInputs.appendKitchenContainer(holder, containers);
						completeRecipeInputs.appendVatContainer(holder, containers);
						for (var slot : containers)
							slots.add(itemNodes(slot));
						appendStationRequirements(typeId, slots);
						return slots;
					}

					void indexRecipe(net.minecraft.world.item.crafting.RecipeHolder<?> holder, String typeId, int[] census) {
						var recipe = holder.value();
						String recipeId = holder.id().toString();
						var problems = new java.util.TreeSet<String>();
						net.minecraft.world.item.ItemStack result = recipe.getResultItem(level.registryAccess());
						boolean adaptedResult = false;
						if (result == null || result.isEmpty()) {
							census[1]++;
							var adapted = reflectiveResult(recipe);
							if (adapted != null) {
								result = adapted;
								adaptedResult = true;
								count("reflective_result_used");
							}
						}
						var fluids = fluidOutputs(recipe);
						boolean hasItem = result != null && !result.isEmpty();
						if (!hasItem && fluids.isEmpty())
							return;
						var slots = completeSlots(holder, typeId, problems);
						boolean adaptedInputs = problems.remove("adapted_inputs");
						if (slots.isEmpty())
							census[2]++;
						if (!problems.isEmpty())
							census[4]++;
						int cap = adaptedResult || adaptedInputs ? 2 : 3;
						var route = new RenewRoute(typeId, recipeId, slots, cap, recipeId + (problems.isEmpty() ? "" : " problems=" + problems) + (cap == 2 ? " adapter=reflective" : ""));
						if (hasItem) {
							String node = outputNode(result);
							if (node != null) {
								addRoute(node, route);
								census[3]++;
								count("recipe_routes_built");
							} else
								count("recipe_variant_output_unmapped");
						}
						for (String fluid : fluids) {
							addRoute(fluidNode(fluid), route);
							count("fluid_output_routes");
						}
						if (!problems.isEmpty())
							count("recipe_routes_incomplete");
					}

					void indexRecipes() {
						// Pass 1: fluids that some recipe produces (so fluid inputs can be mapped to a producing node).
						for (var holder : recipeManager.getRecipes()) {
							var recipe = holder.value();
							standardTypes.add(classificationRules.typeId(holder));
							if (specialRecipeClasses.contains(recipe.getClass().getName()))
								continue;
							try {
								producedFluids.addAll(fluidOutputs(recipe));
							} catch (RuntimeException | LinkageError failure) {
								skip("fluid_output_scan", holder.id().toString(), failure);
							}
						}
						// Pass 2: routes with complete inputs.
						for (var holder : recipeManager.getRecipes()) {
							var recipe = holder.value();
							String typeId = classificationRules.typeId(holder);
							var census = recipeCensus.computeIfAbsent(typeId, ignored -> new int[6]);
							census[0]++;
							if (specialRecipeClasses.contains(recipe.getClass().getName())) {
								census[5]++;
								if (recipe.getClass().getName().equals("cy.jdkdigital.productivetrees.recipe.TreePollinationRecipe")) {
									try {
										indexProductivePollination(holder);
									} catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
										note("productive_pollination_unresolved");
									}
								} else
									count("recipes_special_handled_by_graph");
								continue;
							}
							try {
								indexRecipe(holder, typeId, census);
							} catch (RuntimeException | LinkageError failure) {
								skip("recipe", holder.id().toString(), failure);
							}
						}
						// Tools are resolved only after the full recipe index exists.
						var deferredTools = new java.util.TreeSet<String>();
						for (var routes : renewRoutes.values())
							for (var route : routes)
								for (var slot : route.slots)
									for (String input : slot)
										if (input.startsWith("tool|"))
											deferredTools.add(input);
						for (String tool : deferredTools)
							registerToolNode(tool, tool.substring(5));
						indexImportedKitchenRoutes();
						indexDynamicFishFillets();
					}

					void indexProductivePollination(net.minecraft.world.item.crafting.RecipeHolder<?> holder) throws ReflectiveOperationException {
						var recipe = holder.value();
						var result = (net.minecraft.world.item.ItemStack) acquisitionSources.field(recipe, "result");
						double chance = ((Number) acquisitionSources.field(recipe, "chance")).doubleValue();
						if (result.isEmpty() || !Double.isFinite(chance) || chance <= 0)
							return;
						var a = auditedIngredientNodes((net.minecraft.world.item.crafting.Ingredient) acquisitionSources.field(recipe, "leafA"));
						var b = auditedIngredientNodes((net.minecraft.world.item.crafting.Ingredient) acquisitionSources.field(recipe, "leafB"));
						if (a.isEmpty() || b.isEmpty())
							return;
						String output = outputNode(result);
						if (output == null)
							return;
						markEntity("minecraft:bee");
						registerToolNode("tool|#minecraft:flowers", "#minecraft:flowers");
						// BeeReleaseEvent -> TreeUtil.pollinateLeaves checks live leafA/leafB, chance, and a bee delivering honey.
						addRoute(output, new RenewRoute("productivetrees:tree_pollination", holder.id().toString(), java.util.List.of(a, b, java.util.List.of("entity|minecraft:bee"), java.util.List.of("tool|#minecraft:flowers")), 2,
								"live special pollination recipe, bee release with honey, chance=" + chance));
						count("productive_pollination_routes");
					}

					// Catalog's existing verified schema import was missing from the renewability graph.
					// Preserve every physical input. Worker/research/heat access is deliberately assumed, not confirmed.
					void indexImportedKitchenRoutes() {
						for (var entry : splitSources.recipes.entrySet())
							for (var imported : entry.getValue()) {
								var slots = new java.util.ArrayList<java.util.List<String>>();
								for (var slot : imported.slots)
									slots.add(itemNodes(slot));
								var evidence = imported.evidence;
								if (evidence.has("source_conditions")) {
									var conditions = evidence.getAsJsonObject("source_conditions");
									if (conditions.has("tool")) {
										note("imported_kitchen_unread_tool");
										continue;
									}
									String station = string(conditions, "intermediate");
									if (!station.isEmpty() && !"minecraft:air".equals(station)) {
										if (!itemsById.containsKey(station)) {
											note("imported_kitchen_unresolved_station");
											continue;
										}
										if (!hasCompleteRoute("item|" + station)) {
											note("imported_kitchen_unproven_station");
											continue;
										}
										String marker = "tool|" + station;
										registerToolNode(marker, station);
										slots.add(java.util.List.of(marker));
									}
								}
								addRoute("item|" + entry.getKey(), new RenewRoute(imported.method, string(evidence, "resource"), slots, 2, "existing splitSources schema import; yield=" + imported.count + " evidence=" + evidence));
								count("imported_kitchen_routes");
							}
					}

					// Aquaculture 2.7.22: exact active CustomRecipe, live FishData and exact knife condition.
					// Do not predict fish membership by namespace/name or infer it from the registry alone.
					void indexDynamicFishFillets() {
						boolean active = false;
						for (var holder : recipeManager.getRecipes())
							if (holder.value().getClass().getName().equals("com.teammetallurgy.aquaculture.item.crafting.FishFilletRecipe")) {
								active = true;
								break;
							}
						if (!active)
							return;
						try {
							var api = Class.forName("com.teammetallurgy.aquaculture.api.AquacultureAPI");
							var data = api.getField("FISH_DATA").get(null);
							var has = data.getClass().getMethod("hasFilletAmount", net.minecraft.world.item.Item.class);
							var yield = data.getClass().getMethod("getFilletAmount", net.minecraft.world.item.Item.class);
							var knifeTag = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("c:tools/knife"));
							var tools = new java.util.ArrayList<String>();
							for (var entry : new java.util.TreeMap<>(itemsById).entrySet()) {
								var item = entry.getValue();
								var stack = new net.minecraft.world.item.ItemStack(item);
								if (!(item instanceof net.minecraft.world.item.TieredItem) || !stack.is(knifeTag) || !stack.isDamageableItem())
									continue;
								String tool = "tool|" + entry.getKey();
								registerToolNode(tool, entry.getKey());
								if (hasCompleteRoute("item|" + entry.getKey()))
									tools.add(tool);
							}
							if (tools.isEmpty() || !itemsById.containsKey("aquaculture:fish_fillet_raw")) {
								note("fish_fillet_no_tool_or_output");
								return;
							}
							for (var entry : new java.util.TreeMap<>(itemsById).entrySet()) {
								if (!Boolean.TRUE.equals(has.invoke(data, entry.getValue())))
									continue;
								int amount = ((Number) yield.invoke(data, entry.getValue())).intValue();
								if (amount <= 0)
									continue;
								addRoute("item|aquaculture:fish_fillet_raw", new RenewRoute("aquaculture:dynamic_fillet", "fish_fillet", java.util.List.of(java.util.List.of("item|" + entry.getKey()), tools), 2,
										"FishFilletRecipe matches + FishData.hasFilletAmount; base yield=" + amount + "; knife wear and weight modifier"));
								count("dynamic_fillet_routes");
							}
						} catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
							note("dynamic_fillet_adapter_failed");
						}
					}

					// Routes the acquisition graph built from sources that are not plain recipes (bees, trees, mobs, culinary adapters) are kept as they are.
					// Its plain recipe routes are replaced by the complete-input routes above, because they ignored fluid inputs and fluid-only recipes.
					void convertGraphRoutes() {
						for (var entry : acquisitionSources.routes.entrySet()) {
							for (var route : entry.getValue()) {
								if (java.util.Set.of("productivetrees:tree_pollination", "productivetrees:grown_leaves", "productivetrees:fruit").contains(route.system))
									continue;
								if (standardTypes.contains(route.system) || "omnichef:mob_loot".equals(route.system)) {
									count("graph_recipe_routes_replaced");
									continue;
								}
								var slots = new java.util.ArrayList<java.util.List<String>>();
								String evidence = "graph";
								if ("omnichef:mob_loot".equals(route.system)) {
									var mob = acquisitionSources.mobEvidence.get(route);
									String entityId = mob != null && mob.has("entity") ? mob.get("entity").getAsString() : "";
									if (entityId.isEmpty())
										slots.add(java.util.List.of(unresolved));
									else {
										slots.add(java.util.List.of("entity|" + entityId));
										markEntity(entityId);
									}
									evidence = "mob_loot_graph entity=" + entityId;
								} else {
									for (var slot : route.requirements)
										slots.add(new java.util.ArrayList<>(slot));
								}
								addRoute(entry.getKey(), new RenewRoute(route.system, route.system, slots, 3, evidence));
								count("graph_routes_kept");
							}
						}
					}

					// ---------- loot conditions: explicit semantics, anything else removes the drop ----------
					boolean fishingHookOnly(com.google.gson.JsonObject condition) {
						if (!condition.has("predicate") || !condition.get("predicate").isJsonObject())
							return false;
						var predicate = condition.getAsJsonObject("predicate");
						if (predicate.size() != 1 || !predicate.has("type_specific") || !predicate.get("type_specific").isJsonObject())
							return false;
						String type = string(predicate.getAsJsonObject("type_specific"), "type");
						return type.equals("minecraft:fishing_hook") || type.equals("fishing_hook");
					}

					// Only a predicate made of nothing but "items" is understood. Returns null for anything else, "" for "any tool".
					String toolSpec(com.google.gson.JsonElement predicate) {
						if (predicate == null || predicate.isJsonNull())
							return "";
						if (!predicate.isJsonObject())
							return null;
						var object = predicate.getAsJsonObject();
						if (object.size() == 0)
							return "";
						if (object.size() != 1 || !object.has("items"))
							return null;
						var items = object.get("items");
						var tokens = new java.util.TreeSet<String>();
						if (items.isJsonPrimitive())
							tokens.add(items.getAsString());
						else if (items.isJsonArray()) {
							for (var token : items.getAsJsonArray()) {
								if (!token.isJsonPrimitive())
									return null;
								tokens.add(token.getAsString());
							}
						} else
							return null;
						return String.join(",", tokens);
					}

					// True when some recipe route for the node has every input readable (no unresolved slot) and at least one input.
					boolean hasCompleteRoute(String node) {
						var list = renewRoutes.get(node);
						if (list == null)
							return false;
						for (var route : list) {
							if (route.slots.isEmpty())
								continue;
							boolean complete = true;
							for (var slot : route.slots)
								if (slot.contains(unresolved)) {
									complete = false;
									break;
								}
							if (complete)
								return true;
						}
						return false;
					}

					// A tool node is an ASSUMPTION, never a confirmation: some matching item has a readable complete recipe and a durability bar.
					// That a recipe exists does not show it can be made from renewable materials, so the node is capped at rank 2 and the
					// real chain status of the chosen tool is reported separately (tools section of the audit).
					// maxStackSize == 1 is deliberately NOT used as evidence of repeated use.
					void registerToolNode(String node, String spec) {
						if (testMode || !registeredTools.add(node))
							return;
						var found = new java.util.TreeSet<String>();
						for (String token : spec.split(",")) {
							var ids = new java.util.ArrayList<String>();
							if (token.startsWith("#")) {
								var tag = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse(token.substring(1)));
								var named = net.minecraft.core.registries.BuiltInRegistries.ITEM.getTag(tag);
								if (named.isPresent())
									for (var holder : named.get())
										ids.add(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(holder.value()).toString());
							} else
								ids.add(token);
							for (String id : ids) {
								var item = itemsById.get(id);
								if (item == null)
									continue;
								found.add(id);
							}
						}
						if (!found.isEmpty()) {
							// Equipment needs first acquisition, not repeatable replacement.
							equipmentAccess.put(node, itemNodes(new java.util.ArrayList<>(found)));
							toolSatisfiedBy.put(node, found.first());
							count("tool_nodes_with_candidate");
						} else {
							unverified.add(node);
							count("tool_nodes_unverified");
						}
					}

					java.util.List<DropPath> withKind(java.util.List<DropPath> paths, String kind) {
						var next = new java.util.ArrayList<DropPath>();
						for (var path : paths) {
							var copy = path.copy();
							if (!copy.kinds.contains(kind))
								copy.kinds.add(kind);
							next.add(copy);
						}
						return next;
					}

					java.util.List<DropPath> withChance(java.util.List<DropPath> paths, double chance) {
						var next = new java.util.ArrayList<DropPath>();
						for (var path : paths) {
							var copy = path.copy();
							copy.relativeChance *= Math.max(0.0, Math.min(1.0, chance));
							next.add(copy);
						}
						return next;
					}

					java.util.List<DropPath> applyConditions(java.util.List<DropPath> paths, com.google.gson.JsonArray conditions, java.util.Set<String> unrecognized) {
						var current = paths;
						for (var element : conditions) {
							current = applyCondition(current, element, unrecognized);
							if (current == null || current.isEmpty())
								return null;
						}
						return current;
					}

					// ---------- number providers ----------
					double plainNumber(com.google.gson.JsonElement element) {
						if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber())
							return Double.NaN;
						return element.getAsDouble();
					}

					// Largest value a number provider can produce, or NaN when the provider is not understood.
					double maxOf(com.google.gson.JsonElement provider) {
						if (provider == null || provider.isJsonNull())
							return Double.NaN;
						if (provider.isJsonPrimitive())
							return plainNumber(provider);
						if (!provider.isJsonObject())
							return Double.NaN;
						var object = provider.getAsJsonObject();
						String type = string(object, "type");
						if (type.startsWith("minecraft:"))
							type = type.substring("minecraft:".length());
						if (type.equals("constant"))
							return maxOf(object.get("value"));
						if (type.equals("uniform") || (type.isEmpty() && object.has("max")))
							return maxOf(object.get("max"));
						if (type.equals("binomial")) {
							double n = maxOf(object.get("n"));
							double p = maxOf(object.get("p"));
							if (Double.isNaN(n) || Double.isNaN(p))
								return Double.NaN;
							return p <= 0.0 ? 0.0 : n;
						}
						return Double.NaN;
					}

					// Upper bound imposed by limit_count: a number is an exact bound, an object bounds only if it has "max".
					double limitMax(com.google.gson.JsonElement limit) {
						if (limit == null)
							return Double.NaN;
						if (limit.isJsonPrimitive())
							return plainNumber(limit);
						if (limit.isJsonObject())
							return limit.getAsJsonObject().has("max") ? maxOf(limit.getAsJsonObject().get("max")) : Double.POSITIVE_INFINITY;
						return Double.NaN;
					}

					// ---------- loot functions ----------
					// Known count functions are tracked as an upper bound on the stack size. A function with its own conditions applies only sometimes,
					// so the unmodified path stays possible. Unconditional furnace_smelt replaces the item. Every other function may change the
					// result (components, enchantments, potion, map, ...) and is rejected with a diagnostic instead of being treated as evidence.
					java.util.List<DropPath> applyFunctions(java.util.List<DropPath> paths, com.google.gson.JsonArray functions, java.util.Set<String> unrecognized) {
						var current = paths;
						for (var element : functions) {
							if (!element.isJsonObject()) {
								unrecognized.add("function:non_object");
								return null;
							}
							var function = element.getAsJsonObject();
							String type = string(function, "function");
							boolean conditional = function.has("conditions") && function.get("conditions").isJsonArray() && function.getAsJsonArray("conditions").size() > 0;
							if (type.equals("minecraft:furnace_smelt")) {
								if (!conditional) {
									unrecognized.add("function:minecraft:furnace_smelt_unconditional");
									return null;
								}
								continue;
							}
							if (type.equals("minecraft:enchanted_count_increase") || type.equals("minecraft:looting_enchant") || type.equals("minecraft:apply_bonus") || type.equals("minecraft:explosion_decay"))
								continue;
							if (type.equals("minecraft:set_count") || type.equals("minecraft:limit_count")) {
								if (conditional)
									continue;
								var next = new java.util.ArrayList<DropPath>();
								for (var path : current) {
									var copy = path.copy();
									if (type.equals("minecraft:set_count")) {
										double max = maxOf(function.get("count"));
										if (Double.isNaN(max)) {
											unrecognized.add("function:minecraft:set_count_unreadable_count");
											continue;
										}
										boolean add = function.has("add") && function.get("add").isJsonPrimitive() && function.get("add").getAsJsonPrimitive().isBoolean() && function.get("add").getAsBoolean();
										copy.maxCount = add ? copy.maxCount + max : max;
									} else {
										double limit = limitMax(function.get("limit"));
										if (Double.isNaN(limit)) {
											unrecognized.add("function:minecraft:limit_count_unreadable_limit");
											continue;
										}
										copy.maxCount = Math.min(copy.maxCount, limit);
									}
									if (copy.maxCount < 1.0) {
										unrecognized.add("impossible:" + type + "_result_below_one");
										continue;
									}
									next.add(copy);
								}
								current = next;
								if (current.isEmpty())
									return null;
								continue;
							}
							unrecognized.add("function:" + (type.isEmpty() ? "missing_function_type" : type));
							return null;
						}
						return current;
					}

					// ---------- registry-backed checks for location_check ----------
					java.util.List<String> tokens(com.google.gson.JsonElement spec) {
						if (spec == null)
							return null;
						var found = new java.util.ArrayList<String>();
						if (spec.isJsonPrimitive())
							found.add(spec.getAsString());
						else if (spec.isJsonArray()) {
							for (var element : spec.getAsJsonArray()) {
								if (!element.isJsonPrimitive())
									return null;
								found.add(element.getAsString());
							}
						} else
							return null;
						return found;
					}

					boolean anyBiomeExists(java.util.List<String> tokens) {
						var registry = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME);
						for (String token : tokens) {
							try {
								if (token.startsWith("#")) {
									var tag = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME, net.minecraft.resources.ResourceLocation.parse(token.substring(1)));
									var named = registry.getTag(tag);
									if (named.isPresent() && named.get().size() > 0)
										return true;
								} else if (registry.containsKey(net.minecraft.resources.ResourceLocation.parse(token)))
									return true;
							} catch (RuntimeException invalid) {
								// A malformed identifier can never exist.
							}
						}
						return false;
					}

					boolean anyStructureExists(java.util.List<String> tokens) {
						var registry = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.STRUCTURE);
						for (String token : tokens) {
							try {
								if (token.startsWith("#")) {
									var tag = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.STRUCTURE, net.minecraft.resources.ResourceLocation.parse(token.substring(1)));
									var named = registry.getTag(tag);
									if (named.isPresent() && named.get().size() > 0)
										return true;
								} else if (registry.containsKey(net.minecraft.resources.ResourceLocation.parse(token)))
									return true;
							} catch (RuntimeException invalid) {
								// A malformed identifier can never exist.
							}
						}
						return false;
					}

					boolean dimensionExists(String id) {
						for (var key : level.getServer().levelKeys())
							if (key.location().toString().equals(id))
								return true;
						return false;
					}

					// -1: can never hold or cannot be read. 3: every part was validated against the registries. 2: satisfiable but partly unvalidated.
					int locationVerdict(com.google.gson.JsonObject condition, java.util.Set<String> unrecognized, java.util.List<String> unvalidated) {
						if (!condition.has("predicate"))
							return 3;
						if (!condition.get("predicate").isJsonObject()) {
							unrecognized.add("location_check:predicate_not_object");
							return -1;
						}
						int verdict = 3;
						for (var entry : condition.getAsJsonObject("predicate").entrySet()) {
							String key = entry.getKey();
							var element = entry.getValue();
							if (key.equals("biomes")) {
								var names = tokens(element);
								if (names == null) {
									unrecognized.add("location_check:biomes_unreadable");
									return -1;
								}
								if (!anyBiomeExists(names)) {
									unrecognized.add("impossible:location_check_biome_missing");
									return -1;
								}
							} else if (key.equals("structures")) {
								var names = tokens(element);
								if (names == null) {
									unrecognized.add("location_check:structures_unreadable");
									return -1;
								}
								if (!anyStructureExists(names)) {
									unrecognized.add("impossible:location_check_structure_missing");
									return -1;
								}
							} else if (key.equals("dimension")) {
								if (!element.isJsonPrimitive() || !dimensionExists(element.getAsString())) {
									unrecognized.add("impossible:location_check_dimension_missing");
									return -1;
								}
							} else if (key.equals("can_see_sky") || key.equals("smokey")) {
								if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean()) {
									unrecognized.add("location_check:" + key + "_not_boolean");
									return -1;
								}
							} else {
								unvalidated.add(key);
								verdict = 2;
							}
						}
						return verdict;
					}

					void noteUnvalidated(String key) {
						if (!testMode)
							count("loot_unvalidated:" + key);
					}

					// ---------- entity and mod-specific conditions ----------
					boolean flagsObject(com.google.gson.JsonElement element) {
						if (element == null || !element.isJsonObject())
							return false;
						for (var entry : element.getAsJsonObject().entrySet())
							if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isBoolean())
								return false;
						return true;
					}

					// entity_properties with a flags-only predicate (on fire, sneaking, ...): an entity can be either way, so it never excludes a drop.
					boolean flagsOnlyEntityCondition(com.google.gson.JsonObject condition) {
						if (!"minecraft:entity_properties".equals(string(condition, "condition")))
							return false;
						if (!condition.has("predicate") || !condition.get("predicate").isJsonObject())
							return false;
						var predicate = condition.getAsJsonObject("predicate");
						return predicate.size() == 1 && predicate.has("flags") && flagsObject(predicate.get("flags"));
					}

					java.util.Set<String> entityTypesOf(java.util.List<String> names) {
						var found = new java.util.TreeSet<String>();
						for (String token : names) {
							try {
								if (token.startsWith("#")) {
									var tag = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, net.minecraft.resources.ResourceLocation.parse(token.substring(1)));
									var named = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getTag(tag);
									if (named.isPresent())
										for (var holder : named.get())
											found.add(net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(holder.value()).toString());
								} else if (net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.containsKey(net.minecraft.resources.ResourceLocation.parse(token)))
									found.add(token);
							} catch (RuntimeException invalid) {
								// A malformed identifier can never exist.
							}
						}
						return found;
					}

					// Supported predicate keys: flags, type, equipment.mainhand.items (attacker roles only) and, while analysing fishing, type_specific = fishing hook.
					// Everything else removes the drop and is reported.
					boolean reachableSizePredicate(com.google.gson.JsonElement predicate) {
						if (predicate == null || !predicate.isJsonObject())
							return false;
						var object = predicate.getAsJsonObject();
						if (!"minecraft:slime".equals(string(object, "type")) && !"slime".equals(string(object, "type")))
							return false;
						if (object.size() != 2 || !object.has("size"))
							return false;
						// The built-in size predicate is meaningful for the vanilla splitting
						// entity family only. Unverified custom entity implementations remain unknown.
						var entity = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(net.minecraft.resources.ResourceLocation.parse(activeLootEntity.isEmpty() ? "minecraft:air" : activeLootEntity));
						if (entity != net.minecraft.world.entity.EntityType.SLIME && entity != net.minecraft.world.entity.EntityType.MAGMA_CUBE)
							return false;
						var range = object.get("size");
						double min = Double.NEGATIVE_INFINITY, max = Double.POSITIVE_INFINITY;
						if (range.isJsonPrimitive()) {
							if (!range.getAsJsonPrimitive().isNumber())
								return false;
							min = max = plainNumber(range);
						} else if (range.isJsonObject()) {
							for (String key : range.getAsJsonObject().keySet())
								if (!java.util.Set.of("min", "max").contains(key))
									return false;
							if (range.getAsJsonObject().has("min"))
								min = plainNumber(range.getAsJsonObject().get("min"));
							if (range.getAsJsonObject().has("max"))
								max = plainNumber(range.getAsJsonObject().get("max"));
						} else
							return false;
						// Natural large/medium/small spawns and splitting establish these sizes.
						for (int size : new int[]{1, 2, 4})
							if (size >= min && size <= max)
								return true;
						return false;
					}

					java.util.List<DropPath> applyEntityProperties(java.util.List<DropPath> paths, com.google.gson.JsonObject condition, java.util.Set<String> unrecognized) {
						String role = string(condition, "entity");
						if (role.isEmpty())
							role = "this";
						if (!condition.has("predicate") || !condition.get("predicate").isJsonObject()) {
							unrecognized.add("entity_properties:predicate_not_object");
							return null;
						}
						var current = paths;
						for (var entry : condition.getAsJsonObject("predicate").entrySet()) {
							String key = entry.getKey();
							var part = entry.getValue();
							if (key.equals("flags")) {
								if (!flagsObject(part)) {
									unrecognized.add("entity_properties:flags_not_boolean");
									return null;
								}
								current = withKind(current, "entity_flags");
							} else if (key.equals("type")) {
								var names = tokens(part);
								if (names == null) {
									unrecognized.add("entity_properties:type_unreadable");
									return null;
								}
								if (entityTypesOf(names).isEmpty()) {
									unrecognized.add("impossible:entity_type_missing");
									return null;
								}
								current = withKind(current, "entity_type");
							} else if (key.equals("type_specific") && fishingContext && role.equals("this")) {
								String specific = part.isJsonObject() ? string(part.getAsJsonObject(), "type") : "";
								if (!specific.equals("minecraft:fishing_hook") && !specific.equals("fishing_hook")) {
									unrecognized.add("entity_properties:type_specific_not_fishing_hook");
									return null;
								}
								current = withKind(current, "fishing_open_water");
							} else if (key.equals("type_specific") && role.equals("this") && reachableSizePredicate(part)) {
								current = withKind(current, "reachable_splitting_entity_size");
							} else if (key.equals("equipment") && (role.equals("attacker") || role.equals("direct_attacker") || role.equals("attacking_player"))) {
								if (!part.isJsonObject() || part.getAsJsonObject().size() != 1 || !part.getAsJsonObject().has("mainhand")) {
									unrecognized.add("entity_properties:equipment_not_mainhand_only");
									return null;
								}
								String spec = toolSpec(part.getAsJsonObject().get("mainhand"));
								if (spec == null) {
									unrecognized.add("entity_properties:mainhand_not_items_only");
									return null;
								}
								if (!spec.isEmpty()) {
									String node = "tool|" + spec;
									registerToolNode(node, spec);
									var next = new java.util.ArrayList<DropPath>();
									for (var path : current) {
										var copy = path.copy();
										if (!copy.toolNodes.contains(node))
											copy.toolNodes.add(node);
										copy.kinds.add("tool:" + spec);
										next.add(copy);
									}
									current = next;
								}
							} else {
								unrecognized.add("entity_properties:unknown_key:" + key + ":role=" + role);
								return null;
							}
						}
						return current;
					}

					// Prove the source-entity predicate using the ordinary player attack as a
					// concrete witness. Never assume that an arbitrary mob can kill another mob.
					boolean playerDamageWitness(com.google.gson.JsonObject condition, boolean inverted) {
						if (fishingContext || activeLootEntity.isEmpty() || !"minecraft:damage_source_properties".equals(string(condition, "condition")) || !condition.has("predicate") || !condition.get("predicate").isJsonObject())
							return false;
						var predicate = condition.getAsJsonObject("predicate");
						if (predicate.size() != 1 || !predicate.has("source_entity") || !predicate.get("source_entity").isJsonObject())
							return false;
						var source = predicate.getAsJsonObject("source_entity");
						if (source.size() != 1 || !source.has("type"))
							return false;
						var names = tokens(source.get("type"));
						if (names == null || names.isEmpty())
							return false;
						var types = entityTypesOf(names);
						if (types.isEmpty())
							return false;
						String player = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(net.minecraft.world.entity.EntityType.PLAYER).toString();
						return inverted ? !types.contains(player) : types.contains(player);
					}

					java.util.List<DropPath> applyInverted(java.util.List<DropPath> paths, com.google.gson.JsonObject condition, java.util.Set<String> unrecognized) {
						var term = condition.get("term");
						if (term != null && term.isJsonObject() && flagsOnlyEntityCondition(term.getAsJsonObject()))
							return withKind(paths, "inverted_entity_flags");
						if (term != null && term.isJsonObject() && playerDamageWitness(term.getAsJsonObject(), true))
							return withKind(paths, "damage_source:ordinary_player_attack");
						unrecognized.add("inverted:term_not_entity_flags");
						return null;
					}

					java.util.List<net.minecraft.tags.TagKey<net.minecraft.world.level.biome.Biome>> biomeTags(java.util.List<String> names) {
						var tags = new java.util.ArrayList<net.minecraft.tags.TagKey<net.minecraft.world.level.biome.Biome>>();
						for (String name : names) {
							try {
								tags.add(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME, net.minecraft.resources.ResourceLocation.parse(name.startsWith("#") ? name.substring(1) : name)));
							} catch (RuntimeException invalid) {
								// A malformed identifier can never exist.
							}
						}
						return tags;
					}

					// Mod condition family "<namespace>:biome_tag_check" with predicate.include / predicate.exclude biome tag lists. Whether "include" means any or all is
					// not verified, so a satisfiable result is capped at assumed. Unsatisfiable (no biome fits) removes the drop.
					java.util.List<DropPath> applyBiomeTagCheck(java.util.List<DropPath> paths, com.google.gson.JsonObject condition, java.util.Set<String> unrecognized) {
						if (!condition.has("predicate") || !condition.get("predicate").isJsonObject()) {
							unrecognized.add("biome_tag_check:predicate_not_object");
							return null;
						}
						var predicate = condition.getAsJsonObject("predicate");
						for (var entry : predicate.entrySet())
							if (!entry.getKey().equals("include") && !entry.getKey().equals("exclude")) {
								unrecognized.add("biome_tag_check:unknown_key:" + entry.getKey());
								return null;
							}
						java.util.List<String> include = predicate.has("include") ? tokens(predicate.get("include")) : new java.util.ArrayList<String>();
						java.util.List<String> exclude = predicate.has("exclude") ? tokens(predicate.get("exclude")) : new java.util.ArrayList<String>();
						if (include == null || exclude == null) {
							unrecognized.add("biome_tag_check:lists_unreadable");
							return null;
						}
						var includeTags = biomeTags(include);
						var excludeTags = biomeTags(exclude);
						var registry = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME);
						boolean satisfiable = false;
						for (var holder : registry.holders().toList()) {
							boolean excluded = false;
							for (var tag : excludeTags)
								if (holder.is(tag)) {
									excluded = true;
									break;
								}
							if (excluded)
								continue;
							boolean included = includeTags.isEmpty();
							for (var tag : includeTags)
								if (holder.is(tag)) {
									included = true;
									break;
								}
							if (included) {
								satisfiable = true;
								break;
							}
						}
						if (!satisfiable) {
							unrecognized.add("impossible:biome_tag_check_unsatisfiable");
							return null;
						}
						var next = withKind(paths, "mod_condition:biome_tag_check");
						for (var path : next)
							path.cap = Math.min(path.cap, 2);
						noteUnvalidated("biome_tag_check");
						return next;
					}

					// ---------- loot conditions ----------
					// A condition is evidence only if it can actually hold. Probabilities must be positive without enchantments, ids must exist,
					// weather/time ranges must be reachable. Anything not understood removes the drop and is reported.
					java.util.List<DropPath> applyCondition(java.util.List<DropPath> paths, com.google.gson.JsonElement element, java.util.Set<String> unrecognized) {
						if (!element.isJsonObject()) {
							unrecognized.add("non_object_condition");
							return null;
						}
						var condition = element.getAsJsonObject();
						String type = string(condition, "condition");
						if (type.equals("minecraft:survives_explosion"))
							return paths;
						if (type.equals("minecraft:killed_by_player")) {
							if (fishingContext) {
								unrecognized.add("impossible:killed_by_player_without_killer");
								return null;
							}
							return withKind(paths, "killed_by_player");
						}
						if (type.equals("minecraft:random_chance")) {
							double chance = maxOf(condition.get("chance"));
							if (Double.isNaN(chance)) {
								unrecognized.add("minecraft:random_chance_unreadable_chance");
								return null;
							}
							if (chance <= 0.0) {
								unrecognized.add("impossible:random_chance_zero");
								return null;
							}
							return withChance(withKind(paths, "chance"), chance);
						}
						if (type.equals("minecraft:random_chance_with_enchanted_bonus")) {
							double base = maxOf(condition.get("unenchanted_chance"));
							if (Double.isNaN(base)) {
								unrecognized.add("minecraft:random_chance_with_enchanted_bonus_unreadable_chance");
								return null;
							}
							if (base <= 0.0) {
								unrecognized.add("impossible:chance_needs_enchantment");
								return null;
							}
							return withChance(withKind(paths, "chance"), base);
						}
						if (type.equals("minecraft:table_bonus")) {
							if (!condition.has("chances") || !condition.get("chances").isJsonArray()) {
								unrecognized.add("minecraft:table_bonus_unreadable_chances");
								return null;
							}
							var chances = condition.getAsJsonArray("chances");
							if (chances.size() == 0) {
								unrecognized.add("impossible:table_bonus_empty");
								return null;
							}
							double first = plainNumber(chances.get(0));
							if (Double.isNaN(first)) {
								unrecognized.add("minecraft:table_bonus_unreadable_chances");
								return null;
							}
							if (first <= 0.0) {
								unrecognized.add("impossible:chance_needs_enchantment");
								return null;
							}
							return withChance(withKind(paths, "chance"), first);
						}
						if (type.equals("minecraft:weather_check")) {
							Boolean raining = null, thundering = null;
							for (var entry : condition.entrySet()) {
								String key = entry.getKey();
								if (key.equals("condition"))
									continue;
								if (!key.equals("raining") && !key.equals("thundering")) {
									unrecognized.add("weather_check:unknown_key:" + key);
									return null;
								}
								if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isBoolean()) {
									unrecognized.add("weather_check:" + key + "_not_boolean");
									return null;
								}
								if (key.equals("raining"))
									raining = entry.getValue().getAsBoolean();
								else
									thundering = entry.getValue().getAsBoolean();
							}
							// Thunder needs rain in vanilla, so "not raining but thundering" can never hold.
							if (Boolean.FALSE.equals(raining) && Boolean.TRUE.equals(thundering)) {
								unrecognized.add("impossible:weather_thunder_without_rain");
								return null;
							}
							return withKind(paths, "environment:weather_check");
						}
						if (type.equals("minecraft:time_check")) {
							for (var entry : condition.entrySet()) {
								String key = entry.getKey();
								if (!key.equals("condition") && !key.equals("value") && !key.equals("period")) {
									unrecognized.add("time_check:unknown_key:" + key);
									return null;
								}
							}
							double low = Double.NEGATIVE_INFINITY, high = Double.POSITIVE_INFINITY;
							var range = condition.get("value");
							if (range == null) {
								unrecognized.add("time_check:missing_value");
								return null;
							}
							if (range.isJsonPrimitive()) {
								low = plainNumber(range);
								high = low;
							} else if (range.isJsonObject()) {
								if (range.getAsJsonObject().has("min"))
									low = plainNumber(range.getAsJsonObject().get("min"));
								if (range.getAsJsonObject().has("max"))
									high = plainNumber(range.getAsJsonObject().get("max"));
							} else {
								unrecognized.add("time_check:value_unreadable");
								return null;
							}
							if (Double.isNaN(low) || Double.isNaN(high)) {
								unrecognized.add("time_check:value_unreadable");
								return null;
							}
							double period = Double.NaN;
							if (condition.has("period")) {
								period = plainNumber(condition.get("period"));
								if (Double.isNaN(period) || period <= 0.0) {
									unrecognized.add("time_check:period_unreadable");
									return null;
								}
							}
							// The range is compared with the day time, reduced modulo the period when one is given.
							if (high < 0.0 || low > high || (!Double.isNaN(period) && low > period - 1.0)) {
								unrecognized.add("impossible:time_range_unreachable");
								return null;
							}
							return withKind(paths, "environment:time_check");
						}
						if (type.equals("minecraft:location_check")) {
							var unvalidated = new java.util.ArrayList<String>();
							int verdict = locationVerdict(condition, unrecognized, unvalidated);
							if (verdict < 0)
								return null;
							var next = withKind(paths, "environment:location_check");
							if (verdict == 2) {
								for (var path : next) {
									path.cap = Math.min(path.cap, 2);
									for (String key : unvalidated)
										path.kinds.add("unvalidated:" + key);
								}
								for (String key : unvalidated)
									noteUnvalidated("location_check:" + key);
							}
							return next;
						}
						if (type.equals("minecraft:entity_properties"))
							return applyEntityProperties(paths, condition, unrecognized);
						if (type.equals("minecraft:damage_source_properties")) {
							if (playerDamageWitness(condition, false))
								return withKind(paths, "damage_source:ordinary_player_attack");
							unrecognized.add("minecraft:damage_source_properties");
							return null;
						}
						if (type.equals("minecolonies:entity_in_biome_tag") && !blockLootContext.isEmpty())
							return minecoloniesBlockBiome(paths, condition, unrecognized);
						if (type.equals("minecraft:inverted") && !blockLootContext.isEmpty() && emptyHandCondition(condition))
							return withKind(paths, "block_harvest_without_excluded_tool");
						if (type.equals("minecraft:inverted"))
							return applyInverted(paths, condition, unrecognized);
						if (type.endsWith(":biome_tag_check"))
							return applyBiomeTagCheck(paths, condition, unrecognized);
						if (type.equals("minecraft:match_tool")) {
							String spec = toolSpec(condition.get("predicate"));
							if (spec == null) {
								unrecognized.add("match_tool:predicate_not_items_only");
								return null;
							}
							if (spec.isEmpty())
								return withKind(paths, "any_tool");
							String node = "tool|" + spec;
							registerToolNode(node, spec);
							var next = new java.util.ArrayList<DropPath>();
							for (var path : paths) {
								var copy = path.copy();
								if (!copy.toolNodes.contains(node))
									copy.toolNodes.add(node);
								copy.kinds.add("tool:" + spec);
								next.add(copy);
							}
							return next;
						}
						if (type.equals("minecraft:all_of") && condition.has("terms") && condition.get("terms").isJsonArray())
							return applyConditions(paths, condition.getAsJsonArray("terms"), unrecognized);
						if (type.equals("minecraft:any_of") && condition.has("terms") && condition.get("terms").isJsonArray()) {
							var union = new java.util.ArrayList<DropPath>();
							var failed = new java.util.TreeSet<String>();
							for (var term : condition.getAsJsonArray("terms")) {
								var branch = applyCondition(paths, term, failed);
								if (branch != null)
									union.addAll(branch);
							}
							if (union.isEmpty()) {
								unrecognized.addAll(failed);
								return null;
							}
							return union.size() > 8 ? new java.util.ArrayList<>(union.subList(0, 8)) : union;
						}
						unrecognized.add(type.isEmpty() ? "missing_condition_type" : type);
						return null;
					}

					// Pool rolls, entry weights, conditions and functions are applied at every level (table, pool, entry, nested table, group children).
					void walkLoot(com.google.gson.JsonElement element, java.util.List<DropPath> paths, java.util.Set<String> unrecognized, java.util.Set<String> visiting, int depth, java.util.Map<String, java.util.List<DropPath>> out) {
						if (element == null || paths == null || paths.isEmpty() || depth > 24)
							return;
						if (element.isJsonArray()) {
							for (var child : element.getAsJsonArray())
								walkLoot(child, paths, unrecognized, visiting, depth + 1, out);
							return;
						}
						if (!element.isJsonObject())
							return;
						var object = element.getAsJsonObject();
						if (object.has("rolls")) {
							double rolls = maxOf(object.get("rolls"));
							if (Double.isNaN(rolls)) {
								unrecognized.add("pool_rolls_unreadable");
								return;
							}
							if (rolls < 1.0) {
								unrecognized.add("impossible:pool_rolls_below_one");
								return;
							}
						}
						if (object.has("weight") && object.get("weight").isJsonPrimitive()) {
							double weight = plainNumber(object.get("weight"));
							if (!Double.isNaN(weight) && weight <= 0.0) {
								unrecognized.add("impossible:entry_weight_zero");
								return;
							}
						}
						var current = paths;
						if (object.has("conditions") && object.get("conditions").isJsonArray()) {
							current = applyConditions(current, object.getAsJsonArray("conditions"), unrecognized);
							if (current == null || current.isEmpty())
								return;
						}
						if (object.has("functions") && object.get("functions").isJsonArray()) {
							current = applyFunctions(current, object.getAsJsonArray("functions"), unrecognized);
							if (current == null || current.isEmpty())
								return;
						}
						String type = string(object, "type");
						if (type.equals("minecraft:item")) {
							String name = string(object, "name");
							if (name.isEmpty())
								return;
							var list = out.computeIfAbsent(name, ignored -> new java.util.ArrayList<>());
							for (var path : current)
								if (list.size() < 6)
									list.add(path);
							return;
						}
						if (type.equals("minecraft:loot_table")) {
							String nested = string(object, "value");
							if (nested.isEmpty())
								nested = string(object, "name");
							if (nested.isEmpty() || !visiting.add(nested))
								return;
							try {
								var table = acquisitionSources.lootResource(nested, "loot_table");
								if (table != null)
									walkLoot(table, current, unrecognized, visiting, depth + 1, out);
							} finally {
								visiting.remove(nested);
							}
							return;
						}
						if (type.equals("minecraft:tag")) {
							unrecognized.add("entry:minecraft:tag");
							return;
						}
						if (type.equals("minecraft:dynamic") || type.equals("minecraft:empty"))
							return;
						// A loot pool selects weighted entries; alternatives inside a composite
						// entry are predicates, not independent weighted draws.
						if (object.has("entries") && object.get("entries").isJsonArray()) {
							var entries = object.getAsJsonArray("entries");
							double total = 0;
							boolean known = true;
							for (var row : entries) {
								if (!row.isJsonObject()) {
									known = false;
									break;
								}
								double weight = row.getAsJsonObject().has("weight") ? plainNumber(row.getAsJsonObject().get("weight")) : 1;
								if (!Double.isFinite(weight)) {
									known = false;
									break;
								}
								total += Math.max(0, weight);
							}
							for (var row : entries) {
								double weight = row.isJsonObject() && row.getAsJsonObject().has("weight") ? plainNumber(row.getAsJsonObject().get("weight")) : 1;
								var weighted = known && total > 0 ? withChance(current, Math.max(0, weight) / total) : current;
								walkLoot(row, weighted, unrecognized, visiting, depth + 1, out);
							}
						}
						for (String key : java.util.List.of("pools", "children"))
							if (object.has(key))
								walkLoot(object.get(key), current, unrecognized, visiting, depth + 1, out);
					}

					// ---------- self test: synthetic loot tables evaluated at generation time ----------
					// Executed by the generator itself on every run; the audit reports what actually happened (loot_self_test). Nothing here touches game data.
					void selfTest(String name, String root, String rolls, String pool, String entry, boolean fishing, boolean expectObtainable, String expectDiagnostic, int expectCap, String expectTool) {
						String json = ("{" + root + "'pools':[{'rolls':" + rolls + pool + ",'entries':[{'type':'minecraft:item','name':'test:item'" + entry + "}]}]}").replace('\'', '"');
						var diagnostics = new java.util.TreeSet<String>();
						var drops = new java.util.TreeMap<String, java.util.List<DropPath>>();
						boolean previousFishing = fishingContext;
						boolean previousTest = testMode;
						fishingContext = fishing;
						testMode = true;
						try {
							var start = new java.util.ArrayList<DropPath>();
							start.add(new DropPath());
							walkLoot(com.google.gson.JsonParser.parseString(json), start, diagnostics, new java.util.HashSet<>(), 0, drops);
						} catch (RuntimeException failure) {
							diagnostics.add("self_test_exception:" + failure.getClass().getSimpleName());
						} finally {
							fishingContext = previousFishing;
							testMode = previousTest;
						}
						var paths = drops.get("test:item");
						boolean obtainable = paths != null && !paths.isEmpty();
						int bestCap = 0;
						boolean toolSeen = false;
						if (obtainable)
							for (var path : paths) {
								bestCap = Math.max(bestCap, path.cap);
								if (expectTool != null && path.toolNodes.contains(expectTool))
									toolSeen = true;
							}
						boolean passed = obtainable == expectObtainable;
						if (passed && expectDiagnostic != null)
							passed = diagnostics.stream().anyMatch(d -> d.contains(expectDiagnostic));
						if (passed && expectObtainable && expectCap > 0)
							passed = bestCap == expectCap;
						if (passed && expectTool != null)
							passed = toolSeen;
						if (passed)
							selfTestsPassed++;
						else
							selfTestsFailed++;
						var record = new com.google.gson.JsonObject();
						record.addProperty("case", name);
						record.addProperty("expected_obtainable", expectObtainable);
						record.addProperty("actual_obtainable", obtainable);
						record.addProperty("passed", passed);
						if (!passed)
							record.addProperty("diagnostics", diagnostics.toString());
						selfTests.add(record);
					}

					void runLootSelfTests() {
						String hold = "'minecraft:survives_explosion'";
						selfTest("plain_drop", "", "1", "", "", false, true, null, 3, null);
						selfTest("random_chance_zero", "", "1", ",'conditions':[{'condition':'minecraft:random_chance','chance':0.0}]", "", false, false, "impossible:random_chance_zero", 0, null);
						selfTest("random_chance_positive", "", "1", ",'conditions':[{'condition':'minecraft:random_chance','chance':0.25}]", "", false, true, null, 3, null);
						selfTest("random_chance_uniform_zero", "", "1", ",'conditions':[{'condition':'minecraft:random_chance','chance':{'type':'minecraft:uniform','min':0,'max':0}}]", "", false, false, "impossible:random_chance_zero", 0, null);
						selfTest("table_bonus_needs_enchantment", "", "1", ",'conditions':[{'condition':'minecraft:table_bonus','enchantment':'minecraft:fortune','chances':[0.0,0.1]}]", "", false, false, "impossible:chance_needs_enchantment", 0,
								null);
						selfTest("table_bonus_positive_base", "", "1", ",'conditions':[{'condition':'minecraft:table_bonus','enchantment':'minecraft:fortune','chances':[0.05,0.1]}]", "", false, true, null, 3, null);
						selfTest("enchanted_bonus_zero_base", "", "1",
								",'conditions':[{'condition':'minecraft:random_chance_with_enchanted_bonus','unenchanted_chance':0.0,'enchanted_chance':{'type':'minecraft:linear','base':0.1,'per_level_above_first':0.0},'enchantment':'minecraft:looting'}]",
								"", false, false, "impossible:chance_needs_enchantment", 0, null);
						selfTest("unknown_function_entry", "", "1", "", ",'functions':[{'function':'test:weird'}]", false, false, "function:test:weird", 0, null);
						selfTest("unknown_function_pool", "", "1", ",'functions':[{'function':'test:weird'}]", "", false, false, "function:test:weird", 0, null);
						selfTest("unknown_function_table", "'functions':[{'function':'test:weird'}],", "1", "", "", false, false, "function:test:weird", 0, null);
						selfTest("set_count_zero", "", "1", "", ",'functions':[{'function':'minecraft:set_count','count':0}]", false, false, "impossible:minecraft:set_count_result_below_one", 0, null);
						selfTest("set_count_uniform_zero", "", "1", "", ",'functions':[{'function':'minecraft:set_count','count':{'type':'minecraft:uniform','min':0,'max':0}}]", false, false, "impossible:minecraft:set_count_result_below_one", 0,
								null);
						selfTest("set_count_uniform_up_to_two", "", "1", "", ",'functions':[{'function':'minecraft:set_count','count':{'type':'minecraft:uniform','min':0,'max':2}}]", false, true, null, 3, null);
						selfTest("limit_count_zero_max", "", "1", "", ",'functions':[{'function':'minecraft:limit_count','limit':{'min':0,'max':0}}]", false, false, "impossible:minecraft:limit_count_result_below_one", 0, null);
						selfTest("set_count_zero_is_optional_when_conditional", "", "1", "", ",'functions':[{'function':'minecraft:set_count','count':0,'conditions':[{'condition':'minecraft:random_chance','chance':0.5}]}]", false, true, null, 3,
								null);
						selfTest("furnace_smelt_conditional", "", "1", "", ",'functions':[{'function':'minecraft:furnace_smelt','conditions':[{'condition':'minecraft:entity_properties','entity':'this','predicate':{'flags':{'is_on_fire':true}}}]}]",
								false, true, null, 3, null);
						selfTest("furnace_smelt_unconditional", "", "1", "", ",'functions':[{'function':'minecraft:furnace_smelt'}]", false, false, "function:minecraft:furnace_smelt_unconditional", 0, null);
						selfTest("set_components_rejected", "", "1", "", ",'functions':[{'function':'minecraft:set_components','components':{}}]", false, false, "function:minecraft:set_components", 0, null);
						selfTest("pool_rolls_zero", "", "0", "", "", false, false, "impossible:pool_rolls_below_one", 0, null);
						selfTest("entry_weight_zero", "", "1", "", ",'weight':0", false, false, "impossible:entry_weight_zero", 0, null);
						selfTest("weather_thunder_without_rain", "", "1", ",'conditions':[{'condition':'minecraft:weather_check','raining':false,'thundering':true}]", "", false, false, "impossible:weather_thunder_without_rain", 0, null);
						selfTest("weather_raining", "", "1", ",'conditions':[{'condition':'minecraft:weather_check','raining':true}]", "", false, true, null, 3, null);
						selfTest("time_range_unreachable", "", "1", ",'conditions':[{'condition':'minecraft:time_check','value':{'min':30000,'max':40000},'period':24000}]", "", false, false, "impossible:time_range_unreachable", 0, null);
						selfTest("time_range_reachable", "", "1", ",'conditions':[{'condition':'minecraft:time_check','value':{'min':0,'max':6000},'period':24000}]", "", false, true, null, 3, null);
						selfTest("location_missing_biome", "", "1", ",'conditions':[{'condition':'minecraft:location_check','predicate':{'biomes':'omnichef:does_not_exist'}}]", "", false, false, "impossible:location_check_biome_missing", 0, null);
						selfTest("location_existing_biome", "", "1", ",'conditions':[{'condition':'minecraft:location_check','predicate':{'biomes':'minecraft:plains'}}]", "", false, true, null, 3, null);
						selfTest("location_missing_dimension", "", "1", ",'conditions':[{'condition':'minecraft:location_check','predicate':{'dimension':'omnichef:does_not_exist'}}]", "", false, false, "impossible:location_check_dimension_missing",
								0, null);
						selfTest("location_unvalidated_block_predicate_caps_confidence", "", "1", ",'conditions':[{'condition':'minecraft:location_check','predicate':{'block':{'blocks':'minecraft:stone'}}}]", "", false, true, null, 2, null);
						selfTest("killed_by_player_impossible_in_fishing", "", "1", ",'conditions':[{'condition':'minecraft:killed_by_player'}]", "", true, false, "impossible:killed_by_player_without_killer", 0, null);
						selfTest("killed_by_player_fine_for_mobs", "", "1", ",'conditions':[{'condition':'minecraft:killed_by_player'}]", "", false, true, null, 3, null);
						selfTest("unknown_condition_type", "", "1", ",'conditions':[{'condition':'test:weird_condition'}]", "", false, false, "test:weird_condition", 0, null);
						selfTest("any_of_one_impossible_branch", "", "1", ",'conditions':[{'condition':'minecraft:any_of','terms':[{'condition':'minecraft:random_chance','chance':0.0},{'condition':" + hold + "}]}]", "", false, true, null, 3, null);
						selfTest("any_of_all_branches_impossible", "", "1",
								",'conditions':[{'condition':'minecraft:any_of','terms':[{'condition':'minecraft:random_chance','chance':0.0},{'condition':'minecraft:weather_check','raining':false,'thundering':true}]}]", "", false, false,
								"impossible:", 0, null);
						selfTest("match_tool_non_items_predicate", "", "1", ",'conditions':[{'condition':'minecraft:match_tool','predicate':{'enchantments':[{'enchantments':'minecraft:silk_touch'}]}}]", "", false, false,
								"match_tool:predicate_not_items_only", 0, null);
						selfTest("match_tool_items_adds_tool_node", "", "1", ",'conditions':[{'condition':'minecraft:match_tool','predicate':{'items':'minecraft:shears'}}]", "", false, true, null, 3, "tool|minecraft:shears");
						selfTest("entity_flags_never_exclude", "", "1", ",'conditions':[{'condition':'minecraft:entity_properties','entity':'this','predicate':{'flags':{'is_on_fire':false}}}]", "", false, true, null, 3, null);
						selfTest("inverted_entity_flags", "", "1", ",'conditions':[{'condition':'minecraft:inverted','term':{'condition':'minecraft:entity_properties','entity':'this','predicate':{'flags':{'is_on_fire':true}}}}]", "", false, true,
								null, 3, null);
						selfTest("inverted_other_term_rejected", "", "1", ",'conditions':[{'condition':'minecraft:inverted','term':{'condition':'minecraft:match_tool','predicate':{'items':'minecraft:shears'}}}]", "", false, false,
								"inverted:term_not_entity_flags", 0, null);
						selfTest("attacker_mainhand_adds_tool_node", "", "1", ",'conditions':[{'condition':'minecraft:entity_properties','entity':'attacker','predicate':{'equipment':{'mainhand':{'items':'minecraft:shears'}}}}]", "", false, true,
								null, 3, "tool|minecraft:shears");
						selfTest("equipment_on_victim_rejected", "", "1", ",'conditions':[{'condition':'minecraft:entity_properties','entity':'this','predicate':{'equipment':{'mainhand':{'items':'minecraft:shears'}}}}]", "", false, false,
								"entity_properties:unknown_key:equipment:role=this", 0, null);
						selfTest("entity_unknown_predicate_key_rejected", "", "1", ",'conditions':[{'condition':'minecraft:entity_properties','entity':'this','predicate':{'nbt':'{}'}}]", "", false, false, "entity_properties:unknown_key:nbt", 0,
								null);
						selfTest("entity_type_missing", "", "1", ",'conditions':[{'condition':'minecraft:entity_properties','entity':'this','predicate':{'type':'omnichef:does_not_exist'}}]", "", false, false, "impossible:entity_type_missing", 0,
								null);
						selfTest("entity_type_existing", "", "1", ",'conditions':[{'condition':'minecraft:entity_properties','entity':'this','predicate':{'type':'minecraft:pig'}}]", "", false, true, null, 3, null);
						try {
							activeLootEntity = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(net.minecraft.world.entity.EntityType.SLIME).toString();
							selfTest("splitting_entity_small_size", "", "1", ",'conditions':[{'condition':'minecraft:entity_properties','entity':'this','predicate':{'type_specific':{'type':'minecraft:slime','size':1}}}]", "", false, true, null, 3,
									null);
							selfTest("splitting_entity_impossible_size", "", "1", ",'conditions':[{'condition':'minecraft:entity_properties','entity':'this','predicate':{'type_specific':{'type':'minecraft:slime','size':99}}}]", "", false, false,
									"entity_properties:unknown_key:type_specific", 0, null);
							selfTest("damage_source_other_entity_negation", "", "1",
									",'conditions':[{'condition':'minecraft:inverted','term':{'condition':'minecraft:damage_source_properties','predicate':{'source_entity':{'type':'minecraft:frog'}}}}]", "", false, true, null, 3, null);
							selfTest("damage_source_player_positive", "", "1", ",'conditions':[{'condition':'minecraft:damage_source_properties','predicate':{'source_entity':{'type':'minecraft:player'}}}]", "", false, true, null, 3, null);
							selfTest("damage_source_non_player_positive_unproven", "", "1", ",'conditions':[{'condition':'minecraft:damage_source_properties','predicate':{'source_entity':{'type':'minecraft:frog'}}}]", "", false, false,
									"damage_source_properties", 0, null);
							selfTest("damage_source_excludes_player_unproven", "", "1",
									",'conditions':[{'condition':'minecraft:inverted','term':{'condition':'minecraft:damage_source_properties','predicate':{'source_entity':{'type':'minecraft:player'}}}}]", "", false, false,
									"inverted:term_not_entity_flags", 0, null);
							selfTest("damage_source_unknown_subpredicate_unproven", "", "1", ",'conditions':[{'condition':'minecraft:damage_source_properties','predicate':{'source_entity':{'type':'minecraft:player','nbt':'{}'}}}]", "", false, false,
									"damage_source_properties", 0, null);
							activeLootEntity = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(net.minecraft.world.entity.EntityType.PIG).toString();
							selfTest("splitting_predicate_wrong_entity", "", "1", ",'conditions':[{'condition':'minecraft:entity_properties','entity':'this','predicate':{'type_specific':{'type':'minecraft:slime','size':1}}}]", "", false, false,
									"entity_properties:unknown_key:type_specific", 0, null);
						} finally {
							activeLootEntity = "";
						}
						selfTest("biome_tag_check_satisfiable_is_capped", "", "1", ",'conditions':[{'condition':'test:biome_tag_check','predicate':{'exclude':['c:is_ocean']}}]", "", false, true, null, 2, null);
						selfTest("biome_tag_check_unsatisfiable", "", "1", ",'conditions':[{'condition':'test:biome_tag_check','predicate':{'include':['omnichef:no_such_tag']}}]", "", false, false, "impossible:biome_tag_check_unsatisfiable", 0,
								null);
					}

					String describePath(DropPath path) {
						return path.kinds.isEmpty() ? "unconditional" : String.join(",", path.kinds);
					}

					// Adds routes "item <- [source marker] + [tool nodes]" for every drop whose conditions are all understood.
					void addDropRoutes(java.util.Map<String, java.util.List<DropPath>> drops, String marker, String system, String table, String source, int cap) {
						for (var drop : drops.entrySet()) {
							String node = "item|" + drop.getKey();
							if (!allNodeSet.contains(node))
								continue;
							for (var path : drop.getValue()) {
								var slots = new java.util.ArrayList<java.util.List<String>>();
								slots.add(java.util.List.of(marker));
								for (String tool : path.toolNodes)
									slots.add(java.util.List.of(tool));
								var route = new RenewRoute(system, table, slots, Math.min(cap, path.cap), source + " table=" + table + " conditions=" + describePath(path));
								addRoute(node, route);
								routeRelativeChance.put(route, path.relativeChance);
								count(system.substring("omnichef:".length()) + "_routes");
							}
						}
					}

					void buildMobDropRoutes() {
						for (var entity : net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE) {
							String entityId = "unknown";
							try {
								entityId = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity).toString();
								activeLootEntity = entityId;
								String table = entity.getDefaultLootTable().location().toString();
								var root = acquisitionSources.lootResource(table, "loot_table");
								if (root == null) {
									note("entity_loot_table_not_found");
									continue;
								}
								entityByLootTable.putIfAbsent(table, entityId);
								markEntity(entityId);
								mobEntitiesScanned++;
								var unrecognized = new java.util.TreeSet<String>();
								var drops = new java.util.TreeMap<String, java.util.List<DropPath>>();
								var start = new java.util.ArrayList<DropPath>();
								start.add(new DropPath());
								walkLoot(root, start, unrecognized, new java.util.HashSet<>(java.util.List.of(table)), 0, drops);
								addDropRoutes(drops, "entity|" + entityId, "omnichef:mob_drop", table, "entity=" + entityId, 3);
								for (String kind : unrecognized)
									countUnrecognized("mob_loot:" + kind, entityId + " @ " + table);
							} catch (RuntimeException | LinkageError failure) {
								skip("entity_loot", entityId, failure);
							} finally {
								activeLootEntity = "";
							}
						}
					}

					// ---------- fishing ----------
					void collectNestedTables(com.google.gson.JsonElement element, java.util.Set<String> out, int depth) {
						if (element == null || depth > 24)
							return;
						if (element.isJsonArray()) {
							for (var child : element.getAsJsonArray())
								collectNestedTables(child, out, depth + 1);
							return;
						}
						if (!element.isJsonObject())
							return;
						var object = element.getAsJsonObject();
						if (string(object, "type").equals("minecraft:loot_table")) {
							String nested = string(object, "value");
							if (nested.isEmpty())
								nested = string(object, "name");
							if (!nested.isEmpty())
								out.add(nested);
						}
						for (var entry : object.entrySet())
							if (entry.getValue().isJsonArray() || entry.getValue().isJsonObject())
								collectNestedTables(entry.getValue(), out, depth + 1);
					}

					void addFishingRoot(String table, int cap, String origin) {
						var root = acquisitionSources.lootResource(table, "loot_table");
						if (root == null) {
							note("fishing_table_not_found");
							return;
						}
						var unrecognized = new java.util.TreeSet<String>();
						var drops = new java.util.TreeMap<String, java.util.List<DropPath>>();
						var start = new java.util.ArrayList<DropPath>();
						var rod = new DropPath();
						rod.toolNodes.add("tool|minecraft:fishing_rod");
						rod.kinds.add("tool:minecraft:fishing_rod");
						start.add(rod);
						walkLoot(root, start, unrecognized, new java.util.HashSet<>(java.util.List.of(table)), 0, drops);
						addDropRoutes(drops, "mechanic|fishing", "omnichef:fishing", table, "fishing origin=" + origin, cap);
						for (String kind : unrecognized)
							countUnrecognized("fishing_loot:" + kind, table);
						fishingRoots.add(table + " [" + origin + ", cap=" + cap + "]");
					}

					// The vanilla fishing table is trusted. Other mods: tables of type minecraft:fishing whose path mentions "fishing" and that no other
					// candidate nests. Whether the game's fishing logic (or a mod's own hook) actually uses them is not verified, so they are capped at assumed.
					void buildFishingRoutes() {
						fishingContext = true;
						try {
							raise("mechanic|fishing", 3, "base:fishing_open_water_repeatable");
							repeatRoot("mechanic|fishing", 0);
							registerToolNode("tool|minecraft:fishing_rod", "minecraft:fishing_rod");
							addFishingRoot("minecraft:gameplay/fishing", 3, "vanilla_root");
							var nested = new java.util.TreeSet<String>();
							var vanilla = acquisitionSources.lootResource("minecraft:gameplay/fishing", "loot_table");
							if (vanilla != null)
								collectNestedTables(vanilla, nested, 0);
							var candidates = new java.util.TreeMap<String, com.google.gson.JsonObject>();
							try {
								var manager = level.getServer().getResourceManager();
								for (var found : manager.listResources("loot_table", location -> location.getPath().contains("fishing") && location.getPath().endsWith(".json")).keySet()) {
									String path = found.getPath();
									String tableId = found.getNamespace() + ":" + path.substring("loot_table/".length(), path.length() - ".json".length());
									if (tableId.equals("minecraft:gameplay/fishing"))
										continue;
									var table = acquisitionSources.lootResource(tableId, "loot_table");
									if (table != null && "minecraft:fishing".equals(string(table, "type")))
										candidates.put(tableId, table);
								}
							} catch (RuntimeException | LinkageError failure) {
								skip("fishing_table_listing", "loot_table/*fishing*", failure);
							}
							for (var candidate : candidates.values())
								collectNestedTables(candidate, nested, 0);
							for (String tableId : candidates.keySet())
								if (!nested.contains(tableId))
									addFishingRoot(tableId, 2, "mod_fishing_table_reachability_unverified");
						} catch (RuntimeException | LinkageError failure) {
							skip("fishing_loot", "minecraft:gameplay/fishing", failure);
						} finally {
							fishingContext = false;
						}
					}

					// ---------- global loot modifiers ----------
					java.util.List<String> itemIdsOf(com.google.gson.JsonElement element) {
						var found = new java.util.ArrayList<String>();
						if (element == null)
							return found;
						if (element.isJsonPrimitive()) {
							if (itemsById.containsKey(element.getAsString()))
								found.add(element.getAsString());
						} else if (element.isJsonObject()) {
							for (String key : java.util.List.of("id", "item")) {
								String name = string(element.getAsJsonObject(), key);
								if (!name.isEmpty() && itemsById.containsKey(name)) {
									found.add(name);
									break;
								}
							}
						} else if (element.isJsonArray()) {
							for (var child : element.getAsJsonArray())
								found.addAll(itemIdsOf(child));
						}
						return found;
					}

					// Truncated raw JSON, so a rejected modifier shows its real shape in the next run instead of needing a guess.
					String shape(com.google.gson.JsonObject modifier) {
						String text = modifier.toString();
						return text.length() > 320 ? text.substring(0, 320) + "..." : text;
					}

					// Only modifiers listed in neoforge:loot_modifiers/global_loot_modifiers.json are active.
					// A modifier is understood when it names one target (a loot table id, or one entity_properties type for "this") and adds either a loot table or items.
					// Codecs differ per mod, so results are capped at assumed_renewable and every rejected shape is counted with a raw sample.
					void buildLootModifierRoutes() {
						var entries = new java.util.TreeSet<String>();
						try {
							var manager = level.getServer().getResourceManager();
							for (var resource : manager.getResourceStack(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "loot_modifiers/global_loot_modifiers.json"))) {
								try (var reader = resource.openAsReader()) {
									var json = com.google.gson.JsonParser.parseReader(reader);
									if (json.isJsonObject() && json.getAsJsonObject().has("entries") && json.getAsJsonObject().get("entries").isJsonArray()) {
										if (json.getAsJsonObject().has("replace") && json.getAsJsonObject().get("replace").getAsBoolean())
											entries.clear();
										for (var element : json.getAsJsonObject().getAsJsonArray("entries"))
											if (element.isJsonPrimitive())
												entries.add(element.getAsString());
									}
								}
							}
						} catch (Exception failure) {
							note("glm_list_unavailable:" + failure.getClass().getSimpleName());
							return;
						}
						for (String id : entries) {
							try {
								var modifier = acquisitionSources.lootResource(id, "loot_modifiers");
								if (modifier == null) {
									count("glm_entry_missing");
									continue;
								}
								count("glm_entries_read");
								applyLootModifier(id, modifier);
							} catch (RuntimeException | LinkageError failure) {
								skip("loot_modifier", id, failure);
							}
						}
					}

					void applyLootModifier(String id, com.google.gson.JsonObject modifier) {
						String modifierType = string(modifier, "type");
						if (id.startsWith("productivetrees:") && "productivelib:item_modifier".equals(modifierType)) {
							// Live Productive Trees chest injections use a positive stack addition, sometimes with an OR of table ids.
							if (!modifier.has("addition") || !modifier.get("addition").isJsonObject() || !modifier.has("chance") || modifier.get("chance").getAsDouble() <= 0 || !modifier.has("conditions") || !modifier.get("conditions").isJsonArray()
									|| modifier.getAsJsonArray("conditions").size() != 1)
								return;
							var addition = modifier.getAsJsonObject("addition");
							String item = string(addition, "id");
							if (!itemsById.containsKey(item) || !addition.has("count") || addition.get("count").getAsInt() <= 0)
								return;
							var condition = modifier.getAsJsonArray("conditions").get(0).getAsJsonObject();
							var alternatives = new com.google.gson.JsonArray();
							if ("minecraft:any_of".equals(string(condition, "condition")) && condition.has("terms") && condition.get("terms").isJsonArray())
								alternatives = condition.getAsJsonArray("terms");
							else
								alternatives.add(condition);
							for (var element : alternatives) {
								if (!element.isJsonObject())
									continue;
								var term = element.getAsJsonObject();
								if (!"neoforge:loot_table_id".equals(string(term, "condition")))
									continue;
								String target = string(term, "loot_table_id");
								if (!target.startsWith("minecraft:chests/") || acquisitionSources.lootResource(target, "loot_table") == null)
									continue;
								String marker = "chest|" + target;
								firstRoot(marker, chestFloor(target));
								chestModifierItems.add("item|" + item);
								addRoute("item|" + item, new RenewRoute("omnichef:loot_modifier", id, java.util.List.of(java.util.List.of(marker)), 2, "productive chest injection: " + target));
								count("glm_chest_routes");
							}
							return;
						}
						String targetTable = null;
						java.util.Set<String> targetEntities = null;
						boolean ambiguous = false;
						var rest = new com.google.gson.JsonArray();
						if (modifier.has("conditions") && modifier.get("conditions").isJsonArray()) {
							for (var element : modifier.getAsJsonArray("conditions")) {
								if (!element.isJsonObject()) {
									rest.add(element);
									continue;
								}
								var condition = element.getAsJsonObject();
								String conditionType = string(condition, "condition");
								String role = string(condition, "entity");
								if (conditionType.endsWith("loot_table_id")) {
									String named = string(condition, "loot_table_id");
									if (named.isEmpty())
										named = string(condition, "id");
									if (targetTable != null || targetEntities != null)
										ambiguous = true;
									targetTable = named;
								} else if (conditionType.equals("minecraft:entity_properties") && (role.isEmpty() || role.equals("this")) && condition.has("predicate") && condition.get("predicate").isJsonObject()
										&& condition.getAsJsonObject("predicate").has("type")) {
									var predicate = condition.getAsJsonObject("predicate");
									var names = tokens(predicate.get("type"));
									if (names == null) {
										ambiguous = true;
										continue;
									}
									if (targetTable != null || targetEntities != null)
										ambiguous = true;
									targetEntities = entityTypesOf(names);
									if (predicate.size() > 1) {
										var remaining = condition.deepCopy();
										remaining.getAsJsonObject("predicate").remove("type");
										rest.add(remaining);
									}
								} else
									rest.add(element);
							}
						}
						if (ambiguous || (targetTable == null && targetEntities == null)) {
							countUnrecognized("glm_shape:no_single_target", id + " type=" + modifierType + " " + shape(modifier));
							return;
						}
						if (targetTable != null && (targetTable.contains(":chests/") || targetTable.contains(":blocks/"))) {
							applyAcquisitionLootModifier(id, modifier, targetTable, rest);
							return;
						}
						boolean fishing = "minecraft:gameplay/fishing".equals(targetTable);
						var markers = new java.util.ArrayList<String>();
						if (fishing)
							markers.add("mechanic|fishing");
						else if (targetTable != null) {
							String entityId = entityByLootTable.get(targetTable);
							if (entityId == null) {
								countUnrecognized("glm_shape:target_not_mob_or_fishing", id + " target=" + targetTable);
								return;
							}
							markers.add("entity|" + entityId);
						} else {
							if (targetEntities.isEmpty()) {
								countUnrecognized("glm_shape:target_entity_type_missing", id + " " + shape(modifier));
								return;
							}
							for (String entityId : targetEntities)
								markers.add("entity|" + entityId);
						}
						String added = "";
						for (String key : java.util.List.of("loot_table", "lootTable", "table"))
							if (added.isEmpty())
								added = string(modifier, key);
						var addedTable = added.isEmpty() ? null : acquisitionSources.lootResource(added, "loot_table");
						var addedItems = new java.util.ArrayList<String>();
						if (addedTable == null)
							for (String key : java.util.List.of("item", "stack", "result", "drop", "items"))
								if (addedItems.isEmpty())
									addedItems.addAll(itemIdsOf(modifier.get(key)));
						if (addedTable == null && addedItems.isEmpty()) {
							countUnrecognized("glm_shape:no_resolvable_added_drops", id + " type=" + modifierType + " " + shape(modifier));
							return;
						}
						fishingContext = fishing;
						try {
							if (fishing)
								registerToolNode("tool|minecraft:fishing_rod", "minecraft:fishing_rod");
							var unrecognized = new java.util.TreeSet<String>();
							var start = new java.util.ArrayList<DropPath>();
							var first = new DropPath();
							if (fishing) {
								first.toolNodes.add("tool|minecraft:fishing_rod");
								first.kinds.add("tool:minecraft:fishing_rod");
							}
							start.add(first);
							var paths = applyConditions(start, rest, unrecognized);
							if (paths == null) {
								for (String kind : unrecognized)
									countUnrecognized("glm_conditions:" + kind, id + " " + shape(modifier));
								return;
							}
							var drops = new java.util.TreeMap<String, java.util.List<DropPath>>();
							if (addedTable != null)
								walkLoot(addedTable, paths, unrecognized, new java.util.HashSet<>(java.util.List.of(added)), 0, drops);
							else
								for (String item : addedItems)
									drops.computeIfAbsent(item, ignored -> new java.util.ArrayList<>()).addAll(paths);
							for (String marker : markers) {
								if (marker.startsWith("entity|"))
									markEntity(marker.substring("entity|".length()));
								else {
									raise(marker, 3, "base:fishing_open_water_repeatable");
									repeatRoot(marker, 0);
								}
								addDropRoutes(drops, marker, "omnichef:loot_modifier", addedTable != null ? added : "inline_item", "modifier=" + id + " type=" + modifierType, 2);
							}
							for (String kind : unrecognized)
								countUnrecognized("glm_loot:" + kind, id + " added=" + (addedTable != null ? added : "inline"));
							count("glm_entries_applied");
						} finally {
							fishingContext = false;
						}
					}

					// A loot injection is an acquisition route, not proof that the resulting item reproduces.
					// The recognised codecs either add a specified stack or delegate to another loot table.
					String blockLootContext = "";

					void applyAcquisitionLootModifier(String id, com.google.gson.JsonObject modifier, String target, com.google.gson.JsonArray conditions) {
						String type = string(modifier, "type");
						boolean chest = target.contains(":chests/");
						if (!java.util.Set.of("neoforge:add_table", "herbsandharvest:add_item").contains(type)) {
							countUnrecognized("glm_acquisition:unsupported_codec", id + " type=" + type);
							return;
						}
						if (acquisitionSources.lootResource(target, "loot_table") == null) {
							note("glm_acquisition_target_missing");
							return;
						}
						String block = "";
						int floor = 99;
						if (chest)
							floor = chestFloor(target);
						else {
							for (var entry : plants.blockTables.entrySet())
								if (target.equals(entry.getValue())) {
									int candidate = locationFloor(entry.getKey());
									if (candidate < floor) {
										floor = candidate;
										block = entry.getKey();
									}
								}
							// No located source means the block injection is not yet evidence.
							if (floor == 99) {
								note("glm_block_target_not_located");
								return;
							}
						}
						var rejected = new java.util.TreeSet<String>();
						try {
							blockLootContext = block;
							var start = new java.util.ArrayList<DropPath>();
							start.add(new DropPath());
							var paths = applyConditions(start, conditions, rejected);
							if (paths == null) {
								for (String why : rejected)
									countUnrecognized("glm_acquisition_condition:" + why, id);
								return;
							}
							var drops = new java.util.TreeMap<String, java.util.List<DropPath>>();
							if ("neoforge:add_table".equals(type)) {
								String added = string(modifier, "table");
								var table = acquisitionSources.lootResource(added, "loot_table");
								if (table == null) {
									note("glm_added_table_missing");
									return;
								}
								walkLoot(table, paths, rejected, new java.util.HashSet<>(java.util.List.of(added)), 0, drops);
							} else
								for (String item : itemIdsOf(modifier.get("item")))
									drops.put(item, paths);
							String marker = (chest ? "chest|" : "block_source|") + target;
							if (chest) {
								firstRoot(marker, floor);
								for (String item : drops.keySet())
									chestModifierItems.add("item|" + item);
							} else {
								raise(marker, 2, "base:located_block_loot:" + block);
								repeatRoot(marker, floor);
							}
							addDropRoutes(drops, marker, "omnichef:loot_modifier", id, "target=" + target, 2);
							count(chest ? "glm_chest_routes" : "glm_block_routes");
							for (String why : rejected)
								countUnrecognized("glm_acquisition_condition:" + why, id);
						} finally {
							blockLootContext = "";
						}
					}

					int chestFloor(String target) {
						if (target.startsWith("minecraft:chests/village/"))
							return 0;
						if (target.startsWith("minecraft:chests/nether") || target.startsWith("minecraft:chests/bastion"))
							return 1;
						if (target.startsWith("minecraft:chests/end_city") || !target.startsWith("minecraft:"))
							return 3;
						return 2;
					}

					java.util.List<DropPath> minecoloniesBlockBiome(java.util.List<DropPath> paths, com.google.gson.JsonObject condition, java.util.Set<String> rejected) {
						if (blockLootContext.isEmpty())
							return null;
						var tokens = tokens(condition.get("tag"));
						if (tokens == null || tokens.isEmpty())
							return null;
						var tags = biomeTags(tokens);
						int floor = 99;
						var registry = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME);
						var allowed = new java.util.HashSet<String>();
						for (var holder : registry.holders().toList())
							if (tags.stream().anyMatch(holder::is))
								holder.unwrapKey().ifPresent(key -> allowed.add(key.location().toString()));
						for (var row : plants.locations.getOrDefault(blockLootContext, new com.google.gson.JsonArray())) {
							var data = row.getAsJsonObject();
							var dimensions = data.getAsJsonObject("biome_dimensions");
							for (var biome : data.getAsJsonArray("biomes"))
								if (allowed.contains(biome.getAsString()) && dimensions.has(biome.getAsString()))
									for (var dim : dimensions.getAsJsonArray(biome.getAsString()))
										floor = Math.min(floor, dimensionFloor(dim.getAsString()));
						}
						if (floor == 99) {
							rejected.add("minecolonies:biome_no_located_intersection");
							return null;
						}
						String marker = "biome_gate|" + blockLootContext + "|" + tokens;
						raise(marker, 2, "base:located_block_biome_tag");
						repeatRoot(marker, floor);
						var out = withKind(paths, "minecolonies:entity_in_biome_tag");
						for (var path : out) {
							path.cap = Math.min(path.cap, 2);
							path.toolNodes.add(marker);
						}
						return out;
					}

					// ---------- declared mechanisms: block interactions and block-entity logic that no recipe or loot table describes ----------
					// Such mechanics live in Java and cannot be discovered from data. They can be DECLARED in config/omnichef/RenewabilityMechanisms.json:
					// {"mechanisms":[{"id":"...","kind":"block_entity_processing","outputs":["ns:item"],"inputs":[["ns:item","#ns:tag"]],
					//   "prerequisites":[["ns:item"]],"tools":["#ns:tag"],"details":{...},"source":"where this was verified"}]}
					//  * inputs        consumable materials: each slot must resolve AND be renewable (the chain is still checked);
					//  * prerequisites reusable resources needed once (a sapling for a tree that keeps producing): first access is not verified, so they count as an
					//                  assumption and never raise the result above assumed_renewable;
					//  * tools         must be an obtainable damageable item (assumption), as for loot conditions;
					//  * kind, details optional and descriptive; a row with no inputs and no prerequisites proves nothing and is rejected.
					// The generator never runs the mod's code. Results are capped at assumed_renewable. Rows for mods that are not installed are not_applicable_missing_mod.
					final class MechanismRecord {
						final String id;
						final String kind;
						final String source;
						final java.util.List<String> outputs;
						final java.util.List<java.util.List<String>> inputSlots;
						final java.util.List<String> prerequisiteSpecs;
						final java.util.List<String> toolNodes;

						MechanismRecord(String id, String kind, String source, java.util.List<String> outputs, java.util.List<java.util.List<String>> inputSlots, java.util.List<String> prerequisiteSpecs, java.util.List<String> toolNodes) {
							this.id = id;
							this.kind = kind;
							this.source = source;
							this.outputs = outputs;
							this.inputSlots = inputSlots;
							this.prerequisiteSpecs = prerequisiteSpecs;
							this.toolNodes = toolNodes;
						}
					}

					void ensureNamespaces() {
						if (!itemNamespaces.isEmpty())
							return;
						for (String id : itemsById.keySet()) {
							int colon = id.indexOf(':');
							if (colon > 0)
								itemNamespaces.add(id.substring(0, colon));
						}
					}

					// minecraft and the common namespace are always present; any other namespace is present when it registers at least one item.
					boolean namespaceLoaded(String token) {
						String bare = token.startsWith("#") ? token.substring(1) : token;
						int colon = bare.indexOf(':');
						String namespace = colon > 0 ? bare.substring(0, colon) : "minecraft";
						return namespace.equals("minecraft") || namespace.equals("c") || itemNamespaces.contains(namespace);
					}

					java.util.List<String> itemNodesFor(String token) {
						var nodes = new java.util.ArrayList<String>();
						try {
							if (token.startsWith("#")) {
								var tag = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse(token.substring(1)));
								var named = net.minecraft.core.registries.BuiltInRegistries.ITEM.getTag(tag);
								if (named.isPresent())
									for (var holder : named.get()) {
										if (nodes.size() >= 300)
											break;
										nodes.add("item|" + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(holder.value()));
									}
							} else if (itemsById.containsKey(token))
								nodes.add("item|" + token);
						} catch (RuntimeException invalid) {
							// A malformed identifier resolves to nothing.
						}
						return nodes;
					}

					// Codes: not_applicable_missing_mod, invalid_configuration, unresolved_required_input, unrecognized_mechanism.
					void rejectMechanism(String id, String code, String detail) {
						mechanismReasons.merge(code, 1, Integer::sum);
						count(code.equals("not_applicable_missing_mod") ? "declared_mechanism_not_applicable" : "declared_mechanism_rejected");
						if (mechanismRejected.size() < 40) {
							var record = new com.google.gson.JsonObject();
							record.addProperty("id", id);
							record.addProperty("reason", code);
							record.addProperty("detail", detail);
							mechanismRejected.add(record);
						}
					}

					// Reads an optional list of slots (each a string or an array of strings). Returns false after rejecting the row.
					boolean readSlots(com.google.gson.JsonObject row, String key, java.util.List<java.util.List<String>> slots, String id) {
						if (!row.has(key))
							return true;
						if (!row.get(key).isJsonArray()) {
							rejectMechanism(id, "invalid_configuration", key + "_not_an_array");
							return false;
						}
						for (var element : row.getAsJsonArray(key)) {
							var names = tokens(element);
							if (names == null || names.isEmpty()) {
								rejectMechanism(id, "invalid_configuration", key + "_slot_unreadable");
								return false;
							}
							slots.add(names);
						}
						return true;
					}

					java.util.List<String> resolveSlot(java.util.List<String> names) {
						var alternatives = new java.util.TreeSet<String>();
						for (String name : names)
							alternatives.addAll(itemNodesFor(name));
						return new java.util.ArrayList<>(alternatives);
					}

					void applyMechanism(com.google.gson.JsonObject row, int index) {
						ensureNamespaces();
						String id = string(row, "id");
						if (id.isEmpty())
							id = "row_" + index;
						String source = string(row, "source");
						String kind = string(row, "kind");
						if (!kind.isEmpty() && !java.util.Set.of("block_interaction", "block_entity_processing", "regrowing_harvest", "entity_summon", "trading").contains(kind)) {
							rejectMechanism(id, "unrecognized_mechanism", "kind=" + kind);
							return;
						}
						var outputs = tokens(row.get("outputs"));
						if (outputs == null || outputs.isEmpty()) {
							rejectMechanism(id, "invalid_configuration", "outputs_missing_or_unreadable");
							return;
						}
						var inputSlots = new java.util.ArrayList<java.util.List<String>>();
						var prerequisiteSlots = new java.util.ArrayList<java.util.List<String>>();
						if (!readSlots(row, "inputs", inputSlots, id) || !readSlots(row, "prerequisites", prerequisiteSlots, id))
							return;
						java.util.List<String> tools = row.has("tools") ? tokens(row.get("tools")) : new java.util.ArrayList<String>();
						if (tools == null) {
							rejectMechanism(id, "invalid_configuration", "tools_unreadable");
							return;
						}
						if (inputSlots.isEmpty() && prerequisiteSlots.isEmpty()) {
							rejectMechanism(id, "invalid_configuration", "no_inputs_or_prerequisites_prove_nothing");
							return;
						}
						// An optional integration for a mod that is not installed is not an error.
						// Output namespaces are mandatory. An absent optional alternative must not reject its whole slot.
						for (String token : outputs)
							if (!namespaceLoaded(token)) {
								rejectMechanism(id, "not_applicable_missing_mod", token);
								return;
							}
						for (String output : outputs)
							if (!itemsById.containsKey(output)) {
								rejectMechanism(id, "invalid_configuration", "unknown_output:" + output);
								return;
							}
						var routeSlots = new java.util.ArrayList<java.util.List<String>>();
						var resolvedInputs = new java.util.ArrayList<java.util.List<String>>();
						for (var slot : inputSlots) {
							var alternatives = resolveSlot(slot);
							if (alternatives.isEmpty()) {
								rejectMechanism(id, slot.stream().noneMatch(this::namespaceLoaded) ? "not_applicable_missing_mod" : "unresolved_required_input", "input:" + slot);
								return;
							}
							routeSlots.add(alternatives);
							resolvedInputs.add(alternatives);
						}
						var prerequisiteSpecs = new java.util.ArrayList<String>();
						for (var slot : prerequisiteSlots) {
							if (resolveSlot(slot).isEmpty()) {
								rejectMechanism(id, slot.stream().noneMatch(this::namespaceLoaded) ? "not_applicable_missing_mod" : "unresolved_required_input", "prerequisite:" + slot);
								return;
							}
							String spec = String.join(",", new java.util.TreeSet<>(slot));
							String marker = "tool|" + spec;
							if (testMode)
								raise(marker, 2, "base:declared_test_prerequisite");
							else
								registerToolNode(marker, spec);
							routeSlots.add(java.util.List.of(marker));
							prerequisiteSpecs.add(spec);
						}
						var toolNodes = new java.util.ArrayList<String>();
						for (String tool : tools) {
							if (itemNodesFor(tool).isEmpty()) {
								rejectMechanism(id, namespaceLoaded(tool) ? "unresolved_required_input" : "not_applicable_missing_mod", "tool:" + tool);
								return;
							}
							String node = "tool|" + tool;
							registerToolNode(node, tool);
							routeSlots.add(java.util.List.of(node));
							toolNodes.add(node);
						}
						String details = row.has("details") ? row.get("details").toString() : "";
						String evidence = "declared mechanism " + id + (kind.isEmpty() ? "" : " kind=" + kind) + (source.isEmpty() ? "" : " source=" + source) + (details.isEmpty() ? "" : " details=" + details);
						var outputNodes = new java.util.ArrayList<String>();
						for (String output : outputs) {
							addRoute("item|" + output, new RenewRoute("omnichef:declared_mechanism", id, routeSlots, 2, evidence));
							outputNodes.add("item|" + output);
						}
						mechanismRecords.add(new MechanismRecord(id, kind, source, outputNodes, resolvedInputs, prerequisiteSpecs, toolNodes));
						count("declared_mechanism_routes");
					}

					void loadDeclaredMechanisms() {
						var file = net.neoforged.fml.loading.FMLPaths.GAMEDIR.get().resolve("config/omnichef/RenewabilityMechanisms.json");
						if (!java.nio.file.Files.exists(file)) {
							note("mechanism_file_absent");
							return;
						}
						try (var reader = java.nio.file.Files.newBufferedReader(file)) {
							var json = com.google.gson.JsonParser.parseReader(reader);
							if (!json.isJsonObject() || !json.getAsJsonObject().has("mechanisms") || !json.getAsJsonObject().get("mechanisms").isJsonArray()) {
								note("mechanism_file_shape_invalid");
								return;
							}
							int index = 0;
							for (var element : json.getAsJsonObject().getAsJsonArray("mechanisms")) {
								index++;
								if (!element.isJsonObject()) {
									rejectMechanism("row_" + index, "invalid_configuration", "row_not_object");
									continue;
								}
								try {
									applyMechanism(element.getAsJsonObject(), index);
								} catch (RuntimeException failure) {
									rejectMechanism("row_" + index, "invalid_configuration", "exception:" + failure.getClass().getSimpleName());
								}
							}
						} catch (Exception failure) {
							note("mechanism_file_unreadable:" + failure.getClass().getSimpleName());
						}
					}

					// Status of the best alternative of a slot (renewable > assumed_renewable > unverified_source > unknown).
					String bestStatus(java.util.List<String> nodes) {
						String best = null;
						int bestRank = -1;
						for (String node : nodes) {
							int rank = value.getOrDefault(node, 0);
							if (rank > bestRank) {
								bestRank = rank;
								best = node;
							}
						}
						return best == null ? "unknown" : classify(best);
					}

					com.google.gson.JsonObject mechanismReport(MechanismRecord mechanism) {
						var record = new com.google.gson.JsonObject();
						record.addProperty("id", mechanism.id);
						record.addProperty("kind", mechanism.kind.isEmpty() ? "unspecified" : mechanism.kind);
						record.addProperty("source", mechanism.source);
						var outputs = new com.google.gson.JsonArray();
						for (String node : mechanism.outputs) {
							var output = new com.google.gson.JsonObject();
							output.addProperty("item", node.substring(5));
							output.addProperty("status", classify(node));
							outputs.add(output);
						}
						record.add("outputs", outputs);
						var inputs = new com.google.gson.JsonArray();
						for (var slot : mechanism.inputSlots) {
							var input = new com.google.gson.JsonObject();
							input.addProperty("example", slot.get(0).substring(5));
							input.addProperty("alternatives", slot.size());
							input.addProperty("best_status", bestStatus(slot));
							inputs.add(input);
						}
						record.add("inputs", inputs);
						var prerequisites = new com.google.gson.JsonArray();
						for (String spec : mechanism.prerequisiteSpecs)
							prerequisites.add(spec + " [first access unverified, assumed]");
						record.add("prerequisites", prerequisites);
						var tools = new com.google.gson.JsonArray();
						for (String node : mechanism.toolNodes)
							tools.add(node.substring(5) + " [" + classify(node) + "]");
						record.add("tools", tools);
						return record;
					}

					// ---------- mechanism self tests: the real code path on a scratch instance with stand-in vanilla items ----------
					// Real Herbs & Harvest rows can only be validated against the installed mod; these tests check the rules the rows rely on.
					void mechanismTest(String name, String rowJson, java.util.Map<String, Integer> seeds, String outputNode, int expectedRank, String expectedRejection) {
						String actual;
						boolean passed;
						try {
							var scratch = new RenewabilityAnalysis(plants);
							scratch.testMode = true;
							for (var seed : seeds.entrySet())
								scratch.value.put(seed.getKey(), seed.getValue());
							scratch.applyMechanism(com.google.gson.JsonParser.parseString(rowJson.replace('\'', '"')).getAsJsonObject(), 1);
							scratch.routedNodes.addAll(new java.util.TreeSet<>(scratch.renewRoutes.keySet()));
							scratch.propagate();
							int rank = scratch.value.getOrDefault(outputNode, 0);
							String reason = scratch.mechanismRejected.size() == 0 ? "" : scratch.mechanismRejected.get(0).getAsJsonObject().get("reason").getAsString();
							passed = rank == expectedRank && (expectedRejection == null ? reason.isEmpty() : reason.equals(expectedRejection));
							actual = "rank=" + rank + " rejection=" + (reason.isEmpty() ? "none" : reason);
						} catch (RuntimeException failure) {
							passed = false;
							actual = "exception:" + failure.getClass().getSimpleName();
						}
						if (passed)
							selfTestsPassed++;
						else
							selfTestsFailed++;
						var record = new com.google.gson.JsonObject();
						record.addProperty("case", name);
						record.addProperty("passed", passed);
						if (!passed)
							record.addProperty("actual", actual);
						selfTests.add(record);
					}

					void runMechanismSelfTests() {
						String out = "item|minecraft:bread";
						var none = new java.util.HashMap<String, Integer>();
						mechanismTest("mech_prerequisite_and_tool_give_assumed", "{'id':'t','outputs':['minecraft:bread'],'inputs':[],'prerequisites':[['minecraft:oak_sapling']],'tools':['minecraft:iron_axe']}",
								java.util.Map.of("tool|minecraft:iron_axe", 2), out, 2, null);
						mechanismTest("mech_missing_tool_gives_no_proof", "{'id':'t','outputs':['minecraft:bread'],'inputs':[],'prerequisites':[['minecraft:oak_sapling']],'tools':['minecraft:iron_axe']}", none, out, 0, null);
						mechanismTest("mech_prerequisite_never_exceeds_assumed", "{'id':'t','outputs':['minecraft:bread'],'inputs':[],'prerequisites':[['minecraft:oak_sapling']],'tools':['minecraft:iron_axe']}",
								java.util.Map.of("tool|minecraft:iron_axe", 2, "item|minecraft:oak_sapling", 3), out, 2, null);
						mechanismTest("mech_prerequisite_unresolved_rejected", "{'id':'t','outputs':['minecraft:bread'],'prerequisites':[['minecraft:does_not_exist']]}", none, out, 0, "unresolved_required_input");
						mechanismTest("mech_aging_shape_raw_renewable_gives_at_most_assumed", "{'id':'t','outputs':['minecraft:bread'],'inputs':[['minecraft:wheat']]}", java.util.Map.of("item|minecraft:wheat", 3), out, 2, null);
						mechanismTest("mech_aging_shape_raw_assumed_gives_assumed", "{'id':'t','outputs':['minecraft:bread'],'inputs':[['minecraft:wheat']]}", java.util.Map.of("item|minecraft:wheat", 2), out, 2, null);
						mechanismTest("mech_aging_shape_raw_unknown_gives_nothing", "{'id':'t','outputs':['minecraft:bread'],'inputs':[['minecraft:wheat']]}", none, out, 0, null);
						mechanismTest("mech_missing_mod_is_not_applicable", "{'id':'t','outputs':['omnichef_absent_mod:thing'],'inputs':[['minecraft:wheat']]}", none, "item|omnichef_absent_mod:thing", 0, "not_applicable_missing_mod");
						mechanismTest("mech_old_format_without_prerequisites_works", "{'id':'t','outputs':['minecraft:bread'],'inputs':[['minecraft:wheat']],'source':'old row'}", java.util.Map.of("item|minecraft:wheat", 3), out, 2, null);
						mechanismTest("mech_unknown_input_item_rejected", "{'id':'t','outputs':['minecraft:bread'],'inputs':[['minecraft:does_not_exist']]}", none, out, 0, "unresolved_required_input");
						mechanismTest("mech_unknown_tool_rejected", "{'id':'t','outputs':['minecraft:bread'],'inputs':[['minecraft:wheat']],'tools':['minecraft:does_not_exist']}", java.util.Map.of("item|minecraft:wheat", 3), out, 0,
								"unresolved_required_input");
						mechanismTest("mech_self_dependent_proves_nothing", "{'id':'t','outputs':['minecraft:bread'],'inputs':[['minecraft:bread']]}", none, out, 0, null);
						mechanismTest("mech_no_inputs_and_no_prerequisites_rejected", "{'id':'t','outputs':['minecraft:bread']}", none, out, 0, "invalid_configuration");
						mechanismTest("mech_unrecognized_kind_rejected", "{'id':'t','kind':'teleport','outputs':['minecraft:bread'],'inputs':[['minecraft:wheat']]}", java.util.Map.of("item|minecraft:wheat", 3), out, 0, "unrecognized_mechanism");
						mechanismTest("mech_missing_optional_alternative_keeps_route", "{'id':'t','outputs':['minecraft:bread'],'inputs':[['omnichef_absent_mod:thing','minecraft:wheat']]}", java.util.Map.of("item|minecraft:wheat", 3), out, 2, null);
						mechanismTest("mech_all_optional_alternatives_absent", "{'id':'t','outputs':['minecraft:bread'],'inputs':[['omnichef_absent_mod:thing']]}", none, out, 0, "not_applicable_missing_mod");
						mechanismTest("mech_mandatory_blocker_not_rescued_by_other_slot", "{'id':'t','outputs':['minecraft:bread'],'inputs':[['minecraft:wheat'],['minecraft:carrot']]}", java.util.Map.of("item|minecraft:wheat", 3), out, 0, null);
						var probe = new PlantEvidenceIndex();
						var found = new java.util.TreeSet<String>();
						var fixture = com.google.gson.JsonParser.parseString(
								"{'type':'minecraft:randomized_int_state_provider','source':{'type':'minecraft:weighted_state_provider','entries':[{'weight':1,'data':{'Name':'test:crop'}},{'weight':0,'data':{'Name':'test:impossible'}}]}}"
										.replace('\'', '"'));
						probe.provider(fixture, found, "test:provider");
						recordSourceTest("worldgen_nested_provider_and_weights", found.equals(java.util.Set.of("test:crop")));
						var feature = com.google.gson.JsonParser.parseString("{'type':'minecraft:nether_forest_vegetation','config':{'state_provider':{'type':'minecraft:simple_state_provider','state':{'Name':'test:fungus'}}}}".replace('\'', '"'));
						found.clear();
						probe.configuredBlocks(feature, found, new java.util.HashSet<>(), "test:feature");
						recordSourceTest("worldgen_vegetation_provider", found.equals(java.util.Set.of("test:fungus")));
						var condition = com.google.gson.JsonParser.parseString("{'condition':'minecraft:block_state_property','block':'test:crop','properties':{'ripe':'true'}}".replace('\'', '"')).getAsJsonObject();
						recordSourceTest("loot_growth_state_uses_selected_variant", probe.condition(condition, "test:crop", java.util.Map.of("ripe", "true")) == 1 && probe.condition(condition, "test:crop", java.util.Map.of("ripe", "false")) == 0);
						var ruleFixture = com.google.gson.JsonParser.parseString(
								"{'processor_type':'minecraft:rule','rules':[{'input_predicate':{'predicate_type':'minecraft:random_block_match','block':'test:crop','probability':0.3},'location_predicate':{'predicate_type':'minecraft:always_true'},'output_state':{'Name':'test:replacement'}},{'input_predicate':{'predicate_type':'minecraft:random_block_match','block':'test:crop','probability':0},'output_state':{'Name':'test:impossible'}}]}"
										.replace('\'', '"'));
						found.clear();
						collectStructureCropRules(ruleFixture, java.util.Set.of("test:crop"), found);
						recordSourceTest("structure_crop_processor_positive_chance", found.equals(java.util.Set.of("test:replacement")));
						var weightedFixture = com.google.gson.JsonParser
								.parseString("{'pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':'test:common','weight':9},{'type':'minecraft:item','name':'test:rare','weight':1}]}]}".replace('\'', '"'));
						var weightedDrops = new java.util.TreeMap<String, java.util.List<DropPath>>();
						boolean previousTest = testMode;
						try {
							testMode = true;
							walkLoot(weightedFixture, java.util.List.of(new DropPath()), new java.util.TreeSet<>(), new java.util.HashSet<>(), 0, weightedDrops);
						} finally {
							testMode = previousTest;
						}
						recordSourceTest("loot_weights_rank_relative_chance", Math.abs(weightedDrops.getOrDefault("test:rare", java.util.List.of()).stream().mapToDouble(p -> p.relativeChance).max().orElse(-1) - 0.1) < 0.00001);
					}

					void recordSourceTest(String name, boolean passed) {
						if (passed)
							selfTestsPassed++;
						else
							selfTestsFailed++;
						var record = new com.google.gson.JsonObject();
						record.addProperty("case", name);
						record.addProperty("passed", passed);
						selfTests.add(record);
					}

					// ---------- base facts ----------
					void indexClimateHarvestRoutes() {
						// Weather replenishes snow-layer blocks in cold, precipitation-enabled
						// biomes. Discover the output via the live block loot table, not item IDs.
						int floor = 99;
						try {
							var registry = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME);
							for (var entry : registry.entrySet()) {
								var biome = entry.getValue();
								if (!biome.hasPrecipitation() || biome.getBaseTemperature() >= 0.15f)
									continue;
								String biomeId = entry.getKey().location().toString();
								for (var dimension : level.getServer().getAllLevels()) {
									String id = dimension.dimension().location().toString();
									if (!plants.biomeDimensions.getOrDefault(biomeId, java.util.Collections.emptySet()).contains(id) || !dimension.dimensionType().hasSkyLight() || dimension.dimensionType().hasCeiling())
										continue;
									floor = Math.min(floor, Math.max(1, dimensionFloor(id)));
								}
							}
						} catch (RuntimeException failure) {
							note("climate_harvest_metadata_unresolved");
							return;
						}
						if (floor == 99)
							return;
						for (var entry : plants.drops.entrySet())
							for (var value : entry.getValue()) {
								var drop = value.getAsJsonObject();
								String block = string(drop, "plant_block");
								var registered = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse(block));
								if (!(registered instanceof net.minecraft.world.level.block.SnowLayerBlock) || !acceptableHarvestDrop(drop, block))
									continue;
								String marker = "climate_snow|" + block;
								String tool = "tool|#minecraft:shovels";
								registerToolNode(tool, "#minecraft:shovels");
								raise(marker, 2, "base:renewable_snowfall_in_reachable_cold_biome");
								repeatRoot(marker, floor);
								addRoute("item|" + entry.getKey(),
										new RenewRoute("omnichef:natural_harvest", block, java.util.List.of(java.util.List.of(marker), java.util.List.of(tool)), 2, "climate precipitation, snow-layer loot and obtainable shovel"));
								count("climate_harvest_routes");
							}
					}

					void baseFacts(com.google.gson.JsonObject database) {
						indexClimateHarvestRoutes();
						indexStructureCrops();
						// Finite or renewable natural world resources accepted by product policy. A tag alone is never a source.
						for (var entry : plants.drops.entrySet())
							for (var element : entry.getValue()) {
								var drop = element.getAsJsonObject();
								String block = string(drop, "plant_block");
								int floor = locationFloor(block);
								if (floor == 99)
									continue;
								String node = "item|" + entry.getKey();
								if (acceptableHarvestDrop(drop, block)) {
									raise(node, 2, "base:located_natural_block_drop:" + block);
									repeatRoot(node, floor);
								} else if (acceptableHarvestDrop(drop, block, true)) {
									String marker = "block_source|" + block;
									raise(marker, 2, "base:located_natural_block:" + block);
									repeatRoot(marker, floor);
									registerToolNode("tool|minecraft:shears", "minecraft:shears");
									addRoute(node, new RenewRoute("omnichef:natural_harvest", block, java.util.List.of(java.util.List.of(marker), java.util.List.of("tool|minecraft:shears")), 2, "located natural block with verified shears ability"));
								}
							}
						// Vanilla terrain is generated by the terrain generator, not necessarily a placed feature.
						for (String id : java.util.List.of("minecraft:stone", "minecraft:cobblestone", "minecraft:dirt", "minecraft:sand", "minecraft:gravel", "minecraft:clay_ball")) {
							if (!itemsById.containsKey(id))
								continue;
							raise("item|" + id, 3, "base:vanilla_overworld_terrain");
							repeatRoot("item|" + id, 0);
						}
						// Water is repeatable; the physical container is a separate mandatory input.
						raise("water|minecraft:water", 3, "base:infinite_fluid:minecraft:water");
						repeatRoot("water|minecraft:water", 0);
						repeatRoot(fluidNode("minecraft:water"), 0);
						raise(fluidNode("minecraft:water"), 3, "base:infinite_water");
						addRoute("item|minecraft:water_bucket",
								new RenewRoute("omnichef:fill_bucket", "water", java.util.List.of(java.util.List.of("water|minecraft:water"), java.util.List.of("item|minecraft:bucket")), 3, "water source and consumed bucket"));
						// Vanilla behaviour products that no loot table expresses (laying, milking, bees). Rank follows the producing entity's spawn evidence.
						var products = java.util.Map.of("item|minecraft:egg", "minecraft:chicken", "item|minecraft:milk_bucket", "minecraft:cow", "item|minecraft:honey_bottle", "minecraft:bee", "item|minecraft:honeycomb", "minecraft:bee");
						for (var product : products.entrySet()) {
							if (!allNodeSet.contains(product.getKey()))
								continue;
							markEntity(product.getValue());
							if (product.getValue().equals("minecraft:bee")) {
								int floor = locationFloor("minecraft:bee_nest");
								if (floor < 99) {
									raise("entity|minecraft:bee", 2, "base:worldgen_bee_nest");
									repeatRoot("entity|minecraft:bee", floor);
								}
							}
							var slots = new java.util.ArrayList<java.util.List<String>>();
							slots.add(java.util.List.of("entity|" + product.getValue()));
							if (product.getKey().equals("item|minecraft:milk_bucket"))
								slots.add(java.util.List.of("item|minecraft:bucket"));
							if (product.getKey().equals("item|minecraft:honey_bottle"))
								slots.add(java.util.List.of("item|minecraft:glass_bottle"));
							if (product.getKey().equals("item|minecraft:honeycomb")) {
								registerToolNode("tool|minecraft:shears", "minecraft:shears");
								slots.add(java.util.List.of("tool|minecraft:shears"));
							}
							addRoute(product.getKey(), new RenewRoute("omnichef:livestock", product.getValue(), slots, 2, "vanilla repeatable entity product"));
						}
						indexChestFirstAccess();
						if (itemsById.containsKey("minecraft:dragon_egg"))
							firstRoot("item|minecraft:dragon_egg", 3);
					}

					// Follow live jigsaw structure definitions and their reachable template pools.
					// A crop in a structure is finite first access, not a renewable source by itself.
					void indexStructureCrops() {
						try {
							var registries = level.registryAccess();
							var ops = net.minecraft.resources.RegistryOps.create(com.mojang.serialization.JsonOps.INSTANCE, registries);
							var structures = registries.registryOrThrow(net.minecraft.core.registries.Registries.STRUCTURE);
							var sets = registries.registryOrThrow(net.minecraft.core.registries.Registries.STRUCTURE_SET);
							var pools = registries.registryOrThrow(net.minecraft.core.registries.Registries.TEMPLATE_POOL);
							var processors = registries.registryOrThrow(net.minecraft.core.registries.Registries.PROCESSOR_LIST);
							var placed = new java.util.HashSet<Object>();
							for (var set : sets)
								for (var entry : set.structures())
									placed.add(entry.structure().value());
							var pending = new java.util.ArrayDeque<java.util.Map.Entry<String, Integer>>();
							for (var structure : structures) {
								if (!placed.contains(structure) || !(structure instanceof net.minecraft.world.level.levelgen.structure.structures.JigsawStructure))
									continue;
								int floor = 99;
								for (var biome : structure.biomes())
									if (biome.unwrapKey().isPresent())
										for (String dim : plants.biomeDimensions.getOrDefault(biome.unwrapKey().get().location().toString(), java.util.Collections.emptySet()))
											floor = Math.min(floor, dimensionFloor(dim));
								if (floor >= 99)
									continue;
								var encoded = net.minecraft.world.level.levelgen.structure.Structure.DIRECT_CODEC.encodeStart(ops, structure).result();
								if (encoded.isEmpty() || !encoded.get().isJsonObject())
									continue;
								String start = string(encoded.get().getAsJsonObject(), "start_pool");
								if (!start.isEmpty())
									pending.add(java.util.Map.entry(start, floor));
							}
							var seen = new java.util.HashMap<String, Integer>();
							int budget = 0;
							while (!pending.isEmpty() && budget++ < 5000) {
								var next = pending.removeFirst();
								String poolId = next.getKey();
								int floor = next.getValue();
								if (seen.getOrDefault(poolId, 99) <= floor)
									continue;
								seen.put(poolId, floor);
								var pool = pools.get(net.minecraft.resources.ResourceLocation.parse(poolId));
								if (pool == null)
									continue;
								var encoded = net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.DIRECT_CODEC.encodeStart(ops, pool).result();
								if (encoded.isEmpty() || !encoded.get().isJsonObject())
									continue;
								var json = encoded.get().getAsJsonObject();
								String fallback = string(json, "fallback");
								if (!fallback.isEmpty())
									pending.add(java.util.Map.entry(fallback, floor));
								if (!json.has("elements") || !json.get("elements").isJsonArray())
									continue;
								for (var row : json.getAsJsonArray("elements")) {
									if (!row.isJsonObject())
										continue;
									var element = row.getAsJsonObject();
									if (element.has("weight") && element.get("weight").getAsInt() <= 0)
										continue;
									var queue = new java.util.ArrayDeque<com.google.gson.JsonElement>();
									if (element.has("element"))
										queue.add(element.get("element"));
									while (!queue.isEmpty()) {
										var raw = queue.removeFirst();
										if (!raw.isJsonObject())
											continue;
										var part = raw.getAsJsonObject();
										if (part.has("elements") && part.get("elements").isJsonArray())
											for (var child : part.getAsJsonArray("elements"))
												queue.add(child);
										String location = string(part, "location");
										if (location.isEmpty())
											continue;
										try {
											var template = level.getStructureManager().get(net.minecraft.resources.ResourceLocation.parse(location));
											if (template.isEmpty())
												continue;
											var data = template.get().save(new net.minecraft.nbt.CompoundTag());
											var blocks = new java.util.HashSet<String>();
											var palette = data.getList("palette", 10);
											for (int i = 0; i < palette.size(); i++)
												blocks.add(palette.getCompound(i).getString("Name"));
											var palettes = data.getList("palettes", 9);
											for (int i = 0; i < palettes.size(); i++) {
												var p = palettes.getList(i);
												for (int j = 0; j < p.size(); j++)
													blocks.add(p.getCompound(j).getString("Name"));
											}
											var positions = data.getList("blocks", 10);
											for (int i = 0; i < positions.size(); i++) {
												String child = positions.getCompound(i).getCompound("nbt").getString("pool");
												if (!child.isEmpty())
													pending.add(java.util.Map.entry(child, floor));
											}
											var generated = new java.util.HashSet<>(blocks);
											String processor = string(part, "processors");
											if (!processor.isEmpty()) {
												var list = processors.get(net.minecraft.resources.ResourceLocation.parse(processor));
												if (list != null) {
													var rules = net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType.LIST_OBJECT_CODEC.encodeStart(ops, list).result();
													if (rules.isPresent())
														collectStructureCropRules(rules.get(), blocks, generated);
												}
											} else if (part.has("processors"))
												collectStructureCropRules(part.get("processors"), blocks, generated);
											for (String block : generated)
												if (plants.crops.contains(block))
													structureCropAccess.merge(block, floor, Math::min);
										} catch (RuntimeException | LinkageError failure) {
											skip("structure_crop_template", location, failure);
										}
									}
								}
							}
							if (budget >= 5000)
								note("structure_crop_budget_exceeded");
							for (var crop : structureCropAccess.entrySet()) {
								for (String seed : plants.seeds.getOrDefault(crop.getKey(), java.util.Collections.emptySet()))
									firstRoot("item|" + seed, crop.getValue());
								for (var drop : plants.drops.entrySet())
									for (var row : drop.getValue()) {
										var record = row.getAsJsonObject();
										if (crop.getKey().equals(string(record, "plant_block")) && acceptableHarvestDrop(record, crop.getKey()))
											firstRoot("item|" + drop.getKey(), crop.getValue());
									}
							}
						} catch (RuntimeException | LinkageError failure) {
							skip("structure_crop_index", "live_jigsaw_registry", failure);
						}
					}

					void collectStructureCropRules(com.google.gson.JsonElement raw, java.util.Set<String> blocks, java.util.Set<String> generated) {
						if (raw.isJsonArray()) {
							for (var child : raw.getAsJsonArray())
								collectStructureCropRules(child, blocks, generated);
							return;
						}
						if (!raw.isJsonObject())
							return;
						var object = raw.getAsJsonObject();
						if (object.has("processors"))
							collectStructureCropRules(object.get("processors"), blocks, generated);
						if (!"minecraft:rule".equals(string(object, "processor_type")) || !object.has("rules"))
							return;
						for (var row : object.getAsJsonArray("rules")) {
							if (!row.isJsonObject())
								continue;
							var rule = row.getAsJsonObject();
							if (!rule.has("input_predicate") || !rule.has("output_state"))
								continue;
							var input = rule.getAsJsonObject("input_predicate");
							String type = string(input, "predicate_type");
							if (!java.util.Set.of("minecraft:block_match", "minecraft:random_block_match").contains(type) || !blocks.contains(string(input, "block")))
								continue;
							if (input.has("probability") && input.get("probability").getAsDouble() <= 0)
								continue;
							boolean valid = true;
							for (String key : java.util.List.of("location_predicate", "position_predicate"))
								if (rule.has(key)) {
									var predicate = rule.getAsJsonObject(key);
									String predicateType = string(predicate, "predicate_type");
									if (predicateType.isEmpty())
										predicateType = string(predicate, "pos_predicate_type");
									if (!"minecraft:always_true".equals(predicateType))
										valid = false;
								}
							if (valid)
								generated.add(string(rule.getAsJsonObject("output_state"), "Name"));
						}
					}

					// A chest table establishes possible first acquisition, never repeatability.
					void indexChestFirstAccess() {
						var manager = level.getServer().getResourceManager();
						for (var entry : manager.listResources("loot_table", id -> id.getPath().startsWith("loot_table/chests/") && id.getPath().endsWith(".json")).entrySet()) {
							try (var reader = entry.getValue().openAsReader()) {
								String target = entry.getKey().getNamespace() + ":" + entry.getKey().getPath().substring("loot_table/".length(), entry.getKey().getPath().length() - 5);
								int floor = chestFloor(target);
								chestEntries(com.google.gson.JsonParser.parseReader(reader), 0, floor);
							} catch (Exception failure) {
								note("chest_first_source_unreadable");
							}
						}
					}

					void chestEntries(com.google.gson.JsonElement element, int depth, int floor) {
						if (depth > 30 || element == null)
							return;
						if (element.isJsonArray()) {
							for (var child : element.getAsJsonArray())
								chestEntries(child, depth + 1, floor);
							return;
						}
						if (!element.isJsonObject())
							return;
						var object = element.getAsJsonObject();
						if (object.has("conditions"))
							return;
						if (object.has("weight") && plainNumber(object.get("weight")) <= 0)
							return;
						if (object.has("rolls") && maxOf(object.get("rolls")) <= 0)
							return;
						if ("minecraft:item".equals(string(object, "type")) && itemsById.containsKey(string(object, "name")))
							firstRoot("item|" + string(object, "name"), floor);
						for (String key : java.util.List.of("pools", "entries", "children"))
							if (object.has(key))
								chestEntries(object.get(key), depth + 1, floor);
					}

					// A harvest drop counts only if every remaining loot condition is resolved by the block state or is a plain chance.
					// Silk touch, shears and any other unresolved condition make the drop useless as evidence.
					boolean acceptableHarvestDrop(com.google.gson.JsonObject drop, String block) {
						return acceptableHarvestDrop(drop, block, false);
					}

					boolean acceptableHarvestDrop(com.google.gson.JsonObject drop, String block, boolean allowShears) {
						var state = new java.util.TreeMap<String, String>();
						if (drop.has("state") && drop.get("state").isJsonObject())
							for (var property : drop.getAsJsonObject("state").entrySet())
								state.put(property.getKey(), property.getValue().getAsString());
						else
							state.putAll(plants.states.getOrDefault(block, java.util.Collections.emptyMap()));
						if (drop.has("functions"))
							for (var element : drop.getAsJsonArray("functions")) {
								if (!element.isJsonObject())
									return false;
								var function = element.getAsJsonObject();
								String name = string(function, "function");
								if (name.equals("minecraft:set_count")) {
									if (!function.has("count") || !(maxOf(function.get("count")) > 0))
										return false;
								} else if (!java.util.Set.of("minecraft:apply_bonus", "minecraft:explosion_decay", "minecraft:limit_count").contains(name))
									return false;
							}
						if (!drop.has("conditions") || !drop.get("conditions").isJsonArray())
							return true;
						for (var element : drop.getAsJsonArray("conditions")) {
							if (!element.isJsonObject())
								return false;
							var condition = element.getAsJsonObject();
							if (plants.condition(condition, block, state) == 1 || emptyHandCondition(condition) || doublePlantNeighbor(condition, block))
								continue;
							if (allowShears && knownShearsCondition(condition))
								continue;
							String type = string(condition, "condition");
							if (type.equals("minecraft:survives_explosion"))
								continue;
							// A player harvesting a block supplies the otherwise unconstrained THIS_ENTITY.
							if (type.equals("minecraft:entity_properties") && "this".equals(string(condition, "entity")) && condition.has("predicate") && condition.get("predicate").isJsonObject() && condition.getAsJsonObject("predicate").size() == 0)
								continue;
							if (type.equals("minecraft:table_bonus") && condition.has("chances") && condition.get("chances").isJsonArray() && condition.getAsJsonArray("chances").asList().stream().anyMatch(x -> plainNumber(x) > 0))
								continue;
							if (type.equals("minecraft:random_chance") && plainNumber(condition.get("chance")) > 0)
								continue;
							if (type.equals("minecraft:random_chance_with_enchanted_bonus") && plainNumber(condition.get("unenchanted_chance")) > 0)
								continue;
							return false;
						}
						return true;
					}

					boolean knownShearsCondition(com.google.gson.JsonObject condition) {
						String type = string(condition, "condition");
						if ("neoforge:can_item_perform_ability".equals(type))
							return java.util.Set.of("shears_harvest", "shears_dig").contains(string(condition, "ability"));
						if ("minecraft:any_of".equals(type) && condition.has("terms") && condition.get("terms").isJsonArray())
							for (var term : condition.getAsJsonArray("terms"))
								if (term.isJsonObject() && knownShearsCondition(term.getAsJsonObject()))
									return true;
						return false;
					}

					boolean doublePlantNeighbor(com.google.gson.JsonObject condition, String block) {
						if (!"minecraft:location_check".equals(string(condition, "condition")) || !condition.has("predicate") || !condition.get("predicate").isJsonObject())
							return false;
						if (condition.has("offsetX") && plainNumber(condition.get("offsetX")) != 0 || condition.has("offsetZ") && plainNumber(condition.get("offsetZ")) != 0)
							return false;
						if (!condition.has("offsetY") || plainNumber(condition.get("offsetY")) != 1)
							return false;
						var predicate = condition.getAsJsonObject("predicate");
						if (predicate.size() != 1 || !predicate.has("block") || !predicate.get("block").isJsonObject())
							return false;
						var same = predicate.getAsJsonObject("block");
						if (same.size() != 2 || !block.equals(string(same, "blocks")) || !same.has("state") || !same.get("state").isJsonObject())
							return false;
						var state = same.getAsJsonObject("state");
						if (state.size() != 1 || !"upper".equals(string(state, "half")))
							return false;
						var registered = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse(block));
						return registered instanceof net.minecraft.world.level.block.DoublePlantBlock && "lower".equals(plants.states.getOrDefault(block, java.util.Collections.emptyMap()).get("half"));
					}

					// A negative test against a specified tool can be satisfied by harvesting without that tool.
					// Never turn a positive tool, state, location or unknown predicate into a free source.
					boolean emptyHandCondition(com.google.gson.JsonObject condition) {
						if (!"minecraft:inverted".equals(string(condition, "condition")) || !condition.has("term") || !condition.get("term").isJsonObject())
							return false;
						return requiresTool(condition.getAsJsonObject("term"));
					}

					boolean requiresTool(com.google.gson.JsonObject condition) {
						String type = string(condition, "condition");
						if ("minecraft:match_tool".equals(type) && condition.has("predicate") && condition.get("predicate").isJsonObject()) {
							var p = condition.getAsJsonObject("predicate");
							return p.has("items") || p.has("predicates") || p.has("components");
						}
						if ("neoforge:can_item_perform_ability".equals(type))
							return knownShearsCondition(condition);
						if ("minecraft:any_of".equals(type) && condition.has("terms") && condition.get("terms").isJsonArray()) {
							var terms = condition.getAsJsonArray("terms");
							return !terms.isEmpty() && terms.asList().stream().allMatch(t -> t.isJsonObject() && requiresTool(t.getAsJsonObject()));
						}
						return false;
					}

					// Blocks that can sustain themselves once the first planting item exists:
					// CropBlock and age-property blocks (max age), spreading plant classes, and stems (fruit by naming convention, assumed only).
					void detectSelfReplantingGrowth() {
						var dropsByBlock = new java.util.TreeMap<String, java.util.TreeSet<String>>();
						int rejected = 0;
						for (var entry : plants.drops.entrySet())
							for (var element : entry.getValue()) {
								if (!element.isJsonObject())
									continue;
								var drop = element.getAsJsonObject();
								if (!drop.has("plant_block"))
									continue;
								String block = drop.get("plant_block").getAsString();
								if (!acceptableHarvestDrop(drop, block)) {
									rejected++;
									continue;
								}
								dropsByBlock.computeIfAbsent(block, ignored -> new java.util.TreeSet<>()).add(entry.getKey());
							}
						notes.put("harvest_drop_records_rejected_unresolved_condition", rejected);
						var growth = new java.util.TreeMap<String, String>();
						for (String block : plants.crops)
							growth.put(block, "crop_block_max_age");
						for (String block : plants.ageBlocks)
							growth.putIfAbsent(block, "age_property_max_age");
						for (String block : plants.spreading)
							growth.putIfAbsent(block, "spreading_plant_class");
						for (String block : plants.seeds.keySet()) {
							var registered = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse(block));
							if (registered != null && registered.getClass().getName().equals("net.minecraft.world.level.block.TallFlowerBlock"))
								growth.putIfAbsent(block, "vanilla_tall_flower_bonemeal");
						}
						var ranks = new java.util.HashMap<String, Integer>();
						for (var candidate : growth.entrySet()) {
							String block = candidate.getKey();
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
							int access = locationFloor(block);
							for (String seed : planting)
								access = Math.min(access, firstAccess.getOrDefault("item|" + seed, 99));
							if (access == 99)
								continue;
							int rank = located ? 3 : 2;
							ranks.put(block, rank);
							int previous = growthAccessSeen.getOrDefault(block, 99);
							if (previous <= access)
								continue;
							growthAccessSeen.put(block, access);
							String why = "base:self_replanting_growth:" + block + ":" + candidate.getValue() + (located ? "" : ":anchored_first_access");
							for (String item : dropped)
								if (allNodeSet.contains("item|" + item)) {
									raise("item|" + item, rank, why);
									repeatRoot("item|" + item, access);
									harvestPlanting.computeIfAbsent(item, k -> new java.util.TreeSet<>()).addAll(planting);
								}
							if (previous != 99)
								continue;
							growthKinds.merge(candidate.getValue(), 1, Integer::sum);
							var record = new com.google.gson.JsonObject();
							record.addProperty("block", block);
							record.addProperty("growth_kind", candidate.getValue());
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
						for (String stem : plants.stems) {
							Integer stemRank = ranks.get(stem);
							if (stemRank == null || !stem.endsWith("_stem"))
								continue;
							String fruit = stem.substring(0, stem.length() - 5);
							var fruitDrops = dropsByBlock.get(fruit);
							if (fruitDrops == null) {
								note("stem_fruit_without_recorded_drops");
								continue;
							}
							int access = locationFloor(stem);
							for (String seed : plants.seeds.getOrDefault(stem, java.util.Collections.emptySet()))
								access = Math.min(access, firstAccess.getOrDefault("item|" + seed, 99));
							if (access == 99)
								continue;
							for (String item : fruitDrops)
								if (allNodeSet.contains("item|" + item)) {
									raise("item|" + item, Math.min(2, stemRank), "base:stem_fruit_naming_convention:" + stem + "->" + fruit);
									repeatRoot("item|" + item, access);
								}
							growthKinds.merge("stem_fruit_by_naming_convention", 1, Integer::sum);
						}
					}

					// Verified 1.21.1 crop families that harvest without consuming the planted block.
					// First access and actual growth environment remain assumptions.
					void indexRegrowingCropFamilies() {
						for (var entry : plants.seeds.entrySet()) {
							String blockId = entry.getKey();
							try {
								var block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse(blockId));
								String type = block.getClass().getName();
								boolean minecolonies = type.equals("com.minecolonies.core.blocks.MinecoloniesCropBlock");
								boolean croptopia = type.equals("com.epherical.croptopia.blocks.CroptopiaCropBlock");
								if (!minecolonies && !croptopia)
									continue;
								var inputs = itemNodes(new java.util.ArrayList<>(entry.getValue()));
								if (inputs.isEmpty())
									continue;
								int firstFloor = 99;
								for (String input : inputs)
									firstFloor = Math.min(firstFloor, firstAccess.getOrDefault(input, 99));
								if (firstFloor == 99)
									continue;
								int previous = regrowthAccessSeen.getOrDefault(blockId, 99);
								if (previous <= firstFloor)
									continue;
								String first = "prerequisite|regrowing:" + blockId;
								var slots = new java.util.ArrayList<java.util.List<String>>();
								slots.add(java.util.List.of(first));
								if (minecolonies) {
									var soil = block.getClass().getMethod("getPreferredFarmland").invoke(block);
									if (!(soil instanceof net.minecraft.world.level.block.Block))
										continue;
									String tool = "tool|#minecraft:hoes";
									registerToolNode(tool, "#minecraft:hoes");
									slots.add(java.util.List.of(tool));
									// MineColonies custom block does not always expose its loot in the generic plant index.
									String table = blockId.replace(":", ":blocks/");
									var root = acquisitionSources.lootResource(table, "loot_table");
									if (root == null || !root.has("pools"))
										continue;
									var pools = root.getAsJsonArray("pools");
									if (pools.size() != 1 || root.has("conditions") || root.has("functions"))
										continue;
									var pool = pools.get(0).getAsJsonObject();
									if (pool.has("conditions") || pool.has("functions") || !pool.has("rolls") || !(plainNumber(pool.get("rolls")) >= 1))
										continue;
									var entries = pool.getAsJsonArray("entries");
									if (entries == null || entries.size() != 1)
										continue;
									var alternative = entries.get(0).getAsJsonObject();
									if (!"minecraft:alternatives".equals(string(alternative, "type")) || alternative.has("conditions") || alternative.has("functions"))
										continue;
									var children = alternative.getAsJsonArray("children");
									if (children == null || children.size() != 2)
										continue;
									var mature = children.get(0).getAsJsonObject();
									var fallback = children.get(1).getAsJsonObject();
									String id = string(mature, "name");
									if (!itemsById.containsKey(id) || !entry.getValue().contains(id) || !id.equals(string(fallback, "name")) || !"minecraft:item".equals(string(mature, "type")) || !"minecraft:item".equals(string(fallback, "type"))
											|| fallback.has("conditions") || fallback.has("functions"))
										continue;
									var conditions = mature.getAsJsonArray("conditions");
									if (conditions == null || conditions.size() != 1)
										continue;
									var condition = conditions.get(0).getAsJsonObject();
									if (!"minecraft:block_state_property".equals(string(condition, "condition")) || !blockId.equals(string(condition, "block")) || !condition.has("properties")
											|| !"6".equals(string(condition.getAsJsonObject("properties"), "age")))
										continue;
									if (mature.has("functions") && !mature.getAsJsonArray("functions").asList().stream().allMatch(f -> f.isJsonObject() && "minecraft:apply_bonus".equals(string(f.getAsJsonObject(), "function"))))
										continue;
									raise(first, 2, "base:minecolonies_crop_first_access_and_farmland_assumed:" + blockId);
									repeatRoot(first, Math.max(firstFloor, 1));
									regrowthAccessSeen.put(blockId, firstFloor);
									if (previous != 99)
										continue;
									addRoute("item|" + id,
											new RenewRoute("omnichef:minecolonies_regrowth", table, slots, 2, "MinecoloniesCropBlock.useItemOn(hoe), age 6 -> 0; MinecoloniesFarmland.randomTick; live mature loot; first access/soil assumed"));
									harvestPlanting.computeIfAbsent(id, k -> new java.util.TreeSet<>()).addAll(entry.getValue());
									count("minecolonies_regrowing_routes");
								} else {
									for (var dropEntry : plants.drops.entrySet())
										for (var element : dropEntry.getValue()) {
											if (!element.isJsonObject())
												continue;
											var drop = element.getAsJsonObject();
											if (!blockId.equals(string(drop, "plant_block")) || !acceptableHarvestDrop(drop, blockId))
												continue;
											if (drop.has("functions") && !drop.getAsJsonArray("functions").asList().stream()
													.allMatch(f -> f.isJsonObject() && java.util.Set.of("minecraft:apply_bonus", "minecraft:explosion_decay").contains(string(f.getAsJsonObject(), "function"))))
												continue;
											raise(first, 2, "base:croptopia_crop_seed_first_access_anchored:" + blockId);
											repeatRoot(first, firstFloor);
											regrowthAccessSeen.put(blockId, firstFloor);
											if (previous != 99)
												continue;
											addRoute("item|" + dropEntry.getKey(), new RenewRoute("omnichef:croptopia_regrowth", string(drop, "loot_table"), slots, 2,
													"CroptopiaCropBlock.useWithoutItem at max age harvests and sets age 0; live loot candidate; first access assumed"));
											harvestPlanting.computeIfAbsent(dropEntry.getKey(), k -> new java.util.TreeSet<>()).addAll(entry.getValue());
											count("croptopia_regrowing_routes");
										}
								}
							} catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
								note("regrowing_crop_family_unresolved");
							}
						}
					}

					final java.util.Map<String, java.util.Set<String>> treeRepeatOutputs = new java.util.TreeMap<>();
					final java.util.Map<String, String> productiveGrowthByFeature = new java.util.TreeMap<>();
					final java.util.Map<String, java.util.Set<String>> productiveGrowthByFamily = new java.util.TreeMap<>();
					final java.util.Map<String, Object> productiveTreeObjects = new java.util.TreeMap<>();

					boolean hasClass(Object instance, String name) {
						if (instance == null)
							return false;
						for (Class<?> c = instance.getClass(); c != null; c = c.getSuperclass())
							if (c.getName().equals(name))
								return true;
						return false;
					}

					boolean hasHarvest(String item, String block, boolean shears) {
						for (var element : plants.drops.getOrDefault(item, new com.google.gson.JsonArray()))
							if (element.isJsonObject() && block.equals(string(element.getAsJsonObject(), "plant_block")) && acceptableHarvestDrop(element.getAsJsonObject(), block, shears))
								return true;
						return false;
					}

					void rememberTreeOutput(String growth, String output) {
						treeRepeatOutputs.computeIfAbsent(growth, k -> new java.util.TreeSet<>()).add(output);
					}

					void indexProductiveTrees() {
						try {
							var cls = Class.forName("cy.jdkdigital.productivetrees.registry.TreeFinder", false, getClass().getClassLoader());
							Object registry = cls.getField("trees").get(null);
							if (!(registry instanceof java.util.Map<?, ?> trees))
								return;
							plants.visits = 0;
							for (var entry : trees.entrySet()) {
								String id = entry.getKey().toString();
								try {
									indexProductiveTree(id, entry.getValue());
								} catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
									countUnrecognized("productive_tree_family", id + " " + failure.getClass().getSimpleName());
								}
							}
							// BlockGrowFeatureEvent can replace the parent's growth feature with the mutation target.
							for (var entry : productiveTreeObjects.entrySet()) {
								try {
									Object mutation = acquisitionSources.call(entry.getValue(), "getMutationInfo");
									String target = acquisitionSources.call(mutation, "target").toString();
									double chance = ((Number) acquisitionSources.call(mutation, "chance")).doubleValue();
									String growth = productiveGrowthByFeature.get(target);
									if (growth == null || !(chance > 0))
										continue;
									for (String parent : productiveGrowthByFamily.getOrDefault(entry.getKey(), java.util.Collections.emptySet())) {
										if (parent.equals(growth))
											continue;
										addRoute(growth, new RenewRoute("productivetrees:tree_mutation", target, java.util.List.of(java.util.List.of(parent)), 2, "live TreeObject mutation target, chance=" + chance));
										count("productive_mutation_routes");
									}
								} catch (ReflectiveOperationException | RuntimeException failure) {
									note("productive_mutation_unresolved");
								}
							}
						} catch (ClassNotFoundException absent) {
							/* optional mod */ } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
							note("productive_registry_unresolved");
						}
					}

					void indexProductiveTree(String id, Object tree) throws ReflectiveOperationException {
						String sapling = id + "_sapling";
						var saplingItem = itemsById.get(sapling);
						if (!(saplingItem instanceof net.minecraft.world.item.BlockItem bi) || !hasClass(bi.getBlock(), "cy.jdkdigital.productivetrees.common.block.ProductiveSaplingBlock"))
							return;
						if (acquisitionSources.call(bi.getBlock(), "getTree") != tree)
							return;
						var slots = new java.util.ArrayList<java.util.List<String>>();
						slots.add(java.util.List.of("item|" + sapling));
						// ProductiveSaplingBlock.mayPlaceOn checks the live block soil tag.
						Object soil = acquisitionSources.call(tree, "getSoil");
						if (!(soil instanceof net.minecraft.tags.TagKey<?>))
							return;
						@SuppressWarnings("unchecked")
						var soilTag = (net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block>) soil;
						var soilBlocks = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getTag(soilTag);
						var soilItems = new java.util.TreeSet<String>();
						if (soilBlocks.isPresent())
							for (var holder : soilBlocks.get()) {
								String item = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(holder.value().asItem()).toString();
								if (!"minecraft:air".equals(item) && itemsById.containsKey(item))
									soilItems.add(item);
							}
						if (soilItems.isEmpty()) {
							note("productive_tree_soil_unresolved");
							return;
						}
						String soilMarker = "tool|productive_soil:" + id;
						registerToolNode(soilMarker, String.join(",", soilItems));
						slots.add(java.util.List.of(soilMarker));
						Object conditions = acquisitionSources.call(tree, "getGrowthConditions");
						int minLight = ((Number) acquisitionSources.call(conditions, "minLight")).intValue();
						int maxLight = ((Number) acquisitionSources.call(conditions, "maxLight")).intValue();
						if (minLight > 15 || maxLight < 0 || minLight > maxLight)
							return;
						Object fluid = acquisitionSources.call(conditions, "fluid");
						if (fluid instanceof net.neoforged.neoforge.fluids.FluidStack fs && !fs.isEmpty()) {
							String fluidId = net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(fs.getFluid()).toString();
							slots.add(java.util.List.of(fluidNode(fluidId)));
						}
						Object biome = acquisitionSources.call(conditions, "biome");
						if (biome instanceof java.util.Optional<?> optional && optional.isPresent()) {
							int floor = 99;
							if (optional.get() instanceof java.lang.Iterable<?> holders)
								for (Object h : holders) {
									if (h instanceof net.minecraft.core.Holder<?> holder && holder.unwrapKey().isPresent()) {
										String key = holder.unwrapKey().get().location().toString();
										for (String dim : plants.biomeDimensions.getOrDefault(key, java.util.Collections.emptySet()))
											floor = Math.min(floor, dimensionFloor(dim));
									}
								}
							if (floor == 99) {
								note("productive_tree_biome_unresolved");
								return;
							}
							String marker = "biome_gate|productive_tree:" + id;
							raise(marker, 2, "base:productive_tree_live_biome_requirement");
							repeatRoot(marker, Math.max(1, floor));
							slots.add(java.util.List.of(marker));
						}
						productiveTreeObjects.put(id, tree);
						for (String getter : java.util.List.of("getFeature", "getMegaFeature", "getLargeMegaFeature")) {
							Object key = acquisitionSources.call(tree, getter);
							if (!(key instanceof net.minecraft.resources.ResourceKey<?> resource))
								continue;
							String feature = resource.location().toString();
							if (!plants.configured.containsKey(feature))
								continue;
							var placedBlocks = new java.util.TreeSet<String>();
							plants.configuredBlocks(new com.google.gson.JsonPrimitive(feature), placedBlocks, new java.util.HashSet<>(), feature);
							if (placedBlocks.isEmpty()) {
								note("productive_tree_feature_not_interpreted");
								continue;
							}
							// Only real outputs of the species' configured feature are accepted.
							String growth = "tree_growth|" + id + "|" + feature;
							if (productiveGrowthByFeature.putIfAbsent(feature, growth) != null)
								continue;
							productiveGrowthByFamily.computeIfAbsent(id, k -> new java.util.TreeSet<>()).add(growth);
							addRoute(growth, new RenewRoute("productivetrees:grown_tree", feature, new java.util.ArrayList<>(slots), 2, "live sapling, configured growth feature, soil and growth conditions"));
							count("productive_growth_routes");
							for (String block : placedBlocks) {
								var actual = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse(block));
								String leaf = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(actual.asItem()).toString();
								if (!"minecraft:air".equals(leaf) && itemsById.containsKey(leaf) && actual instanceof net.minecraft.world.level.block.LeavesBlock) {
									boolean bare = hasHarvest(leaf, block, false);
									if (bare || hasHarvest(leaf, block, true)) {
										var leafSlots = new java.util.ArrayList<java.util.List<String>>();
										leafSlots.add(java.util.List.of(growth));
										if (!bare) {
											registerToolNode("tool|minecraft:shears", "minecraft:shears");
											leafSlots.add(java.util.List.of("tool|minecraft:shears"));
										}
										addRoute("item|" + leaf, new RenewRoute("productivetrees:grown_leaves", feature, leafSlots, 2, "placed leaf and live harvest loot"));
									}
								}
								if (hasHarvest(sapling, block, false)) {
									addRoute("item|" + sapling, new RenewRoute("productivetrees:sapling_return", feature, java.util.List.of(java.util.List.of(growth)), 2, "live leaf loot returns the species sapling"));
									rememberTreeOutput(growth, "item|" + sapling);
								}
								// Right-clicking a ripe ProductiveFruitBlock directly returns TreeObject.Fruit.getItem
								// and resets age to 0. The planted tree is retained across harvests.
								if (hasClass(actual, "cy.jdkdigital.productivetrees.common.block.ProductiveFruitBlock") && acquisitionSources.call(actual, "getTree") == tree && Boolean.TRUE.equals(acquisitionSources.call(tree, "hasFruit"))) {
									Object f = acquisitionSources.call(tree, "getFruit");
									double speed = ((Number) acquisitionSources.call(f, "growthSpeed")).doubleValue();
									var stack = (net.minecraft.world.item.ItemStack) acquisitionSources.call(f, "getItem");
									if (speed > 0 && !stack.isEmpty()) {
										String output = outputNode(stack);
										if (output != null) {
											rememberTreeOutput(growth, output);
											count("productive_regrowing_fruit_routes");
										}
									}
								}
							}
						}
					}

					// Only an independently obtainable grown tree can anchor its sapling loop or regrowing fruit.
					void detectSelfReplantingTrees() {
						for (var entry : treeRepeatOutputs.entrySet()) {
							int floor = firstAccess.getOrDefault(entry.getKey(), 99);
							if (floor == 99)
								continue;
							for (String output : entry.getValue()) {
								int before = directRepeatAccess.getOrDefault(output, 99);
								raise(output, 2, "base:verified_tree_cycle:" + entry.getKey());
								repeatRoot(output, floor);
								if (before > floor)
									count("tree_regrowth_anchored");
							}
						}
					}

					// ---------- fixed point ----------
					int routeValue(RenewRoute route) {
						if (route.slots.isEmpty())
							return 0;
						int worst = route.cap;
						for (var slot : route.slots) {
							int best = 0;
							for (String alternative : slot)
								best = Math.max(best, value.getOrDefault(alternative, 0));
							worst = Math.min(worst, best);
							if (worst == 0)
								return 0;
						}
						return worst;
					}

					boolean routeUnverified(RenewRoute route) {
						for (var slot : route.slots) {
							boolean every = !slot.isEmpty();
							for (String alternative : slot)
								if (!unverified.contains(alternative)) {
									every = false;
									break;
								}
							if (every)
								return true;
						}
						return false;
					}

					void run(com.google.gson.JsonObject database) {
						runLootSelfTests();
						runMechanismSelfTests();
						var catalogNodes = new java.util.TreeSet<String>();
						for (String group : java.util.List.of("ingredients", "foods"))
							for (var tier : database.getAsJsonObject(group).getAsJsonObject("tiers").entrySet())
								for (var element : tier.getValue().getAsJsonArray())
									catalogNodes.add("item|" + element.getAsJsonObject().get("id").getAsString());
						var probeSet = new java.util.TreeSet<String>(acquisitionSources.nodes);
						probeSet.addAll(catalogNodes);
						for (String id : itemsById.keySet())
							probeSet.add("item|" + id);
						probeNodes.addAll(probeSet);
						allNodeSet.addAll(probeSet);
						long stageStart = System.nanoTime();
						indexStructureAndRaidSpawns();
						indexRecipes();
						convertGraphRoutes();
						indexProductiveTrees();
						note("recipe_index_ms:" + (System.nanoTime() - stageStart) / 1000000L);
						buildMobDropRoutes();
						buildFishingRoutes();
						buildLootModifierRoutes();
						loadDeclaredMechanisms();
						baseFacts(database);
						routedNodes.addAll(new java.util.TreeSet<>(renewRoutes.keySet()));
						solveAccess(false);
						for (int pass = 0; pass < 16; pass++) {
							var before = new java.util.HashMap<>(firstAccess);
							detectSelfReplantingGrowth();
							indexRegrowingCropFamilies();
							detectSelfReplantingTrees();
							routedNodes.clear();
							routedNodes.addAll(new java.util.TreeSet<>(renewRoutes.keySet()));
							solveAccess(false);
							if (firstAccess.equals(before))
								break;
							if (pass == 15)
								throw new IllegalStateException("Plant first-access closure did not converge");
						}
						for (String tool : registeredTools)
							if (firstAccess.containsKey(tool))
								raise(tool, 2, "base:equipment_first_access");
						propagate();
						solveAccess(true);
						measureSourceRarity();
						int satisfiedTools = 0;
						for (String tool : registeredTools)
							if (value.getOrDefault(tool, 0) >= 2)
								satisfiedTools++;
						adapterCounts.put("tool_nodes_satisfied", satisfiedTools);
						adapterCounts.put("tool_nodes_unverified", registeredTools.size() - satisfiedTools);
					}

					// Probability is ranking evidence only. A verified easier independent source wins.
					void measureSourceRarity() {
						var fishTag = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("c:foods/raw_fish"));
						for (var entry : renewRoutes.entrySet()) {
							String node = entry.getKey();
							if (!node.startsWith("item|") || !eligible(node) || directRepeatAccess.containsKey(node))
								continue;
							String id = node.substring(5);
							double best = 0, fishing = 0;
							int habitat = 0;
							boolean other = false;
							for (var route : entry.getValue()) {
								if (routeValue(route) < 2 || routeAccess(route, repeatAccess, true) >= 99)
									continue;
								if (route.system.equals("omnichef:fishing")) {
									double chance = routeRelativeChance.getOrDefault(route, 1.0);
									fishing = Math.max(fishing, chance);
									best = Math.max(best, chance);
								} else if (route.system.equals("omnichef:mob_drop")) {
									String marker = route.slots.get(0).get(0);
									int coverage = marker.startsWith("entity|") ? mobBiomeCoverage.getOrDefault(marker.substring(7), 0) : 0;
									habitat = Math.max(habitat, coverage);
									best = Math.max(best, routeRelativeChance.getOrDefault(route, 1.0) * Math.min(1.0, coverage / 8.0));
								} else
									other = true;
							}
							if (other)
								continue;
							if (best > 0)
								sourceRarity.put(id, Math.min(3.0, -Math.log(Math.min(1.0, best))));
							if (classificationRules.matchesTags(id, java.util.List.of(fishTag)) && fishing > 0 && fishing <= 0.13 && habitat <= 8)
								evidenceMinimumTiers.put(id, 2);
						}
						for (var entry : plants.drops.entrySet()) {
							if (!eligible("item|" + entry.getKey()))
								continue;
							for (var element : entry.getValue()) {
								var drop = element.getAsJsonObject();
								String block = string(drop, "plant_block");
								if (locationFloor(block) >= 99 || !acceptableHarvestDrop(drop, block))
									continue;
								var registered = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse(block));
								if (registered.defaultBlockState().getFluidState().is(net.minecraft.tags.FluidTags.WATER))
									evidenceMinimumTiers.merge(entry.getKey(), 1, Math::max);
							}
						}
					}

					// Least fixed point over all routes, then the "unverified_source" pass. Also used by the mechanism self tests on a scratch instance.
					void propagate() {
						boolean changed = true;
						while (changed && passes < maxPasses) {
							changed = false;
							passes++;
							for (String node : routedNodes) {
								var list = renewRoutes.get(node);
								if (list == null)
									continue;
								for (var route : list) {
									int rank = routeValue(route);
									if (rank > value.getOrDefault(node, 0)) {
										raise(node, rank, "route:" + route.system + "[" + route.evidence + "]");
										bestRoute.put(node, route);
										changed = true;
									}
								}
							}
						}
						converged = !changed;
						// "unverified_source": routes exist, none has repeatability evidence, nothing renewable was found. "limited" stays reserved for positive evidence.
						boolean unverifiedChanged = true;
						int guard = 0;
						while (unverifiedChanged && guard++ < maxPasses) {
							unverifiedChanged = false;
							for (String node : routedNodes) {
								if (unverified.contains(node) || value.getOrDefault(node, 0) > 0)
									continue;
								var list = renewRoutes.get(node);
								if (list == null || list.isEmpty())
									continue;
								boolean every = true;
								for (var route : list)
									if (!routeUnverified(route)) {
										every = false;
										break;
									}
								if (every) {
									unverified.add(node);
									unverifiedChanged = true;
								}
							}
						}
					}

					String classify(String node) {
						int rank = value.getOrDefault(node, 0);
						if (rank < 2 && (directAccess.containsKey(node) || chestModifierItems.contains(node) && firstAccess.containsKey(node)) && node.startsWith("item|"))
							return "limited";
						return rank >= 3 ? "renewable" : rank == 2 ? "assumed_renewable" : unverified.contains(node) ? "unverified_source" : "unknown";
					}

					// ---------- blocker analysis ----------
					int bestRank(java.util.List<String> slot) {
						int best = 0;
						for (String alternative : slot)
							best = Math.max(best, value.getOrDefault(alternative, 0));
						return best;
					}

					java.util.List<java.util.List<String>> blockingSlots(RenewRoute route) {
						var blocking = new java.util.ArrayList<java.util.List<String>>();
						for (var slot : route.slots)
							if (bestRank(slot) < 2)
								blocking.add(slot);
						return blocking;
					}

					// Root blockers follow the least-blocked route, depth-capped and memoized; a diagnostic approximation.
					java.util.Set<String> rootsOf(String node, int depth, java.util.Set<String> visiting) {
						var cached = rootMemo.get(node);
						if (cached != null)
							return cached;
						var result = new java.util.TreeSet<String>();
						if (value.getOrDefault(node, 0) >= 2)
							return result;
						var list = renewRoutes.get(node);
						if (list == null || list.isEmpty() || depth >= 8 || !visiting.add(node)) {
							result.add(node);
							return result;
						}
						try {
							java.util.List<java.util.List<String>> chosen = null;
							boolean leafRoute = false;
							for (var route : list) {
								var blocking = blockingSlots(route);
								if (blocking.isEmpty()) {
									leafRoute = true;
									break;
								}
								if (chosen == null || blocking.size() < chosen.size())
									chosen = blocking;
							}
							if (leafRoute || chosen == null)
								result.add(node);
							else
								for (var slot : chosen) {
									java.util.List<String> alternatives = new java.util.ArrayList<>(new java.util.TreeSet<>(slot));
									for (int i = 0; i < Math.min(3, alternatives.size()); i++)
										result.addAll(rootsOf(alternatives.get(i), depth + 1, visiting));
								}
						} finally {
							visiting.remove(node);
						}
						rootMemo.put(node, result);
						return result;
					}

					String nextBlockingNode(String node, String root) {
						var list = renewRoutes.get(node);
						if (list == null)
							return null;
						for (var route : list)
							for (var slot : blockingSlots(route)) {
								for (String alternative : new java.util.TreeSet<>(slot)) {
									if (alternative.equals(root) || rootsOf(alternative, 1, new java.util.HashSet<>()).contains(root))
										return alternative;
								}
							}
						return null;
					}

					// Food -> intermediate -> raw material -> source, following the least-blocked route.
					com.google.gson.JsonArray pathToRoot(String start, String root) {
						var path = new com.google.gson.JsonArray();
						var seen = new java.util.HashSet<String>();
						String current = start;
						for (int step = 0; step < 10 && current != null; step++) {
							path.add(current.startsWith("item|") ? current.substring(5) : current);
							if (current.equals(root) || !seen.add(current))
								break;
							current = nextBlockingNode(current, root);
						}
						return path;
					}

					String sourceType(String basisText) {
						if (basisText.startsWith("route:")) {
							int end = basisText.indexOf('[');
							String system = basisText.substring(6, end < 0 ? basisText.length() : end);
							if (system.equals("omnichef:mob_drop") || system.equals("omnichef:mob_loot"))
								return "mob_drop";
							if (system.equals("omnichef:fishing"))
								return "fishing";
							if (system.equals("omnichef:loot_modifier"))
								return "loot_modifier";
							return "recipe_or_adapter";
						}
						if (basisText.contains("entity_spawn"))
							return "mob_drop";
						if (basisText.contains("fishing"))
							return "fishing";
						if (basisText.contains("self_replanting_growth") || basisText.contains("stem_fruit") || basisText.contains("verified_tree_cycle"))
							return "plant_growth";
						if (basisText.contains("world_resource"))
							return "world_resource";
						if (basisText.contains("infinite_fluid"))
							return "fluid";
						if (basisText.contains("livestock"))
							return "livestock";
						if (basisText.contains("natural_plant"))
							return "plant_tag";
						if (basisText.contains("tool_assumed"))
							return "tool";
						if (basisText.startsWith("base:"))
							return "base_fact";
						return "none";
					}

					String missingEvidence(String status, String basisText, boolean hasRoutes) {
						if (status.equals("renewable"))
							return "";
						if (status.equals("assumed_renewable")) {
							if (basisText.contains("first_access_unverified"))
								return "first_access_not_verified";
							if (basisText.contains("tag_assumption"))
								return "assumption_not_evidence";
							if (basisText.contains("world_resource"))
								return "assumed_unlimited_world_resource";
							if (basisText.contains("adapter=reflective"))
								return "reflective_adapter_unverified";
							if (basisText.contains("tool_assumed"))
								return "tool_chain_unverified";
							if (basisText.contains("omnichef:loot_modifier") || basisText.contains("tool:"))
								return "tool_or_modifier_unverified";
							return "partial_evidence";
						}
						if (status.equals("unverified_source"))
							return "spawn_or_source_unverified";
						return hasRoutes ? "inputs_not_renewable" : "no_known_route_or_base_fact";
					}

					String countsLine(java.util.Map<String, Integer> counts, String prefix) {
						StringBuilder line = new StringBuilder();
						for (String status : java.util.List.of("renewable", "assumed_renewable", "unknown", "unverified_source", "limited")) {
							if (line.length() > 0)
								line.append(", ");
							line.append(status).append('=').append(counts.getOrDefault(prefix + status, 0));
						}
						return line.toString();
					}

					// ---------- report ----------
					com.google.gson.JsonObject report(com.google.gson.JsonObject database) {
						var result = new com.google.gson.JsonObject();
						result.addProperty("status", converged ? "completed" : "pass_limit_reached");
						result.addProperty("version", 38);
						result.addProperty("revision", 4);
						result.addProperty("patch", "category_floors_material_cost_and_source_rarity");
						var cropSources = new com.google.gson.JsonObject();
						for (var crop : structureCropAccess.entrySet())
							cropSources.addProperty(crop.getKey(), crop.getValue());
						result.add("structure_crop_sources", cropSources);
						result.addProperty("policy",
								"v37: final catalog only accepts repeatable/assumed repeatable sources with anchored first access and recurring access. Consumable slots are mandatory; equipment is a first-access gate. Tags alone do not prove renewal. Rejected variants and recipes are filtered before final tiering. Unsupported mechanisms do not provide evidence. The full diagnostic baseline is retained for comparison.");
						result.addProperty("audit_only_component_nodes", auditVariantNodes.size());
						result.addProperty("passes", passes);
						result.addProperty("nodes_with_routes", routedNodes.size());
						var counts = new java.util.TreeMap<String, Integer>();
						var unknownByNamespace = new java.util.TreeMap<String, Integer>();
						var items = new com.google.gson.JsonObject();
						var blockedFoods = new java.util.ArrayList<String>();
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
									var routeList = renewRoutes.get(node);
									String itemBasis = basis.getOrDefault(node, unverified.contains(node) ? "all_recognized_routes_lack_repeatability_evidence" : routeList != null ? "routes_without_renewable_inputs" : "no_route_and_no_base_fact");
									var entry = new com.google.gson.JsonObject();
									entry.addProperty("status", status);
									entry.addProperty("first_access_class", firstAccess.getOrDefault(node, -1));
									entry.addProperty("recurring_access_class", repeatAccess.getOrDefault(node, -1));
									entry.addProperty("source_rarity", sourceRarity.getOrDefault(id, 0.0));
									entry.addProperty("evidence_minimum_tier", evidenceMinimumTiers.getOrDefault(id, 0));
									entry.addProperty("eligible", eligible(node));
									entry.addProperty("group", group);
									entry.addProperty("servable", servable);
									entry.addProperty("basis", itemBasis);
									entry.addProperty("source_type", sourceType(itemBasis));
									entry.addProperty("confidence", status.equals("renewable") ? "confirmed" : status.equals("assumed_renewable") ? "assumed" : status.equals("unverified_source") ? "unverified" : "none");
									entry.addProperty("route_count", routeList == null ? 0 : routeList.size());
									var best = bestRoute.get(node);
									if (best != null && value.getOrDefault(node, 0) > 0 && (best.system.startsWith("omnichef:") || !status.equals("renewable"))) {
										entry.addProperty("mechanism", best.system);
										entry.addProperty("evidence", best.evidence);
									}
									if (!status.equals("renewable")) {
										String missing = missingEvidence(status, itemBasis, routeList != null);
										entry.addProperty("missing_evidence", missing);
										var dependencies = new com.google.gson.JsonArray();
										if (routeList != null && !status.equals("assumed_renewable")) {
											int taken = 0;
											for (String root : rootsOf(node, 0, new java.util.HashSet<>())) {
												if (taken++ >= 3)
													break;
												dependencies.add(root.startsWith("item|") ? root.substring(5) : root);
											}
										}
										entry.add("unresolved_dependencies", dependencies);
										entry.addProperty("reason", status + ": " + missing + (dependencies.size() > 0 ? "; blocked by " + dependencies.get(0).getAsString() : ""));
										if (routeList != null) {
											var systems = new java.util.TreeSet<String>();
											for (var route : routeList)
												systems.add(route.system);
											var systemArray = new com.google.gson.JsonArray();
											for (String system : systems)
												systemArray.add(system);
											entry.add("route_systems", systemArray);
										}
									}
									items.add(id, entry);
									if (status.equals("unknown") && servable) {
										int colon = id.indexOf(':');
										unknownByNamespace.merge(colon > 0 ? id.substring(0, colon) : id, 1, Integer::sum);
									}
									if (group.equals("foods") && (status.equals("unknown") || status.equals("unverified_source")))
										blockedFoods.add(id);
								}
						}
						// Include pre-catalog exclusions as well, otherwise their failed raw
						// inputs disappear from the audit even though they still block real food.
						var inputAudit = new com.google.gson.JsonObject();
						var technical = database.getAsJsonObject("technical").getAsJsonObject("diagnostics");
						if (technical.has("blocked_inputs"))
							for (var item : technical.getAsJsonObject("blocked_inputs").entrySet()) {
								String id = item.getKey(), node = "item|" + id;
								var row = new com.google.gson.JsonObject();
								row.addProperty("status", classify(node));
								row.addProperty("eligible", eligible(node));
								row.addProperty("first_access_class", firstAccess.getOrDefault(node, -1));
								row.addProperty("recurring_access_class", repeatAccess.getOrDefault(node, -1));
								row.addProperty("basis", basis.getOrDefault(node, "none"));
								var systems = new java.util.TreeSet<String>();
								for (var route : renewRoutes.getOrDefault(node, java.util.Collections.emptyList()))
									systems.add(route.system);
								row.add("route_systems", new com.google.gson.Gson().toJsonTree(systems));
								inputAudit.add(id, row);
							}
						result.add("blocked_input_source_audit", inputAudit);
						if (technical.has("excluded_progression"))
							for (var value : technical.getAsJsonArray("excluded_progression")) {
								String id = value.getAsJsonObject().get("id").getAsString();
								if (!eligible("item|" + id) && !blockedFoods.contains(id))
									blockedFoods.add(id);
							}
						// Root blockers with a traceable path: Food -> intermediate -> raw material -> source.
						var rootCounts = new java.util.TreeMap<String, Integer>();
						var rootSamples = new java.util.TreeMap<String, java.util.List<String>>();
						for (String foodId : blockedFoods) {
							for (String root : rootsOf("item|" + foodId, 0, new java.util.HashSet<>())) {
								rootCounts.merge(root, 1, Integer::sum);
								var samples = rootSamples.computeIfAbsent(root, ignored -> new java.util.ArrayList<>());
								if (samples.size() < 5)
									samples.add(foodId);
							}
						}
						var rankedRoots = new java.util.ArrayList<>(rootCounts.entrySet());
						rankedRoots.sort((a, b) -> {
							int byCount = Integer.compare(b.getValue(), a.getValue());
							return byCount != 0 ? byCount : a.getKey().compareTo(b.getKey());
						});
						var rootArray = new com.google.gson.JsonArray();
						StringBuilder topRoots = new StringBuilder();
						for (int i = 0; i < Math.min(40, rankedRoots.size()); i++) {
							String root = rankedRoots.get(i).getKey();
							var record = new com.google.gson.JsonObject();
							record.addProperty("node", root);
							record.addProperty("foods_blocked", rankedRoots.get(i).getValue());
							record.addProperty("status", classify(root));
							record.addProperty("basis", basis.getOrDefault(root, "none"));
							var routeList = renewRoutes.get(root);
							var systems = new java.util.TreeSet<String>();
							if (routeList != null)
								for (var route : routeList)
									systems.add(route.system);
							var systemArray = new com.google.gson.JsonArray();
							for (String system : systems)
								systemArray.add(system);
							record.add("route_systems", systemArray);
							var sampleArray = new com.google.gson.JsonArray();
							for (String sample : rootSamples.get(root))
								sampleArray.add(sample);
							record.add("sample_foods", sampleArray);
							if (i < 20) {
								var paths = new com.google.gson.JsonArray();
								int shown = 0;
								for (String sample : rootSamples.get(root)) {
									if (shown++ >= 2)
										break;
									paths.add(pathToRoot("item|" + sample, root));
								}
								record.add("sample_paths", paths);
							}
							rootArray.add(record);
							if (i < 12) {
								if (topRoots.length() > 0)
									topRoots.append(", ");
								topRoots.append(root.startsWith("item|") ? root.substring(5) : root).append('=').append(rankedRoots.get(i).getValue());
							}
						}
						// Tier 0 ingredients that sit there only because no evidence existed (assumed_base + confidence assumed).
						int tier0Total = 0, tier0Assumed = 0, tier0AssumedNotRenewable = 0;
						var tier0List = new com.google.gson.JsonArray();
						var ingredientTiers = database.getAsJsonObject("ingredients").getAsJsonObject("tiers");
						if (ingredientTiers.has("0"))
							for (var element : ingredientTiers.getAsJsonArray("0")) {
								var item = element.getAsJsonObject();
								tier0Total++;
								if (!item.has("analysis") || !item.get("analysis").isJsonObject() || !item.getAsJsonObject("analysis").has("acquisition"))
									continue;
								var acquisition = item.getAsJsonObject("analysis").getAsJsonObject("acquisition");
								boolean assumed = acquisition.has("status") && "assumed_base".equals(acquisition.get("status").getAsString()) && acquisition.has("confidence") && "assumed".equals(acquisition.get("confidence").getAsString());
								if (!assumed)
									continue;
								tier0Assumed++;
								String id = item.get("id").getAsString();
								String status = classify("item|" + id);
								if (!status.equals("renewable") && !status.equals("assumed_renewable")) {
									tier0AssumedNotRenewable++;
									if (tier0List.size() < 60)
										tier0List.add(id + " [" + status + "]");
								}
							}
						var countsJson = new com.google.gson.JsonObject();
						for (var entry : counts.entrySet())
							countsJson.addProperty(entry.getKey(), entry.getValue());
						result.add("counts", countsJson);
						var notesJson = new com.google.gson.JsonObject();
						for (var entry : notes.entrySet())
							notesJson.addProperty(entry.getKey(), entry.getValue());
						result.add("notes", notesJson);
						// Adapter counters: handled, rejected and unrecognized routes for the new mechanisms.
						var adaptersJson = new com.google.gson.JsonObject();
						for (var entry : adapterCounts.entrySet())
							adaptersJson.addProperty(entry.getKey(), entry.getValue());
						result.add("adapters", adaptersJson);
						var unrecognizedJson = new com.google.gson.JsonObject();
						for (var entry : unrecognizedCounts.entrySet()) {
							var record = new com.google.gson.JsonObject();
							record.addProperty("count", entry.getValue());
							var samples = new com.google.gson.JsonArray();
							for (String sample : unrecognizedSamples.getOrDefault(entry.getKey(), java.util.List.of()))
								samples.add(sample);
							record.add("samples", samples);
							unrecognizedJson.add(entry.getKey(), record);
						}
						result.add("unrecognized", unrecognizedJson);
						// Tools used as loot conditions: always an assumption, with the real chain status of the chosen tool shown next to it.
						var toolsJson = new com.google.gson.JsonArray();
						int toolsAssumed = 0, toolsChainRenewable = 0;
						for (String toolNode : registeredTools) {
							var toolRecord = new com.google.gson.JsonObject();
							toolRecord.addProperty("tool", toolNode.substring(5));
							String by = toolSatisfiedBy.get(toolNode);
							if (by != null && value.getOrDefault(toolNode, 0) >= 2) {
								var route = bestRoute.get(toolNode);
								if (route != null)
									for (String alternative : route.slots.get(0))
										if (value.getOrDefault(alternative, 0) > value.getOrDefault("item|" + by, 0))
											by = alternative.substring(5);
								toolsAssumed++;
								String chain = classify("item|" + by);
								if (chain.equals("renewable"))
									toolsChainRenewable++;
								toolRecord.addProperty("status", "assumed");
								toolRecord.addProperty("satisfied_by", by);
								toolRecord.addProperty("tool_item_chain_status", chain);
							} else {
								toolRecord.addProperty("status", "unverified");
								toolRecord.addProperty("reason", by == null ? "no matching item with a complete readable recipe and a durability bar" : "replacement chain lacks repeatability evidence");
							}
							if (toolsJson.size() < 60)
								toolsJson.add(toolRecord);
						}
						result.add("tools", toolsJson);
						var selfTestJson = new com.google.gson.JsonObject();
						selfTestJson.addProperty("executed_during_generation", true);
						selfTestJson.addProperty("passed", selfTestsPassed);
						selfTestJson.addProperty("failed", selfTestsFailed);
						selfTestJson.add("cases", selfTests);
						result.add("loot_self_test", selfTestJson);
						var mechanismJson = new com.google.gson.JsonObject();
						mechanismJson.addProperty("file", "config/omnichef/RenewabilityMechanisms.json");
						mechanismJson.addProperty("accepted", adapterCounts.getOrDefault("declared_mechanism_routes", 0));
						mechanismJson.addProperty("not_applicable_missing_mod", adapterCounts.getOrDefault("declared_mechanism_not_applicable", 0));
						mechanismJson.addProperty("rejected", adapterCounts.getOrDefault("declared_mechanism_rejected", 0));
						var reasonJson = new com.google.gson.JsonObject();
						for (var reason : mechanismReasons.entrySet())
							reasonJson.addProperty(reason.getKey(), reason.getValue());
						mechanismJson.add("by_reason", reasonJson);
						var acceptedRows = new com.google.gson.JsonArray();
						for (var mechanism : mechanismRecords) {
							if (acceptedRows.size() >= 40)
								break;
							acceptedRows.add(mechanismReport(mechanism));
						}
						mechanismJson.add("accepted_rows", acceptedRows);
						mechanismJson.add("rejected_rows", mechanismRejected);
						result.add("declared_mechanisms", mechanismJson);
						var fishingJson = new com.google.gson.JsonArray();
						for (String fishingRoot : fishingRoots)
							fishingJson.add(fishingRoot);
						result.add("fishing_roots", fishingJson);
						// Recipe types whose recipes were skipped, empty or incomplete: where an adapter or more data is needed.
						var censusRows = new java.util.ArrayList<java.util.Map.Entry<String, int[]>>(recipeCensus.entrySet());
						censusRows.removeIf(row -> row.getValue()[1] + row.getValue()[2] + row.getValue()[4] == 0);
						censusRows.sort((a, b) -> Integer.compare(b.getValue()[1] + b.getValue()[4], a.getValue()[1] + a.getValue()[4]));
						var censusJson = new com.google.gson.JsonArray();
						StringBuilder topCensus = new StringBuilder();
						for (int i = 0; i < Math.min(30, censusRows.size()); i++) {
							var row = censusRows.get(i);
							var record = new com.google.gson.JsonObject();
							record.addProperty("recipe_type", row.getKey());
							record.addProperty("recipes", row.getValue()[0]);
							record.addProperty("empty_standard_result", row.getValue()[1]);
							record.addProperty("no_readable_inputs", row.getValue()[2]);
							record.addProperty("routes_built", row.getValue()[3]);
							record.addProperty("incomplete_routes", row.getValue()[4]);
							censusJson.add(record);
							if (i < 8) {
								if (topCensus.length() > 0)
									topCensus.append(", ");
								topCensus.append(row.getKey()).append('=').append(row.getValue()[1]).append('/').append(row.getValue()[4]);
							}
						}
						result.add("recipe_census", censusJson);
						var mobClasses = new com.google.gson.JsonObject();
						mobClasses.addProperty("biome_spawn_entities", acquisitionSources.biomeSpawnEntities.size());
						mobClasses.addProperty("structure_spawn_entities", structureSpawnEntities.size());
						mobClasses.addProperty("raid_member_entities", raidEntities.size());
						mobClasses.addProperty("entity_loot_tables_scanned", mobEntitiesScanned);
						mobClasses.addProperty("structures_scanned", structuresScanned);
						result.add("mob_evidence", mobClasses);
						var skippedJson = new com.google.gson.JsonObject();
						skippedJson.addProperty("total", skippedTotal);
						var skippedReasons = new com.google.gson.JsonObject();
						for (var reason : skippedByReason.entrySet())
							skippedReasons.addProperty(reason.getKey(), reason.getValue());
						skippedJson.add("by_reason", skippedReasons);
						skippedJson.add("samples", skippedSources);
						skippedJson.addProperty("policy", "A skipped source contributes no evidence and is never marked renewable. Items that depended on it keep the status the remaining evidence supports.");
						result.add("skipped_sources", skippedJson);
						result.add("self_replanting_crops", selfReplanting);
						result.add("root_blockers", rootArray);
						result.addProperty("tier0_ingredients", tier0Total);
						result.addProperty("tier0_assumed_base", tier0Assumed);
						result.add("tier0_without_evidence", tier0List);
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
						lines.add("ODNAWIALNOSC ZRODEL v38.4 — filtrowanie zrodel przed koncowym tierowaniem");
						lines.add("Status analizy: " + result.get("status").getAsString() + "; przebiegi: " + passes + "; wezlow z trasami: " + routedNodes.size());
						lines.add("Trasy receptur z kompletnymi wejsciami: zbudowane=" + adapterCounts.getOrDefault("recipe_routes_built", 0) + ", niekompletne=" + adapterCounts.getOrDefault("recipe_routes_incomplete", 0)
								+ ", trasy grafu zastapione=" + adapterCounts.getOrDefault("graph_recipe_routes_replaced", 0) + ", trasy grafu zachowane=" + adapterCounts.getOrDefault("graph_routes_kept", 0));
						lines.add("Plyny: wejscia zmapowane=" + adapterCounts.getOrDefault("fluid_input_mapped", 0) + ", niezmapowane=" + adapterCounts.getOrDefault("fluid_input_unmapped", 0) + ", trasy wyjsc plynnych="
								+ adapterCounts.getOrDefault("fluid_output_routes", 0) + "; adapter refleksyjny: wynik=" + adapterCounts.getOrDefault("reflective_result_used", 0) + ", wejscia="
								+ adapterCounts.getOrDefault("reflective_ingredients_used", 0));
						lines.add("Dropy mobow z warunkami: trasy=" + adapterCounts.getOrDefault("mob_drop_routes", 0) + "; lowienie: trasy=" + adapterCounts.getOrDefault("fishing_routes", 0) + "; loot modifiers: wczytane="
								+ adapterCounts.getOrDefault("glm_entries_read", 0) + ", zastosowane=" + adapterCounts.getOrDefault("glm_entries_applied", 0) + ", trasy=" + adapterCounts.getOrDefault("loot_modifier_routes", 0)
								+ "; narzedzia: spelnione=" + adapterCounts.getOrDefault("tool_nodes_satisfied", 0) + ", niezweryfikowane=" + adapterCounts.getOrDefault("tool_nodes_unverified", 0));
						StringBuilder failedTests = new StringBuilder();
						for (var testRecord : selfTests)
							if (!testRecord.getAsJsonObject().get("passed").getAsBoolean())
								failedTests.append(failedTests.length() > 0 ? ", " : "").append(testRecord.getAsJsonObject().get("case").getAsString());
						lines.add("Autotesty loot i mechanizmow: zaliczone=" + selfTestsPassed + ", niezaliczone=" + selfTestsFailed + (failedTests.length() > 0 ? " [" + failedTests + "]" : ""));
						lines.add("Adaptery rodzin: imported_kitchen_routes=" + adapterCounts.getOrDefault("imported_kitchen_routes", 0) + ", dynamic_fillet_routes=" + adapterCounts.getOrDefault("dynamic_fillet_routes", 0)
								+ ", component_water_input_accepted=" + adapterCounts.getOrDefault("component_water_input_accepted", 0) + ", minecolonies_regrowing_routes=" + adapterCounts.getOrDefault("minecolonies_regrowing_routes", 0)
								+ ", croptopia_regrowing_routes=" + adapterCounts.getOrDefault("croptopia_regrowing_routes", 0));
						lines.add("Korzenie lowienia: " + fishingRoots.size() + " (modowe, osiagalnosc niezweryfikowana, zalozenie: " + fishingRoots.stream().filter(root -> root.contains("cap=2")).count()
								+ "); mechanizmy deklarowane (plik): przyjete=" + adapterCounts.getOrDefault("declared_mechanism_routes", 0) + ", niedotyczy(brak moda)=" + adapterCounts.getOrDefault("declared_mechanism_not_applicable", 0)
								+ ", odrzucone=" + adapterCounts.getOrDefault("declared_mechanism_rejected", 0) + " " + mechanismReasons + (notes.containsKey("mechanism_file_absent") ? " (brak pliku)" : ""));
						lines.add("Narzedzia w warunkach loot (zawsze zalozenie): zalozone=" + toolsAssumed + " (lancuch narzedzia odnawialny=" + toolsChainRenewable + "), niezweryfikowane=" + adapterCounts.getOrDefault("tool_nodes_unverified", 0));
						lines.add("Odrzucone lub nierozpoznane warunki/funkcje/ksztalty (rodzaje): " + unrecognizedCounts.size() + "; lacznie: " + unrecognizedCounts.values().stream().mapToInt(Integer::intValue).sum()
								+ " (lista: technical.diagnostics.renewability_audit.unrecognized)");
						lines.add("Samopodtrzymujace sie rosliny (wg rodzaju): " + selfReplanting.size() + " " + growthKinds + "; odrzucone dropy z nierozwiazanym warunkiem: "
								+ notes.getOrDefault("harvest_drop_records_rejected_unresolved_condition", 0));
						lines.add("Moby z dowodem spawnu: biomy=" + acquisitionSources.biomeSpawnEntities.size() + ", struktury(zalozone)=" + structureSpawnEntities.size() + ", rajdy(zalozone)=" + raidEntities.size() + "; przeskanowane struktury: "
								+ structuresScanned + "; tabele lootu mobow: " + mobEntitiesScanned);
						lines.add("Pominiete zrodla diagnostyczne (nie wnosza dowodu, nigdy nie sa renewable): " + skippedTotal + " " + skippedByReason);
						lines.add("Ingredients, wszystkie: " + countsLine(counts, "ingredients all "));
						lines.add("Ingredients, do podawania: " + countsLine(counts, "ingredients servable "));
						lines.add("Food, wszystkie: " + countsLine(counts, "foods all "));
						lines.add("Food, do podawania: " + countsLine(counts, "foods servable "));
						lines.add("Tier 0 Ingredients: " + tier0Total + "; z zalozenia (assumed_base + confidence assumed): " + tier0Assumed + "; z tego bez potwierdzonej odnawialnosci: " + tier0AssumedNotRenewable
								+ " (lista: tier0_without_evidence)");
						lines.add("Glowni blokerzy potraw (korzenie, liczba potraw; sciezki w root_blockers): " + (topRoots.length() == 0 ? "brak" : topRoots.toString()));
						lines.add("Typy receptur z pustym wynikiem/niekompletnymi wejsciami (top 8, puste/niekompletne): " + (topCensus.length() == 0 ? "brak" : topCensus.toString()));
						lines.add("Pozycje do podawania ze statusem unknown wg moda (top 15): " + (top.length() == 0 ? "brak" : top.toString()));
						lines.add(
								"unverified_source = brak dowodu powtarzalnosci; limited = znane pierwsze zrodlo bez udowodnionej powtarzalnosci; unknown = za malo danych. Do katalogu trafia tylko odnawialny przedmiot z pierwszym i regularnym dostepem.");
						result.add("summary_lines", lines);
						return result;
					}

					void attach(com.google.gson.JsonObject database) {
						long started = System.nanoTime();
						run(database);
						var report = report(database);
						report.addProperty("elapsed_ms", (System.nanoTime() - started) / 1000000L);
						database.getAsJsonObject("technical").getAsJsonObject("diagnostics").add("renewability_audit", report);
						net.mcreator.omnichef.OmnichefMod.LOGGER.info("Omnichef renewability diagnostics v36.2: {} passes, status {}, {} recipe routes, {} self-replanting growth blocks, {} skipped sources, {} ms. Diagnostic only.", passes,
								report.get("status").getAsString(), adapterCounts.getOrDefault("recipe_routes_built", 0), selfReplanting.size(), skippedTotal, report.get("elapsed_ms"));
					}

					java.util.Set<String> exclusionCandidates(com.google.gson.JsonObject database) {
						var denied = new java.util.TreeSet<String>();
						for (String id : itemsById.keySet())
							if (!eligible("item|" + id))
								denied.add(id);
						for (String id : completeRecipeInputs.fluidProduction.keySet())
							if (!eligible(id))
								denied.add(id);
						return denied;
					}

					boolean eligible(String node) {
						return value.getOrDefault(node, 0) >= 2 && firstAccess.containsKey(node) && repeatAccess.containsKey(node);
					}

					java.util.Map<String, java.util.Set<String>> approvedRecipeIndex() {
						var result = new java.util.HashMap<String, java.util.Set<String>>();
						for (var entry : renewRoutes.entrySet()) {
							String key = entry.getKey();
							if (!eligible(key))
								continue;
							String id = key.startsWith("item|") ? key.substring(5) : key;
							for (var route : entry.getValue())
								if (routeValue(route) >= 2 && routeAccess(route, repeatAccess, true) < 99)
									result.computeIfAbsent(id, ignored -> new java.util.HashSet<>()).add(route.system + "|" + route.recipeId);
						}
						return result;
					}

					java.util.Set<String> independentIngredientSourceIndex() {
						var result = new java.util.HashSet<String>();
						for (String node : directRepeatAccess.keySet())
							if (node.startsWith("item|") && eligible(node))
								result.add(node.substring(5));
						var independentMethods = java.util.Set.of("omnichef:mob_drop", "omnichef:fishing", "omnichef:livestock", "omnichef:natural_harvest", "omnichef:minecolonies_regrowth", "omnichef:croptopia_regrowth",
								"productivetrees:grown_leaves", "productivetrees:sapling_return");
						for (var entry : renewRoutes.entrySet()) {
							String node = entry.getKey();
							if (!node.startsWith("item|") || !eligible(node))
								continue;
							for (var route : entry.getValue()) {
								if (independentMethods.contains(route.system) && routeValue(route) >= 2 && routeAccess(route, repeatAccess, true) < 99) {
									result.add(node.substring(5));
									break;
								}
							}
						}
						return result;
					}
				}
				// The exclusion candidate is deliberately written to a separate file until gameplay/save migration tests pass.
				// No diagnostic-only run changes any catalog entry, score, tier, or discovery ID.
				class RenewabilityPolicyPreview {
					boolean publish = true;

					boolean requested() throws java.io.IOException {
						var path = net.neoforged.fml.loading.FMLPaths.GAMEDIR.get().resolve("config/omnichef/RenewabilityPolicy.json");
						if (!java.nio.file.Files.isRegularFile(path))
							return true;
						try (var reader = java.nio.file.Files.newBufferedReader(path)) {
							var config = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
							publish = !config.has("v37_preview_only") || !config.get("v37_preview_only").getAsBoolean();
							return true;
						}
					}

					java.util.Map<String, String> owners(com.google.gson.JsonObject database) {
						var owners = new java.util.TreeMap<String, String>();
						for (var entry : database.getAsJsonObject("discovery_variants").entrySet()) {
							owners.put(entry.getKey(), entry.getKey());
							for (var member : entry.getValue().getAsJsonArray())
								owners.put(member.getAsJsonObject().get("id").getAsString(), entry.getKey());
						}
						for (String group : java.util.List.of("ingredients", "foods"))
							for (var tier : database.getAsJsonObject(group).getAsJsonObject("tiers").entrySet())
								for (var item : tier.getValue().getAsJsonArray()) {
									String id = item.getAsJsonObject().get("id").getAsString();
									owners.put(id, id);
								}
						return owners;
					}

					com.google.gson.JsonObject write(com.google.gson.JsonObject baseline, RenewabilityAnalysis audit, java.nio.file.Path activePath) throws java.io.IOException {
						var diagnostics = baseline.getAsJsonObject("technical").getAsJsonObject("diagnostics");
						var status = new com.google.gson.JsonObject();
						diagnostics.add("renewability_policy_preview", status);
						status.addProperty("requested", "exclude");
						status.addProperty("active_catalog_unchanged", true);
						if (!audit.converged || audit.selfTestsFailed != 0) {
							throw new IllegalStateException("v37 audit did not converge or its self-tests failed");
						}
						var denied = audit.exclusionCandidates(baseline);
						com.google.gson.JsonObject filtered = null;
						var approved = audit.approvedRecipeIndex();
						var independentSources = audit.independentIngredientSourceIndex();
						for (int pass = 0; pass < 8; pass++) {
							var builder = new FoodCatalogBuilder(denied);
							builder.strictPolicy = true;
							builder.firstAccess = audit.firstAccess;
							builder.repeatAccess = audit.repeatAccess;
							builder.approvedRecipes = approved;
							builder.harvestPlanting = audit.harvestPlanting;
							builder.independentIngredientSources = independentSources;
							builder.sourceRarity = audit.sourceRarity;
							builder.evidenceMinimumTiers = audit.evidenceMinimumTiers;
							var renewablePlants = new java.util.HashSet<String>();
							for (var entry : audit.plants.drops.entrySet())
								if (audit.directRepeatAccess.containsKey("item|" + entry.getKey()))
									for (var raw : entry.getValue()) {
										var drop = raw.getAsJsonObject();
										String block = audit.string(drop, "plant_block");
										if (audit.plants.spreading.contains(block) && audit.locationFloor(block) < 99 && audit.acceptableHarvestDrop(drop, block)) {
											renewablePlants.add(entry.getKey());
											break;
										}
									}
							builder.renewablePlantInputs = renewablePlants;
							filtered = builder.build();
							var unresolved = filtered.getAsJsonObject("technical").getAsJsonObject("diagnostics").getAsJsonArray("unresolved_foods");
							boolean changed = false;
							for (var element : unresolved)
								changed |= denied.add(element.getAsString());
							if (!changed)
								break;
							if (pass == 7)
								throw new IllegalStateException("v37 catalog pruning did not converge");
						}
						var oldOwners = owners(baseline);
						var newOwners = owners(filtered);
						var issues = new com.google.gson.JsonArray();
						var reassigned = new com.google.gson.JsonObject();
						for (var entry : newOwners.entrySet()) {
							if (denied.contains(entry.getKey()))
								issues.add("denied_id_retained:" + entry.getKey());
							if (!entry.getValue().equals(oldOwners.get(entry.getKey())))
								reassigned.addProperty(entry.getKey(), entry.getValue());
							if (!audit.eligible("item|" + entry.getKey()))
								issues.add("unproven_discovery_id:" + entry.getKey());
						}
						status.add("reassigned_discovery_ids", reassigned);
						var graph = filtered.getAsJsonObject("technical").getAsJsonObject("recipe_nodes");
						// Validation is read-only. Never alter slots after computing tier/ranking.
						for (var entry : graph.entrySet()) {
							var graphNode = entry.getValue().getAsJsonObject();
							if (graphNode.has("unlock_requires_any")) {
								var parents = graphNode.getAsJsonArray("unlock_requires_any");
								boolean possible = false;
								for (var parent : parents) {
									String id = parent.getAsString();
									if (id.equals(entry.getKey()) || !graph.has(id)) {
										issues.add("invalid_unlock_parent:" + entry.getKey() + ":" + id);
										continue;
									}
									var p = graph.getAsJsonObject(id);
									if (!"INGREDIENT".equals(p.get("kind").getAsString()) || !p.has("tier") || !graphNode.has("tier")) {
										issues.add("invalid_unlock_parent:" + entry.getKey() + ":" + id);
										continue;
									}
									if (p.get("tier").getAsInt() <= graphNode.get("tier").getAsInt())
										possible = true;
								}
								if (!possible)
									issues.add("no_earlier_unlock_parent:" + entry.getKey());
							}
							for (var recipe : graphNode.getAsJsonArray("recipes")) {
								var row = recipe.getAsJsonObject();
								for (var slot : row.getAsJsonArray("ingredientSlots")) {
									if (slot.getAsJsonArray().isEmpty())
										issues.add("empty_recipe_slot:" + entry.getKey());
									for (var item : slot.getAsJsonArray()) {
										String id = item.getAsString();
										if (denied.contains(id) || !graph.has(id) || "BLOCKED".equals(graph.getAsJsonObject(id).get("kind").getAsString()))
											issues.add("invalid_recipe_alternative:" + entry.getKey() + ":" + id);
									}
								}
							}
							if ("FOOD".equals(graphNode.get("kind").getAsString()) && graphNode.getAsJsonArray("recipes").isEmpty())
								issues.add("food_without_usable_route:" + entry.getKey());
						}
						for (String role : java.util.List.of("ingredients", "foods"))
							for (var tier : filtered.getAsJsonObject(role).getAsJsonObject("tiers").entrySet())
								for (var item : tier.getValue().getAsJsonArray()) {
									var row = item.getAsJsonObject();
									String id = row.get("id").getAsString();
									if (!graph.has(id))
										issues.add("catalog_id_missing_from_graph:" + id);
									else if (!row.getAsJsonArray("recipes").equals(graph.getAsJsonObject(id).getAsJsonArray("recipes")))
										issues.add("recipe_graph_mismatch:" + id);
								}
						if (filtered.getAsJsonArray("starter_options").size() < 3 || filtered.getAsJsonArray("starter_safe_options").isEmpty())
							issues.add("insufficient_safe_starters");
						for (var starter : filtered.getAsJsonArray("starter_options"))
							if (graph.getAsJsonObject(starter.getAsString()).has("unlock_requires_any"))
								issues.add("dependent_starter:" + starter.getAsString());
						if (filtered.get("food_count").getAsInt() == 0)
							issues.add("empty_food_catalog");
						for (String role : java.util.List.of("ingredients", "foods"))
							for (var tier : filtered.getAsJsonObject(role).getAsJsonObject("tiers").entrySet())
								if (tier.getValue().getAsJsonArray().isEmpty())
									issues.add("empty_tier:" + role + ":" + tier.getKey());
						var removed = new com.google.gson.JsonArray();
						for (String id : oldOwners.keySet())
							if (!newOwners.containsKey(id))
								removed.add(id);
						status.add("removed_physical_ids", removed);
						var removedEvidence = new com.google.gson.JsonObject();
						for (String id : oldOwners.keySet())
							if (!newOwners.containsKey(id)) {
								var item = new com.google.gson.JsonObject();
								String node = "item|" + id;
								item.addProperty("status", audit.classify(node));
								item.addProperty("first_access", audit.firstAccess.getOrDefault(node, -1));
								item.addProperty("recurring_access", audit.repeatAccess.getOrDefault(node, -1));
								item.addProperty("source_rarity", audit.sourceRarity.getOrDefault(id, 0.0));
								item.addProperty("evidence_minimum_tier", audit.evidenceMinimumTiers.getOrDefault(id, 0));
								item.addProperty("reason",
										!audit.eligible(node)
												? !audit.firstAccess.containsKey(node) ? "first_access_unproven" : !audit.repeatAccess.containsKey(node) ? "recurring_access_unproven" : "renewability_unproven"
												: denied.contains(id) ? "no_viable_discovery_recipe_after_filter" : "classification_or_no_retained_culinary_use");
								removedEvidence.add(id, item);
							}
						status.add("removed_evidence", removedEvidence);
						status.addProperty("denied_graph_nodes", denied.size());
						status.add("validation_issues", issues);
						boolean canPublish = publish && issues.isEmpty();
						status.addProperty("status", issues.isEmpty() ? (canPublish ? "exclude_requested_and_structural_checks_passed" : "preview_passed_structural_checks_pending_gameplay") : "preview_failed_structural_checks");
						var filteredDiagnostics = filtered.getAsJsonObject("technical").getAsJsonObject("diagnostics");
						filteredDiagnostics.add("renewability_audit", audit.report(filtered));
						if (diagnostics.has("plant_source_audit"))
							filteredDiagnostics.add("plant_source_audit", diagnostics.get("plant_source_audit").deepCopy());
						filteredDiagnostics.add("renewability_policy_preview", status.deepCopy());
						writeSnapshot(filtered, activePath.resolveSibling("FoodDatabase_exclusion_preview.json"));
						writeSnapshot(baseline, activePath.resolveSibling("FoodDatabase_full_diagnostic.json"));
						if (!issues.isEmpty())
							throw new IllegalStateException("v37 filtered catalog failed validation: " + issues);
						if (canPublish) {
							// The owner must set publish_excluded_catalog only after both fresh-world and save-copy gameplay tests.
							filteredDiagnostics.getAsJsonObject("renewability_policy_preview").addProperty("active_catalog_unchanged", false);
							return filtered;
						}
						return baseline;
					}

					void writeSnapshot(com.google.gson.JsonObject database, java.nio.file.Path previewPath) throws java.io.IOException {
						var activePath = previewPath;
						var temp = java.nio.file.Files.createTempFile(activePath.getParent(), "FoodDatabase-exclusion-", ".tmp");
						try {
							java.nio.file.Files.writeString(temp, new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(database), java.nio.charset.StandardCharsets.UTF_8);
							try {
								java.nio.file.Files.move(temp, previewPath, java.nio.file.StandardCopyOption.ATOMIC_MOVE, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
							} catch (java.nio.file.AtomicMoveNotSupportedException failure) {
								java.nio.file.Files.move(temp, previewPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
							}
						} finally {
							java.nio.file.Files.deleteIfExists(temp);
						}
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
					var audit = new RenewabilityAnalysis(plantDiagnostics.index);
					audit.attach(splitPreview);
					var policy = new RenewabilityPolicyPreview();
					if (policy.requested()) {
						java.nio.file.Files.createDirectories(splitPreviewFile.getParent());
						splitPreview = policy.write(splitPreview, audit, splitPreviewFile);
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