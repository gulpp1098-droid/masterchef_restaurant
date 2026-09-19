package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class ClientMaximumLifetimeCheckProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		Entity client = null;
		double restaurantID = 0;
		client = entity;
		restaurantID = client.getPersistentData().getDouble("RestaurantID");
		if (!(client.getPersistentData().getString("state")).equals("leave") && !client.getPersistentData().getBoolean("leaving_started") && world.dayTime() >= client.getPersistentData().getDouble("client_expire_time")
				&& client.getPersistentData().getDouble("client_expire_time") > 0) {
			client.getPersistentData().putBoolean("patience_needed", false);
			if ((client.getPersistentData().getString("food_delivered")).contains("" + 1)) {
				ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), 1, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "daily_stats", "customers_served");
			} else {
				ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), 1, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "daily_stats", "customers_lost");
			}
			ClientExpPayProcedure.execute(world, entity);
			ClientBeginLeavingProcedure.execute(world, entity);
			entity.getPersistentData().putDouble("despawn_time", (world.dayTime()));
			return true;
		}
		return false;
	}
}