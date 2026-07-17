package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class DebugReturnProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		return entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Debug;
	}
}