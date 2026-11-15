package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class DebugAddFoodChoiceProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		double restaurantID = 0;
		double restaurantIndex = 0;
		double pendingUnlocks = 0;
		if (world.isClientSide()) {
			return false;
		}
		if (!OmnichefModVariables.MapVariables.get(world).ModDataReady) {
			return false;
		}
		restaurantID = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID;
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (restaurantIndex < 0) {
			return false;
		}
		pendingUnlocks = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
				"pending_food_unlocks");
		if (pendingUnlocks >= 5) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("You already have 5 pending food choices."), false);
			return false;
		}
		ModifyRestaurantNumberParameterProcedure.execute(pendingUnlocks + 1, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
				"pending_food_unlocks");
		GeneratePendingCardsProcedure.execute(world, restaurantID);
		CreateOverlayDataTransferProcedure.execute(world, entity);
		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal("Added one food choice."), false);
		return false;
	}
}