package net.mcreator.omnichef.procedures;

import net.neoforged.fml.loading.FMLPaths;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import java.lang.reflect.Array;

import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

public class CreatingClientsNameDatabaseJsonFileProcedure {
	public static void execute(LevelAccessor world) {
		File RestaurantsFile = new File("");
		File ClientsNameListFile = new File("");
		com.google.gson.JsonArray Array = new com.google.gson.JsonArray();
		com.google.gson.JsonArray ArrayCritics = new com.google.gson.JsonArray();
		com.google.gson.JsonObject Object = new com.google.gson.JsonObject();
		if (!world.isClientSide()) {
			if (Level.OVERWORLD == (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))) {
				ClientsNameListFile = new File((FMLPaths.GAMEDIR.get().toString() + "/config/omnichef"), File.separator + "ClientsNameList.json");
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
				Object.add("Critics names", ArrayCritics);
				ArrayCritics.add("papa_louie");
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
}