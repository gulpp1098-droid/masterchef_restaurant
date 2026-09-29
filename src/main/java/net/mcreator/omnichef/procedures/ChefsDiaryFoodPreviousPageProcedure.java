package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ChefsDiaryFoodPreviousPageProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double currentTier = 0;
		double currentPage = 0;
		double tierCount = 0;
		double nextTier = 0;
		double previousTier = 0;
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		boolean foundTier = false;
		if (!world.isClientSide()) {
			currentTier = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodTier;
			currentPage = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodPage;
			tierCount = GetFoodListTierCountProcedure.execute(world);
			if (currentPage > 0) {
				{
					OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
					_vars.DiaryFoodPage = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodPage - 1;
					_vars.markSyncDirty();
				}
				ChefsDiaryFoodTiersProcedure.execute(world, x, y, z, entity);
			} else {
				previousTier = currentTier - 1;
				while (previousTier >= 0 && !foundTier) {
					foodArray = GetFoodListFromTierProcedure.execute(world, previousTier);
					if (foodArray.size() > 0) {
						{
							OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
							_vars.DiaryFoodTier = previousTier;
							_vars.DiaryFoodPage = Math.floor((foodArray.size() - 1) / 18);
							_vars.markSyncDirty();
						}
						foundTier = true;
					} else {
						previousTier = previousTier - 1;
					}
				}
				if (foundTier) {
					ChefsDiaryFoodTiersProcedure.execute(world, x, y, z, entity);
				}
			}
		}
	}
}