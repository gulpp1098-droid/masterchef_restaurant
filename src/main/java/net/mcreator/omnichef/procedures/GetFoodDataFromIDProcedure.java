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
		tiersObject = database.get("tiers").getAsJsonObject();
		tierCount = database.get("tier_count").getAsDouble();
		for (int _i1 = 0; _i1 < (int) tierCount; _i1++) {
			tierKey = new java.text.DecimalFormat("#").format(tierIndex);
			tierArray = tiersObject.get(tierKey).getAsJsonArray();
			foodIndex = 0;
			for (int _i2 = 0; _i2 < (int) tierArray.size(); _i2++) {
				currentFood = tierArray.get((int) foodIndex).getAsJsonObject();
				if ((currentFood.get("id").getAsString()).equals(foodID)) {
					return currentFood;
				}
				foodIndex = foodIndex + 1;
			}
			tierIndex = tierIndex + 1;
		}
		disabledArray = database.get("disabledFoods").getAsJsonArray();
		foodIndex = 0;
		for (int _i1 = 0; _i1 < (int) disabledArray.size(); _i1++) {
			currentFood = disabledArray.get((int) foodIndex).getAsJsonObject();
			if ((currentFood.get("id").getAsString()).equals(foodID)) {
				return currentFood;
			}
			foodIndex = foodIndex + 1;
		}
		return emptyResult;
	}
}