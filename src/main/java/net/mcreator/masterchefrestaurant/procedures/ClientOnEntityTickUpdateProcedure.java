package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class ClientOnEntityTickUpdateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		ClientAnimationSetUpProcedure.execute(entity);
		client = entity;
		if (!world.isClientSide()) {
			if (world.dayTime() % 36 == 0) {
				ClientPatianceGoingDownProcedure.execute(world, entity);
			}
			if (world.dayTime() % 10 == 0) {
				if (client.getPersistentData().getBoolean("leader")) {
					if ((client.getPersistentData().getString("state")).equals("restaurant_go")) {
						if (!GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID")), "restaurants",
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
							ClientExpPayProcedure.execute(world, entity);
							ClientLeaveStateProcedure.execute(world, x, y, z, entity);
						} else {
							ClientRestaurantGoStateProcedure.execute(world, entity);
						}
					} else if ((client.getPersistentData().getString("state")).equals("queue_wait")) {
						if (!GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID")), "restaurants",
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
							ClientExpPayProcedure.execute(world, entity);
							ClientLeaveStateProcedure.execute(world, x, y, z, entity);
						} else {
							ClientQueueWaitStateProcedure.execute(world, entity);
						}
					} else if ((client.getPersistentData().getString("state")).equals("queue_move")) {
						ClientQueueMoveStateProcedure.execute(entity);
					} else if ((client.getPersistentData().getString("state")).equals("table_go")) {
						ClientTableGoStateProcedure.execute(world, entity);
					} else if ((client.getPersistentData().getString("state")).equals("group_wait")) {
						ClientGroupWaitStateProcedure.execute(world, x, y, z, entity);
					}
				} else {
					ClientCopyLeaderLeaveStateProcedure.execute(world, entity);
					if ((client.getPersistentData().getString("state")).equals("table_go")) {
						ClientTableGoCopyNBTStateProcedure.execute(world, entity);
					}
				}
				if ((client.getPersistentData().getString("state")).equals("find_chair")) {
					ClientFindChairStateProcedure.execute(entity);
				} else if ((client.getPersistentData().getString("state")).equals("order_pick")) {
					ClientOrderPickStateProcedure.execute(world, entity);
				} else if ((client.getPersistentData().getString("state")).equals("food_wait")) {
					ClientFoodWaitStateProcedure.execute(world, entity);
				} else if ((client.getPersistentData().getString("state")).equals("food_eat")) {
					ClientFoodEatStateProcedure.execute(world, entity);
				} else if ((client.getPersistentData().getString("state")).equals("leave")) {
					ClientLeaveStateProcedure.execute(world, x, y, z, entity);
				} else if ((client.getPersistentData().getString("state")).equals("order_wait")) {
				} else {
					ClientFollowStateProcedure.execute(world, entity);
				}
				EntityStaysOnTableProcedure.execute(world, entity);
				OrderWaitingProcedure.execute(world, x, y, z, entity);
			}
			if (world.dayTime() % 100 == 0) {
				ClientStuckStateProcedure.execute(world, x, y, z, entity);
			}
		}
	}
}