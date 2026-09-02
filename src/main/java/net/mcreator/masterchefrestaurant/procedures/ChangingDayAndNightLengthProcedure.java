package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;

public class ChangingDayAndNightLengthProcedure {
	public static void execute(LevelAccessor world) {
		if (!world.isClientSide()) {
			if (world.getLevelData().getGameTime() % 2 == 0) {
				if (world instanceof ServerLevel _level)
					_level.setDayTime((int) (world.dayTime() + 1));
			}
		}
	}
}