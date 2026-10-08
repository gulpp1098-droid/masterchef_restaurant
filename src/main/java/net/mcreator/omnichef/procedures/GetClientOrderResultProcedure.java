package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.OmnichefMod;

public class GetClientOrderResultProcedure {
	public static com.google.gson.JsonObject execute(Entity clientEntity) {
		if (clientEntity == null)
			return new com.google.gson.JsonObject();
		Entity client = null;
		String resultText = "";
		com.google.gson.JsonObject resultObject = new com.google.gson.JsonObject();
		client = clientEntity;
		resultText = client.getPersistentData().getString("order_result");
		if ((resultText).equals("")) {
			resultText = "{}";
		}
		resultObject = new Object() {
			public com.google.gson.JsonObject parse(String rawJson) {
				try {
					return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
				} catch (Exception e) {
					OmnichefMod.LOGGER.error(e);
					return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
				}
			}
		}.parse(resultText);
		return resultObject;
	}
}