package net.mcreator.omnichef.procedures;

import net.neoforged.fml.loading.FMLPaths;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class GetFoodListTierCountProcedure {
	public static double execute(LevelAccessor world) {
		File FoodDatabase = new File("");
		com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject fileObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray tierArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		double foodIndex = 0;
		double tierCount = 0;
		FoodDatabase = new File((FMLPaths.GAMEDIR.get().toString() + "/config/masterchef"), File.separator + OmnichefModVariables.MapVariables.get(world).FoodDatabase_File_Name);
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
				if (fileObject.get("tier_count").isJsonPrimitive() ? fileObject.get("tier_count").getAsJsonPrimitive().isNumber() : false) {
					tierCount = fileObject.get("tier_count").getAsDouble();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tierCount;
	}
}