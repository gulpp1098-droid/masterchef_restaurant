package net.mcreator.omnichef.procedures;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import net.mcreator.omnichef.network.OmnichefModVariables;

import javax.annotation.Nullable;

import java.lang.reflect.Array;

import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

@EventBusSubscriber
public class CreatingRestaurantJsonFileProcedure {
	@SubscribeEvent
	public static void onWorldLoad(net.neoforged.neoforge.event.level.LevelEvent.Load event) {
		execute(event, event.getLevel());
	}

	public static void execute(LevelAccessor world) {
		execute(null, world);
	}

	private static void execute(@Nullable Event event, LevelAccessor world) {
		File RestaurantsFile = new File("");
		com.google.gson.JsonArray Array = new com.google.gson.JsonArray();
		com.google.gson.JsonObject Object = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantData = new com.google.gson.JsonObject();
		com.google.gson.JsonObject stats = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantDefaults = new com.google.gson.JsonObject();
		com.google.gson.JsonObject objectHelper = new com.google.gson.JsonObject();
		double index = 0;
		boolean changed = false;
		boolean repairChanged = false;
		if (!world.isClientSide()) {
			if ((world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD)) == Level.OVERWORLD) {
				OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path = GetMasterchefWorldPathProcedure.execute(world);
				OmnichefModVariables.MapVariables.get(world).markSyncDirty();
				RestaurantsFile = new File(OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name);
				if (!RestaurantsFile.exists()) {
					try {
						RestaurantsFile.getParentFile().mkdirs();
						RestaurantsFile.createNewFile();
					} catch (IOException exception) {
						exception.printStackTrace();
					}
					Object.add("restaurants", Array);
					Object.addProperty("last_stats_reset_day", Math.floor(world.dayTime() / 24000d));
					{
						com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
						try {
							FileWriter fileWriter = new FileWriter(RestaurantsFile);
							fileWriter.write(mainGSONBuilderVariable.toJson(Object));
							fileWriter.close();
						} catch (IOException exception) {
							exception.printStackTrace();
						}
					}
				}
				{
					try {
						BufferedReader bufferedReader = new BufferedReader(new FileReader(RestaurantsFile));
						StringBuilder jsonstringbuilder = new StringBuilder();
						String line;
						while ((line = bufferedReader.readLine()) != null) {
							jsonstringbuilder.append(line);
						}
						bufferedReader.close();
						Object = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
						Array = Object.get("restaurants").getAsJsonArray();
						objectHelper = CreateDefaultRestaurantStatsProcedure.execute();
						restaurantDefaults.add("daily_stats", objectHelper);
						restaurantDefaults.add("last_day_stats", objectHelper);
						restaurantDefaults.addProperty("open", false);
						restaurantDefaults.addProperty("close_time", 0);
						restaurantDefaults.addProperty("last_day_open", (-1));
						restaurantDefaults.addProperty("menu_advance_ready", false);
						for (int _i1 = 0; _i1 < (int) Array.size(); _i1++) {
							restaurantData = Array.get((int) index).getAsJsonObject();
							repairChanged = FillMissingJsonPropertiesProcedure.execute(restaurantDefaults, restaurantData);
							if (repairChanged) {
								changed = true;
							}
							index = index + 1;
						}
						if (changed) {
							{
								com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
								try {
									FileWriter fileWriter = new FileWriter(RestaurantsFile);
									fileWriter.write(mainGSONBuilderVariable.toJson(Object));
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
			}
		}
	}
}