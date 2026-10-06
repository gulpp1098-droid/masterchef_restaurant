package net.mcreator.omnichef.procedures;

public class GetRecipeIngredientSlotsProcedure {
	public static com.google.gson.JsonArray execute(com.google.gson.JsonObject recipeData) {
		if (recipeData == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonObject recipeObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray ingredientSlots = new com.google.gson.JsonArray();
		recipeObject = recipeData;
		ingredientSlots = recipeObject.get("ingredientSlots").getAsJsonArray();
		return ingredientSlots;
	}
}