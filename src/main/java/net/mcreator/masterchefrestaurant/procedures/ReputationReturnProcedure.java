package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.stats.Stats;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

public class ReputationReturnProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		double restaurantID = 0;
		com.google.gson.JsonObject Stats = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Data = new com.google.gson.JsonObject();
		if (entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID >= 0) {
			Data = new Object() {
				public com.google.gson.JsonObject parse(String rawJson) {
					try {
						return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
					} catch (Exception e) {
						MasterchefRestaurantMod.LOGGER.error(e);
						return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
					}
				}
			}.parse(entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).GUIstring);
			return Data.get("reputation_change").getAsString();
		}
		return "0";
	}
}