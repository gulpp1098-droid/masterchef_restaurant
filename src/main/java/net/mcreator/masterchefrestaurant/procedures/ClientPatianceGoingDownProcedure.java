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
		nbtPatience = client.getPersistentData().getDouble("patience");
		if ((client.getPersistentData().getString("state")).equals("food_wait") || (client.getPersistentData().getString("state")).equals("queue_wait") || (client.getPersistentData().getString("state")).equals("order_wait")
				|| (client.getPersistentData().getString("state")).equals("queue_move")) {
			if (nbtPatience > 0) {
				client.getPersistentData().putDouble("patience", (nbtPatience - 1));
			} else {
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
}