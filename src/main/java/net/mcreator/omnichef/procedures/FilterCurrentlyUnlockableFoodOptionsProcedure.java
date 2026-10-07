package net.mcreator.omnichef.procedures;

public class FilterCurrentlyUnlockableFoodOptionsProcedure {
	public static com.google.gson.JsonArray execute(com.google.gson.JsonArray options, com.google.gson.JsonArray unlockedFoods, com.google.gson.JsonObject databaseObject) {
		if (options == null || unlockedFoods == null || databaseObject == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonArray optionsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockedFoodsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray filteredOptions = new com.google.gson.JsonArray();
		String foodID = "";
		double optionIndex = 0;
		database = databaseObject;
		optionsArray = options;
		unlockedFoodsArray = unlockedFoods;
		for (int _i1 = 0; _i1 < (int) optionsArray.size(); _i1++) {
			foodID = optionsArray.get((int) optionIndex).getAsString();
			if (IsFoodRecipeCurrentlyUnlockableProcedure.execute(unlockedFoodsArray, database, foodID)) {
				filteredOptions.add(foodID);
			}
			optionIndex = optionIndex + 1;
		}
		return filteredOptions;
	}
}