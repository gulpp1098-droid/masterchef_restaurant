package net.mcreator.omnichef.procedures;

public class IsFoodRecipeCurrentlyUnlockableProcedure {
	public static boolean execute(com.google.gson.JsonArray unlockedFoods, com.google.gson.JsonObject databaseObject, String foodID) {
		if (unlockedFoods == null || databaseObject == null || foodID == null)
			return false;
		com.google.gson.JsonObject foodData = new com.google.gson.JsonObject();
		com.google.gson.JsonObject recipeData = new com.google.gson.JsonObject();
		com.google.gson.JsonObject recipeNodes = new com.google.gson.JsonObject();
		com.google.gson.JsonObject alternativeData = new com.google.gson.JsonObject();
		com.google.gson.JsonArray recipes = new com.google.gson.JsonArray();
		com.google.gson.JsonArray ingredientSlots = new com.google.gson.JsonArray();
		com.google.gson.JsonArray alternatives = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockedFoodsArray = new com.google.gson.JsonArray();
		String alternativeID = "";
		String alternativeKind = "";
		double recipeIndex = 0;
		double slotIndex = 0;
		double alternativeIndex = 0;
		double unlockedIndex = 0;
		boolean foundPossibleRecipe = false;
		boolean recipePossible = false;
		boolean slotPossible = false;
		recipeNodes = databaseObject;
		recipeNodes = recipeNodes.get("technical").getAsJsonObject();
		recipeNodes = recipeNodes.get("recipe_nodes").getAsJsonObject();
		foodData = GetFoodDataFromIDProcedure.execute(databaseObject, foodID);
		if (!IsFoodInDatabaseProcedure.execute(databaseObject, foodID)) {
			if (!recipeNodes.has(foodID)) {
				return false;
			}
			foodData = recipeNodes.get(foodID).getAsJsonObject();
			if (!(foodData.get("kind").getAsString()).equals("INTERMEDIATE")) {
				return false;
			}
		} else {
			if ((GetFoodRoleByIDProcedure.execute(databaseObject, foodID)).equals("INGREDIENT")) {
				if (!foodData.get("unlock_requires_recipe").getAsBoolean()) {
					return true;
				}
			}
		}
		unlockedFoodsArray = unlockedFoods;
		recipes = foodData.get("recipes").getAsJsonArray();
		for (int _i1 = 0; _i1 < (int) recipes.size(); _i1++) {
			recipeData = GetFoodRecipeFromDataProcedure.execute(foodData, recipeIndex);
			ingredientSlots = GetRecipeIngredientSlotsProcedure.execute(recipeData);
			recipePossible = true;
			slotIndex = 0;
			for (int _i2 = 0; _i2 < (int) ingredientSlots.size(); _i2++) {
				alternatives = GetIngredientsAlternativesFromSlotProcedure.execute(ingredientSlots, slotIndex);
				slotPossible = false;
				alternativeIndex = 0;
				for (int _i3 = 0; _i3 < (int) alternatives.size(); _i3++) {
					alternativeID = GetFoodDiscoveryIDProcedure.execute(databaseObject, alternatives.get((int) alternativeIndex).getAsString());
					if (!IsFoodInDatabaseProcedure.execute(databaseObject, alternativeID)) {
						if (recipeNodes.has(alternativeID)) {
							alternativeData = recipeNodes.get(alternativeID).getAsJsonObject();
							alternativeKind = alternativeData.get("kind").getAsString();
							if ((alternativeKind).equals("AUXILIARY") || (alternativeKind).equals("EXTERNAL")) {
								slotPossible = true;
							}
							if ((alternativeKind).equals("ALIAS") || (alternativeKind).equals("INTERMEDIATE")) {
								if ((alternativeKind).equals("ALIAS")) {
									alternativeID = alternativeData.get("discovery_id").getAsString();
								}
								unlockedIndex = 0;
								for (int _i4 = 0; _i4 < (int) unlockedFoodsArray.size(); _i4++) {
									if ((GetFoodDiscoveryIDProcedure.execute(databaseObject, unlockedFoodsArray.get((int) unlockedIndex).getAsString())).equals(alternativeID)) {
										slotPossible = true;
									}
									unlockedIndex = unlockedIndex + 1;
								}
							}
						}
					} else {
						unlockedIndex = 0;
						for (int _i4 = 0; _i4 < (int) unlockedFoodsArray.size(); _i4++) {
							if ((GetFoodDiscoveryIDProcedure.execute(databaseObject, unlockedFoodsArray.get((int) unlockedIndex).getAsString())).equals(alternativeID)) {
								slotPossible = true;
							}
							unlockedIndex = unlockedIndex + 1;
						}
					}
					alternativeIndex = alternativeIndex + 1;
				}
				if (!slotPossible) {
					recipePossible = false;
				}
				slotIndex = slotIndex + 1;
			}
			if (recipePossible) {
				foundPossibleRecipe = true;
			}
			recipeIndex = recipeIndex + 1;
		}
		return foundPossibleRecipe;
	}
}