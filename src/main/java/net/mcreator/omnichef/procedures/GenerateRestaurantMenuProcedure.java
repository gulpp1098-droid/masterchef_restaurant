package net.mcreator.omnichef.procedures;

import net.neoforged.fml.loading.FMLPaths;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Tier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

import net.mcreator.omnichef.network.OmnichefModVariables;

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
		com.google.gson.JsonArray UnlockedFood = new com.google.gson.JsonArray();
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
		double restaurantIndex = 0;
		double randomFoodIndex = 0;
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantIndexDependency);
		UnlockedFood = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlocked", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
		MaxRestaurantLevel = OmnichefModVariables.MapVariables.get(world).MaxRestaurantLevel;
		CurrentRestaurantLevel = restaurantLevelDependency;
		ListOfFood = new File((FMLPaths.GAMEDIR.get().toString() + "/config/omnichef"), File.separator + OmnichefModVariables.MapVariables.get(world).FoodDatabase_File_Name);
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
			CurrentTier = TierCount - 1;
		}
		if (CurrentTier < 0) {
			CurrentTier = 0;
		}
		LevelProgress = CurrentRestaurantLevel / MaxRestaurantLevel;
		minMenuPercent = 0.05;
		maxMenuPercent = 0.5;
		menuPercent = minMenuPercent + (maxMenuPercent - minMenuPercent) * LevelProgress;
		MenuSize = Math.ceil(UnlockedFood.size() * menuPercent);
		if (MenuSize > UnlockedFood.size()) {
			MenuSize = UnlockedFood.size();
		}
		while (menuArray.size() < MenuSize) {
			totalWeight = 0;
			loopTier = 0;
			for (int _i1 = 0; _i1 < (int) (CurrentTier + 1); _i1++) {
				Tier = Tiers.get(("" + (int) loopTier)).getAsJsonArray();
				Tier = FilterAvailableFoodsFromTierArrayProcedure.execute(UnlockedFood, Tier);
				if (Tier.size() > 0) {
					distance = CurrentTier - loopTier;
					weight = Math.round(95 * Math.pow(0.6, distance) + 5);
					totalWeight = totalWeight + weight;
				}
				loopTier = loopTier + 1;
			}
			if (totalWeight <= 0) {
				break;
			}
			randomWeight = Mth.nextInt(RandomSource.create(), 1, (int) totalWeight);
			runningWeight = 0;
			selectedTier = -1;
			loopTier = 0;
			for (int _i1 = 0; _i1 < (int) (CurrentTier + 1); _i1++) {
				Tier = Tiers.get(("" + (int) loopTier)).getAsJsonArray();
				Tier = FilterAvailableFoodsFromTierArrayProcedure.execute(UnlockedFood, Tier);
				if (Tier.size() > 0) {
					distance = CurrentTier - loopTier;
					weight = Math.round(95 * Math.pow(0.6, distance) + 5);
					runningWeight = runningWeight + weight;
					if (randomWeight <= runningWeight) {
						selectedTier = loopTier;
						break;
					}
				}
				loopTier = loopTier + 1;
			}
			Tier = Tiers.get(("" + (int) selectedTier)).getAsJsonArray();
			Tier = FilterAvailableFoodsFromTierArrayProcedure.execute(UnlockedFood, Tier);
			randomFoodIndex = Mth.nextInt(RandomSource.create(), 0, (int) (Tier.size() - 1));
			MealID = Tier.get((int) randomFoodIndex).getAsString();
			menuArray.add(MealID);
			UnlockedFood = RemoveFoodFromArrayProcedure.execute(UnlockedFood, MealID);
		}
		Menufile = new File(OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, File.separator + OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name);
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
				RestaurantObject = menusArray.get((int) restaurantIndex).getAsJsonObject();
				RestaurantObject.add("next_menu", menuArray);
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