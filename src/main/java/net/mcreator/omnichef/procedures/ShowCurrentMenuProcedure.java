package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ShowCurrentMenuProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (!world.isClientSide()) {
			{
				OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
				_vars.MenuShowNext = false;
				_vars.MenuPage = 0;
				_vars.markSyncDirty();
			}
			FillSlotsNextMenuProcedure.execute(world, entity);
		}
	}
}