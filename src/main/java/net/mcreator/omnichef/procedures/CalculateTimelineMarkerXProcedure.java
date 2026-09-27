package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.OmnichefMod;

public class CalculateTimelineMarkerXProcedure {
	public static double execute(Entity entity, double groupIndex) {
		if (entity == null)
			return 0;
		com.google.gson.JsonObject Object = new com.google.gson.JsonObject();
		com.google.gson.JsonObject group = new com.google.gson.JsonObject();
		com.google.gson.JsonArray groups = new com.google.gson.JsonArray();
		double spawnTIme = 0;
		double progress = 0;
		double openTime = 0;
		double closeTime = 0;
		String overlayString = "";
		overlayString = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).OverlayString;
		if (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID >= 0 && overlayString.contains("groups") && overlayString.contains("spawn_time") && overlayString.contains("openTime") && overlayString.contains("closeTime")) {
			Object = new Object() {
				public com.google.gson.JsonObject parse(String rawJson) {
					try {
						return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
					} catch (Exception e) {
						OmnichefMod.LOGGER.error(e);
						return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
					}
				}
			}.parse(overlayString);
			groups = Object.get("groups").getAsJsonArray();
			if (groupIndex >= groups.size() && groupIndex < 0) {
				return -1;
			}
			group = groups.get((int) groupIndex).getAsJsonObject();
			spawnTIme = group.get("spawn_time").getAsDouble();
			openTime = Object.get("openTime").getAsDouble();
			closeTime = Object.get("closeTime").getAsDouble();
			if (closeTime <= spawnTIme) {
				return -1;
			}
			progress = (spawnTIme - openTime) / (closeTime - openTime);
			if (progress < 0) {
				return 0;
			} else if (progress > 1) {
				return 120;
			}
			return progress * 120;
		}
		return -1;
	}
}