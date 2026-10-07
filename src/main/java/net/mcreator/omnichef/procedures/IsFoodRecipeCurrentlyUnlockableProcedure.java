package net.mcreator.omnichef.procedures;

public class IsFoodRecipeCurrentlyUnlockableProcedure {
	public static boolean execute(com.google.gson.JsonArray unlockedFoods, com.google.gson.JsonObject databaseObject, String foodID) {
		if (unlockedFoods == null || databaseObject == null || foodID == null)
			return false;
		com.google.gson.JsonObject foodData = new com.google.gson.JsonObject();
		com.google.gson.JsonObject recipeData = new com.google.gson.JsonObject();
		com.google.gson.JsonArray recipes = new com.google.gson.JsonArray();
		com.google.gson.JsonArray ingredientSlots = new com.google.gson.JsonArray();
		com.google.gson.JsonArray alternatives = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockedFoodsArray = new com.google.gson.JsonArray();
		String alternativeID = "";
		double recipeIndex = 0;
		double slotIndex = 0;
		double alternativeIndex = 0;
		double unlockedIndex = 0;
		boolean foundPossibleRecipe = false;
		boolean recipePossible = false;
		boolean slotPossible = false;
		foodData = GetFoodDataFromIDProcedure.execute(databaseObject, foodID);
		if (!IsFoodInDatabaseProcedure.execute(databaseObject, foodID)) {
			return false;
		}
		if ((GetFoodRoleByIDProcedure.execute(databaseObject, foodID)).equals("INGREDIENT")) {
			return true;
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
					alternativeID = alternatives.get((int) alternativeIndex).getAsString();
					if (!IsFoodInDatabaseProcedure.execute(databaseObject, alternativeID)) {
						slotPossible = true;
					} else {
						unlockedIndex = 0;
						for (int _i4 = 0; _i4 < (int) unlockedFoodsArray.size(); _i4++) {
							if ((alternativeID).equals(unlockedFoodsArray.get((int) unlockedIndex).getAsString())) {
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