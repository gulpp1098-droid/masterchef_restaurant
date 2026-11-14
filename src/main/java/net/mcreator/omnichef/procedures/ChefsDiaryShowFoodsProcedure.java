package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ChefsDiaryShowFoodsProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (!world.isClientSide()) {
			{
				OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
				_vars.DiaryFoodGroup = "foods";
				_vars.DiaryFoodTier = 0;
				_vars.DiaryFoodPage = 0;
				_vars.markSyncDirty();
			}
			ChefsDiaryFoodTiersProcedure.execute(world, x, y, z, entity);
		}
	}
}