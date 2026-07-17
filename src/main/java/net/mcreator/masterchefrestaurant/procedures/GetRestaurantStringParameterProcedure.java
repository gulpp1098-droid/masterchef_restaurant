package net.mcreator.masterchefrestaurant.procedures;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class GetRestaurantStringParameterProcedure {
	public static String execute(double numberIndex, String arrayNameDependency, String fileNameDependency, String filePathDependency, String propertyNameDependency) {
		if (arrayNameDependency == null || fileNameDependency == null || filePathDependency == null || propertyNameDependency == null)
			return "";
		File ListOfRestaurants = new File("");
		com.google.gson.JsonObject RestaurantsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonArray RestaurantsArray = new com.google.gson.JsonArray();
		double index = 0;
		String value = "";
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
				value = Restaurant.get(propertyNameDependency).getAsString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
}