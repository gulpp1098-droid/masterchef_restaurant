package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

public class MenuNameReturnProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		com.google.gson.JsonObject JSONObject = new com.google.gson.JsonObject();
		JSONObject = new Object() {
			public com.google.gson.JsonObject parse(String rawJson) {
				try {
					return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
				} catch (Exception e) {
					MasterchefRestaurantMod.LOGGER.error(e);
					return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
				}
			}
		}.parse(entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).GUIstring);
		if (JSONObject != null && !(JSONObject.size() == 0) && JSONObject.get("open").getAsBoolean()) {
			return "Current Menu";
		}
		return "Incoming Menu";
	}
}