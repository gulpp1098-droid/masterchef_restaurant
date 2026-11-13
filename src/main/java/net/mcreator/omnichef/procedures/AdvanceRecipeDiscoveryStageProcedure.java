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
		double ingredientTierCount = 0;
		double ingredientStage = 0;
		double ingredientTarget = 0;
		com.google.gson.JsonArray unlocksOptions = new com.google.gson.JsonArray();
		com.google.gson.JsonArray newTierFoods = new com.google.gson.JsonArray();
		com.google.gson.JsonObject databaseObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject groupObject = new com.google.gson.JsonObject();
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (restaurantIndex >= 0) {
			stageTier = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"food_stage_tier");
			restaurantLevel = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"level");
			databaseObject = ReadFoodDatabaseProcedure.execute(world);
			groupObject = databaseObject.get("foods").getAsJsonObject();
			tierCount = groupObject.get("tier_count").getAsDouble();
			groupObject = databaseObject.get("ingredients").getAsJsonObject();
			ingredientTierCount = groupObject.get("tier_count").getAsDouble();
			maxRestaurantLevel = OmnichefModVariables.MapVariables.get(world).MaxRestaurantLevel;
			if (tierCount > 0 && maxRestaurantLevel > 0) {
				targetTier = Math.floor((restaurantLevel / maxRestaurantLevel) * tierCount);
				targetTier = Math.min(targetTier, tierCount - 1);
				targetTier = Math.max(targetTier, 0);
				if (targetTier > stageTier) {
					ModifyRestaurantNumberParameterProcedure.execute(targetTier, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
							"food_stage_tier");
				}
				if (ingredientTierCount > 0) {
					ingredientStage = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
							OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "ingredient_stage_tier");
					ingredientTarget = Math.min(targetTier, ingredientTierCount - 1);
					if (ingredientTarget > ingredientStage) {
						ModifyRestaurantNumberParameterProcedure.execute(ingredientTarget, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
								OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "ingredient_stage_tier");
					}
				}
			}
		}
	}
}