package net.mcreator.masterchefrestaurant.procedures;

import java.io.File;

public class NewMenuGenerateTriggerProcedure {
	public static void execute() {
		File ListOfMenus = new File("");
		com.google.gson.JsonArray menusArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray menuArray = new com.google.gson.JsonArray();
		double currentTime = 0;
		double currentDay = 0;
		double AmountOfRestaurant = 0;
		double index = 0;
		double restaurantID = 0;
		double restaurantTier = 0;
		com.google.gson.JsonObject restaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantMenus = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurant = new com.google.gson.JsonObject();
	}
}