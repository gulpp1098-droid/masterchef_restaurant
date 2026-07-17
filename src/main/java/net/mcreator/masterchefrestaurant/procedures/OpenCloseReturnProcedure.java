package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

public class OpenCloseReturnProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		boolean Open = false;
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
		if (JSONObject != null && !(JSONObject.size() == 0)) {
			if (JSONObject.get("open").getAsBoolean()) {
				return 0;
			}
		}
		return 1;
	}
}