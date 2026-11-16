package net.mcreator.omnichef.procedures;

public class GetFoodDiscoveryVariantsProcedure {
	public static com.google.gson.JsonArray execute(com.google.gson.JsonObject databaseObject, String foodID) {
		if (databaseObject == null || foodID == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonObject variantsMap = new com.google.gson.JsonObject();
		com.google.gson.JsonObject member = new com.google.gson.JsonObject();
		com.google.gson.JsonObject data = new com.google.gson.JsonObject();
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonArray variants = new com.google.gson.JsonArray();
		com.google.gson.JsonArray result = new com.google.gson.JsonArray();
		String discoveryID = "";
		double index = 0;
		discoveryID = GetFoodDiscoveryIDProcedure.execute(databaseObject, foodID);
		data = GetFoodDataFromIDProcedure.execute(databaseObject, discoveryID);
		database = databaseObject;
		if (!data.has("id")) {
			return result;
		}
		result.add(discoveryID);
		if (database.has("discovery_variants")) {
			variantsMap = database.get("discovery_variants").getAsJsonObject();
			if (variantsMap.has(discoveryID)) {
				variants = variantsMap.get(discoveryID).getAsJsonArray();
				for (int _i1 = 0; _i1 < (int) variants.size(); _i1++) {
					member = variants.get((int) index).getAsJsonObject();
					result.add(member.get("id").getAsString());
					index = index + 1;
				}
			}
		}
		return result;
	}
}