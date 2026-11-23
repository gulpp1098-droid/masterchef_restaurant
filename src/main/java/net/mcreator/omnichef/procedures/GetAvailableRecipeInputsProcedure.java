package net.mcreator.omnichef.procedures;

public class GetAvailableRecipeInputsProcedure {
	public static com.google.gson.JsonArray execute(com.google.gson.JsonArray discoveredItems, com.google.gson.JsonObject databaseObject) {
		if (discoveredItems == null || databaseObject == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonObject technical = new com.google.gson.JsonObject();
		com.google.gson.JsonObject diagnostics = new com.google.gson.JsonObject();
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonArray intermediateCandidates = new com.google.gson.JsonArray();
		com.google.gson.JsonArray availableIntermediates = new com.google.gson.JsonArray();
		com.google.gson.JsonArray result = new com.google.gson.JsonArray();
		com.google.gson.JsonArray discovered = new com.google.gson.JsonArray();
		double index = 0;
		database = databaseObject;
		discovered = discoveredItems;
		technical = database.get("technical").getAsJsonObject();
		diagnostics = technical.get("diagnostics").getAsJsonObject();
		intermediateCandidates = diagnostics.get("preparation_intermediates").getAsJsonArray();
		availableIntermediates = FilterCurrentlyUnlockableFoodOptionsProcedure.execute(intermediateCandidates, discovered, database);
		for (int _i1 = 0; _i1 < (int) discovered.size(); _i1++) {
			result.add(discovered.get((int) index).getAsString());
			index = index + 1;
		}
		index = 0;
		for (int _i1 = 0; _i1 < (int) availableIntermediates.size(); _i1++) {
			result.add(availableIntermediates.get((int) index).getAsString());
			index = index + 1;
		}
		return result;
	}
}