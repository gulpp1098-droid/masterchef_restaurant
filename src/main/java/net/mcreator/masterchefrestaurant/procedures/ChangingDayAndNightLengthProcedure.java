package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class ChangingDayAndNightLengthProcedure {
	public static void execute(LevelAccessor world) {
		if (!world.isClientSide()) {
			if (MasterchefRestaurantModVariables.MapVariables.get(world).Time_control_tick % 2 == 0) {
				if (world instanceof ServerLevel _level)
					_level.setDayTime((int) (world.dayTime() - 1));
				MasterchefRestaurantModVariables.MapVariables.get(world).Time_control_tick = MasterchefRestaurantModVariables.MapVariables.get(world).Time_control_tick + 1;
				MasterchefRestaurantModVariables.MapVariables.get(world).markSyncDirty();
			} else {
				MasterchefRestaurantModVariables.MapVariables.get(world).Time_control_tick = MasterchefRestaurantModVariables.MapVariables.get(world).Time_control_tick - 1;
				MasterchefRestaurantModVariables.MapVariables.get(world).markSyncDirty();
			}
		}
	}
}