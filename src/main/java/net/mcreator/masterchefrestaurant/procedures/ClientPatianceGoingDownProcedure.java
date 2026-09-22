package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class ClientPatianceGoingDownProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		double nbtPatience = 0;
		double restaurantID = 0;
		client = entity;
		if (client.getPersistentData().getBoolean("patience_needed") && world.dayTime() >= client.getPersistentData().getDouble("patience_end_time") && !client.getPersistentData().getBoolean("leaving_started")) {
			client.getPersistentData().putBoolean("patience_needed", false);
			restaurantID = client.getPersistentData().getDouble("RestaurantID");
			if ((client.getPersistentData().getString("food_delivered")).contains("" + 1)) {
				ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), 1, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "daily_stats", "customers_served");
			} else {
				ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), 1, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "daily_stats", "customers_lost");
			}
			client.getPersistentData().putString("state", "group_wait");
		}
	}
}