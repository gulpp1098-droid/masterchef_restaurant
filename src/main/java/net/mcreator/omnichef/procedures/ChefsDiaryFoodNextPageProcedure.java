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
		String groupName = "";
		com.google.gson.JsonObject databaseObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject groupObject = new com.google.gson.JsonObject();
		if (!world.isClientSide()) {
			groupName = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodGroup;
			currentTier = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodTier;
			currentPage = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodPage;
			databaseObject = ReadFoodDatabaseProcedure.execute(world);
			groupObject = databaseObject.get(groupName).getAsJsonObject();
			tierCount = groupObject.get("tier_count").getAsDouble();
			foodArray = GetFoodDataFromGroupTierProcedure.execute(world, currentTier, groupName);
			if ((currentPage + 1) * 18 < foodArray.size()) {
				{
					OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
					_vars.DiaryFoodPage = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodPage + 1;
					_vars.markSyncDirty();
				}
				ChefsDiaryFoodTiersProcedure.execute(world, x, y, z, entity);
			} else {
				nextTier = currentTier + 1;
				while (nextTier < tierCount && !foundTier) {
					foodArray = GetFoodDataFromGroupTierProcedure.execute(world, nextTier, groupName);
					if (foodArray.size() > 0) {
						{
							OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
							_vars.DiaryFoodTier = nextTier;
							_vars.DiaryFoodPage = 0;
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