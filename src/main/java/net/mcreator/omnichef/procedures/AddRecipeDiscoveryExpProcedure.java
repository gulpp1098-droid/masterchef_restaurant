package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

import net.mcreator.omnichef.network.OmnichefModVariables;

import java.util.UUID;

public class AddRecipeDiscoveryExpProcedure {
	public static void execute(LevelAccessor world, double restaurantID) {
		double restaurantIndex = 0;
		double discoveryExp = 0;
		double starterRemaining = 0;
		double newDiscoveryExp = 0;
		double pendingUnlocks = 0;
		double unlockIndex = 0;
		com.google.gson.JsonArray pendingCards = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockOptions = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockedFoods = new com.google.gson.JsonArray();
		com.google.gson.JsonArray currentlyUnlockableOptions = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockedDishes = new com.google.gson.JsonArray();
		Entity owner = null;
		String ownerUUID = "";
		com.google.gson.JsonObject databaseObject = new com.google.gson.JsonObject();
		if (!world.isClientSide()) {
			restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
			if (restaurantIndex >= 0) {
				ownerUUID = GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "owner");
				owner = world instanceof ServerLevel _level1 ? getEntityFromUUID(_level1, ownerUUID) : null;
				starterRemaining = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						"ingredient_starter_unlocks_remaining");
				pendingUnlocks = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						"pending_food_unlocks");
				pendingCards = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "pending_food_cards", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
				if (starterRemaining <= 0 && pendingUnlocks < 5) {
					discoveryExp = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
							"food_exp");
					newDiscoveryExp = Math.min(discoveryExp + 1, OmnichefModVariables.MapVariables.get(world).RecipeDiscoveryExpRequired);
					if (newDiscoveryExp >= OmnichefModVariables.MapVariables.get(world).RecipeDiscoveryExpRequired) {
						ModifyRestaurantNumberParameterProcedure.execute(0, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
								"food_exp");
						ModifyRestaurantNumberParameterProcedure.execute(pendingUnlocks + 1, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
								OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "pending_food_unlocks");
						if (pendingCards.isEmpty()) {
							GeneratePendingCardsProcedure.execute(world, restaurantID);
						}
						if (owner instanceof Player) {
							if (owner instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("You earned a food unlock! Unlock the required ingredients to reveal available dishes."), true);
						}
					} else {
						ModifyRestaurantNumberParameterProcedure.execute(newDiscoveryExp, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
								OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "food_exp");
					}
					if (owner != null) {
						CreateOverlayDataTransferProcedure.execute(world, owner);
					}
				}
			}
		}
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}