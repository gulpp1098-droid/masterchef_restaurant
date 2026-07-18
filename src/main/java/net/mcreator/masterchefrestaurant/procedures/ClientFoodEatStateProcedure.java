package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class ClientFoodEatStateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		double restaurantID = 0;
		client = entity;
		if (client.getPersistentData().getDouble("food_eat_time") <= world.dayTime()) {
			ClientCoinPayProcedure.execute(world, entity);
			ClientExpPayProcedure.execute(world, entity);
			restaurantID = client.getPersistentData().getDouble("RestaurantID");
			ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), 1, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "daily_stats", "customers_served_fully");
			client.getPersistentData().putString("state", "group_wait");
		}
	}
}