package net.mcreator.masterchefrestaurant.procedures;

import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class RemoveRestaurantArrayParameterIndexProcedure {
	public static void execute(double numberIndex, String arrayNameDependency, String arrayParameterNameDependency, String fileNameDependency, String filePathDependency, String locationDependency) {
		if (arrayNameDependency == null || arrayParameterNameDependency == null || fileNameDependency == null || filePathDependency == null || locationDependency == null)
			return;
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
				alreadyExists = false;
				duplicateLocationIndex = 0;
				for (int _i1 = 0; _i1 < (int) EditArrayLocation.size(); _i1++) {
					if ((EditArrayLocation.get((int) duplicateLocationIndex).getAsString()).equals(locationDependency)) {
						alreadyExists = true;
						break;
					}
					duplicateLocationIndex = duplicateLocationIndex + 1;
				}
				if (alreadyExists) {
					EditArrayLocation.remove(((int) duplicateLocationIndex));
				}
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