package net.mcreator.omnichef.procedures;

public class IsRecipeInputAvailableProcedure {
	public static boolean execute(com.google.gson.JsonArray availableInput, com.google.gson.JsonObject databaseObject, String ingredientID) {
		if (availableInput == null || databaseObject == null || ingredientID == null)
			return false;
		com.google.gson.JsonObject databaseObj = new com.google.gson.JsonObject();
		com.google.gson.JsonObject technical = new com.google.gson.JsonObject();
		com.google.gson.JsonObject recipeNodes = new com.google.gson.JsonObject();
		com.google.gson.JsonObject node = new com.google.gson.JsonObject();
		com.google.gson.JsonArray availableInp = new com.google.gson.JsonArray();
		String discoveryID = "";
		String kind = "";
		double index = 0;
		databaseObj = databaseObject;
		availableInp = availableInput;
		discoveryID = GetFoodDiscoveryIDProcedure.execute(databaseObj, ingredientID);
		technical = databaseObj.get("technical").getAsJsonObject();
		recipeNodes = technical.get("recipe_nodes").getAsJsonObject();
		if (recipeNodes.has(discoveryID)) {
			node = recipeNodes.get(discoveryID).getAsJsonObject();
			kind = node.get("kind").getAsString();
			if ((kind).equals("AUXILIARY") || (kind).equals("EXTERNAL")) {
				return true;
			}
		}
		for (int _i1 = 0; _i1 < (int) availableInp.size(); _i1++) {
			if ((availableInp.get((int) index).getAsString()).equals(discoveryID)) {
				return true;
			}
			index = index + 1;
		}
		return false;
	}
}