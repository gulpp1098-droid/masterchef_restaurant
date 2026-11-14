package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class AddIngredientExpProcedure {
	public static void execute(LevelAccessor world, double amount, double restaurantID) {
		double restaurantIndex = 0;
		double ingredientExp = 0;
		double pendingUnlocks = 0;
		double newExp = 0;
		boolean unlockEarned = false;
		String ownerUUID = "";
		Entity owner = null;
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (restaurantIndex >= 0 && amount > 0) {
			ingredientExp = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"ingredient_exp");
			pendingUnlocks = GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"pending_ingredient_unlocks");
			if (pendingUnlocks < 5) {
				newExp = ingredientExp + Math.min(amount, 100);
				if (newExp >= 100) {
					newExp = newExp - 100;
					ModifyRestaurantNumberParameterProcedure.execute(pendingUnlocks + 1, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
							OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "pending_ingredient_unlocks");
					unlockEarned = true;
				}
				ModifyRestaurantNumberParameterProcedure.execute(newExp, restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						"ingredient_exp");
				if (unlockEarned) {
					GeneratePendingCardsProcedure.execute(world, restaurantID);
				}
				ownerUUID = GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "owner");
				if (world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
					try {
						owner = serverLevel.getServer().getPlayerList().getPlayer(java.util.UUID.fromString(ownerUUID));
					} catch (IllegalArgumentException ignored) {
						owner = null;
					}
				}
				if (owner instanceof ServerPlayer) {
					CreateOverlayDataTransferProcedure.execute(world, owner);
				}
			}
		}
	}
}