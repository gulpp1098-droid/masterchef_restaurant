package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

public class GetAvailableFoodFromTierProcedure {
	public static com.google.gson.JsonArray execute(LevelAccessor world, com.google.gson.JsonArray options, double tier) {
		if (options == null)
			return new com.google.gson.JsonArray();
		com.google.gson.JsonArray tierFoods = new com.google.gson.JsonArray();
		com.google.gson.JsonArray availableFoods = new com.google.gson.JsonArray();
		com.google.gson.JsonArray optionsArray = new com.google.gson.JsonArray();
		double tierIndex = 0;
		double optionIndex = 0;
		String foodID = "";
		tierFoods = GetFoodListFromTierProcedure.execute(world, tier);
		optionsArray = options;
		for (int _i1 = 0; _i1 < (int) tierFoods.size(); _i1++) {
			foodID = tierFoods.get((int) tierIndex).getAsString();
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