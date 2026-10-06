package net.mcreator.omnichef.procedures;

public class GetFoodRoleByIDProcedure {
	public static String execute(com.google.gson.JsonObject databaseObject, String foodID) {
		if (databaseObject == null || foodID == null)
			return "";
		com.google.gson.JsonObject foodData = new com.google.gson.JsonObject();
		foodData = GetFoodDataFromIDProcedure.execute(databaseObject, foodID);
		return foodData.get("role").getAsString();
	}
}