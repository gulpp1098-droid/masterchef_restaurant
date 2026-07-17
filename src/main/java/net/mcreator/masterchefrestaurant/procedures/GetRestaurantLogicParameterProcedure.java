package net.mcreator.masterchefrestaurant.procedures;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class GetRestaurantLogicParameterProcedure {
	public static boolean execute(double numberIndex, String arrayNameDependency, String fileNameDependency, String filePathDependency, String propertyDependency) {
		if (arrayNameDependency == null || fileNameDependency == null || filePathDependency == null || propertyDependency == null)
			return false;
		File ListOfRestaurants = new File("");
		com.google.gson.JsonObject RestaurantsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonArray RestaurantsArray = new com.google.gson.JsonArray();
		double index = 0;
		boolean value = false;
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
				index = numberIndex;
				Restaurant = RestaurantsArray.get((int) index).getAsJsonObject();
				value = Restaurant.get(propertyDependency).getAsBoolean();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
}