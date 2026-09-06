package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;

import java.lang.reflect.Array;

import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

@EventBusSubscriber
public class CreatingClientsNameDatabaseJsonFileProcedure {
	@SubscribeEvent
	public static void onWorldLoad(net.neoforged.neoforge.event.level.LevelEvent.Load event) {
		execute(event, event.getLevel());
	}

	public static void execute(LevelAccessor world) {
		execute(null, world);
	}

	private static void execute(@Nullable Event event, LevelAccessor world) {
		File RestaurantsFile = new File("");
		File ClientsNameListFile = new File("");
		com.google.gson.JsonArray Array = new com.google.gson.JsonArray();
		com.google.gson.JsonObject Object = new com.google.gson.JsonObject();
		if (!world.isClientSide()) {
			ClientsNameListFile = new File((FMLPaths.GAMEDIR.get().toString() + "/config/masterchef"), File.separator + "ClientsNameList.json");
			try {
				ClientsNameListFile.getParentFile().mkdirs();
				ClientsNameListFile.createNewFile();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			Object.add("Clients names", Array);
			Array.add("akari");
			Array.add("alberto");
			Array.add("carlo_romano");
			Array.add("chuck");
			Array.add("maggie");
			Array.add("ninjoy");
			Array.add("tohru");
			Array.add("wally");
			{
				com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
				try {
					FileWriter fileWriter = new FileWriter(ClientsNameListFile);
					fileWriter.write(mainGSONBuilderVariable.toJson(Object));
					fileWriter.close();
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		}
	}
}