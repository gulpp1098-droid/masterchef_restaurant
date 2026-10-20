package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class EnsureTradeBoardCurrentDayProcedure {
	public static boolean execute(LevelAccessor world, double restaurantID) {
		File restaurantsFile = new File("");
		File unlocksFile = new File("");
		com.google.gson.JsonObject root = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonObject tradeBoard = new com.google.gson.JsonObject();
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonObject unlockRoot = new com.google.gson.JsonObject();
		com.google.gson.JsonObject unlockRestaurant = new com.google.gson.JsonObject();
		com.google.gson.JsonArray restaurants = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockRestaurants = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlocked = new com.google.gson.JsonArray();
		com.google.gson.JsonArray offers = new com.google.gson.JsonArray();
		double restaurantIndex = 0;
		double index = 0;
		double currentDay = 0;
		boolean foundUnlocks = false;
		boolean success = false;
		if (world.isClientSide()) {
			return success;
		}
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (restaurantIndex < 0) {
			return success;
		}
		currentDay = Math.floor(world.dayTime() / 24000d);
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
				root = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				restaurants = root.get("restaurants").getAsJsonArray();
				restaurant = restaurants.get((int) restaurantIndex).getAsJsonObject();
				tradeBoard = restaurant.get("trade_board").getAsJsonObject();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (tradeBoard.get("day").getAsDouble() >= currentDay) {
			return true;
		}
		unlocksFile = new File(OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name);
		{
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(unlocksFile));
				StringBuilder jsonstringbuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonstringbuilder.append(line);
				}
				bufferedReader.close();
				unlockRoot = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				unlockRestaurants = unlockRoot.get("restaurants").getAsJsonArray();
				for (int _i1 = 0; _i1 < (int) unlockRestaurants.size(); _i1++) {
					unlockRestaurant = unlockRestaurants.get((int) index).getAsJsonObject();
					if (unlockRestaurant.get("ID").getAsDouble() == restaurantID) {
						unlocked = unlockRestaurant.get("unlocked").getAsJsonArray();
						foundUnlocks = true;
						break;
					}
				}
				if (foundUnlocks) {
					database = ReadFoodDatabaseProcedure.execute(world);
					offers = GenerateTradeBoardOffersProcedure.execute(unlocked, database);
					tradeBoard = CreateDefaultTradeBoardDataProcedure.execute();
					tradeBoard.addProperty("day", currentDay);
					tradeBoard.add("offers", offers);
					restaurant.add("trade_board", tradeBoard);
					success = SaveJsonAtomicallyProcedure.execute(root, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return success;
	}
}