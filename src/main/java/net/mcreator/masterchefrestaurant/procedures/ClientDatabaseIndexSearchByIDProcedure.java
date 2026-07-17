package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import java.io.File;

public class ClientDatabaseIndexSearchByIDProcedure {
	public static double execute(LevelAccessor world, double numberiD) {
		Entity Owner = null;
		File ListOfRestaurants = new File("");
		com.google.gson.JsonArray Restaurant_Array = new com.google.gson.JsonArray();
		com.google.gson.JsonObject Restaurants = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Restaurant = new com.google.gson.JsonObject();
		double index = 0;
		double indexFound = 0;
		double restaurantID = 0;
		double currentID = 0;
		Restaurant_Array = GetClientDatabaseListArrayProcedure.execute(world);
		indexFound = -1;
		index = 0;
		restaurantID = numberiD;
		for (int index0 = 0; index0 < (int) Restaurant_Array.size(); index0++) {
			Restaurant = Restaurant_Array.get((int) index).getAsJsonObject();
			currentID = Restaurant.get("ID").getAsDouble();
			if (currentID == restaurantID) {
				indexFound = index;
				break;
			}
			index = index + 1;
		}
		return indexFound;
	}
}