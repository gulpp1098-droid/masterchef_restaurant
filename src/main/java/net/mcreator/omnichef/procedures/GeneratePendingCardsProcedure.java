package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class GeneratePendingCardsProcedure {
	public static void execute(LevelAccessor world, double restaurantID) {
		double restaurantIndex = 0;
		double stageTier = 0;
		double cardsToGenerate = 0;
		double cardIndex = 0;
		double tierIndex = 0;
		double poolIndex = 0;
		double selectedIndex = 0;
		double currentWeight = 0;
		double previousWeight = 0;
		double olderWeight = 0;
		double totalWeight = 0;
		double randomWeight = 0;
		com.google.gson.JsonArray options = new com.google.gson.JsonArray();
		com.google.gson.JsonArray pendingCards = new com.google.gson.JsonArray();
		com.google.gson.JsonArray currentPool = new com.google.gson.JsonArray();
		com.google.gson.JsonArray previousPool = new com.google.gson.JsonArray();
		com.google.gson.JsonArray olderPool = new com.google.gson.JsonArray();
		com.google.gson.JsonArray temporaryPool = new com.google.gson.JsonArray();
		com.google.gson.JsonArray selectedPool = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockedFoods = new com.google.gson.JsonArray();
		String selectedFood = "";
		com.google.gson.JsonObject databaseObject = new com.google.gson.JsonObject();
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (restaurantIndex >= 0) {
			options = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlock_options", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
					OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
			unlockedFoods = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlocked", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
					OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
			databaseObject = ReadFoodDatabaseProcedure.execute(world);
			options = FilterCurrentlyUnlockableFoodOptionsProcedure.execute(options, unlockedFoods, databaseObject);
			stageTier = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"stage_tier");
			currentPool = GetAvailableFoodFromTierProcedure.execute(world, options, stageTier);
			if (stageTier >= 1) {
				previousPool = GetAvailableFoodFromTierProcedure.execute(world, options, stageTier - 1);
			}
			if (stageTier >= 2) {
				for (int _i1 = 0; _i1 < (int) (stageTier - 1); _i1++) {
					temporaryPool = GetAvailableFoodFromTierProcedure.execute(world, options, tierIndex);
					poolIndex = 0;
					for (int _i2 = 0; _i2 < (int) temporaryPool.size(); _i2++) {
						olderPool.add(temporaryPool.get((int) poolIndex).getAsString());
						poolIndex = poolIndex + 1;
					}
					tierIndex = tierIndex + 1;
				}
			}
			cardsToGenerate = Math.min(options.size(), 3);
			for (int _i1 = 0; _i1 < (int) cardsToGenerate; _i1++) {
				currentWeight = 0;
				previousWeight = 0;
				olderWeight = 0;
				if (stageTier == 0) {
					if (currentPool.size() > 0) {
						currentWeight = 100;
					}
				} else if (stageTier == 1) {
					if (currentPool.size() > 0) {
						currentWeight = 20;
					}
					if (previousPool.size() > 0) {
						previousWeight = 80;
					}
				} else {
					if (currentPool.size() > 0) {
						currentWeight = 20;
					}
					if (previousPool.size() > 0) {
						previousWeight = 40;
					}
					if (olderPool.size() > 0) {
						olderWeight = 40;
					}
				}
				totalWeight = currentWeight + olderWeight + previousWeight;
				if (totalWeight > 0) {
					randomWeight = Mth.nextInt(RandomSource.create(), 1, (int) totalWeight);
					if (randomWeight <= currentWeight) {
						selectedPool = currentPool;
					} else if (randomWeight <= currentWeight + previousWeight) {
						selectedPool = previousPool;
					} else {
						selectedPool = olderPool;
					}
				} else {
					selectedPool = options;
				}
				selectedIndex = Mth.nextInt(RandomSource.create(), 0, (int) (selectedPool.size() - 1));
				selectedFood = selectedPool.get((int) selectedIndex).getAsString();
				pendingCards.add(selectedFood);
				options = RemoveFoodFromArrayProcedure.execute(options, selectedFood);
				currentPool = RemoveFoodFromArrayProcedure.execute(currentPool, selectedFood);
				previousPool = RemoveFoodFromArrayProcedure.execute(previousPool, selectedFood);
				olderPool = RemoveFoodFromArrayProcedure.execute(olderPool, selectedFood);
				cardIndex = cardIndex + 1;
			}
			ModifyRestaurantWholeArrayParameterProcedure.execute(pendingCards, restaurantIndex, "restaurants", "pending_cards", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
					OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
		}
	}
}