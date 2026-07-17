package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class ClientOrderPickStateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		client = entity;
		if (client.getPersistentData().getDouble("order_pick_time") <= world.dayTime()) {
			client.getPersistentData().putString("state", "order_wait");
		}
	}
}