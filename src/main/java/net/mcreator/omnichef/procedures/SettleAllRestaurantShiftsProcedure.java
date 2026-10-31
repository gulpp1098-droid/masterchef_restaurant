package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class SettleAllRestaurantShiftsProcedure {
	public static boolean execute(LevelAccessor world) {
		com.google.gson.JsonArray restaurants = new com.google.gson.JsonArray();
		com.google.gson.JsonObject restaurantData = new com.google.gson.JsonObject();
		double index = 0;
		double restaurantID = 0;
		boolean allSettled = false;
		if (world.isClientSide()) {
			return false;
		}
		if (!OmnichefModVariables.MapVariables.get(world).ModDataReady) {
			return false;
		}
		restaurants = GetClientDatabaseListArrayProcedure.execute(world);
		allSettled = true;
		for (int _i1 = 0; _i1 < (int) restaurants.size(); _i1++) {
			restaurantData = restaurants.get((int) index).getAsJsonObject();
			restaurantID = restaurantData.get("ID").getAsDouble();
			if (RestaurantIndexSearchByIDProcedure.execute(world, restaurantID) >= 0) {
				if (!SettleRestaurantShiftProcedure.execute(world, restaurantID)) {
					allSettled = false;
				}
			}
			index = index + 1;
		}
		return allSettled;
	}
}