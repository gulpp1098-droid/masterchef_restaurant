package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class SettleRestaurantShiftProcedure {
	public static boolean execute(LevelAccessor world, double restaurantID) {
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject clientData = new com.google.gson.JsonObject();
		com.google.gson.JsonObject shiftResult = new com.google.gson.JsonObject();
		com.google.gson.JsonObject dailyStats = new com.google.gson.JsonObject();
		com.google.gson.JsonArray restaurants = new com.google.gson.JsonArray();
		double restaurantIndex = 0;
		double shiftID = 0;
		double currentReputation = 0;
		double requestedChange = 0;
		double targetReputation = 0;
		double restaurantLevel = 0;
		double checkpointReputation = 0;
		double gateLevel = 0;
		double gateReputation = 0;
		double actualChange = 0;
		if (world.isClientSide()) {
			return false;
		}
		if (!OmnichefModVariables.MapVariables.get(world).ModDataReady) {
			return false;
		}
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (restaurantIndex < 0) {
			return false;
		}
		database = ReadRestaurantsDatabaseProcedure.execute(world);
		restaurants = database.get("restaurants").getAsJsonArray();
		restaurant = restaurants.get((int) restaurantIndex).getAsJsonObject();
		if (restaurant.get("open").getAsBoolean()) {
			return false;
		}
		if (restaurant.get("active_groups").getAsDouble() > 0) {
			return false;
		}
		clientData = FindClientsInfoByIndexViaIDProcedure.execute(world, restaurantID);
		if (!clientData.has("groups")) {
			return false;
		}
		shiftID = clientData.get("shift_id").getAsDouble();
		if (restaurant.has("last_settled_shift_id")) {
			if (restaurant.get("last_settled_shift_id").getAsDouble() == shiftID) {
				return true;
			}
		}
		shiftResult = CalculateRestaurantShiftResultProcedure.execute(clientData);
		if (!shiftResult.get("ready").getAsBoolean()) {
			return false;
		}
		currentReputation = restaurant.get("reputation").getAsDouble();
		restaurantLevel = restaurant.get("level").getAsDouble();
		requestedChange = shiftResult.get("reputation_change").getAsDouble();
		targetReputation = currentReputation;
		checkpointReputation = GetRequiredReputationForLevelProcedure.execute(world, Math.floor(restaurantLevel / 10) * 10);
		gateLevel = Math.min(OmnichefModVariables.MapVariables.get(world).MaxRestaurantLevel, Math.floor(restaurantLevel / 10 + 1) * 10);
		gateReputation = GetRequiredReputationForLevelProcedure.execute(world, gateLevel);
		if (requestedChange > 0) {
			targetReputation = Math.max(currentReputation, Math.min(currentReputation + requestedChange, gateReputation));
		} else if (requestedChange < 0) {
			if (!((restaurantLevel + 1) % 10 == 0 && currentReputation >= gateReputation)) {
				targetReputation = Math.max(currentReputation + requestedChange, checkpointReputation);
			}
		}
		actualChange = targetReputation - currentReputation;
		dailyStats = restaurant.get("daily_stats").getAsJsonObject();
		restaurant.addProperty("reputation", targetReputation);
		dailyStats.addProperty("reputation_change", (dailyStats.get("reputation_change").getAsDouble() + actualChange));
		shiftResult.addProperty("raw_reputation_change", requestedChange);
		shiftResult.addProperty("reputation_change", actualChange);
		restaurant.add("last_shift_result", shiftResult);
		restaurant.addProperty("last_settled_shift_id", shiftID);
		return SaveJsonAtomicallyProcedure.execute(database, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name);
	}
}