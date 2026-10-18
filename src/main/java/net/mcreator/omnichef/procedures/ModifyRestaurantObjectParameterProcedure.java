package net.mcreator.omnichef.procedures;

import java.io.File;

public class ModifyRestaurantObjectParameterProcedure {
	public static void execute(double numberIndex, double value, String arrayNameDependency, String fileNameDependency, String filePathDependency, String objectNameDependency, String valueNameDependency) {
		if (arrayNameDependency == null || fileNameDependency == null || filePathDependency == null || objectNameDependency == null || valueNameDependency == null)
			return;
		File ListOfRestaurants = new File("");
		com.google.gson.JsonArray RestaurantsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray EditArrayLocation = new com.google.gson.JsonArray();
		boolean alreadyExists = false;
		boolean saveSuccessful = false;
		com.google.gson.JsonObject RestaurantsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject RestaurantObject = new com.google.gson.JsonObject();
		double index = 0;
		double duplicateLocationIndex = 0;
		double ObjectProperty = 0;
		alreadyExists = TryModifyRestaurantObjectParameterProcedure.execute(numberIndex, value, arrayNameDependency, fileNameDependency, filePathDependency, objectNameDependency, valueNameDependency);
	}
}