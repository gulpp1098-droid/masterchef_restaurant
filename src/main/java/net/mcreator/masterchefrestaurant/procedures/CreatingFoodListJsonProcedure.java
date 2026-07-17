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
			// =====================================================
			// EXCLUDED FOODS
			// =====================================================
			java.util.Set<String> excludedFoods = new java.util.HashSet<>();
			excludedFoods.add("minecraft:ominous_bottle");
			excludedFoods.add("minecraft:enchanted_golden_apple");
			excludedFoods.add("minecraft:suspicious_stew");
			java.util.List<com.google.gson.JsonObject> allFoods = new java.util.ArrayList<>();
			// =====================================================
			// MAIN JSON
			// =====================================================
			com.google.gson.JsonObject foodDatabase = new com.google.gson.JsonObject();
			com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
			com.google.gson.JsonArray disabledFoodsArray = new com.google.gson.JsonArray();
			// =====================================================
			// DUPLICATE PROTECTION
			// =====================================================
			java.util.Set<String> processedFoods = new java.util.HashSet<>();
			// =====================================================
			// RECIPE MANAGER
			// =====================================================
			net.minecraft.world.item.crafting.RecipeManager recipeManager = level.getRecipeManager();
			// =====================================================
			// ETAP 1 - FOOD WITH RECIPES
			// =====================================================
			for (net.minecraft.world.item.crafting.RecipeHolder<?> recipeHolder : recipeManager.getRecipes()) {
				net.minecraft.world.item.crafting.Recipe<?> recipe = recipeHolder.value();
				net.minecraft.world.item.ItemStack result = recipe.getResultItem(level.registryAccess());
				if (result.isEmpty())
					continue;
				net.minecraft.world.item.Item item = result.getItem();
				// =================================================
				// CHECK IF FOOD
				// =================================================
				if (item.getFoodProperties(result, null) == null)
					continue;
				var itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
				if (itemId == null)
					continue;
				String itemName = itemId.toString();
				if (excludedFoods.contains(itemName))
					continue;
				// =================================================
				// DUPLICATE CHECK
				// =================================================
				if (processedFoods.contains(itemName))
					continue;
				processedFoods.add(itemName);
				// =================================================
				// FOOD PROPERTIES
				// =================================================
				net.minecraft.world.food.FoodProperties food = item.getFoodProperties(result, null);
				int hunger = food.nutrition();
				float saturation = food.saturation();
				// =================================================
				// EFFECTS
				// =================================================
				com.google.gson.JsonArray positiveEffectsArray = new com.google.gson.JsonArray();
				com.google.gson.JsonArray negativeEffectsArray = new com.google.gson.JsonArray();
				for (net.minecraft.world.food.FoodProperties.PossibleEffect pe : food.effects()) {
					var mei = pe.effect();
					if (mei == null)
						continue;
					var holder = mei.getEffect();
					if (holder == null)
						continue;
					var effect = holder.value();
					var effectId = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getKey(effect);
					if (effectId == null)
						continue;
					if (effect.isBeneficial()) {
						positiveEffectsArray.add(effectId.toString());
					} else {
						negativeEffectsArray.add(effectId.toString());
					}
				}
				// =================================================
				// INGREDIENTS
				// =================================================
				com.google.gson.JsonArray ingredientsArray = new com.google.gson.JsonArray();
				java.util.Set<String> ingredientSet = new java.util.HashSet<>();
				double ingredientPoints = 0;
				for (net.minecraft.world.item.crafting.Ingredient ing : recipe.getIngredients()) {
					for (net.minecraft.world.item.ItemStack stack : ing.getItems()) {
						var ingId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
						if (ingId != null) {
							String ingredientName = ingId.toString();
							if (!ingredientSet.contains(ingredientName)) {
								ingredientSet.add(ingredientName);
								ingredientsArray.add(ingredientName);
								ingredientPoints += 1;
								if (stack.getItem().getFoodProperties(stack, null) != null) {
									ingredientPoints += 1.5;
								}
							}
							break;
						}
					}
				}
				// =================================================
				// RECIPE TYPE
				// =================================================
				String recipeType = recipe.getType().toString();
				// =================================================
				// SCORE
				// =================================================
				double hungerPoints = hunger;
				double saturationPoints = saturation * 1.5;
				double effectPoints = positiveEffectsArray.size() * 4 - negativeEffectsArray.size() * 8;
				double preparePoints = 1;
				if (recipeType.contains("smoking") || recipeType.contains("campfire_cooking") || recipeType.contains("smelting")) {
					preparePoints = 3;
				} else if (recipeType.contains("crafting")) {
					preparePoints = 4;
				} else if (recipeType.contains("cutting")) {
					preparePoints = 2.5;
				} else {
					preparePoints = 5;
				}
				ingredientPoints = Math.sqrt(ingredientPoints) * 2;
				double score = hungerPoints + saturationPoints + effectPoints + preparePoints + ingredientPoints;
				// =================================================
				// FOOD OBJECT
				// =================================================
				com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
				foodObject.addProperty("id", itemName);
				foodObject.addProperty("nutrition", hunger);
				foodObject.addProperty("saturation", saturation);
				foodObject.add("ingredients", ingredientsArray);
				foodObject.addProperty("method", recipeType);
				foodObject.addProperty("positiveEffects", positiveEffectsArray.size());
				foodObject.addProperty("negativeEffects", negativeEffectsArray.size());
				foodObject.add("positiveEffectsList", positiveEffectsArray);
				foodObject.add("negativeEffectsList", negativeEffectsArray);
				foodObject.addProperty("score", score);
				if (negativeEffectsArray.size() > 0) {
					disabledFoodsArray.add(foodObject);
				} else {
					allFoods.add(foodObject);
				}
			}
			// =====================================================
			// ETAP 2 - FOOD WITHOUT RECIPES
			// =====================================================
			for (net.minecraft.world.item.Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
				net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
				if (item.getFoodProperties(stack, null) == null)
					continue;
				var itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
				if (itemId == null)
					continue;
				String itemName = itemId.toString();
				if (excludedFoods.contains(itemName))
					continue;
				// =================================================
				// DUPLICATE CHECK
				// =================================================
				if (processedFoods.contains(itemName))
					continue;
				processedFoods.add(itemName);
				// =================================================
				// FOOD PROPERTIES
				// =================================================
				var food = item.getFoodProperties(stack, null);
				int hunger = food.nutrition();
				float saturation = food.saturation();
				// =================================================
				// EFFECTS
				// =================================================
				com.google.gson.JsonArray positiveEffectsArray = new com.google.gson.JsonArray();
				com.google.gson.JsonArray negativeEffectsArray = new com.google.gson.JsonArray();
				for (var pe : food.effects()) {
					var mei = pe.effect();
					if (mei == null)
						continue;
					var holder = mei.getEffect();
					if (holder == null)
						continue;
					var effect = holder.value();
					var effectId = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getKey(effect);
					if (effectId == null)
						continue;
					if (effect.isBeneficial()) {
						positiveEffectsArray.add(effectId.toString());
					} else {
						negativeEffectsArray.add(effectId.toString());
					}
				}
				// =================================================
				// SCORE
				// =================================================
				String recipeType = "none";
				double hungerPoints = hunger;
				double saturationPoints = saturation * 1.5;
				double effectPoints = positiveEffectsArray.size() * 4 - negativeEffectsArray.size() * 8;
				double preparePoints = 0;
				double ingredientPoints = 0;
				double score = hungerPoints + saturationPoints + effectPoints + preparePoints + ingredientPoints;
				// =================================================
				// FOOD OBJECT
				// =================================================
				com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
				foodObject.addProperty("id", itemName);
				foodObject.addProperty("nutrition", hunger);
				foodObject.addProperty("saturation", saturation);
				foodObject.add("ingredients", new com.google.gson.JsonArray());
				foodObject.addProperty("method", "none");
				foodObject.addProperty("positiveEffects", positiveEffectsArray.size());
				foodObject.addProperty("negativeEffects", negativeEffectsArray.size());
				foodObject.add("positiveEffectsList", positiveEffectsArray);
				foodObject.add("negativeEffectsList", negativeEffectsArray);
				foodObject.addProperty("score", score);
				// =================================================
				// DISABLED FOOD CHECK
				// =================================================
				if (negativeEffectsArray.size() > 0) {
					disabledFoodsArray.add(foodObject);
				} else {
					allFoods.add(foodObject);
				}
			}
			// =====================================================
			// SORTING JSON
			// =====================================================
			allFoods.sort(java.util.Comparator.comparingDouble(food -> food.get("score").getAsDouble()));
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
				String itemId = food.get("id").getAsString();
				if (itemId.equals("minecraft:chorus_fruit"))
					tier = 4;
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
			foodDatabase.add("disabledFoods", disabledFoodsArray);
			// =====================================================
			// FINAL RESULT
			// =====================================================
			// foodDatabase
			//
			// Możesz teraz:
			// - zapisać do pliku
			// - wrzucić do variable
			// - debug print gson.toJson(foodDatabase)
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