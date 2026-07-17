package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class OrdersCheckboxesCheckSlot3Procedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if ((entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered).length() == 5) {
			if ((entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(4)).equals("0")) {
				return false;
			}
		} else if ((entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered).length() > 5) {
			if ((entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(4, 5)).equals("0")) {
				return false;
			}
		}
		return true;
	}
}