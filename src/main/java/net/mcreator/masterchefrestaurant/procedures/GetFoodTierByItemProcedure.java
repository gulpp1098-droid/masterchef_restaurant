package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.fml.loading.FMLPaths;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class GetFoodTierByItemProcedure {
	public static double execute(LevelAccessor world, ItemStack itemDependency) {
		File foodDatabaseFile = new File("");
		com.google.gson.JsonArray tierArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject foodDatabase = new com.google.gson.JsonObject();
		com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
		String foodID = "";
		boolean found = false;
		double tier = 0;
		double foodIndex = 0;
		double tierIndex = 0;
		tier = -1;
		found = false;
		foodID = BuiltInRegistries.ITEM.getKey(itemDependency.getItem()).toString();
		if ((foodID).equals("minecraft:air")) {
			return tier;
		}
		foodDatabaseFile = new File((FMLPaths.GAMEDIR.get().toString() + "/config/masterchef"), File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).FoodDatabase_File_Name);
		{
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(foodDatabaseFile));
				StringBuilder jsonstringbuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonstringbuilder.append(line);
				}
				bufferedReader.close();
				foodDatabase = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				tiersObject = foodDatabase.get("tiers").getAsJsonObject();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		tierIndex = 0;
		for (int index0 = 0; index0 < (int) tiersObject.size(); index0++) {
			tierArray = tiersObject.get(("" + (int) tierIndex)).getAsJsonArray();
			foodIndex = 0;
			for (int index1 = 0; index1 < (int) tierArray.size(); index1++) {
				foodObject = tierArray.get((int) foodIndex).getAsJsonObject();
				if ((foodID).equals(foodObject.get("id").getAsString())) {
					found = true;
					tier = tierIndex;
					break;
				}
				foodIndex = foodIndex + 1;
			}
			if (found) {
				break;
			}
			tierIndex = tierIndex + 1;
		}
		return tier;
	}
}