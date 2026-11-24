package net.mcreator.omnichef.procedures;

public class DoesItemDependOnProcedure {
	public static boolean execute(com.google.gson.JsonObject databaseObject, String dependencyID, String targetID) {
		if (databaseObject == null || dependencyID == null || targetID == null)
			return false;
		com.google.gson.JsonObject databaseObj = new com.google.gson.JsonObject();
		com.google.gson.JsonObject recipeNodes = new com.google.gson.JsonObject();
		com.google.gson.JsonObject targetNode = new com.google.gson.JsonObject();
		com.google.gson.JsonObject recipeData = new com.google.gson.JsonObject();
		com.google.gson.JsonArray recipes = new com.google.gson.JsonArray();
		com.google.gson.JsonArray parents = new com.google.gson.JsonArray();
		com.google.gson.JsonArray ingredientSlots = new com.google.gson.JsonArray();
		com.google.gson.JsonArray alternatives = new com.google.gson.JsonArray();
		String selectedID = "";
		String kind = "";
		double recipeIndex = 0;
		double slotIndex = 0;
		double alternativeIndex = 0;
		double parentIndex = 0;
		databaseObj = databaseObject;
		recipeNodes = databaseObj.get("technical").getAsJsonObject();
		recipeNodes = recipeNodes.get("recipe_nodes").getAsJsonObject();
		if (!recipeNodes.has(targetID)) {
			return false;
		}
		targetNode = recipeNodes.get(targetID).getAsJsonObject();
		kind = targetNode.get("kind").getAsString();
		selectedID = GetFoodDiscoveryIDProcedure.execute(databaseObj, dependencyID);
		if ((targetID).equals(selectedID)) {
			return false;
		}
		if (targetNode.has("unlock_requires_any")) {
			parents = targetNode.get("unlock_requires_any").getAsJsonArray();
			for (int _i1 = 0; _i1 < (int) parents.size(); _i1++) {
				if ((parents.get((int) parentIndex).getAsString()).equals(selectedID)) {
					return true;
				}
				parentIndex = parentIndex + 1;
			}
		}
		if ((kind).equals("INGREDIENT")) {
			if (!targetNode.get("unlock_requires_recipe").getAsBoolean()) {
				return false;
			}
			recipes = targetNode.get("unlock_recipes").getAsJsonArray();
		} else if ((kind).equals("FOOD") || (kind).equals("INTERMEDIATE")) {
			recipes = targetNode.get("recipes").getAsJsonArray();
		} else {
			return false;
		}
		for (int _i1 = 0; _i1 < (int) recipes.size(); _i1++) {
			recipeData = recipes.get((int) recipeIndex).getAsJsonObject();
			ingredientSlots = GetRecipeIngredientSlotsProcedure.execute(recipeData);
			slotIndex = 0;
			for (int _i2 = 0; _i2 < (int) ingredientSlots.size(); _i2++) {
				alternatives = GetIngredientsAlternativesFromSlotProcedure.execute(ingredientSlots, slotIndex);
				alternativeIndex = 0;
				for (int _i3 = 0; _i3 < (int) alternatives.size(); _i3++) {
					if ((selectedID).equals(GetFoodDiscoveryIDProcedure.execute(databaseObj, alternatives.get((int) alternativeIndex).getAsString()))) {
						return true;
					}
					alternativeIndex = alternativeIndex + 1;
				}
				slotIndex = slotIndex + 1;
			}
			recipeIndex = recipeIndex + 1;
		}
		return false;
	}
}