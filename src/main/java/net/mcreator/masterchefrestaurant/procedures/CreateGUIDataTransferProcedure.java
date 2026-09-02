package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

public class CreateGUIDataTransferProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double closeTime = 0;
		double restaurantLevel = 0;
		double requiredReputation = 0;
		double restaurantIndex = 0;
		com.google.gson.JsonObject JSONObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject RestaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject dailyStatsObject = new com.google.gson.JsonObject();
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
			if (entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID > -1) {
				RestaurantObject = FindRestaurantInfoByIndexViaIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
				dailyStatsObject = RestaurantObject.get("daily_stats").getAsJsonObject();
				restaurantLevel = RestaurantObject.get("level").getAsDouble();
				JSONObject.addProperty("open", RestaurantObject.get("open").getAsBoolean());
				closeTime = RestaurantObject.get("close_time").getAsDouble();
				JSONObject.addProperty("closeTime", ("Close time: " + (int) Math.floor(closeTime / 1000 + 6) + ":" + new java.text.DecimalFormat("00").format(Math.floor((closeTime % 1000) * (60d / 1000)))));
				JSONObject.addProperty("level", ("Level: " + (int) restaurantLevel));
				JSONObject.addProperty("name", ("Name: " + RestaurantObject.get("name").getAsString()));
				JSONObject.addProperty("owner", ("Owner: " + RestaurantObject.get("ownerName").getAsString()));
				if (restaurantLevel >= 100) {
					JSONObject.addProperty("reputation", ("Reputation: " + (int) RestaurantObject.get("reputation").getAsDouble() + " / " + "MAX"));
				} else {
					requiredReputation = (restaurantLevel + 1) * 40 + Math.pow(restaurantLevel + 1, 2) * 6 + Math.pow(restaurantLevel + 1, 3) * 0.08;
					JSONObject.addProperty("reputation", ("Reputation: " + (int) RestaurantObject.get("reputation").getAsDouble() + " / " + (int) requiredReputation));
				}
				JSONObject.addProperty("tables", ("Max: " + (int) (Math.floor(restaurantLevel / 10) + 1)));
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
				_vars.GUIstring = JSONObject.toString();
				_vars.markSyncDirty();
			}
		}
	}
}