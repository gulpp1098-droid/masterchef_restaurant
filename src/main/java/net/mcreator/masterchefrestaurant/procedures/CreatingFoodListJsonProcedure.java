package net.mcreator.masterchefrestaurant.procedures;

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
			com.google.gson.JsonObject loadedSpecialFoodBonuses = CreateSpecialFoodBonusMapProcedure.execute();
			final com.google.gson.JsonObject specialFoodBonuses = loadedSpecialFoodBonuses != null ? loadedSpecialFoodBonuses : new com.google.gson.JsonObject();
			com.google.gson.JsonObject loadedFoodCategoryBonuses = CreateFoodCategoryBonusMapProcedure.execute();
			final com.google.gson.JsonObject foodCategoryBonuses = loadedFoodCategoryBonuses != null ? loadedFoodCategoryBonuses : new com.google.gson.JsonObject();
			java.util.List<com.google.gson.JsonObject> allFoods = new java.util.ArrayList<>();
			com.google.gson.JsonObject foodDatabase = new com.google.gson.JsonObject();
			com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
			com.google.gson.JsonArray disabledFoodsArray = new com.google.gson.JsonArray();
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
				final java.util.Set<String> resolving = new java.util.HashSet<>();

				double resolve(String itemName) {
					return resolve(itemName, 0);
				}

				double resolve(String itemName, int depth) {
					Double cached = scoreCache.get(itemName);
					if (cached != null)
						return cached;
					if (depth >= MAX_DEPTH || resolving.contains(itemName))
						return INVALID;
					net.minecraft.world.item.Item item = itemsById.get(itemName);
					if (item == null)
						return INVALID;
					net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
					boolean isFood = item.getFoodProperties(stack, null) != null;
					double qualityScore = isFood ? getQualityScore(itemName) : 1.0;
					java.util.List<net.minecraft.world.item.crafting.RecipeHolder<?>> recipes = recipesByOutput.get(itemName);
					boolean hasRecipes = recipes != null && !recipes.isEmpty();
					double bestRecipeScore = INVALID;
					net.minecraft.world.item.crafting.RecipeHolder<?> bestRecipe = null;
					java.util.List<String> bestIngredients = null;
					resolving.add(itemName);
					if (hasRecipes) {
						for (net.minecraft.world.item.crafting.RecipeHolder<?> recipeHolder : recipes) {
							java.util.List<String> selectedIngredients = new java.util.ArrayList<>();
							double currentRecipeScore = calculateRecipeScore(recipeHolder, itemName, depth + 1, selectedIngredients);
							if (Double.isFinite(currentRecipeScore) && currentRecipeScore < bestRecipeScore) {
								bestRecipeScore = currentRecipeScore;
								bestRecipe = recipeHolder;
								bestIngredients = selectedIngredients;
							}
						}
					}
					resolving.remove(itemName);
					double customScore = getSpecialFoodBonus(itemName);
					double resolvedScore;
					if (isFood) {
						resolvedScore = Double.isFinite(bestRecipeScore) ? Math.max(qualityScore, bestRecipeScore) : qualityScore;
					} else if (Double.isFinite(bestRecipeScore)) {
						resolvedScore = bestRecipeScore;
					} else if (!hasRecipes) {
						resolvedScore = 1.0;
					} else {
						return Math.max(1.0, 1.0 + customScore);
					}
					resolvedScore = Math.max(1.0, resolvedScore + customScore);
					scoreCache.put(itemName, resolvedScore);
					if (Double.isFinite(bestRecipeScore)) {
						recipeScoreCache.put(itemName, bestRecipeScore);
						chosenRecipeCache.put(itemName, bestRecipe);
						chosenIngredientsCache.put(itemName, bestIngredients);
					} else {
						recipeScoreCache.put(itemName, 0.0);
						chosenIngredientsCache.put(itemName, new java.util.ArrayList<>());
					}
					return resolvedScore;
				}

				double getSpecialFoodBonus(String itemName) {
					if (itemName == null || !specialFoodBonuses.has(itemName))
						return 0.0;
					com.google.gson.JsonElement bonusElement = specialFoodBonuses.get(itemName);
					if (bonusElement == null || !bonusElement.isJsonPrimitive() || !bonusElement.getAsJsonPrimitive().isNumber()) {
						return 0.0;
					}
					try {
						return bonusElement.getAsDouble();
					} catch (Exception ignored) {
						return 0.0;
					}
				}

				double calculateRecipeScore(net.minecraft.world.item.crafting.RecipeHolder<?> recipeHolder, String outputItemName, int depth, java.util.List<String> selectedIngredients) {
					if (depth >= MAX_DEPTH)
						return INVALID;
					net.minecraft.world.item.crafting.Recipe<?> recipe = recipeHolder.value();
					java.util.List<net.minecraft.world.item.crafting.Ingredient> ingredients = recipe.getIngredients();
					if (ingredients == null || ingredients.isEmpty())
						return INVALID;
					net.minecraft.world.item.ItemStack result = recipe.getResultItem(level.registryAccess());
					if (result.isEmpty())
						return INVALID;
					int outputCount = Math.max(1, result.getCount());
					double ingredientTotal = 0.0;
					for (net.minecraft.world.item.crafting.Ingredient ingredient : ingredients) {
						net.minecraft.world.item.ItemStack[] alternatives = ingredient.getItems();
						if (alternatives == null || alternatives.length == 0)
							return INVALID;
						double cheapestAlternative = INVALID;
						String cheapestAlternativeName = null;
						for (net.minecraft.world.item.ItemStack alternative : alternatives) {
							if (alternative == null || alternative.isEmpty())
								continue;
							var alternativeId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(alternative.getItem());
							if (alternativeId == null)
								continue;
							String alternativeName = alternativeId.toString();
							if (outputCount > 1 && hasRecipeUsingItem(alternativeName, outputItemName)) {
								continue;
							}
							double alternativeScore = resolve(alternativeName, depth + 1);
							if (Double.isFinite(alternativeScore) && alternativeScore < cheapestAlternative) {
								cheapestAlternative = alternativeScore;
								cheapestAlternativeName = alternativeName;
							}
						}
						if (!Double.isFinite(cheapestAlternative) || cheapestAlternativeName == null) {
							return INVALID;
						}
						ingredientTotal += cheapestAlternative;
						selectedIngredients.add(cheapestAlternativeName);
					}
					double preparePoints = getPreparePoints(recipe.getType().toString());
					return (ingredientTotal + preparePoints) / outputCount;
				}

				boolean hasRecipeUsingItem(String producedItemName, String requiredItemName) {
					java.util.List<net.minecraft.world.item.crafting.RecipeHolder<?>> reverseRecipes = recipesByOutput.get(producedItemName);
					if (reverseRecipes == null)
						return false;
					for (net.minecraft.world.item.crafting.RecipeHolder<?> reverseRecipeHolder : reverseRecipes) {
						for (net.minecraft.world.item.crafting.Ingredient reverseIngredient : reverseRecipeHolder.value().getIngredients()) {
							for (net.minecraft.world.item.ItemStack reverseAlternative : reverseIngredient.getItems()) {
								if (reverseAlternative == null || reverseAlternative.isEmpty()) {
									continue;
								}
								var reverseId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(reverseAlternative.getItem());
								if (reverseId != null && reverseId.toString().equals(requiredItemName)) {
									return true;
								}
							}
						}
					}
					return false;
				}

				double getPreparePoints(String recipeType) {
					if (recipeType.contains("smoking") || recipeType.contains("campfire_cooking") || recipeType.contains("smelting")) {
						return 3.0;
					}
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
						if (mobEffectInstance == null || mobEffectInstance.getEffect() == null) {
							continue;
						}
						if (mobEffectInstance.getEffect().value().isBeneficial()) {
							positiveEffects++;
						} else {
							negativeEffects++;
						}
					}
					return food.nutrition() + food.saturation() * 1.5 + positiveEffects * 4.0 - negativeEffects * 8.0;
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
					if (mobEffectInstance == null || mobEffectInstance.getEffect() == null) {
						continue;
					}
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
				double customScore = scoreResolver.getSpecialFoodBonus(itemName);
				double score = scoreResolver.resolve(itemName);
				if (!Double.isFinite(score)) {
					score = Math.max(0.0, qualityScore + customScore);
				}
				double recipeScore = scoreResolver.recipeScoreCache.getOrDefault(itemName, 0.0);
				net.minecraft.world.item.crafting.RecipeHolder<?> chosenRecipe = scoreResolver.chosenRecipeCache.get(itemName);
				java.util.List<String> selectedIngredients = scoreResolver.chosenIngredientsCache.getOrDefault(itemName, new java.util.ArrayList<>());
				com.google.gson.JsonArray ingredientsArray = new com.google.gson.JsonArray();
				for (String ingredientName : selectedIngredients) {
					ingredientsArray.add(ingredientName);
				}
				String recipeType = chosenRecipe == null ? "none" : chosenRecipe.value().getType().toString();
				int recipeOutputCount = 0;
				if (chosenRecipe != null) {
					net.minecraft.world.item.ItemStack recipeResult = chosenRecipe.value().getResultItem(level.registryAccess());
					recipeOutputCount = Math.max(1, recipeResult.getCount());
				}
				int recipeCount = recipesByOutput.getOrDefault(itemName, java.util.Collections.emptyList()).size();
				com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
				foodObject.addProperty("id", itemName);
				foodObject.addProperty("nutrition", hunger);
				foodObject.addProperty("saturation", saturation);
				foodObject.add("ingredients", ingredientsArray);
				foodObject.addProperty("method", recipeType);
				foodObject.addProperty("recipeCount", recipeCount);
				foodObject.addProperty("recipeOutputCount", recipeOutputCount);
				foodObject.addProperty("positiveEffects", positiveEffectsArray.size());
				foodObject.addProperty("negativeEffects", negativeEffectsArray.size());
				foodObject.add("positiveEffectsList", positiveEffectsArray);
				foodObject.add("negativeEffectsList", negativeEffectsArray);
				foodObject.addProperty("qualityScore", qualityScore);
				foodObject.addProperty("recipeScore", recipeScore);
				foodObject.addProperty("customPoints", customScore);
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
			for (int i = 0; i < totalFoods; i++) {
				com.google.gson.JsonObject food = allFoods.get(i);
				double percentile = (double) i / totalFoods;
				int tier = (int) (percentile * tierCount);
				if (tier >= tierCount)
					tier = tierCount - 1;
				food.addProperty("tier", tier);
				switch (tier) {
					case 0 :
						tier0Array.add(food);
						break;
					case 1 :
						tier1Array.add(food);
						break;
					case 2 :
						tier2Array.add(food);
						break;
					case 3 :
						tier3Array.add(food);
						break;
					case 4 :
						tier4Array.add(food);
						break;
					case 5 :
						tier5Array.add(food);
						break;
					case 6 :
						tier6Array.add(food);
						break;
					case 7 :
						tier7Array.add(food);
						break;
					case 8 :
						tier8Array.add(food);
						break;
					case 9 :
						tier9Array.add(food);
						break;
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
			foodDatabase.addProperty("scoring_version", 2);
			foodDatabase.add("disabledFoods", disabledFoodsArray);
			// =====================================================
			// FINAL RESULT
			// =====================================================
			// foodDatabase
			FoodDatabaseObject = foodDatabase;
			FoodDatabase = new File((FMLPaths.GAMEDIR.get().toString() + "/config/masterchef"), File.separator + "FoodDatabase.json");
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