package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class OrdersCheckboxesCheckSlot2Procedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if ((entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered).length() == 3) {
			if ((entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(2)).equals("0")) {
				return false;
			}
		} else if ((entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered).length() > 3) {
			if ((entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(2, 3)).equals("0")) {
				return false;
			}
		}
		return true;
	}
}