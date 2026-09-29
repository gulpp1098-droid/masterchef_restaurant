package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class SelectChefsDiaryFoodTier6Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (!world.isClientSide()) {
			if (GetFoodListTierCountProcedure.execute(world) > 6) {
				{
					OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
					_vars.DiaryFoodTier = 6;
					_vars.DiaryFoodPage = 0;
					_vars.markSyncDirty();
				}
				ChefsDiaryFoodTiersProcedure.execute(world, x, y, z, entity);
			}
		}
	}
}