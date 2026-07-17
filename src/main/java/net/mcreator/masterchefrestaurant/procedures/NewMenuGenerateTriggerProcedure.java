package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class NewMenuGenerateTriggerProcedure {
	public static void execute(LevelAccessor world) {
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
		currentTime = world.dayTime() % 24000;
		currentDay = Math.floor(world.dayTime() / 24000d);
		if (currentTime >= 9000) {
			if (currentDay > MasterchefRestaurantModVariables.MapVariables.get(world).LastProcessedDay) {
				MasterchefRestaurantModVariables.MapVariables.get(world).LastProcessedDay = currentDay;
				MasterchefRestaurantModVariables.MapVariables.get(world).markSyncDirty();
				index = 0;
				ListOfMenus = new File(MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name);
				{
					try {
						BufferedReader bufferedReader = new BufferedReader(new FileReader(ListOfMenus));
						StringBuilder jsonstringbuilder = new StringBuilder();
						String line;
						while ((line = bufferedReader.readLine()) != null) {
							jsonstringbuilder.append(line);
						}
						bufferedReader.close();
						restaurantMenus = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
						menusArray = restaurantMenus.get("restaurants").getAsJsonArray();
						AmountOfRestaurant = menusArray.size();
						for (int index0 = 0; index0 < (int) AmountOfRestaurant; index0++) {
							restaurantObject = menusArray.get((int) index).getAsJsonObject();
							if (restaurantObject.get("creation_menu_day").getAsDouble() < restaurantObject.get("last_day_open").getAsDouble()) {
								restaurantID = restaurantObject.get("ID").getAsDouble();
								restaurantTier = restaurantObject.get("level").getAsDouble();
								restaurantObject.addProperty("creation_menu_day", currentDay);
								{
									com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
									try {
										FileWriter fileWriter = new FileWriter(ListOfMenus);
										fileWriter.write(mainGSONBuilderVariable.toJson(restaurantMenus));
										fileWriter.close();
									} catch (IOException exception) {
										exception.printStackTrace();
									}
								}
								GenerateRestaurantMenuProcedure.execute(world, restaurantID, restaurantTier);
							}
							index = index + 1;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}