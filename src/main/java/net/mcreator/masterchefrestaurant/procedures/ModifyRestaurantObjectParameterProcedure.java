package net.mcreator.masterchefrestaurant.procedures;

import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class ModifyRestaurantObjectParameterProcedure {
	public static void execute(double numberIndex, double value, String arrayNameDependency, String fileNameDependency, String filePathDependency, String objectNameDependency, String valueNameDependency) {
		if (arrayNameDependency == null || fileNameDependency == null || filePathDependency == null || objectNameDependency == null || valueNameDependency == null)
			return;
		File ListOfRestaurants = new File("");
		com.google.gson.JsonArray RestaurantsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray EditArrayLocation = new com.google.gson.JsonArray();
		boolean alreadyExists = false;
		com.google.gson.JsonObject RestaurantsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject RestaurantObject = new com.google.gson.JsonObject();
		double index = 0;
		double duplicateLocationIndex = 0;
		double ObjectProperty = 0;
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
				RestaurantObject = Restaurant.get(objectNameDependency).getAsJsonObject();
				ObjectProperty = RestaurantObject.get(valueNameDependency).getAsDouble();
				RestaurantObject.addProperty(valueNameDependency, (value + ObjectProperty));
				{
					com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
					try {
						FileWriter fileWriter = new FileWriter(ListOfRestaurants);
						fileWriter.write(mainGSONBuilderVariable.toJson(RestaurantsObject));
						fileWriter.close();
					} catch (IOException exception) {
						exception.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}