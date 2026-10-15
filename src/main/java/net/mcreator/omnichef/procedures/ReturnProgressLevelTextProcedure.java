package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.OmnichefMod;

public class ReturnProgressLevelTextProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		com.google.gson.JsonObject data = new com.google.gson.JsonObject();
		data = new Object() {
			public com.google.gson.JsonObject parse(String rawJson) {
				try {
					return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
				} catch (Exception e) {
					OmnichefMod.LOGGER.error(e);
					return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
				}
			}
		}.parse(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).OverlayString);
		if (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID <= 0) {
			return "";
		}
		if (!data.has("restaurant_level")) {
			return "";
		}
		return "Lvl " + (int) data.get("restaurant_level").getAsDouble();
	}
}