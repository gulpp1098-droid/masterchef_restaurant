package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class DailyResetSaveConfirmedProcedure {
	public static boolean execute(LevelAccessor world) {
		File clientDatabaseFile = new File("");
		File restaurantsFile = new File("");
		com.google.gson.JsonObject clientDatabaseObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantsObject = new com.google.gson.JsonObject();
		double currentDay = 0;
		boolean ClientsSaveConfirm = false;
		boolean RestaurantSaveConfirm = false;
		com.google.gson.JsonArray restaurantsArray = new com.google.gson.JsonArray();
		currentDay = Math.floor(world.dayTime() / 24000d);
		clientDatabaseFile = new File(OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + OmnichefModVariables.MapVariables.get(world).ClientsDatabase_File_Name);
		{
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(clientDatabaseFile));
				StringBuilder jsonstringbuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonstringbuilder.append(line);
				}
				bufferedReader.close();
				clientDatabaseObject = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				restaurantsArray = clientDatabaseObject.get("restaurants").getAsJsonArray();
				if (clientDatabaseObject.get("database_day").getAsDouble() == currentDay && restaurantsArray.size() == 0) {
					ClientsSaveConfirm = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		restaurantsFile = new File(OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name);
		{
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(restaurantsFile));
				StringBuilder jsonstringbuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonstringbuilder.append(line);
				}
				bufferedReader.close();
				restaurantsObject = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				if (restaurantsObject.get("last_stats_reset_day").getAsDouble() == currentDay) {
					RestaurantSaveConfirm = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (ClientsSaveConfirm && RestaurantSaveConfirm) {
			return true;
		}
		return false;
	}
}