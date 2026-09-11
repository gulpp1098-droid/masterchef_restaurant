package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class DeleteClientsDatabaseFileProcedure {
	public static void execute(LevelAccessor world) {
		com.google.gson.JsonArray emptyArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray restaurantsList = new com.google.gson.JsonArray();
		double currentDay = 0;
		double index = 0;
		double currentTime = 0;
		File clientsDatabase = new File("");
		File restaurantFile = new File("");
		com.google.gson.JsonObject restaurants = new com.google.gson.JsonObject();
		com.google.gson.JsonObject emptyObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantsFileObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject dailyStatsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject lastDayStatsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject newDayStatsObject = new com.google.gson.JsonObject();
		boolean ClientsAlreadyReset = false;
		boolean StatsAlreadyReset = false;
		currentTime = world.dayTime() % 24000;
		currentDay = Math.floor(world.dayTime() / 24000d);
		if (MasterchefRestaurantModVariables.MapVariables.get(world).LastClientsDatabaseResetDay < currentDay && currentTime <= 40) {
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
					restaurantsList = restaurants.get("restaurants").getAsJsonArray();
					if ((restaurants.get("database_day").isJsonPrimitive() ? restaurants.get("database_day").getAsJsonPrimitive().isNumber() : false) && restaurants.get("database_day").getAsDouble() == currentDay && restaurantsList.size() == 0) {
						ClientsAlreadyReset = true;
					}
					if (!ClientsAlreadyReset) {
						emptyObject.add("restaurants", emptyArray);
						emptyObject.addProperty("database_day", currentDay);
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
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			restaurantFile = new File(MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name);
			{
				try {
					BufferedReader bufferedReader = new BufferedReader(new FileReader(restaurantFile));
					StringBuilder jsonstringbuilder = new StringBuilder();
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						jsonstringbuilder.append(line);
					}
					bufferedReader.close();
					restaurantsFileObject = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
					if ((restaurantsFileObject.get("last_stats_reset_day").isJsonPrimitive() ? restaurantsFileObject.get("last_stats_reset_day").getAsJsonPrimitive().isNumber() : false)
							&& restaurantsFileObject.get("last_stats_reset_day").getAsDouble() == currentDay) {
						StatsAlreadyReset = true;
					}
					if (!StatsAlreadyReset) {
						restaurantsList = restaurantsFileObject.get("restaurants").getAsJsonArray();
						index = 0;
						for (int _i1 = 0; _i1 < (int) restaurantsList.size(); _i1++) {
							restaurantObject = restaurantsList.get((int) index).getAsJsonObject();
							dailyStatsObject = restaurantObject.get("daily_stats").getAsJsonObject();
							lastDayStatsObject = new Object() {
								public com.google.gson.JsonObject parse(String rawJson) {
									try {
										return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
									} catch (Exception e) {
										MasterchefRestaurantMod.LOGGER.error(e);
										return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
									}
								}
							}.parse("{}");
							lastDayStatsObject.addProperty("customers_served_fully", dailyStatsObject.get("customers_served_fully").getAsDouble());
							lastDayStatsObject.addProperty("customers_served", dailyStatsObject.get("customers_served").getAsDouble());
							lastDayStatsObject.addProperty("customers_lost", dailyStatsObject.get("customers_lost").getAsDouble());
							lastDayStatsObject.addProperty("coins_earned", dailyStatsObject.get("coins_earned").getAsDouble());
							lastDayStatsObject.addProperty("reputation_change", dailyStatsObject.get("reputation_change").getAsDouble());
							restaurantObject.add("last_day_stats", lastDayStatsObject);
							newDayStatsObject = new Object() {
								public com.google.gson.JsonObject parse(String rawJson) {
									try {
										return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
									} catch (Exception e) {
										MasterchefRestaurantMod.LOGGER.error(e);
										return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
									}
								}
							}.parse("{}");
							newDayStatsObject.addProperty("customers_served_fully", 0);
							newDayStatsObject.addProperty("customers_served", 0);
							newDayStatsObject.addProperty("customers_lost", 0);
							newDayStatsObject.addProperty("coins_earned", 0);
							newDayStatsObject.addProperty("reputation_change", 0);
							restaurantObject.add("daily_stats", newDayStatsObject);
							index = index + 1;
						}
						restaurantsFileObject.addProperty("last_stats_reset_day", currentDay);
						{
							com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
							try {
								FileWriter fileWriter = new FileWriter(restaurantFile);
								fileWriter.write(mainGSONBuilderVariable.toJson(restaurantsFileObject));
								fileWriter.close();
							} catch (IOException exception) {
								exception.printStackTrace();
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (DailyResetSaveConfirmedProcedure.execute(world)) {
				MasterchefRestaurantModVariables.MapVariables.get(world).LastClientsDatabaseResetDay = currentDay;
				MasterchefRestaurantModVariables.MapVariables.get(world).markSyncDirty();
			} else {
				MasterchefRestaurantMod.LOGGER.info("DeleteClientsDatabaseFile: daily reset was not saved correctly.");
			}
		}
	}
}