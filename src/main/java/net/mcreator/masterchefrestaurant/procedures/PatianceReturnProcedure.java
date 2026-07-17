package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class PatianceReturnProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).ClientPatiance;
	}
}