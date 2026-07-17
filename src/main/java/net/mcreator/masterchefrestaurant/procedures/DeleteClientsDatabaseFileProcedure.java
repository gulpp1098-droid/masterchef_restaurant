package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class DeleteClientsDatabaseFileProcedure {
	public static void execute(LevelAccessor world) {
		com.google.gson.JsonArray emptyArray = new com.google.gson.JsonArray();
		File clientsDatabase = new File("");
		com.google.gson.JsonObject restaurants = new com.google.gson.JsonObject();
		com.google.gson.JsonObject emptyObject = new com.google.gson.JsonObject();
		double currentDay = 0;
		if (world.dayTime() % 100 == 30) {
			currentDay = Math.floor(world.dayTime() / 24000d);
			if (MasterchefRestaurantModVariables.MapVariables.get(world).LastClientsDatabaseResetDay < currentDay) {
				clientsDatabase = new File(MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).ClientsDatabase_File_Name);
				{
					try {
						BufferedReader bufferedReader = new BufferedReader(new FileReader(clientsDatabase));
						StringBuilder jsonstringbuilder = new StringBuilder();
						String line;
						while ((line = bufferedReader.readLine()) != null) {
							jsonstringbuilder.append(line);
						}
						bufferedReader.close();
						restaurants = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
						emptyObject.add("restaurants", emptyArray);
						{
							com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
							try {
								FileWriter fileWriter = new FileWriter(clientsDatabase);
								fileWriter.write(mainGSONBuilderVariable.toJson(emptyObject));
								fileWriter.close();
							} catch (IOException exception) {
								exception.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				MasterchefRestaurantModVariables.MapVariables.get(world).LastClientsDatabaseResetDay = currentDay;
				MasterchefRestaurantModVariables.MapVariables.get(world).markSyncDirty();
			}
		}
	}
}