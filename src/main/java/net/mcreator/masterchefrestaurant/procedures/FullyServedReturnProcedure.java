package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.stats.Stats;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class FullyServedReturnProcedure {
	public static String execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return "";
		com.google.gson.JsonObject Stats = new com.google.gson.JsonObject();
		double restaurantID = 0;
		restaurantID = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID;
		if (restaurantID > 0) {
			Stats = GetRestaurantObjectParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "daily_stats");
			return Stats.get("customers_served_fully").getAsString();
		}
		return "0";
	}
}