package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class CanRelocateRestaurantProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		Entity owner = null;
		com.google.gson.JsonObject restaurantObject = new com.google.gson.JsonObject();
		double restaurantIndex = 0;
		owner = entity;
		if (true) {
			if (IsUserRestaurantOwnerProcedure.execute(world, entity)) {
				restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
				if (restaurantIndex >= 0) {
					restaurantObject = FindRestaurantInfoByIndexViaIDProcedure.execute(world, restaurantIndex);
					if (!(restaurantObject.get("locations").getAsString()).equals("")) {
						if (!GetRestaurantLogicParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
							if (GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
									MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "active_groups") == 0) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}