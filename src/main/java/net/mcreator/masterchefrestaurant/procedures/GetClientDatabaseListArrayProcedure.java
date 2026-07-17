package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class GetClientDatabaseListArrayProcedure {
	public static com.google.gson.JsonArray execute(LevelAccessor world) {
		File ListOfRestaurants = new File("");
		com.google.gson.JsonObject RestaurantsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray RestaurantsArray = new com.google.gson.JsonArray();
		ListOfRestaurants = new File(MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).ClientsDatabase_File_Name);
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
				RestaurantsArray = RestaurantsObject.get("restaurants").getAsJsonArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return RestaurantsArray;
	}
}