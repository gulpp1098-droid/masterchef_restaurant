package net.mcreator.omnichef.procedures;

public class FilterCurrentlyUnlockableFoodOptionsProcedure {
	public static com.google.gson.JsonArray execute(com.google.gson.JsonArray options, com.google.gson.JsonArray unlockedFoods, com.google.gson.JsonObject databaseObject) {
		if (options == null || unlockedFoods == null || databaseObject == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonObject analysisData = new com.google.gson.JsonObject();
		com.google.gson.JsonArray optionsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockedFoodsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray filteredOptions = new com.google.gson.JsonArray();
		com.google.gson.JsonArray intermediateCandidates = new com.google.gson.JsonArray();
		String foodID = "";
		String intermediateID = "";
		double optionIndex = 0;
		double passIndex = 0;
		double intermediateIndex = 0;
		double availableIndex = 0;
		boolean foundNewIntermediate = false;
		boolean intermediateAlreadyAvailable = false;
		database = databaseObject;
		optionsArray = options;
		unlockedFoodsArray = unlockedFoods;
		unlockedFoodsArray = unlockedFoodsArray.deepCopy();
		analysisData = database.get("technical").getAsJsonObject();
		analysisData = analysisData.get("diagnostics").getAsJsonObject();
		intermediateCandidates = analysisData.get("preparation_intermediates").getAsJsonArray();
		foundNewIntermediate = true;
		while (foundNewIntermediate && passIndex < intermediateCandidates.size()) {
			foundNewIntermediate = false;
			intermediateIndex = 0;
			for (int _i1 = 0; _i1 < (int) intermediateCandidates.size(); _i1++) {
				intermediateID = intermediateCandidates.get((int) intermediateIndex).getAsString();
				intermediateAlreadyAvailable = false;
				availableIndex = 0;
				for (int _i2 = 0; _i2 < (int) unlockedFoodsArray.size(); _i2++) {
					if ((intermediateID).equals(unlockedFoodsArray.get((int) availableIndex).getAsString())) {
						intermediateAlreadyAvailable = true;
					}
					availableIndex = availableIndex + 1;
				}
				if (!intermediateAlreadyAvailable) {
					if (IsFoodRecipeCurrentlyUnlockableProcedure.execute(unlockedFoodsArray, database, foodID)) {
						unlockedFoodsArray.add(intermediateID);
						foundNewIntermediate = true;
					}
				}
				intermediateIndex = intermediateIndex + 1;
			}
			passIndex = passIndex + 1;
		}
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