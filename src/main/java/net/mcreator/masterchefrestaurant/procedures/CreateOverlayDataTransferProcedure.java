package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

public class CreateOverlayDataTransferProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double closeTime = 0;
		double restaurantLevel = 0;
		double requiredReputation = 0;
		double restaurantIndex = 0;
		double groupsIndex = 0;
		double memberIndex = 0;
		com.google.gson.JsonObject JSONObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject RestaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject dailyStatsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject ClientsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject groupsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject groupObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject memberObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray groupsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray members = new com.google.gson.JsonArray();
		com.google.gson.JsonArray timelineGroupsArray = new com.google.gson.JsonArray();
		boolean critic = false;
		if (!world.isClientSide()) {
			JSONObject = new Object() {
				public com.google.gson.JsonObject parse(String rawJson) {
					try {
						return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
					} catch (Exception e) {
						MasterchefRestaurantMod.LOGGER.error(e);
						return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
					}
				}
			}.parse("{}");
			JSONObject.addProperty("open", false);
			if (entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID > -1) {
				RestaurantObject = FindRestaurantInfoByIndexViaIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
				closeTime = RestaurantObject.get("close_time").getAsDouble();
				JSONObject.addProperty("closeTime", closeTime);
				JSONObject.addProperty("openTime", (closeTime - 8000));
				if (RestaurantObject.get("open").getAsBoolean()) {
					ClientsObject = FindClientsInfoByIndexViaIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
					groupsArray = ClientsObject.get("groups").getAsJsonArray();
					groupsIndex = 0;
					for (int _i1 = 0; _i1 < (int) groupsArray.size(); _i1++) {
						groupObject = groupsArray.get((int) groupsIndex).getAsJsonObject();
						groupsObject = new Object() {
							public com.google.gson.JsonObject parse(String rawJson) {
								try {
									return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
								} catch (Exception e) {
									MasterchefRestaurantMod.LOGGER.error(e);
									return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
								}
							}
						}.parse("{}");
						critic = false;
						groupsObject.addProperty("spawn_time", groupObject.get("spawn_time").getAsDouble());
						members = groupObject.get("members").getAsJsonArray();
						memberIndex = 0;
						for (int _i2 = 0; _i2 < (int) members.size(); _i2++) {
							memberObject = members.get((int) memberIndex).getAsJsonObject();
							if (memberObject.get("critic").getAsBoolean()) {
								critic = true;
							}
							memberIndex = memberIndex + 1;
						}
						if (critic) {
							groupsObject.addProperty("critic", true);
						} else {
							groupsObject.addProperty("critic", false);
						}
						timelineGroupsArray.add(groupsObject);
						groupsIndex = groupsIndex + 1;
					}
				}
				JSONObject.add("groups", timelineGroupsArray);
				JSONObject.addProperty("open", RestaurantObject.get("open").getAsBoolean());
				dailyStatsObject = RestaurantObject.get("daily_stats").getAsJsonObject();
				restaurantLevel = RestaurantObject.get("level").getAsDouble();
				JSONObject.addProperty("level", ("Level: " + (int) restaurantLevel));
				JSONObject.addProperty("name", ("Name: " + RestaurantObject.get("name").getAsString()));
				JSONObject.addProperty("owner", ("Owner: " + RestaurantObject.get("ownerName").getAsString()));
				if (restaurantLevel >= 100) {
					JSONObject.addProperty("reputation", ("Reputation: " + (int) RestaurantObject.get("reputation").getAsDouble() + " / " + "MAX"));
				} else {
					requiredReputation = (restaurantLevel + 1) * 40 + Math.pow(restaurantLevel + 1, 2) * 6 + Math.pow(restaurantLevel + 1, 3) * 0.08;
					JSONObject.addProperty("reputation", ("Reputation: " + (int) RestaurantObject.get("reputation").getAsDouble() + " / " + (int) requiredReputation));
				}
				JSONObject.addProperty("tables", ("Max: " + (int) Math.min(10, Math.floor(restaurantLevel / 10) + 1)));
				JSONObject.addProperty("queue", ("Max: " + (int) (Math.floor(restaurantLevel / 10) + 2)));
				JSONObject.addProperty("locations", ("Max: " + (int) Math.min(30, Math.floor(restaurantLevel * (26d / 100)) + 4)));
				JSONObject.addProperty("customers_served", dailyStatsObject.get("customers_served").getAsDouble());
				JSONObject.addProperty("customers_served_fully", dailyStatsObject.get("customers_served_fully").getAsDouble());
				JSONObject.addProperty("customers_lost", dailyStatsObject.get("customers_lost").getAsDouble());
				JSONObject.addProperty("coins_earned", dailyStatsObject.get("coins_earned").getAsDouble());
				JSONObject.addProperty("reputation_change", dailyStatsObject.get("reputation_change").getAsDouble());
			}
			{
				MasterchefRestaurantModVariables.PlayerVariables _vars = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
				_vars.OverlayString = JSONObject.toString();
				_vars.markSyncDirty();
			}
		}
	}
}