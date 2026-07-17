package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

public class ClientFoodWaitStateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		client = entity;
		if (!(client.getPersistentData().getString("food_delivered")).contains("0")) {
			client.getPersistentData().putDouble("food_eat_time", (Mth.nextInt(RandomSource.create(), 200, 400) + world.dayTime()));
			client.getPersistentData().putString("state", "food_eat");
		}
	}
}