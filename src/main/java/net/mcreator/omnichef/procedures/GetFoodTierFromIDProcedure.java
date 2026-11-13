package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

public class GetFoodTierFromIDProcedure {
	public static double execute(LevelAccessor world, String foodID) {
		if (foodID == null)
			return 0;
		com.google.gson.JsonObject fileObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
		fileObject = ReadFoodDatabaseProcedure.execute(world);
		foodObject = GetFoodDataFromIDProcedure.execute(fileObject, foodID);
		if (foodObject.has("tier")) {
			return foodObject.get("tier").getAsDouble();
		}
		return 0;
	}
}