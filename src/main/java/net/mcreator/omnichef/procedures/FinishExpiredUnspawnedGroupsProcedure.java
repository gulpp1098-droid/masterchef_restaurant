package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class FinishExpiredUnspawnedGroupsProcedure {
	public static boolean execute(LevelAccessor world) {
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantData = new com.google.gson.JsonObject();
		com.google.gson.JsonObject group = new com.google.gson.JsonObject();
		com.google.gson.JsonArray restaurants = new com.google.gson.JsonArray();
		com.google.gson.JsonArray groups = new com.google.gson.JsonArray();
		double restaurantIndex = 0;
		double groupIndex = 0;
		double restaurantID = 0;
		double dataIndex = 0;
		double expireTime = 0;
		boolean changed = false;
		boolean restaurantOpen = false;
		boolean groupFinished = false;
		if (world.isClientSide()) {
			return false;
		}
		if (!OmnichefModVariables.MapVariables.get(world).ModDataReady) {
			return false;
		}
		database = ReadClientsDatabaseProcedure.execute(world);
		restaurants = database.get("restaurants").getAsJsonArray();
		for (int _i1 = 0; _i1 < (int) restaurants.size(); _i1++) {
			restaurantData = restaurants.get((int) dataIndex).getAsJsonObject();
			restaurantID = restaurantData.get("ID").getAsDouble();
			restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
			if (restaurantIndex >= 0) {
				restaurantOpen = GetRestaurantLogicParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						"open");
				groups = restaurantData.get("groups").getAsJsonArray();
				groupIndex = 0;
				for (int _i2 = 0; _i2 < (int) groups.size(); _i2++) {
					group = groups.get((int) groupIndex).getAsJsonObject();
					groupFinished = false;
					if (group.has("finished")) {
						groupFinished = group.get("finished").getAsBoolean();
					}
					if (!group.get("spawned").getAsBoolean() && !groupFinished) {
						if (group.has("client_expire_time")) {
							expireTime = group.get("client_expire_time").getAsDouble();
						} else {
							expireTime = Math.floor(restaurantData.get("shift_id").getAsDouble() / 24000) * 24000 + group.get("spawn_time").getAsDouble() + 8000;
						}
						if (world.dayTime() >= expireTime || !restaurantOpen) {
							group = FinishUnspawnedClientGroupProcedure.execute(group);
							changed = true;
						}
					}
					groupIndex = groupIndex + 1;
				}
			}
			dataIndex = dataIndex + 1;
		}
		if (changed) {
			return SaveJsonAtomicallyProcedure.execute(database, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, OmnichefModVariables.MapVariables.get(world).ClientsDatabase_File_Name);
		}
		return true;
	}
}