package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ChefsDiaryChangeDependentPageProcedure {
	public static void execute(LevelAccessor world, Entity entity, double direction) {
		if (entity == null)
			return;
		if (!world.isClientSide()) {
			{
				OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
				_vars.DiaryDependentPage = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryDependentPage + direction;
				_vars.markSyncDirty();
			}
			FillChefsDiaryDependentsProcedure.execute(entity, ReadFoodDatabaseProcedure.execute(world));
		}
	}
}