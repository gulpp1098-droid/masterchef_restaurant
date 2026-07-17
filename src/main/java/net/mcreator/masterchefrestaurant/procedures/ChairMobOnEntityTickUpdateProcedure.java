package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

public class ChairMobOnEntityTickUpdateProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		Entity Chair = null;
		Chair = entity;
		if (!Chair.isVehicle()) {
			if (Chair.getPersistentData().getDouble("despawnCounter") >= 20) {
				if (!Chair.level().isClientSide())
					Chair.discard();
			} else {
				Chair.getPersistentData().putDouble("despawnCounter", (Chair.getPersistentData().getDouble("despawnCounter") + 1));
			}
		} else {
			Chair.getPersistentData().putDouble("despawnCounter", 0);
		}
	}
}