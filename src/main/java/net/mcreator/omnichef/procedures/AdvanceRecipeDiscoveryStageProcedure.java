package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class AdvanceRecipeDiscoveryStageProcedure {
	public static void execute(LevelAccessor world, double restaurantID) {
		double restaurantIndex = 0;
		double stageTier = 0;
		double stageUnlocks = 0;
		double index = 0;
		double tierCount = 0;
		com.google.gson.JsonArray unlocksOptions = new com.google.gson.JsonArray();
		com.google.gson.JsonArray newTierFoods = new com.google.gson.JsonArray();
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (restaurantIndex >= 0) {
			stageTier = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"stage_tier");
			stageUnlocks = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"stage_unlocks");
			if (stageUnlocks >= OmnichefModVariables.MapVariables.get(world).RecipeDiscoveryStageLimit) {
				tierCount = GetFoodListTierCountProcedure.execute(world);
				if (stageTier + 1 < tierCount) {
					newTierFoods = GetFoodListFromTierProcedure.execute(world, stageTier + 1);
					if (newTierFoods.size() > 0) {
						unlocksOptions = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlock_options", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
								OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
						index = 0;
						for (int _i1 = 0; _i1 < (int) newTierFoods.size(); _i1++) {
							unlocksOptions.add(newTierFoods.get((int) index).getAsString());
							index = index + 1;
						}
						ModifyRestaurantWholeArrayParameterProcedure.execute(unlocksOptions, restaurantIndex, "restaurants", "unlock_options", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
								OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
						ModifyRestaurantNumberParameterProcedure.execute(stageTier + 1, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
								OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "stage_tier");
						ModifyRestaurantNumberParameterProcedure.execute(0, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
								"stage_unlocks");
					}
				}
			}
		}
	}
}