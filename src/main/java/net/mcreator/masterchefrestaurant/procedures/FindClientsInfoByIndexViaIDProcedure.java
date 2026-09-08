package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

import java.io.File;

public class FindClientsInfoByIndexViaIDProcedure {
	public static com.google.gson.JsonObject execute(LevelAccessor world, double numberID) {
		File ListOfRestaurants = new File("");
		double index = 0;
		com.google.gson.JsonArray Restaurant_Array = new com.google.gson.JsonArray();
		com.google.gson.JsonObject Restaurants = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Restaurant = new com.google.gson.JsonObject();
		String ownerUUID = "";
		Restaurant_Array = GetClientDatabaseListArrayProcedure.execute(world);
		index = ClientDatabaseIndexSearchByIDProcedure.execute(world, numberID);
		if (index >= 0) {
			Restaurant = Restaurant_Array.get((int) index).getAsJsonObject();
			return Restaurant;
		}
		return new Object() {
			public com.google.gson.JsonObject parse(String rawJson) {
				try {
					return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
				} catch (Exception e) {
					MasterchefRestaurantMod.LOGGER.error(e);
					return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
				}
			}
		}.parse("{}");
	}
}