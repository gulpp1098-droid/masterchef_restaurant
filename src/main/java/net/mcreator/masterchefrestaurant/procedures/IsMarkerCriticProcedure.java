package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

public class IsMarkerCriticProcedure {
	public static boolean execute(Entity entity, double groupIndex) {
		if (entity == null)
			return false;
		com.google.gson.JsonObject Object = new com.google.gson.JsonObject();
		com.google.gson.JsonObject group = new com.google.gson.JsonObject();
		com.google.gson.JsonArray groups = new com.google.gson.JsonArray();
		double spawnTIme = 0;
		double progress = 0;
		double openTime = 0;
		double closeTime = 0;
		if (entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID >= 0) {
			Object = new Object() {
				public com.google.gson.JsonObject parse(String rawJson) {
					try {
						return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
					} catch (Exception e) {
						MasterchefRestaurantMod.LOGGER.error(e);
						return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
					}
				}
			}.parse(entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).OverlayString);
			groups = Object.get("groups").getAsJsonArray();
			if (groupIndex >= groups.size()) {
				return false;
			}
			group = groups.get((int) groupIndex).getAsJsonObject();
			return group.get("critic").getAsBoolean();
		}
		return false;
	}
}