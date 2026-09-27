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
		double stageUnlocks = 0;
		double starterRemaining = 0;
		double newDiscoveryExp = 0;
		com.google.gson.JsonArray pendingCards = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockOptions = new com.google.gson.JsonArray();
		Entity owner = null;
		String ownerUUID = "";
		if (!world.isClientSide()) {
			restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
			if (restaurantIndex >= 0) {
				ownerUUID = GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "owner");
				owner = world instanceof ServerLevel _level1 ? getEntityFromUUID(_level1, ownerUUID) : null;
				starterRemaining = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						"starter_unlocks_remaining");
				pendingCards = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "pending_cards", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
				unlockOptions = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlock_options", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
				stageUnlocks = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						"stage_unlocks");
				if (starterRemaining <= 0 && pendingCards.isEmpty() && !unlockOptions.isEmpty()) {
					discoveryExp = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
							"discovery_exp") + 1;
					if (stageUnlocks < OmnichefModVariables.MapVariables.get(world).RecipeDiscoveryStageLimit) {
						newDiscoveryExp = Math.min(discoveryExp + 1, OmnichefModVariables.MapVariables.get(world).RecipeDiscoveryExpRequired);
						if (newDiscoveryExp >= OmnichefModVariables.MapVariables.get(world).RecipeDiscoveryExpRequired) {
							ModifyRestaurantNumberParameterProcedure.execute(0, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
									"discovery_exp");
							GeneratePendingCardsProcedure.execute(world, restaurantID);
							if (owner instanceof Player) {
								if (owner instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("New recipe cards are ready! Use the Golden Spatula."), true);
							}
						} else {
							ModifyRestaurantNumberParameterProcedure.execute(discoveryExp, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
									OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "discovery_exp");
						}
					} else {
						if (discoveryExp > OmnichefModVariables.MapVariables.get(world).RecipeDiscoveryExpRequired) {
							discoveryExp = OmnichefModVariables.MapVariables.get(world).RecipeDiscoveryExpRequired;
						}
						ModifyRestaurantNumberParameterProcedure.execute(discoveryExp, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
								OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "discovery_exp");
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