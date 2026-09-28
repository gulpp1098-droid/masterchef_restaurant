package net.mcreator.omnichef.procedures;

public class FilterAvailableFoodsFromTierArrayProcedure {
	public static com.google.gson.JsonArray execute(com.google.gson.JsonArray options, com.google.gson.JsonArray tierFoods) {
		if (options == null || tierFoods == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonArray tierFoodsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray optionsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray availableFoods = new com.google.gson.JsonArray();
		com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
		String foodID = "";
		double tierIndex = 0;
		double optionIndex = 0;
		tierFoodsArray = tierFoods;
		optionsArray = options;
		for (int _i1 = 0; _i1 < (int) tierFoodsArray.size(); _i1++) {
			foodObject = tierFoodsArray.get((int) tierIndex).getAsJsonObject();
			foodID = foodObject.get("id").getAsString();
			optionIndex = 0;
			for (int _i2 = 0; _i2 < (int) optionsArray.size(); _i2++) {
				if ((foodID).equals(optionsArray.get((int) optionIndex).getAsString())) {
					availableFoods.add(foodID);
					break;
				}
				optionIndex = optionIndex + 1;
			}
			tierIndex = tierIndex + 1;
		}
		return availableFoods;
	}
}