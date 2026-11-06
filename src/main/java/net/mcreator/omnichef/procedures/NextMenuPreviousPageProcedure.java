package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class NextMenuPreviousPageProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).MenuPage > 0) {
			{
				OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
				_vars.MenuPage = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).MenuPage - 1;
				_vars.markSyncDirty();
			}
			FillSlotsNextMenuProcedure.execute(world, entity);
		}
	}
}