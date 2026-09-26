package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.OmnichefMod;

public class MaxTablesReturnProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		boolean Open = false;
		com.google.gson.JsonObject JSONObject = new com.google.gson.JsonObject();
		JSONObject = new Object() {
			public com.google.gson.JsonObject parse(String rawJson) {
				try {
					return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
				} catch (Exception e) {
					OmnichefMod.LOGGER.error(e);
					return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
				}
			}
		}.parse(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).GUIstring);
		if (JSONObject != null && entity.getData(OmnichefModVariables.PLAYER_VARIABLES).GUIstring.contains("tables")) {
			return JSONObject.get("tables").getAsString();
		}
		return " - ";
	}
}