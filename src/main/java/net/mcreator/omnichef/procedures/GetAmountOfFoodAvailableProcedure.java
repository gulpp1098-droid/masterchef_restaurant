package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

public class GetAmountOfFoodAvailableProcedure {
	public static double execute(LevelAccessor world) {
		com.google.gson.JsonObject FoodDatabase = new com.google.gson.JsonObject();
		FoodDatabase = ReadFoodDatabaseProcedure.execute(world);
		return FoodDatabase.get("item_count").getAsDouble();
	}
}