package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.OmnichefMod;

public class IsMarkerCriticProcedure {
	public static boolean execute(Entity entity, double groupIndex) {
		if (entity == null)
			return false;
		com.google.gson.JsonObject Object = new com.google.gson.JsonObject();
		com.google.gson.JsonObject group = new com.google.gson.JsonObject();
		com.google.gson.JsonArray groups = new com.google.gson.JsonArray();
		String overlayString = "";
		overlayString = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).OverlayString;
		if (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID >= 0 && overlayString.contains("groups") && overlayString.contains("critic")) {
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
			if (groupIndex >= groups.size() || groupIndex < 0) {
				return false;
			}
			group = groups.get((int) groupIndex).getAsJsonObject();
			return group.get("critic").getAsBoolean();
		}
		return false;
	}
}