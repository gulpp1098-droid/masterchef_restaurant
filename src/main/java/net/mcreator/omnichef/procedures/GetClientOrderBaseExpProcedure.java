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
		client = clientEntity;
		database = ReadFoodDatabaseProcedure.execute(world);
		foodArray = GetClientOrderFoodArrayProcedure.execute(client);
		foodAmountArray = GetClientOrderAmountArrayProcedure.execute(client);
		return GetClientOrderBaseExpFromArraysProcedure.execute(foodAmountArray, foodArray, database);
	}
}