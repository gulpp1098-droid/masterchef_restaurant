package net.mcreator.omnichef.procedures;

public class GetFoodRecipeFromDataProcedure {
	public static com.google.gson.JsonObject execute(com.google.gson.JsonObject foodData, double recipeIndex) {
		if (foodData == null)
			return new com.google.gson.JsonObject();
		com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject recipeObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray recipesArray = new com.google.gson.JsonArray();
		foodObject = foodData;
		recipesArray = foodObject.get("recipes").getAsJsonArray();
		recipeObject = recipesArray.get((int) recipeIndex).getAsJsonObject();
		return recipeObject;
	}
}