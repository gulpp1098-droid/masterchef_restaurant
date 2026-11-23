package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ChefsDiaryChangeRecipeProcedure {
	public static void execute(LevelAccessor world, Entity entity, double direction) {
		if (entity == null)
			return;
		double newIndex = 0;
		com.google.gson.JsonObject databaseObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray recipes = new com.google.gson.JsonArray();
		if (!world.isClientSide()) {
			databaseObject = ReadFoodDatabaseProcedure.execute(world);
			foodObject = GetFoodDataFromIDProcedure.execute(databaseObject, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiarySelectedItem);
			if (foodObject.has("recipes")) {
				recipes = foodObject.get("recipes").getAsJsonArray();
				newIndex = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryRecipeIndex + direction;
				if (newIndex >= 0 && newIndex < recipes.size()) {
					{
						OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
						_vars.DiaryRecipeIndex = newIndex;
						_vars.markSyncDirty();
					}
					FillChefsDiaryItemDetailProcedure.execute(world, entity);
				}
			}
		}
	}
}