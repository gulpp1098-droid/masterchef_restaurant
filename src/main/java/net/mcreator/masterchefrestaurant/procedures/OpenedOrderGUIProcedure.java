package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class OpenedOrderGUIProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (world.dayTime() % 5 == 0) {
			FillOrderSlotsProcedure.execute(world, entity);
			ClientIsServedProcedure.execute(world, entity);
			ClosingOrderGUIProcedure.execute(world, entity);
		}
		PatianceInOrderGUIProcedure.execute(world, entity);
	}
}