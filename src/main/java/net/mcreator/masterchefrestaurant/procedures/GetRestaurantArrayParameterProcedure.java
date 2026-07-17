package net.mcreator.masterchefrestaurant.procedures;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class GetRestaurantArrayParameterProcedure {
	public static com.google.gson.JsonArray execute(double numberIndex, String arrayNameDependency, String arrayParameterNameDependency, String fileNameDependency, String filePathDependency) {
		if (arrayNameDependency == null || arrayParameterNameDependency == null || fileNameDependency == null || filePathDependency == null)
			return new com.google.gson.JsonArray();
		File ListOfRestaurants = new File("");
		com.google.gson.JsonObject RestaurantsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonArray RestaurantsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray EditArrayLocation = new com.google.gson.JsonArray();
		boolean alreadyExists = false;
		double index = 0;
		double duplicateLocationIndex = 0;
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
				EditArrayLocation = Restaurant.get(arrayParameterNameDependency).getAsJsonArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return EditArrayLocation;
	}
}