package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.commands.CommandSourceStack;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.lang.reflect.Array;

import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.DoubleArgumentType;

public class DebugRemoveRestaurantProcedure {
	public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments) {
		File ListOfRestaurants = new File("");
		boolean found = false;
		com.google.gson.JsonArray Array = new com.google.gson.JsonArray();
		com.google.gson.JsonObject Object = new com.google.gson.JsonObject();
		com.google.gson.JsonObject RestaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject RestaurantFound = new com.google.gson.JsonObject();
		double Array_ID = 0;
		double Restaurant_ID = 0;
		double index = 0;
		double foundIndex = 0;
		Entity owner = null;
		index = RestaurantIndexSearchByIDProcedure.execute(world, DoubleArgumentType.getDouble(arguments, "Restaurant_ID"));
		if (index >= 0) {
			ListOfRestaurants = new File(MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name);
			{
				try {
					BufferedReader bufferedReader = new BufferedReader(new FileReader(ListOfRestaurants));
					StringBuilder jsonstringbuilder = new StringBuilder();
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						jsonstringbuilder.append(line);
					}
					bufferedReader.close();
					Object = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
					Array = Object.get("restaurants").getAsJsonArray();
					Array.remove(((int) index));
					Object.add("restaurants", Array);
					{
						com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
						try {
							FileWriter fileWriter = new FileWriter(ListOfRestaurants);
							fileWriter.write(mainGSONBuilderVariable.toJson(Object));
							fileWriter.close();
						} catch (IOException exception) {
							exception.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}