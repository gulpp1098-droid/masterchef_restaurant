package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.stats.Stats;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.OmnichefMod;

public class CoinsEarnedReturnProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		double restaurantID = 0;
		com.google.gson.JsonObject Stats = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Data = new com.google.gson.JsonObject();
		if (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID >= 0) {
			Data = new Object() {
				public com.google.gson.JsonObject parse(String rawJson) {
					try {
						return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
					} catch (Exception e) {
						OmnichefMod.LOGGER.error(e);
						return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
					}
				}
			}.parse(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).GUIstring);
			if (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).GUIstring.contains("coins_earned")) {
				return Data.get("coins_earned").getAsString();
			}
		}
		return "0";
	}
}