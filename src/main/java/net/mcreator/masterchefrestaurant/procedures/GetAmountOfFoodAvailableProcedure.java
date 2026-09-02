package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.fml.loading.FMLPaths;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Tier;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class GetAmountOfFoodAvailableProcedure {
	public static double execute(LevelAccessor world) {
		File FoodFile = new File("");
		com.google.gson.JsonObject FoodDatabase = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Tiers = new com.google.gson.JsonObject();
		com.google.gson.JsonArray Tier = new com.google.gson.JsonArray();
		double TotalAmount = 0;
		double index = 0;
		FoodFile = new File((FMLPaths.GAMEDIR.get().toString() + "/config/masterchef"), File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).FoodDatabase_File_Name);
		{
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(FoodFile));
				StringBuilder jsonstringbuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonstringbuilder.append(line);
				}
				bufferedReader.close();
				FoodDatabase = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				Tiers = FoodDatabase.get("tiers").getAsJsonObject();
				index = 0;
				for (int _i1 = 0; _i1 < (int) Tiers.size(); _i1++) {
					Tier = Tiers.get((new java.text.DecimalFormat("#").format(index))).getAsJsonArray();
					TotalAmount = TotalAmount + Tier.size();
					index = index + 1;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return TotalAmount;
	}
}