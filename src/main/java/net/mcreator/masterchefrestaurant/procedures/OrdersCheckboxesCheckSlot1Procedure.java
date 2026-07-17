package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class OrdersCheckboxesCheckSlot1Procedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		OrdersCheckboxesCheckProcedure.execute(world, entity);
		if ((entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(0, 1)).equals("0")
				|| !(entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(0, 1)).equals("1")) {
			return false;
		}
		return true;
	}
}