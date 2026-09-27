package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class AdvanceRecipeDiscoveryStageProcedure {
	public static void execute(LevelAccessor world, double restaurantID) {
		double restaurantIndex = 0;
		double stageTier = 0;
		double index = 0;
		double tierCount = 0;
		double restaurantLevel = 0;
		double maxRestaurantLevel = 0;
		double targetTier = 0;
		double tierToAdd = 0;
		com.google.gson.JsonArray unlocksOptions = new com.google.gson.JsonArray();
		com.google.gson.JsonArray newTierFoods = new com.google.gson.JsonArray();
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (restaurantIndex >= 0) {
			stageTier = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"stage_tier");
			restaurantLevel = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"level");
			tierCount = GetFoodListTierCountProcedure.execute(world);
			maxRestaurantLevel = OmnichefModVariables.MapVariables.get(world).MaxRestaurantLevel;
			if (tierCount > 0 && maxRestaurantLevel > 0) {
				targetTier = Math.floor((restaurantLevel / maxRestaurantLevel) * tierCount);
				targetTier = Math.min(targetTier, tierCount - 1);
				targetTier = Math.max(targetTier, 0);
				if (targetTier > stageTier) {
					unlocksOptions = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlock_options", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
							OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
					tierToAdd = stageTier + 1;
					for (int _i1 = 0; _i1 < (int) (targetTier - stageTier); _i1++) {
						newTierFoods = GetFoodListFromTierProcedure.execute(world, tierToAdd);
						index = 0;
						for (int _i2 = 0; _i2 < (int) newTierFoods.size(); _i2++) {
							unlocksOptions.add(newTierFoods.get((int) index).getAsString());
							index = index + 1;
						}
						tierToAdd = tierToAdd + 1;
					}
					ModifyRestaurantWholeArrayParameterProcedure.execute(unlocksOptions, restaurantIndex, "restaurants", "unlock_options", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
							OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
					ModifyRestaurantNumberParameterProcedure.execute(targetTier, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
							"stage_tier");
				}
			}
		}
	}
}