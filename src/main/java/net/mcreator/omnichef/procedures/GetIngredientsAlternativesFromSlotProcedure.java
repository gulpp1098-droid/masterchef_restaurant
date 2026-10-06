package net.mcreator.omnichef.procedures;

public class GetIngredientsAlternativesFromSlotProcedure {
	public static com.google.gson.JsonArray execute(com.google.gson.JsonArray ingredientSlots, double slotIndex) {
		if (ingredientSlots == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonArray slotsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray alternativesArray = new com.google.gson.JsonArray();
		slotsArray = ingredientSlots;
		alternativesArray = slotsArray.get((int) ((int) slotIndex)).getAsJsonArray();
		return alternativesArray;
	}
}