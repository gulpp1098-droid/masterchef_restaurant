package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

public class ReturnTimelineProgressProcedure {
	public static double execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return 0;
		double progress = 0;
		double visiblePart = 0;
		double openTime = 0;
		double closeTime = 0;
		String String = "";
		com.google.gson.JsonObject Object = new com.google.gson.JsonObject();
		if (entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID >= 0) {
			String = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).OverlayString;
			Object = new Object() {
				public com.google.gson.JsonObject parse(String rawJson) {
					try {
						return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
					} catch (Exception e) {
						MasterchefRestaurantMod.LOGGER.error(e);
						return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
					}
				}
			}.parse(String);
			openTime = Object.get("openTime").getAsDouble();
			closeTime = Object.get("closeTime").getAsDouble();
			progress = (world.dayTime() % 24000 - openTime) / (closeTime - openTime);
			if (progress < 0) {
				return 0;
			}
			visiblePart = 120 * progress;
			return visiblePart;
		}
		return 0;
	}
}