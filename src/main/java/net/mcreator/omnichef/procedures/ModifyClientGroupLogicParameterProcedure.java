package net.mcreator.omnichef.procedures;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class ModifyClientGroupLogicParameterProcedure {
	public static void execute(boolean logicDependency, double objectIndex, double restaurantIndex, String arrayNameDependency, String fileNameDependency, String filePathDependency, String innerArrayNameDependency, String propertyDependency) {
		if (arrayNameDependency == null || fileNameDependency == null || filePathDependency == null || innerArrayNameDependency == null || propertyDependency == null)
			return;
		File ListOfRestaurants = new File("");
		com.google.gson.JsonArray RestaurantsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray GroupsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject RestaurantsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject GroupObject = new com.google.gson.JsonObject();
		boolean saveSuccessful = false;
		ListOfRestaurants = new File(filePathDependency, File.separator + fileNameDependency);
		{
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(ListOfRestaurants));
				StringBuilder jsonstringbuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonstringbuilder.append(line);
				}
				bufferedReader.close();
				RestaurantsObject = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				RestaurantsArray = RestaurantsObject.get(arrayNameDependency).getAsJsonArray();
				Restaurant = RestaurantsArray.get((int) restaurantIndex).getAsJsonObject();
				GroupsArray = Restaurant.get(innerArrayNameDependency).getAsJsonArray();
				GroupObject = GroupsArray.get((int) objectIndex).getAsJsonObject();
				GroupObject.addProperty(propertyDependency, logicDependency);
				saveSuccessful = SaveJsonAtomicallyProcedure.execute(RestaurantsObject, filePathDependency, fileNameDependency);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}