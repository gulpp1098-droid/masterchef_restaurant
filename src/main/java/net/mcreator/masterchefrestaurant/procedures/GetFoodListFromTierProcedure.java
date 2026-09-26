package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.fml.loading.FMLPaths;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class GetFoodListFromTierProcedure {
	public static com.google.gson.JsonArray execute(LevelAccessor world, double tier) {
		File FoodDatabase = new File("");
		com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject fileObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray tierArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		double foodIndex = 0;
		FoodDatabase = new File((FMLPaths.GAMEDIR.get().toString() + "/config/masterchef"), File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).FoodDatabase_File_Name);
		{
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(FoodDatabase));
				StringBuilder jsonstringbuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonstringbuilder.append(line);
				}
				bufferedReader.close();
				fileObject = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				tiersObject = fileObject.get("tiers").getAsJsonObject();
				tierArray = tiersObject.get(("" + (int) tier)).getAsJsonArray();
				foodIndex = 0;
				for (int _i1 = 0; _i1 < (int) tierArray.size(); _i1++) {
					foodObject = tierArray.get((int) foodIndex).getAsJsonObject();
					foodArray.add(foodObject.get("id").getAsString());
					foodIndex = foodIndex + 1;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return foodArray;
	}
}