package net.mcreator.omnichef.procedures;

public class GetFoodDiscoveryIDProcedure {
	public static String execute(com.google.gson.JsonObject databaseObject, String foodID) {
		if (databaseObject == null || foodID == null)
			return "";
		String result = "";
		com.google.gson.JsonObject technical = new com.google.gson.JsonObject();
		com.google.gson.JsonObject nodes = new com.google.gson.JsonObject();
		com.google.gson.JsonObject node = new com.google.gson.JsonObject();
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		result = foodID;
		database = databaseObject;
		if (database.has("technical")) {
			technical = database.get("technical").getAsJsonObject();
			if (technical.has("recipe_nodes")) {
				nodes = technical.get("recipe_nodes").getAsJsonObject();
				if (nodes.has(foodID)) {
					node = nodes.get(foodID).getAsJsonObject();
					if (node.has("kind") && node.has("discovery_id")) {
						if ((node.get("kind").getAsString()).equals("ALIAS")) {
							result = node.get("discovery_id").getAsString();
						}
					}
				}
			}
		}
		return result;
	}
}