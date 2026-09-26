package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class AddRecipeDiscoveryExpProcedure {
	public static void execute(LevelAccessor world, double restaurantID) {
		double restaurantIndex = 0;
		double discoveryExp = 0;
		double stageUnlocks = 0;
		double starterRemaining = 0;
		com.google.gson.JsonArray pendingCards = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockOptions = new com.google.gson.JsonArray();
		if (!world.isClientSide()) {
			restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
			if (restaurantIndex >= 0) {
				starterRemaining = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						"starter_unlocks_remaining");
				pendingCards = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "pending_cards", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
				pendingCards = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlock_options", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
				restaurantIndex = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						"stage_unlocks");
				if (starterRemaining <= 0 && pendingCards.isEmpty() && !unlockOptions.isEmpty()) {
					discoveryExp = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
							"discovery_exp") + 1;
					if (discoveryExp >= OmnichefModVariables.MapVariables.get(world).RecipeDiscoveryExpRequired) {
						ModifyRestaurantNumberParameterProcedure.execute(0, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
								"discovery_exp");
						GeneratePendingCardsProcedure.execute(world, restaurantID);
					} else {
						ModifyRestaurantNumberParameterProcedure.execute(discoveryExp, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
								OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "discovery_exp");
					}
				}
			}
		}
	}
}