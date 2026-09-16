package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

public class ReturnTimelineOpenRestaurantProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		com.google.gson.JsonObject Data = new com.google.gson.JsonObject();
		if (Level.OVERWORLD == (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))) {
			if (entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID >= 0) {
				Data = new Object() {
					public com.google.gson.JsonObject parse(String rawJson) {
						try {
							return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
						} catch (Exception e) {
							MasterchefRestaurantMod.LOGGER.error(e);
							return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
						}
					}
				}.parse(entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).OverlayString);
				return Data.get("open").getAsBoolean();
			}
		}
		return false;
	}
}