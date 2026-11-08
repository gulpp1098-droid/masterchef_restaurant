package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ClientFinishActiveGroupProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		double restaurantID = 0;
		double restaurantIndex = 0;
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject clientData = new com.google.gson.JsonObject();
		com.google.gson.JsonObject completedGroups = new com.google.gson.JsonObject();
		com.google.gson.JsonArray restaurants = new com.google.gson.JsonArray();
		String groupKey = "";
		if (!world.isClientSide() && entity.getPersistentData().getBoolean("leader") && !entity.getPersistentData().getBoolean("active_group_finished")) {
			restaurantID = entity.getPersistentData().getDouble("RestaurantID");
			restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
			if (restaurantIndex >= 0) {
				clientData = FindClientsInfoByIndexViaIDProcedure.execute(world, restaurantID);
				if (!clientData.has("shift_id")) {
					return true;
				}
				if (!(entity.getPersistentData().getDouble("shift_id") == clientData.get("shift_id").getAsDouble())) {
					return true;
				}
				database = ReadRestaurantsDatabaseProcedure.execute(world);
				restaurants = database.get("restaurants").getAsJsonArray();
				restaurant = restaurants.get((int) restaurantIndex).getAsJsonObject();
				groupKey = new java.text.DecimalFormat("##.##").format(entity.getPersistentData().getDouble("group"));
				restaurant = FinishActiveGroupDataProcedure.execute(restaurant, clientData.get("shift_id").getAsDouble(), groupKey);
				if (SaveJsonAtomicallyProcedure.execute(database, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name)) {
					entity.getPersistentData().putBoolean("active_group_finished", true);
				}
			}
		}
		return true;
	}
}