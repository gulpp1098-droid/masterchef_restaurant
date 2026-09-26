package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.OmnichefMod;

public class ReturnRestaurantStarsProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
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
			return (int) Math.floor(new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(Data.get("level").getAsString().substring((int) Data.get("level").getAsString().indexOf(" "))) / 10);
		}
		return 0;
	}
}