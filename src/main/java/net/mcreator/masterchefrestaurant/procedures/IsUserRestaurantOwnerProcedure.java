package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.io.File;

public class IsUserRestaurantOwnerProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		Entity Owner = null;
		File ListOfRestaurants = new File("");
		com.google.gson.JsonArray Restaurant_Array = new com.google.gson.JsonArray();
		com.google.gson.JsonObject Restaurants = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Restaurant = new com.google.gson.JsonObject();
		String ownerUUID = "";
		boolean RestaurantFound = false;
		double index = 0;
		double RestaurantID = 0;
		RestaurantID = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID;
		if (RestaurantID >= 0) {
			index = RestaurantIndexSearchByIDProcedure.execute(world, RestaurantID);
			if (index >= 0) {
				return true;
			}
		}
		return false;
	}
}