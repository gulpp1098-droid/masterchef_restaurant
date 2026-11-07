package net.mcreator.omnichef.procedures;

public class BuildFoodRoleLookupProcedure {
	public static com.google.gson.JsonObject execute(com.google.gson.JsonObject database) {
		if (database == null)
			return new com.google.gson.JsonObject();
		com.google.gson.JsonObject lookup = new com.google.gson.JsonObject();
		com.google.gson.JsonObject tiers = new com.google.gson.JsonObject();
		com.google.gson.JsonObject food = new com.google.gson.JsonObject();
		com.google.gson.JsonObject databaseObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray foods = new com.google.gson.JsonArray();
		double tierIndex = 0;
		double foodIndex = 0;
		databaseObject = database;
		tiers = databaseObject.get("tiers").getAsJsonObject();
		for (int _i1 = 0; _i1 < (int) databaseObject.get("tier_count").getAsDouble(); _i1++) {
			foods = tiers.get((new java.text.DecimalFormat("0").format(tierIndex))).getAsJsonArray();
			foodIndex = 0;
			for (int _i2 = 0; _i2 < (int) foods.size(); _i2++) {
				food = foods.get((int) foodIndex).getAsJsonObject();
				lookup.addProperty(food.get("id").getAsString(), food.get("role").getAsString());
				foodIndex = foodIndex + 1;
			}
			tierIndex = tierIndex + 1;
		}
		return lookup;
	}
}