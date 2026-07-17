package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.client.Minecraft;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import javax.annotation.Nullable;

import java.lang.reflect.Array;

import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

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
		if (!world.isClientSide()) {
			MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path = FMLPaths.GAMEDIR.get().toString() + "/saves/"
					+ (world.isClientSide() ? Minecraft.getInstance().getSingleplayerServer().getWorldData().getLevelName() : ServerLifecycleHooks.getCurrentServer().getWorldData().getLevelName()) + "/masterchef";
			MasterchefRestaurantModVariables.MapVariables.get(world).markSyncDirty();
			RestaurantsFile = new File(MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name);
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
		}
	}
}