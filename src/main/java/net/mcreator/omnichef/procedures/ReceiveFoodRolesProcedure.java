package net.mcreator.omnichef.procedures;

import net.mcreator.omnichef.OmnichefMod;

public class ReceiveFoodRolesProcedure {
	public static void execute(String inboundString) {
		if (inboundString == null)
			return;
		com.google.gson.JsonObject payload = new com.google.gson.JsonObject();
		payload = new Object() {
			public com.google.gson.JsonObject parse(String rawJson) {
				try {
					return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
				} catch (Exception e) {
					OmnichefMod.LOGGER.error(e);
					return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
				}
			}
		}.parse(inboundString);
		net.mcreator.omnichef.FoodRoleClientCache.receive(payload);
	}
}