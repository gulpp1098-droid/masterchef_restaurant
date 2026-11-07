package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class ForceClientToLeaveProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		if ((entity.getPersistentData().getString("state")).equals("leave") || entity.getPersistentData().getBoolean("leaving_started")) {
			return true;
		}
		entity.getPersistentData().putDouble("client_expire_time", (world.dayTime()));
		return ClientMaximumLifetimeCheckProcedure.execute(world, entity);
	}
}