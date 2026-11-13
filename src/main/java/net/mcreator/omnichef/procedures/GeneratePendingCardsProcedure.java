package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class GeneratePendingCardsProcedure {
	public static void execute(LevelAccessor world, double restaurantID) {
		double restaurantIndex = 0;
		double stageTier = 0;
		double cardsToGenerate = 0;
		double cardIndex = 0;
		double tierIndex = 0;
		double selectedIndex = 0;
		double currentWeight = 0;
		double previousWeight = 0;
		double olderWeight = 0;
		double totalWeight = 0;
		double randomWeight = 0;
		double starterRemaining = 0;
		double unlockIndex = 0;
		double groupIndex = 0;
		double pendingChoices = 0;
		com.google.gson.JsonArray options = new com.google.gson.JsonArray();
		com.google.gson.JsonArray pendingCards = new com.google.gson.JsonArray();
		com.google.gson.JsonArray currentPool = new com.google.gson.JsonArray();
		com.google.gson.JsonArray previousPool = new com.google.gson.JsonArray();
		com.google.gson.JsonArray olderPool = new com.google.gson.JsonArray();
		com.google.gson.JsonArray selectedPool = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockedFoods = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockedDishes = new com.google.gson.JsonArray();
		com.google.gson.JsonArray emptyOptions = new com.google.gson.JsonArray();
		com.google.gson.JsonArray tierItems = new com.google.gson.JsonArray();
		String selectedFood = "";
		String cardGroup = "";
		String stageKey = "";
		String cardsKey = "";
		String choicesKey = "";
		com.google.gson.JsonObject databaseObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject unlockObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject optionData = new com.google.gson.JsonObject();
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (restaurantIndex >= 0) {
			unlockedFoods = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlocked_ingredients", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
					OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
			unlockedDishes = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlocked_foods", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
					OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
			unlockIndex = 0;
			for (int _i1 = 0; _i1 < (int) unlockedDishes.size(); _i1++) {
				unlockedFoods.add(unlockedDishes.get((int) unlockIndex).getAsString());
				unlockIndex = unlockIndex + 1;
			}
			databaseObject = ReadFoodDatabaseProcedure.execute(world);
			starterRemaining = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"ingredient_starter_unlocks_remaining");
			for (int _i1 = 0; _i1 < 2; _i1++) {
				if (groupIndex == 0) {
					cardGroup = "INGREDIENT";
					stageKey = "ingredient_stage_tier";
					cardsKey = "\tpending_ingredient_cards";
					choicesKey = "pending_ingredient_unlocks";
				} else {
					cardGroup = "FOOD";
					stageKey = "food_stage_tier";
					cardsKey = "\tpending_food_cards";
					choicesKey = "pending_food_unlocks";
				}
				stageTier = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						stageKey);
				pendingChoices = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						choicesKey);
				pendingCards = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						cardsKey);
				if (pendingCards.isEmpty() && (pendingChoices > 0 || (cardGroup).equals("INGREDIENT") && starterRemaining > 0)) {
					options = emptyOptions.deepCopy();
					tierIndex = 0;
					for (int _i2 = 0; _i2 < (int) (stageTier + 1); _i2++) {
						tierItems = GetFoodListFromTierProcedure.execute(world, tierIndex);
						cardIndex = 0;
						for (int _i3 = 0; _i3 < (int) tierItems.size(); _i3++) {
							optionData = tierItems.get((int) cardIndex).getAsJsonObject();
							options.add(optionData.get("id").getAsString());
							cardIndex = cardIndex + 1;
						}
						tierIndex = tierIndex + 1;
					}
					unlockIndex = 0;
					for (int _i2 = 0; _i2 < (int) unlockedFoods.size(); _i2++) {
						options = RemoveFoodFromArrayProcedure.execute(options, unlockedFoods.get((int) unlockIndex).getAsString());
						unlockIndex = unlockIndex + 1;
					}
				}
				unlockObject.add("unlock_options", options);
				unlockObject.addProperty("stage_tier", stageTier);
				unlockObject.addProperty("unlock_group", cardGroup);
				unlockObject.addProperty("starter_unlocks_remaining", 0);
				if ((cardGroup).equals("INGREDIENT")) {
					unlockObject.addProperty("starter_unlocks_remaining", starterRemaining);
				}
				pendingCards = GeneratePendingCardsFromDataProcedure.execute(world, unlockObject);
				ModifyRestaurantWholeArrayParameterProcedure.execute(pendingCards, restaurantIndex, "restaurants", "pending_cards", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
				groupIndex = groupIndex + 1;
			}
		}
	}
}