package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

public class GetFoodListFromTierProcedure {
	public static com.google.gson.JsonArray execute(LevelAccessor world, double tier) {
		com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray tierArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		double foodIndex = 0;
		tierArray = GetFoodListFromTierProcedure.execute(world, tier);
		foodIndex = 0;
		for (int _i1 = 0; _i1 < (int) tierArray.size(); _i1++) {
			foodObject = tierArray.get((int) foodIndex).getAsJsonObject();
			foodArray.add(foodObject.get("id").getAsString());
			foodIndex = foodIndex + 1;
		}
		return foodArray;
	}
}