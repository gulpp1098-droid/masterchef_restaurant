package net.mcreator.omnichef.procedures;

import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;

import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

@EventBusSubscriber
public class CreatingFoodListJsonProcedure {
	@SubscribeEvent
	public static void onWorldLoad(net.neoforged.neoforge.event.level.LevelEvent.Load event) {
		execute(event, event.getLevel());
	}

	public static void execute(LevelAccessor world) {
		execute(null, world);
	}

	private static void execute(@Nullable Event event, LevelAccessor world) {
		com.google.gson.JsonObject FoodDatabaseObject = new com.google.gson.JsonObject();
		File FoodDatabase = new File("");
		if (!world.isClientSide()) {// =====================================================
			// SERVER SIDE ONLY
			// =====================================================
			if (!(world instanceof net.minecraft.server.level.ServerLevel level)) {
				return;
			}
			if (!level.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
				return;
			}
			// =====================================================
			// SETTINGS
			// =====================================================
			java.util.Set<String> excludedFoods = new java.util.HashSet<>();
			excludedFoods.add("minecraft:ominous_bottle");
			excludedFoods.add("minecraft:enchanted_golden_apple");
			excludedFoods.add("minecraft:suspicious_stew");
			// Editable configuration created by MCreator block procedures.
			com.google.gson.JsonObject loadedSpecialFoodBonuses = CreateSpecialFoodBonusMapProcedure.execute();
			final com.google.gson.JsonObject specialFoodBonuses = loadedSpecialFoodBonuses != null ? loadedSpecialFoodBonuses : new com.google.gson.JsonObject();
			com.google.gson.JsonObject loadedFoodCategoryBonuses = CreateFoodCategoryBonusMapProcedure.execute();
			final com.google.gson.JsonObject foodCategoryBonuses = loadedFoodCategoryBonuses != null ? loadedFoodCategoryBonuses : new com.google.gson.JsonObject();
			java.util.List<com.google.gson.JsonObject> allFoods = new java.util.ArrayList<>();
			com.google.gson.JsonObject foodDatabase = new com.google.gson.JsonObject();
			com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
			com.google.gson.JsonArray disabledFoodsArray = new com.google.gson.JsonArray();
			final double MODDED_FOOD_BONUS = 2.0;
			// =====================================================
			// ITEMS BY ID
			// =====================================================
			java.util.Map<String, net.minecraft.world.item.Item> itemsById = new java.util.HashMap<>();
			for (net.minecraft.world.item.Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
				var itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
				if (itemId != null) {
					itemsById.put(itemId.toString(), item);
				}
			}
			// =====================================================
			// ALL RECIPES GROUPED BY OUTPUT
			// =====================================================
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
			// =====================================================
			// RECURSIVE SCORE RESOLVER
			// =====================================================
			class FoodScoreResolver {
				static final int MAX_DEPTH = 12;
				static final double INVALID = Double.POSITIVE_INFINITY;
				final java.util.Map<String, Double> scoreCache = new java.util.HashMap<>();
				final java.util.Map<String, Double> recipeScoreCache = new java.util.HashMap<>();
				final java.util.Map<String, net.minecraft.world.item.crafting.RecipeHolder<?>> chosenRecipeCache = new java.util.HashMap<>();
				final java.util.Map<String, java.util.List<String>> chosenIngredientsCache = new java.util.HashMap<>();
				final java.util.Map<String, java.util.List<RecipeAnalysis>> validRecipesCache = new java.util.HashMap<>();
				final java.util.Map<String, Double> categoryBonusCache = new java.util.HashMap<>();
				final java.util.Map<String, java.util.List<String>> categoriesCache = new java.util.HashMap<>();
				final java.util.Set<String> resolving = new java.util.HashSet<>();
				int blockedPathCounter = 0;

				final class RecipeAnalysis {
					final net.minecraft.world.item.crafting.RecipeHolder<?> recipeHolder;
					final String method;
					final int outputCount;
					final double preparePoints;
					final double ingredientScore;
					final double recipeScore;
					final java.util.List<java.util.List<String>> ingredientSlots;
					final java.util.List<String> selectedIngredients;

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
						recipeObject.addProperty("outputCount", outputCount);
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

				double resolve(String itemName) {
					return resolve(itemName, 0);
				}

				double roundScore(double value) {
					if (!Double.isFinite(value))
						return value;
					return Math.round(value * 10.0) / 10.0;
				}

				double resolve(String itemName, int depth) {
					Double cached = scoreCache.get(itemName);
					if (cached != null)
						return cached;
					if (itemName == null)
						return INVALID;
					if (depth >= MAX_DEPTH || resolving.contains(itemName)) {
						blockedPathCounter++;
						return INVALID;
					}
					int blockedPathsBeforeResolution = blockedPathCounter;
					net.minecraft.world.item.Item item = itemsById.get(itemName);
					if (item == null)
						return INVALID;
					net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
					boolean isFood = item.getFoodProperties(stack, null) != null;
					double qualityScore = isFood ? getQualityScore(itemName) : 0.0;
					java.util.List<net.minecraft.world.item.crafting.RecipeHolder<?>> recipes = recipesByOutput.get(itemName);
					java.util.List<RecipeAnalysis> validRecipes = new java.util.ArrayList<>();
					RecipeAnalysis bestRecipe = null;
					resolving.add(itemName);
					try {
						if (recipes != null) {
							for (net.minecraft.world.item.crafting.RecipeHolder<?> recipeHolder : recipes) {
								RecipeAnalysis analysis = analyzeRecipe(recipeHolder, itemName, depth + 1);
								if (analysis == null || !Double.isFinite(analysis.recipeScore))
									continue;
								validRecipes.add(analysis);
								if (bestRecipe == null || compareRecipes(analysis, bestRecipe) < 0) {
									bestRecipe = analysis;
								}
							}
						}
					} finally {
						resolving.remove(itemName);
					}
					validRecipes.sort((first, second) -> compareRecipes(first, second));
					double appliedAcquisitionBonus = getAppliedAcquisitionBonus(itemName);
					double productionScore = bestRecipe == null ? 1.0 : bestRecipe.recipeScore;
					// Raw items keep the base value of 1. Crafted bulk ingredients may
					// fall below 1 per item, for example one ninth of a gold ingot.
					double minimumScore = bestRecipe == null ? 1.0 : 0.1;
					double resolvedScore = roundScore(Math.max(minimumScore, productionScore + qualityScore + appliedAcquisitionBonus));
					// A nested result calculated while one of its paths was blocked by
					// recursion depends on the current parent chain and must not become
					// the global cached value. Root food results are always retained.
					boolean safeToCache = depth == 0 || blockedPathsBeforeResolution == blockedPathCounter;
					if (safeToCache) {
						scoreCache.put(itemName, resolvedScore);
						validRecipesCache.put(itemName, validRecipes);
						if (bestRecipe != null) {
							recipeScoreCache.put(itemName, bestRecipe.recipeScore);
							chosenRecipeCache.put(itemName, bestRecipe.recipeHolder);
							chosenIngredientsCache.put(itemName, new java.util.ArrayList<>(bestRecipe.selectedIngredients));
						} else {
							recipeScoreCache.put(itemName, 0.0);
							chosenIngredientsCache.put(itemName, new java.util.ArrayList<>());
						}
					}
					return resolvedScore;
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

				RecipeAnalysis analyzeRecipe(net.minecraft.world.item.crafting.RecipeHolder<?> recipeHolder, String outputItemName, int depth) {
					if (recipeHolder == null || depth >= MAX_DEPTH)
						return null;
					net.minecraft.world.item.crafting.Recipe<?> recipe = recipeHolder.value();
					java.util.List<net.minecraft.world.item.crafting.Ingredient> ingredients = recipe.getIngredients();
					if (ingredients == null || ingredients.isEmpty())
						return null;
					net.minecraft.world.item.ItemStack result = recipe.getResultItem(level.registryAccess());
					if (result.isEmpty())
						return null;
					int outputCount = Math.max(1, result.getCount());
					double ingredientTotal = 0.0;
					boolean materialUnpackingRecipe = ingredients.size() == 1 && outputCount > 1;
					java.util.List<java.util.List<String>> ingredientSlots = new java.util.ArrayList<>();
					java.util.List<String> selectedIngredients = new java.util.ArrayList<>();
					for (net.minecraft.world.item.crafting.Ingredient ingredient : ingredients) {
						net.minecraft.world.item.ItemStack[] alternatives = ingredient.getItems();
						if (alternatives == null || alternatives.length == 0)
							return null;
						double cheapestAlternativeScore = INVALID;
						String cheapestAlternativeName = null;
						java.util.Set<String> validAlternativeNames = new java.util.TreeSet<>();
						for (net.minecraft.world.item.ItemStack alternative : alternatives) {
							if (alternative == null || alternative.isEmpty())
								continue;
							var alternativeId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(alternative.getItem());
							if (alternativeId == null)
								continue;
							String alternativeName = alternativeId.toString();
							// A reversible unpacking recipe is allowed only when the packed
							// material has an independent production route. This permits
							// gold ingot -> nuggets, but rejects hay block -> wheat loops.
							boolean reversePackingRecipe = outputCount > 1 && hasRecipeUsingItem(alternativeName, outputItemName);
							if (reversePackingRecipe && !hasIndependentRecipe(alternativeName, outputItemName))
								continue;
							double alternativeScore = resolve(alternativeName, depth + 1);
							if (!Double.isFinite(alternativeScore))
								continue;
							validAlternativeNames.add(alternativeName);
							if (alternativeScore < cheapestAlternativeScore || (Double.compare(alternativeScore, cheapestAlternativeScore) == 0 && (cheapestAlternativeName == null || alternativeName.compareTo(cheapestAlternativeName) < 0))) {
								cheapestAlternativeScore = alternativeScore;
								cheapestAlternativeName = alternativeName;
							}
						}
						if (!Double.isFinite(cheapestAlternativeScore) || cheapestAlternativeName == null || validAlternativeNames.isEmpty())
							return null;
						// Repeated ingredient slots intentionally count more than once.
						ingredientTotal += cheapestAlternativeScore;
						ingredientSlots.add(new java.util.ArrayList<>(validAlternativeNames));
						selectedIngredients.add(cheapestAlternativeName);
						materialUnpackingRecipe = materialUnpackingRecipe && hasRecipeUsingItem(cheapestAlternativeName, outputItemName) && hasIndependentRecipe(cheapestAlternativeName, outputItemName);
					}
					String method = recipe.getType().toString();
					ingredientTotal = roundScore(ingredientTotal);
					// Pure material conversion is not a culinary preparation step.
					double preparePoints = materialUnpackingRecipe ? 0.0 : getPreparePoints(method);
					double recipeScore = roundScore((ingredientTotal + preparePoints) / outputCount);
					return new RecipeAnalysis(recipeHolder, method, outputCount, preparePoints, ingredientTotal, recipeScore, ingredientSlots, selectedIngredients);
				}

				boolean hasRecipeUsingItem(String producedItemName, String requiredItemName) {
					java.util.List<net.minecraft.world.item.crafting.RecipeHolder<?>> reverseRecipes = recipesByOutput.get(producedItemName);
					if (reverseRecipes == null)
						return false;
					for (net.minecraft.world.item.crafting.RecipeHolder<?> reverseRecipeHolder : reverseRecipes) {
						for (net.minecraft.world.item.crafting.Ingredient reverseIngredient : reverseRecipeHolder.value().getIngredients()) {
							for (net.minecraft.world.item.ItemStack reverseAlternative : reverseIngredient.getItems()) {
								if (reverseAlternative == null || reverseAlternative.isEmpty())
									continue;
								var reverseId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(reverseAlternative.getItem());
								if (reverseId != null && reverseId.toString().equals(requiredItemName))
									return true;
							}
						}
					}
					return false;
				}

				boolean hasIndependentRecipe(String producedItemName, String excludedIngredientName) {
					java.util.List<net.minecraft.world.item.crafting.RecipeHolder<?>> candidateRecipes = recipesByOutput.get(producedItemName);
					if (candidateRecipes == null)
						return false;
					for (net.minecraft.world.item.crafting.RecipeHolder<?> candidateHolder : candidateRecipes) {
						java.util.List<net.minecraft.world.item.crafting.Ingredient> candidateIngredients = candidateHolder.value().getIngredients();
						if (candidateIngredients == null || candidateIngredients.isEmpty())
							continue;
						boolean canCraftWithoutExcludedIngredient = true;
						for (net.minecraft.world.item.crafting.Ingredient candidateIngredient : candidateIngredients) {
							boolean slotHasIndependentAlternative = false;
							for (net.minecraft.world.item.ItemStack candidateAlternative : candidateIngredient.getItems()) {
								if (candidateAlternative == null || candidateAlternative.isEmpty())
									continue;
								var candidateId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(candidateAlternative.getItem());
								if (candidateId != null && !candidateId.toString().equals(excludedIngredientName)) {
									slotHasIndependentAlternative = true;
									break;
								}
							}
							if (!slotHasIndependentAlternative) {
								canCraftWithoutExcludedIngredient = false;
								break;
							}
						}
						if (canCraftWithoutExcludedIngredient)
							return true;
					}
					return false;
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
			// =====================================================
			// BUILD EVERY FOOD OBJECT
			// =====================================================
			for (net.minecraft.world.item.Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
				net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
				net.minecraft.world.food.FoodProperties food = item.getFoodProperties(stack, null);
				if (food == null)
					continue;
				var itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
				if (itemId == null)
					continue;
				String itemName = itemId.toString();
				if (excludedFoods.contains(itemName))
					continue;
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
				net.minecraft.world.item.crafting.RecipeHolder<?> chosenRecipe = scoreResolver.chosenRecipeCache.get(itemName);
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
					if (recipeAnalysis.recipeHolder == chosenRecipe)
						selectedRecipeIndex = recipeIndex;
				}
				com.google.gson.JsonArray categoriesArray = new com.google.gson.JsonArray();
				for (String categoryName : scoreResolver.getCategories(itemName)) {
					categoriesArray.add(categoryName);
				}
				String recipeType = chosenRecipe == null ? "none" : chosenRecipe.value().getType().toString();
				int recipeOutputCount = 0;
				if (chosenRecipe != null) {
					net.minecraft.world.item.ItemStack recipeResult = chosenRecipe.value().getResultItem(level.registryAccess());
					recipeOutputCount = Math.max(1, recipeResult.getCount());
				}
				int recipeCount = recipesByOutput.getOrDefault(itemName, java.util.Collections.emptyList()).size();
				String role = validRecipes.isEmpty() ? "INGREDIENT" : "FOOD";
				com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
				foodObject.addProperty("id", itemName);
				foodObject.addProperty("role", role);
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
			// =====================================================
			// SORTING JSON
			// =====================================================
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
			// First assign tiers normally from score/percentile.
			for (int i = 0; i < totalFoods; i++) {
				com.google.gson.JsonObject food = allFoods.get(i);
				double percentile = (double) i / totalFoods;
				int tier = (int) (percentile * tierCount);
				if (tier >= tierCount)
					tier = tierCount - 1;
				food.addProperty("tier", tier);
			}
			// Lookup only foods that participate in the tier system.
			java.util.Map<String, com.google.gson.JsonObject> foodObjectsById = new java.util.HashMap<>();
			for (com.google.gson.JsonObject food : allFoods) {
				foodObjectsById.put(food.get("id").getAsString(), food);
			}
			// A prepared dish can never be below any selected FOOD ingredient.
			// Repeat until stable because an ingredient may itself be a prepared dish
			// whose tier was promoted by its own ingredients.
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
			// =====================================================
			// FINAL TIER SORTING AND BASE EXP
			// =====================================================
			// Arrays were created earlier, but are still empty here.
			java.util.List<com.google.gson.JsonArray> finalTierArrays = java.util.Arrays.asList(tier0Array, tier1Array, tier2Array, tier3Array, tier4Array, tier5Array, tier6Array, tier7Array, tier8Array, tier9Array);
			// The lowest tier begins at 10 EXP and the highest at 100 EXP.
			double tierStep = tierCount > 1 ? 90.0 / (tierCount - 1) : 90.0;
			for (int finalTierIndex = 0; finalTierIndex < tierCount; finalTierIndex++) {
				java.util.List<com.google.gson.JsonObject> foodsInTier = new java.util.ArrayList<>();
				// Collect foods assigned to this tier after all ingredient promotions.
				for (com.google.gson.JsonObject food : allFoods) {
					if (food.get("tier").getAsInt() == finalTierIndex) {
						foodsInTier.add(food);
					}
				}
				// Sort the final contents of the tier by score.
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
			// =====================================================
			// FINAL JSON STRUCTURE
			// =====================================================
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
			foodDatabase.addProperty("scoring_version", 7);
			foodDatabase.add("disabledFoods", disabledFoodsArray);
			// =====================================================
			// FINAL RESULT
			// =====================================================
			// foodDatabase
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