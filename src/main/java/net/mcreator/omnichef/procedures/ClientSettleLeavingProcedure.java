package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.entity.ClientEntity;

public class ClientSettleLeavingProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		double restaurantID = 0;
		String satisfactionLevel = "";
		client = entity;
		if (!world.isClientSide()) {
			if (client instanceof ClientEntity) {
				restaurantID = client.getPersistentData().getDouble("RestaurantID");
				if (RestaurantIndexSearchByIDProcedure.execute(world, restaurantID) >= 0) {
					if (!client.getPersistentData().getBoolean("service_result_counted")) {
						if (client.getPersistentData().getBoolean("order_served")) {
							satisfactionLevel = GetClientSatisfactionLevelProcedure.execute(client);
							if ((satisfactionLevel).equals("perfect")) {
								ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), 1, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
										OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "daily_stats", "customers_served_fully");
								if (client instanceof ClientEntity) {
									AddRecipeDiscoveryExpProcedure.execute(world, restaurantID);
								}
							} else if ((satisfactionLevel).equals("satisfied")) {
								ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), 1, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
										OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "daily_stats", "customers_served");
							} else {
								ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), 1, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
										OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "daily_stats", "customers_lost");
							}
						} else {
							ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), 1, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
									OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "daily_stats", "customers_lost");
						}
						client.getPersistentData().putBoolean("service_result_counted", true);
					}
					ClientCoinPayProcedure.execute(world, client);
					ClientExpPayProcedure.execute(world, client);
				}
			}
		}
	}
}