package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class IsInsideAnyRestaurantProcedure {
	public static boolean execute(LevelAccessor world, double dependencyX, double dependencyZ) {
		com.google.gson.JsonArray restaurantsArray = new com.google.gson.JsonArray();
		double restaurantIndex = 0;
		double restaurantID = 0;
		restaurantsArray = GetRestaurantsListArrayProcedure.execute(world);
		restaurantIndex = 0;
		for (int _i1 = 0; _i1 < (int) restaurantsArray.size(); _i1++) {
			restaurantID = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "ID");
			if (IsInsideRestaurantProcedure.execute(world, dependencyX, dependencyZ, restaurantID)) {
				return true;
			}
			restaurantIndex = restaurantIndex + 1;
		}
		return false;
	}
}