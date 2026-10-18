package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import net.mcreator.omnichef.network.OmnichefModVariables;

import java.lang.reflect.Array;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class DebugRemoveRestaurantAllProcedure {
	public static void execute(LevelAccessor world) {
		File ListOfRestaurants = new File("");
		double foundIndex = 0;
		double index = 0;
		com.google.gson.JsonArray Array = new com.google.gson.JsonArray();
		com.google.gson.JsonArray RestaurantsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject RestaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject RestaurantsObject = new com.google.gson.JsonObject();
		boolean saveSuccessful = false;
		if (!world.isClientSide()) {
			if (Level.OVERWORLD == (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))) {
				if (OmnichefModVariables.MapVariables.get(world).ModDataReady) {
					ListOfRestaurants = new File(OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name);
					{
						try {
							BufferedReader bufferedReader = new BufferedReader(new FileReader(ListOfRestaurants));
							StringBuilder jsonstringbuilder = new StringBuilder();
							String line;
							while ((line = bufferedReader.readLine()) != null) {
								jsonstringbuilder.append(line);
							}
							bufferedReader.close();
							RestaurantsObject = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
							RestaurantsObject.add("restaurants", Array);
							saveSuccessful = SaveJsonAtomicallyProcedure.execute(RestaurantsObject, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}