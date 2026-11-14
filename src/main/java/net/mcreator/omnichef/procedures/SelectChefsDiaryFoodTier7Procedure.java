package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class SelectChefsDiaryFoodTier7Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		String groupName = "";
		com.google.gson.JsonArray array = new com.google.gson.JsonArray();
		if (!world.isClientSide()) {
			groupName = "foods";
			if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodGroup).equals("ingredients")) {
				groupName = "ingredients";
			}
			array = GetFoodDataFromGroupTierProcedure.execute(world, 7, groupName);
			if (array.size() > 7) {
				{
					OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
					_vars.DiaryFoodTier = 7;
					_vars.DiaryFoodPage = 0;
					_vars.markSyncDirty();
				}
				ChefsDiaryFoodTiersProcedure.execute(world, x, y, z, entity);
			}
		}
	}
}