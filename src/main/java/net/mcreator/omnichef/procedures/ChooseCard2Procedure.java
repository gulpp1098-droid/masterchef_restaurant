package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ChooseCard2Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		ChoosePendingCardProcedure.execute(world, x, y, z, entity, 1, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).FoodUnlockGroup);
	}
}