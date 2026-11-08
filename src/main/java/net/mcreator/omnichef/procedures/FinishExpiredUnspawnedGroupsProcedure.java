package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class FinishExpiredUnspawnedGroupsProcedure {
	public static boolean execute(LevelAccessor world) {
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantData = new com.google.gson.JsonObject();
		com.google.gson.JsonObject group = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantDatabase = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray restaurants = new com.google.gson.JsonArray();
		com.google.gson.JsonArray groups = new com.google.gson.JsonArray();
		com.google.gson.JsonArray restaurantsArray = new com.google.gson.JsonArray();
		double restaurantIndex = 0;
		double groupIndex = 0;
		double restaurantID = 0;
		double dataIndex = 0;
		double expireTime = 0;
		boolean changed = false;
		boolean restaurantOpen = false;
		boolean groupFinished = false;
		boolean restaurantChanged = false;
		String groupKey = "";
		if (world.isClientSide()) {
			return false;
		}
		if (!OmnichefModVariables.MapVariables.get(world).ModDataReady) {
			return false;
		}
		database = ReadClientsDatabaseProcedure.execute(world);
		restaurantDatabase = ReadRestaurantsDatabaseProcedure.execute(world);
		restaurantsArray = restaurantDatabase.get("restaurants").getAsJsonArray();
		restaurants = database.get("restaurants").getAsJsonArray();
		for (int _i1 = 0; _i1 < (int) restaurants.size(); _i1++) {
			restaurantData = restaurants.get((int) dataIndex).getAsJsonObject();
			restaurantID = restaurantData.get("ID").getAsDouble();
			restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
			if (restaurantIndex >= 0) {
				restaurantObject = restaurantsArray.get((int) restaurantIndex).getAsJsonObject();
				restaurantOpen = restaurantObject.get("open").getAsBoolean();
				groups = restaurantData.get("groups").getAsJsonArray();
				groupIndex = 0;
				for (int _i2 = 0; _i2 < (int) groups.size(); _i2++) {
					group = groups.get((int) groupIndex).getAsJsonObject();
					groupFinished = false;
					if (group.has("finished")) {
						groupFinished = group.get("finished").getAsBoolean();
					}
					if (!groupFinished) {
						if (group.has("client_expire_time")) {
							expireTime = group.get("client_expire_time").getAsDouble();
						} else {
							expireTime = Math.floor(restaurantData.get("shift_id").getAsDouble() / 24000) * 24000 + group.get("spawn_time").getAsDouble() + 8000;
						}
						if (group.get("spawned").getAsBoolean()) {
							if (world.dayTime() >= expireTime) {
								groupKey = new java.text.DecimalFormat("##.##").format(group.get("shift_id").getAsDouble());
								restaurantObject = FinishActiveGroupDataProcedure.execute(restaurantObject, group.get("shift_id").getAsDouble(), groupKey);
								group = FinishSpawnedClientGroupProcedure.execute(group);
								restaurantChanged = true;
								changed = true;
							}
						} else {
							if (world.dayTime() >= expireTime || !restaurantOpen) {
								group = FinishUnspawnedClientGroupProcedure.execute(group);
								changed = true;
							}
						}
					}
					groupIndex = groupIndex + 1;
				}
			}
			dataIndex = dataIndex + 1;
		}
		if (restaurantChanged) {
			if (!SaveJsonAtomicallyProcedure.execute(restaurantDatabase, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name)) {
				return false;
			}
		}
		if (changed) {
			return SaveJsonAtomicallyProcedure.execute(database, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, OmnichefModVariables.MapVariables.get(world).ClientsDatabase_File_Name);
		}
		return true;
	}
}