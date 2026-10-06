package net.mcreator.omnichef.procedures;

public class IsFoodInDatabaseProcedure {
	public static boolean execute(com.google.gson.JsonObject databaseObject, String foodID) {
		if (databaseObject == null || foodID == null)
			return false;
		com.google.gson.JsonObject foodData = new com.google.gson.JsonObject();
		foodData = GetFoodDataFromIDProcedure.execute(databaseObject, foodID);
		return foodData.get("id").isJsonPrimitive() ? foodData.get("id").getAsJsonPrimitive().isString() : false;
	}
}