package net.mcreator.omnichef.procedures;

import net.neoforged.fml.loading.FMLPaths;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class GetFoodTierFromIDProcedure {
	public static double execute(LevelAccessor world, String foodID) {
		if (foodID == null)
			return 0;
		File FoodDatabase = new File("");
		com.google.gson.JsonObject fileObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray tierArray = new com.google.gson.JsonArray();
		double tierIndex = 0;
		double foodIndex = 0;
		double tierCount = 0;
		FoodDatabase = new File((FMLPaths.GAMEDIR.get().toString() + "/config/omnichef"), File.separator + OmnichefModVariables.MapVariables.get(world).FoodDatabase_File_Name);
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
				tierCount = fileObject.get("tier_count").getAsDouble();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (int _i1 = 0; _i1 < (int) tierCount; _i1++) {
			tierArray = tiersObject.get(("" + (int) tierIndex)).getAsJsonArray();
			foodIndex = 0;
			for (int _i2 = 0; _i2 < (int) tierArray.size(); _i2++) {
				foodObject = tierArray.get((int) foodIndex).getAsJsonObject();
				if ((foodObject.get("id").getAsString()).equals(foodID)) {
					return tierIndex + 1;
				}
				foodIndex = foodIndex + 1;
			}
			tierIndex = tierIndex + 1;
		}
		return 0;
	}
}