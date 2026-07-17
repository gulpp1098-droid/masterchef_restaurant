package net.mcreator.masterchefrestaurant.procedures;

import org.spongepowered.asm.mixin.injection.Group;

import org.checkerframework.checker.units.qual.s;

import net.neoforged.fml.loading.FMLPaths;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class RestaurantIsOpenProcedure {
	public static void execute(LevelAccessor world, Entity entity, double ChairAmount, double TablesAmount) {
		if (entity == null)
			return;
		File clientsFile = new File("");
		File foodDatabaseFile = new File("");
		com.google.gson.JsonArray members = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodList = new com.google.gson.JsonArray();
		com.google.gson.JsonArray GroupsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray emptyArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray restaurantsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray FoodDeliveredArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray tierArray = new com.google.gson.JsonArray();
		String receptionString = "";
		String chosedFood = "";
		String FoodMenu = "";
		com.google.gson.JsonObject Group = new com.google.gson.JsonObject();
		com.google.gson.JsonObject membersObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject emptyObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject clientDatabase = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject foodDatabase = new com.google.gson.JsonObject();
		com.google.gson.JsonObject tiersObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject mealObject = new com.google.gson.JsonObject();
		boolean Found = false;
		double indexGroup = 0;
		double indexMember = 0;
		double SpawnTime = 0;
		double smallestSpawnTime = 0;
		double smallestTimeIndex = 0;
		double CloseTime = 0;
		double RecX = 0;
		double RecY = 0;
		double RecZ = 0;
		double MinFood = 0;
		double MaxFood = 0;
		double tier = 0;
		double FoodAmountOrder = 0;
		double indexObject = 0;
		double indexArray = 0;
		double restaurantIndex = 0;
		indexGroup = 0;
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
		CloseTime = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
				MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "close_time");
		foodList = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "menu", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
				MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
		foodDatabaseFile = new File((FMLPaths.GAMEDIR.get().toString() + "/config/masterchef"), File.separator + "FoodDatabase.json");
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
		for (int index0 = 0; index0 < Mth.nextInt(RandomSource.create(), (int) TablesAmount, (int) (TablesAmount * 2)); index0++) {
			SpawnTime = Mth.nextInt(RandomSource.create(), (int) (CloseTime - 7900), (int) (CloseTime - 500));
			if (SpawnTime < smallestSpawnTime || indexGroup == 0) {
				smallestSpawnTime = SpawnTime;
				smallestTimeIndex = indexGroup;
			}
			indexMember = 0;
			members = emptyArray.deepCopy();
			Group = new Object() {
				public com.google.gson.JsonObject parse(String rawJson) {
					try {
						return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
					} catch (Exception e) {
						MasterchefRestaurantMod.LOGGER.error(e);
						return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
					}
				}
			}.parse("{}");
			Group.addProperty("group_id", ((int) indexGroup));
			Group.addProperty("spawn_time", ((int) SpawnTime));
			Group.addProperty("spawned", false);
			Group.addProperty("state", "walk");
			for (int index1 = 0; index1 < Mth.nextInt(RandomSource.create(), 1, (int) ChairAmount); index1++) {
				foodArray = emptyArray.deepCopy();
				FoodDeliveredArray = emptyArray.deepCopy();
				membersObject = new Object() {
					public com.google.gson.JsonObject parse(String rawJson) {
						try {
							return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
						} catch (Exception e) {
							MasterchefRestaurantMod.LOGGER.error(e);
							return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
						}
					}
				}.parse("{}");
				if (indexMember == 0) {
					membersObject.addProperty("leader", true);
				} else {
					membersObject.addProperty("leader", false);
				}
				tier = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
				if ((tier) > ((-1)) && (tier) < (6)) {
					MinFood = 1;
					MaxFood = 1;
				} else if ((tier) > (5) && (tier) < (16)) {
					MinFood = 1;
					MaxFood = 2;
				} else if ((tier) > (15) && (tier) < (26)) {
					MinFood = 1;
					MaxFood = 3;
				} else if ((tier) > (25) && (tier) < (41)) {
					MinFood = 2;
					MaxFood = 3;
				} else if ((tier) > (40) && (tier) < (61)) {
					MinFood = 2;
					MaxFood = 4;
				} else if ((tier) > (60) && (tier) < (71)) {
					MinFood = 2;
					MaxFood = 5;
				} else if ((tier) > (70) && (tier) < (81)) {
					MinFood = 3;
					MaxFood = 5;
				} else {
					MinFood = 4;
					MaxFood = 5;
				}
				FoodAmountOrder = Mth.nextInt(RandomSource.create(), (int) MinFood, (int) MaxFood);
				FoodMenu = "";
				for (int index2 = 0; index2 < (int) FoodAmountOrder; index2++) {
					Found = false;
					chosedFood = foodList.get((int) (Mth.nextInt(RandomSource.create(), 0, (int) (foodList.size() - 1)))).getAsString();
					foodArray.add(chosedFood);
					indexObject = 0;
					for (int index3 = 0; index3 < (int) tiersObject.size(); index3++) {
						tierArray = tiersObject.get(("" + (int) indexObject)).getAsJsonArray();
						indexArray = 0;
						for (int index4 = 0; index4 < (int) tierArray.size(); index4++) {
							mealObject = tierArray.get((int) indexArray).getAsJsonObject();
							if ((mealObject.get("id").getAsString()).equals(chosedFood)) {
								Found = true;
								break;
							}
							indexArray = indexArray + 1;
						}
						if (Found) {
							break;
						}
						indexObject = indexObject + 1;
					}
					if (Found) {
						FoodMenu = FoodMenu + "," + (int) mealObject.get("tier").getAsDouble();
					}
				}
				membersObject.add("food", foodArray);
				membersObject.addProperty("foodTiers", (FoodMenu.substring(1)));
				membersObject.addProperty("patience", 100);
				members.add(membersObject);
				indexMember = indexMember + 1;
			}
			Group.add("members", members);
			GroupsArray.add(Group);
			indexGroup = indexGroup + 1;
		}
		receptionString = GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
				MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception");
		RecX = new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(GetPartFromStringProcedure.execute(0, receptionString));
		RecY = new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(GetPartFromStringProcedure.execute(1, receptionString));
		RecZ = new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(GetPartFromStringProcedure.execute(2, receptionString));
		clientsFile = new File(MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).ClientsDatabase_File_Name);
		{
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(clientsFile));
				StringBuilder jsonstringbuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonstringbuilder.append(line);
				}
				bufferedReader.close();
				clientDatabase = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				restaurantsArray = clientDatabase.get("restaurants").getAsJsonArray();
				restaurantObject.addProperty("ID", ((int) entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID));
				restaurantObject.addProperty("next_spawn_group_time", smallestSpawnTime);
				restaurantObject.addProperty("next_spawn_group_index", smallestTimeIndex);
				restaurantObject.addProperty("queue_start", (getBlockNBTString(world, BlockPos.containing(RecX, RecY, RecZ), "last_rug")));
				restaurantObject.add("groups", GroupsArray);
				restaurantsArray.add(restaurantObject);
				clientDatabase.add("restaurants", restaurantsArray);
				{
					com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
					try {
						FileWriter fileWriter = new FileWriter(clientsFile);
						fileWriter.write(mainGSONBuilderVariable.toJson(clientDatabase));
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

	private static String getBlockNBTString(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getString(tag);
		return "";
	}
}