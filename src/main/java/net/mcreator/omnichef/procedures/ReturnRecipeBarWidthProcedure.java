package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.OmnichefMod;

public class ReturnRecipeBarWidthProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		com.google.gson.JsonObject data = new com.google.gson.JsonObject();
		double current = 0;
		double target = 0;
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
		if (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID <= 0 || !data.has("reputation_current")) {
			return 0;
		}
		current = data.get("discovery_current").getAsDouble();
		target = data.get("discovery_target").getAsDouble();
		if (target <= 0) {
			return 0;
		}
		return 76 * Math.min(1, Math.max(0, current / target));
	}
}