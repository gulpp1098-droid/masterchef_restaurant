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
public class CreatingRestaurantFoodJsonFileProcedure {
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
		com.google.gson.JsonArray pendingCards = new com.google.gson.JsonArray();
		com.google.gson.JsonObject Object = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantData = new com.google.gson.JsonObject();
		double index = 0;
		boolean changed = false;
		if (!world.isClientSide()) {
			if ((world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD)) == Level.OVERWORLD) {
				OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path = GetMasterchefWorldPathProcedure.execute(world);
				OmnichefModVariables.MapVariables.get(world).markSyncDirty();
				RestaurantsFile = new File(OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name);
				if (!RestaurantsFile.exists()) {
					try {
						RestaurantsFile.getParentFile().mkdirs();
						RestaurantsFile.createNewFile();
					} catch (IOException exception) {
						exception.printStackTrace();
					}
					Object.add("restaurants", Array);
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
						for (int _i1 = 0; _i1 < (int) Array.size(); _i1++) {
							restaurantData = Array.get((int) index).getAsJsonObject();
							if (!Object.has("pending_unlocks")) {
								pendingCards = restaurantData.get("pending_cards").getAsJsonArray();
								if (restaurantData.get("starter_unlocks_remaining").getAsDouble() <= 0 && pendingCards.size() > 0) {
									restaurantData.addProperty("pending_unlocks", 1);
								} else {
									restaurantData.addProperty("pending_unlocks", 0);
								}
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