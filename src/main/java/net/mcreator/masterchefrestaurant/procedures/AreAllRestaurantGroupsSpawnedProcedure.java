package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;

public class AreAllRestaurantGroupsSpawnedProcedure {
	public static boolean execute(LevelAccessor world, double RestaurantID) {
		double ClientDatabaseIndex = 0;
		double groupIndex = 0;
		boolean allGroupsSpawned = false;
		com.google.gson.JsonArray groups = new com.google.gson.JsonArray();
		com.google.gson.JsonArray restaurants = new com.google.gson.JsonArray();
		com.google.gson.JsonObject restaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject group = new com.google.gson.JsonObject();
		ClientDatabaseIndex = ClientDatabaseIndexSearchByIDProcedure.execute(world, RestaurantID);
		if (ClientDatabaseIndex < 0) {
			return false;
		}
		restaurants = GetClientDatabaseListArrayProcedure.execute(world);
		restaurantObject = restaurants.get((int) ClientDatabaseIndex).getAsJsonObject();
		groups = restaurantObject.get("groups").getAsJsonArray();
		allGroupsSpawned = true;
		for (int _i1 = 0; _i1 < (int) restaurants.size(); _i1++) {
			group = groups.get((int) groupIndex).getAsJsonObject();
			if (!restaurantObject.get("spawned").getAsBoolean()) {
				allGroupsSpawned = false;
			}
			groupIndex = groupIndex + 1;
		}
		return allGroupsSpawned;
	}
}