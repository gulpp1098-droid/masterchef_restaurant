package net.mcreator.omnichef.procedures;

public class GetFoodDataFromIDProcedure {
	public static com.google.gson.JsonObject execute(com.google.gson.JsonObject databaseObject, String foodID) {
		if (databaseObject == null || foodID == null)
			return new com.google.gson.JsonObject();
		com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject currentFood = new com.google.gson.JsonObject();
		com.google.gson.JsonObject emptyResult = new com.google.gson.JsonObject();
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonArray tierArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray disabledArray = new com.google.gson.JsonArray();
		double tierCount = 0;
		double tierIndex = 0;
		double foodIndex = 0;
		String tierKey = "";
		database = databaseObject;
		if (!database.get("tiers").isJsonObject() || (database.get("tier_count").isJsonPrimitive() ? database.get("tier_count").getAsJsonPrimitive().isNumber() : false) || (foodID).equals("")) {
			return emptyResult;
		}
		tiersObject = database.get("tiers").getAsJsonObject();
		tierCount = tiersObject.get("tier_count").getAsDouble();
		for (int _i1 = 0; _i1 < (int) tierCount; _i1++) {
			tierKey = new java.text.DecimalFormat("#").format(tierIndex);
			if (tiersObject.get(tierKey).isJsonPrimitive() ? tiersObject.get(tierKey).getAsJsonPrimitive().isString() : false) {
				tierArray = tiersObject.get(tierKey).getAsJsonArray();
				foodIndex = 0;
				for (int _i2 = 0; _i2 < (int) tierArray.size(); _i2++) {
					currentFood = tierArray.get((int) foodIndex).getAsJsonObject();
					if ((currentFood.get("id").getAsString()).equals(foodID)) {
						return currentFood;
					}
					foodIndex = foodIndex + 1;
				}
			}
			tierIndex = tierIndex + 1;
		}
		return emptyResult;
	}
}