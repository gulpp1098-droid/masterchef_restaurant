package net.mcreator.omnichef.procedures;

public class GetFoodTierByIDProcedure {
	public static double execute(com.google.gson.JsonObject databaseObject, String foodID) {
		if (databaseObject == null || foodID == null)
			return 0;
		com.google.gson.JsonObject foodData = new com.google.gson.JsonObject();
		foodData = GetFoodDataFromIDProcedure.execute(databaseObject, foodID);
		return foodData.get("tier").getAsDouble();
	}
}