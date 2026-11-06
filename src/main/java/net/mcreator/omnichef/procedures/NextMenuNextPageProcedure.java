package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class NextMenuNextPageProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		com.google.gson.JsonArray menu = new com.google.gson.JsonArray();
		com.google.gson.JsonArray checkMenu = new com.google.gson.JsonArray();
		double page = 0;
		double ID = 0;
		ID = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID;
		if (ID > 0) {
			menu = GetDisplayedRestaurantMenuProcedure.execute(world, entity);
			page = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).MenuPage;
			if (menu.size() > (page + 1) * 6) {
				{
					OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
					_vars.MenuPage = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).MenuPage + 1;
					_vars.markSyncDirty();
				}
				FillSlotsNextMenuProcedure.execute(world, entity);
			}
		}
	}
}