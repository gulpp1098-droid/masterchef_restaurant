package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.lang.reflect.Array;

import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class DebugRemoveRestaurantAllProcedure {
	public static void execute(LevelAccessor world) {
		File ListOfRestaurants = new File("");
		double foundIndex = 0;
		double index = 0;
		com.google.gson.JsonArray Array = new com.google.gson.JsonArray();
		com.google.gson.JsonArray RestaurantsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject RestaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject RestaurantsObject = new com.google.gson.JsonObject();
		ListOfRestaurants = new File(MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name);
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
				RestaurantsObject.add("restaurants", Array);
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