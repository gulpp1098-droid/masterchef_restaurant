package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class EnsureClientsDatabaseCurrentDayProcedure {
	public static boolean execute(LevelAccessor world) {
		double currentDay = 0;
		currentDay = Math.floor(world.dayTime() / 24000d);
		if (MasterchefRestaurantModVariables.MapVariables.get(world).LastClientsDatabaseResetDay < currentDay) {
			DeleteClientsDatabaseFileProcedure.execute(world);
		}
		return MasterchefRestaurantModVariables.MapVariables.get(world).LastClientsDatabaseResetDay >= currentDay;
	}
}