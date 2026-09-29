package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ChefsDiaryFoodNextPageProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double currentTier = 0;
		double currentPage = 0;
		double tierCount = 0;
		double nextTier = 0;
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		boolean foundTier = false;
		if (!world.isClientSide()) {
			currentTier = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodTier;
			currentPage = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodPage;
			tierCount = GetFoodListTierCountProcedure.execute(world);
			foodArray = GetFoodListFromTierProcedure.execute(world, currentTier);
			if ((currentPage + 1) * 18 == foodArray.size()) {
				{
					OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
					_vars.DiaryFoodPage = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodPage + 1;
					_vars.markSyncDirty();
				}
				ChefsDiaryFoodTiersProcedure.execute(world, x, y, z, entity);
			} else {
				nextTier = currentTier + 1;
				while (nextTier < tierCount && !foundTier) {
					foodArray = GetFoodListFromTierProcedure.execute(world, nextTier);
					if (foodArray.size() > 0) {
						{
							OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
							_vars.DiaryFoodTier = nextTier + 1;
							_vars.markSyncDirty();
						}
						foundTier = true;
					} else {
						nextTier = nextTier + 1;
					}
				}
				if (foundTier) {
					ChefsDiaryFoodTiersProcedure.execute(world, x, y, z, entity);
				}
			}
		}
	}
}