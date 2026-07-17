package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.fml.loading.FMLPaths;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Tier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;

public class GenerateRestaurantMenuProcedure {
	public static void execute(LevelAccessor world, double restaurantIndexDependency, double restaurantLevelDependency) {
		File ListOfFood = new File("");
		File Menufile = new File("");
		com.google.gson.JsonArray Tier = new com.google.gson.JsonArray();
		com.google.gson.JsonArray menusArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray menuArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject Tiers = new com.google.gson.JsonObject();
		com.google.gson.JsonObject Meal = new com.google.gson.JsonObject();
		com.google.gson.JsonObject FoodDatabase = new com.google.gson.JsonObject();
		com.google.gson.JsonObject menusObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject RestaurantObject = new com.google.gson.JsonObject();
		String MealID = "";
		String MealDuplicate = "";
		boolean isDuplicate = false;
		double MaxRestaurantLevel = 0;
		double CurrentRestaurantLevel = 0;
		double TierCount = 0;
		double CurrentTier = 0;
		double minTier = 0;
		double tierWindow = 0;
		double totalWeight = 0;
		double distance = 0;
		double loopTier = 0;
		double randomWeight = 0;
		double runningWeight = 0;
		double weight = 0;
		double selectedTier = 0;
		double TierSize = 0;
		double LevelProgress = 0;
		double minMenuPercent = 0;
		double maxMenuPercent = 0;
		double menuPercent = 0;
		double MenuSize = 0;
		double indexDuplicate = 0;
		double amountOfTiers = 0;
		MaxRestaurantLevel = MasterchefRestaurantModVariables.MapVariables.get(world).MaxRestaurantLevel;
		CurrentRestaurantLevel = restaurantLevelDependency;
		ListOfFood = new File((FMLPaths.GAMEDIR.get().toString() + "/config/masterchef"), File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).FoodDatabase_File_Name);
		{
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(ListOfFood));
				StringBuilder jsonstringbuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonstringbuilder.append(line);
				}
				bufferedReader.close();
				FoodDatabase = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				TierCount = FoodDatabase.get("tier_count").getAsDouble();
				Tiers = FoodDatabase.get("tiers").getAsJsonObject();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		CurrentTier = Math.floor((CurrentRestaurantLevel / MaxRestaurantLevel) * TierCount);
		if (CurrentTier >= TierCount) {
			CurrentTier = CurrentTier - 1;
		}
		minTier = 0;
		totalWeight = 0;
		loopTier = minTier;
		amountOfTiers = CurrentTier - minTier + 1;
		for (int index0 = 0; index0 < (int) (CurrentTier - minTier + 1); index0++) {
			distance = CurrentTier - loopTier;
			totalWeight = totalWeight + 95 * Math.pow(0.6, distance) + 5;
			loopTier = loopTier + 1;
		}
		LevelProgress = CurrentRestaurantLevel / MaxRestaurantLevel;
		minMenuPercent = 0.05;
		maxMenuPercent = 0.5;
		menuPercent = minMenuPercent + (maxMenuPercent - minMenuPercent) * LevelProgress;
		MenuSize = Math.ceil(GetAmountOfFoodAvailableInTiersProcedure.execute(world, amountOfTiers, minTier) * menuPercent);
		while (menuArray.size() < MenuSize) {
			randomWeight = Mth.nextInt(RandomSource.create(), 1, (int) totalWeight);
			loopTier = minTier;
			runningWeight = 0;
			for (int index2 = 0; index2 < (int) (CurrentTier - minTier + 1); index2++) {
				distance = CurrentTier - loopTier;
				weight = 95 * Math.pow(0.6, distance) + 5;
				runningWeight = runningWeight + weight;
				if (randomWeight <= runningWeight) {
					selectedTier = loopTier;
					break;
				}
				loopTier = loopTier + 1;
			}
			Tier = Tiers.get((new java.text.DecimalFormat("0").format(selectedTier))).getAsJsonArray();
			TierSize = Tier.size();
			Meal = Tier.get((int) (Mth.nextInt(RandomSource.create(), 0, (int) (TierSize - 1)))).getAsJsonObject();
			MealID = Meal.get("id").getAsString();
			if (menuArray.size() <= 0) {
				menuArray.add(MealID);
			} else {
				indexDuplicate = 0;
				isDuplicate = false;
				for (int index3 = 0; index3 < (int) menuArray.size(); index3++) {
					if ((menuArray.get((int) indexDuplicate).getAsString()).equals(MealID)) {
						isDuplicate = true;
						break;
					}
					indexDuplicate = indexDuplicate + 1;
				}
				if (!isDuplicate) {
					menuArray.add(MealID);
				}
			}
		}
		Menufile = new File(MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name);
		{
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(Menufile));
				StringBuilder jsonstringbuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonstringbuilder.append(line);
				}
				bufferedReader.close();
				menusObject = new com.google.gson.Gson().fromJson(jsonstringbuilder.toString(), com.google.gson.JsonObject.class);
				menusArray = menusObject.get("restaurants").getAsJsonArray();
				RestaurantObject = menusArray.get((int) RestaurantIndexSearchByIDProcedure.execute(world, restaurantIndexDependency)).getAsJsonObject();
				RestaurantObject.add("menu", menuArray);
				{
					com.google.gson.Gson mainGSONBuilderVariable = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
					try {
						FileWriter fileWriter = new FileWriter(Menufile);
						fileWriter.write(mainGSONBuilderVariable.toJson(menusObject));
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