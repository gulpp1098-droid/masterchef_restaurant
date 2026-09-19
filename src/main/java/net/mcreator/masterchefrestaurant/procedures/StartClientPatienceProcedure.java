package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class StartClientPatienceProcedure {
	public static void execute(LevelAccessor world, Entity entity, double duration) {
		if (entity == null)
			return;
		Entity client = null;
		client = entity;
		client.getPersistentData().putBoolean("patience_needed", true);
		client.getPersistentData().putDouble("patience_end_time", (world.dayTime() + duration));
	}
}