package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class CanRelocateRestaurantProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		Entity owner = null;
		com.google.gson.JsonObject restaurantObject = new com.google.gson.JsonObject();
		double restaurantIndex = 0;
		com.google.gson.JsonArray locationArray = new com.google.gson.JsonArray();
		owner = entity;
		if (true) {
			if (IsUserRestaurantOwnerProcedure.execute(world, entity)) {
				restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, owner.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
				if (restaurantIndex >= 0) {
					restaurantObject = FindRestaurantInfoByIndexViaIDProcedure.execute(world, restaurantIndex);
					locationArray = restaurantObject.get("locations").getAsJsonArray();
					if (!locationArray.isEmpty()) {
						if (!GetRestaurantLogicParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
							if (GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
									"active_groups") == 0) {
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