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
		double poolIndex = 0;
		double selectedIndex = 0;
		double currentWeight = 0;
		double previousWeight = 0;
		double olderWeight = 0;
		double totalWeight = 0;
		double randomWeight = 0;
		double starterRemaining = 0;
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
		com.google.gson.JsonObject unlockObject = new com.google.gson.JsonObject();
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (restaurantIndex >= 0) {
			options = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlock_options", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
					OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
			unlockObject.add("unlock_options", options);
			unlockedFoods = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlocked", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
					OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
			unlockObject.add("unlocked", unlockedFoods);
			stageTier = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"stage_tier");
			unlockObject.addProperty("stage_tier", stageTier);
			starterRemaining = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"starter_unlocks_remaining");
			unlockObject.addProperty("starter_unlocks_remaining", starterRemaining);
			pendingCards = GeneratePendingCardsFromDataProcedure.execute(world, unlockObject);
			ModifyRestaurantWholeArrayParameterProcedure.execute(pendingCards, restaurantIndex, "restaurants", "pending_cards", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
					OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
		}
	}
}