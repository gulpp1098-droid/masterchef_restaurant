package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class RecordClientShiftResultProcedure {
	public static boolean execute(LevelAccessor world, Entity clientEntity) {
		if (clientEntity == null)
			return false;
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject groupObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject memberObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray groups = new com.google.gson.JsonArray();
		com.google.gson.JsonArray members = new com.google.gson.JsonArray();
		com.google.gson.JsonArray restaurants = new com.google.gson.JsonArray();
		double restaurantIndex = 0;
		double groupIndex = 0;
		double memberIndex = 0;
		double satisfaction = 0;
		boolean served = false;
		if (world.isClientSide()) {
			return false;
		}
		database = ReadClientsDatabaseProcedure.execute(world);
		restaurants = database.get("restaurants").getAsJsonArray();
		restaurantIndex = ClientDatabaseIndexSearchByIDProcedure.execute(world, clientEntity.getPersistentData().getDouble("RestaurantID"));
		if (restaurantIndex < 0) {
			return false;
		}
		restaurant = restaurants.get((int) restaurantIndex).getAsJsonObject();
		if (!(clientEntity.getPersistentData().getDouble("shift_id") == restaurant.get("shift_id").getAsDouble())) {
			return false;
		}
		groups = restaurant.get("groups").getAsJsonArray();
		groupIndex = clientEntity.getPersistentData().getDouble("group");
		if (groupIndex < 0 || groupIndex >= groups.size()) {
			return false;
		}
		groupObject = groups.get((int) groupIndex).getAsJsonObject();
		members = groupObject.get("members").getAsJsonArray();
		memberIndex = clientEntity.getPersistentData().getDouble("member");
		if (memberIndex < 0 || memberIndex >= members.size()) {
			return false;
		}
		memberObject = members.get((int) memberIndex).getAsJsonObject();
		if (memberObject.get("result_recorded").getAsBoolean()) {
			return true;
		}
		served = clientEntity.getPersistentData().getBoolean("order_served");
		if (served) {
			satisfaction = GetClientOrderSatisfactionProcedure.execute(clientEntity);
		}
		memberObject.addProperty("order_served", served);
		memberObject.addProperty("final_satisfaction", satisfaction);
		memberObject.addProperty("result_recorded", true);
		return SaveJsonAtomicallyProcedure.execute(database, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, OmnichefModVariables.MapVariables.get(world).ClientsDatabase_File_Name);
	}
}