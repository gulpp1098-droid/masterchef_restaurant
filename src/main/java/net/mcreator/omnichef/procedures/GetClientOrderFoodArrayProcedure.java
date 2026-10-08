package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.OmnichefMod;

public class GetClientOrderFoodArrayProcedure {
	public static com.google.gson.JsonArray execute(Entity clientEntity) {
		if (clientEntity == null)
			return new com.google.gson.JsonArray();
		Entity client = null;
		String foodText = "";
		com.google.gson.JsonObject wrapperObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		client = clientEntity;
		foodText = client.getPersistentData().getString("food");
		if ((foodText).equals("")) {
			foodText = "[]";
		}
		wrapperObject = new Object() {
			public com.google.gson.JsonObject parse(String rawJson) {
				try {
					return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
				} catch (Exception e) {
					OmnichefMod.LOGGER.error(e);
					return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
				}
			}
		}.parse(("{\"data\":" + "" + foodText + "}"));
		foodArray = wrapperObject.get("data").getAsJsonArray();
		return foodArray;
	}
}