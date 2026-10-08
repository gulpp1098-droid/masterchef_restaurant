package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class GetClientOrderBaseExpProcedure {
	public static double execute(LevelAccessor world, Entity clientEntity) {
		if (clientEntity == null)
			return 0;
		Entity client = null;
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodAmountArray = new com.google.gson.JsonArray();
		String foodID = "";
		double index = 0;
		double loopSize = 0;
		double amount = 0;
		double baseExp = 0;
		double totalBaseExp = 0;
		client = clientEntity;
		database = ReadFoodDatabaseProcedure.execute(world);
		foodArray = GetClientOrderFoodArrayProcedure.execute(client);
		foodAmountArray = GetClientOrderAmountArrayProcedure.execute(client);
		loopSize = Math.min(foodArray.size(), foodAmountArray.size());
		for (int _i1 = 0; _i1 < (int) loopSize; _i1++) {
			foodID = foodArray.get((int) index).getAsString();
			amount = foodAmountArray.get((int) index).getAsDouble();
			baseExp = GetFoodBaseExpByIDProcedure.execute(database, foodID);
			totalBaseExp = totalBaseExp + baseExp + amount;
			index = index + 1;
		}
		return totalBaseExp;
	}
}